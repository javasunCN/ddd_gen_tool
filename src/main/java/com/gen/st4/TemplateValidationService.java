package com.gen.st4;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.*;
import org.stringtemplate.v4.misc.STMessage;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * ST4 模板校验服务
 */
@Slf4j
@Service
public class TemplateValidationService {

    private final St4TemplateProperties properties;
    private final PathMatchingResourcePatternResolver resourceResolver;

    // 线程池用于并行校验
    private ExecutorService validationExecutor;

    public TemplateValidationService(St4TemplateProperties properties) {
        this.properties = properties;
        this.resourceResolver = new PathMatchingResourcePatternResolver();
    }

    @PostConstruct
    public void init() {
        // 初始化线程池
        int processors = Runtime.getRuntime().availableProcessors();
        this.validationExecutor = Executors.newFixedThreadPool(
                Math.min(processors * 2, 8),
                new ThreadFactory() {
                    private final AtomicInteger counter = new AtomicInteger(1);
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r, "st4-validation-" + counter.getAndIncrement());
                        thread.setDaemon(true);
                        return thread;
                    }
                }
        );
    }

    @PreDestroy
    public void destroy() {
        if (validationExecutor != null) {
            validationExecutor.shutdown();
            try {
                if (!validationExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    validationExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                validationExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 校验所有模板文件
     */
    public BatchValidationResult validateAllTemplates() {
        long startTime = System.currentTimeMillis();
        BatchValidationResult batchResult = new BatchValidationResult();

        try {
            log.info("开始校验 ST4 模板文件，模板目录: {}", properties.getTemplateDir());

            // 查找所有模板文件
            List<Resource> templateResources = findTemplateResources();
            log.info("找到 {} 个模板文件", templateResources.size());

            if (templateResources.isEmpty()) {
                log.warn("未找到任何模板文件");
                return batchResult;
            }

            // 并行校验
            List<CompletableFuture<TemplateValidationResult>> futures = new ArrayList<>();

            for (Resource resource : templateResources) {
                CompletableFuture<TemplateValidationResult> future = CompletableFuture.supplyAsync(
                        () -> validateTemplateResource(resource),
                        validationExecutor
                );
                futures.add(future);
            }

            // 等待所有校验完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // 收集结果
            for (CompletableFuture<TemplateValidationResult> future : futures) {
                try {
                    TemplateValidationResult result = future.get();
                    batchResult.addResult(result);
                } catch (Exception e) {
                    log.error("获取校验结果失败", e);
                }
            }

            long endTime = System.currentTimeMillis();
            batchResult.setTotalDuration(endTime - startTime);
            batchResult.calculateSummary();

            // 生成报告
            if (properties.isGenerateHtmlReport() || properties.isGenerateJsonReport()) {
                generateReports(batchResult);
            }

            // 输出统计信息
            log.info("模板校验完成: 总共 {} 个文件, 有效 {} 个, 无效 {} 个, 有警告 {} 个, 耗时 {}ms",
                    batchResult.getTotalFiles(),
                    batchResult.getValidFiles(),
                    batchResult.getInvalidFiles(),
                    batchResult.getFilesWithWarnings(),
                    batchResult.getTotalDuration());

            if (batchResult.getInvalidFiles() > 0) {
                log.warn("发现 {} 个无效模板文件", batchResult.getInvalidFiles());
                for (TemplateValidationResult invalid : batchResult.getInvalidResults()) {
                    log.warn("无效文件: {}, 错误: {}",
                            invalid.getTemplatePath(),
                            String.join("; ", invalid.getErrors()));
                }
            }

        } catch (Exception e) {
            log.error("模板校验过程发生异常", e);
        }

        return batchResult;
    }

    /**
     * 查找所有模板资源
     */
    private List<Resource> findTemplateResources() throws IOException {
        List<Resource> resources = new ArrayList<>();

        // 构建搜索模式
        StringBuilder patternBuilder = new StringBuilder();
        patternBuilder.append(properties.getTemplateDir());
        if (!patternBuilder.toString().endsWith("/")) {
            patternBuilder.append("/");
        }
        patternBuilder.append("**/*");

        // 构建扩展名匹配
        if (!properties.getExtensions().isEmpty()) {
            patternBuilder.append("{");
            for (int i = 0; i < properties.getExtensions().size(); i++) {
                if (i > 0) {
                    patternBuilder.append(",");
                }
                patternBuilder.append(properties.getExtensions().get(i));
            }
            patternBuilder.append("}");
        }

        String pattern = patternBuilder.toString();
        log.debug("搜索模板文件模式: {}", pattern);

        // 查找资源
        Resource[] foundResources = resourceResolver.getResources(pattern);
        for (Resource resource : foundResources) {
            if (resource.isReadable()) {
                resources.add(resource);
            }
        }

        return resources;
    }

    /**
     * 校验单个模板资源
     */
    private TemplateValidationResult validateTemplateResource(Resource resource) {
        TemplateValidationResult result = new TemplateValidationResult();
        result.setTemplatePath(getResourcePath(resource));

        try {
            // 检查资源是否可读
            if (!resource.isReadable()) {
                result.addError("资源不可读");
                return result;
            }

            // 获取文件信息
            if (resource.isFile() && resource.getFile().isFile()) {
                File file = resource.getFile();
                result.setFileSize(file.length());
                result.setLastModified(new Date(file.lastModified()));
            }

            // 检测编码
            String encoding = detectEncoding(resource);
            result.setEncoding(encoding);

            // 读取文件内容
            String content = readResourceContent(resource, encoding);

            // 检查常见问题
            checkCommonIssues(content, result, resource);

            // 尝试编译模板
            if (!result.hasErrors()) {
                compileTemplate(content, resource, result);
            }

        } catch (Exception e) {
            result.addError("校验过程中发生异常: " + e.getMessage());
            log.error("校验模板资源失败: {}", resource.getDescription(), e);
        }

        return result;
    }

    /**
     * 获取资源路径
     */
    private String getResourcePath(Resource resource) {
        try {
            if (resource.getURI() != null) {
                return resource.getURI().toString();
            }
        } catch (IOException e) {
            // 忽略
        }
        return resource.getDescription();
    }

    /**
     * 检测编码
     */
    private String detectEncoding(Resource resource) throws IOException {
        // 尝试从配置获取
        if (properties.getEncoding() != null) {
            try {
                Charset.forName(properties.getEncoding());
                return properties.getEncoding();
            } catch (Exception e) {
                // 配置的编码无效
            }
        }

        // 读取前几个字节检测 BOM
        byte[] bytes = new byte[4];
        try (InputStream is = resource.getInputStream()) {
            int read = is.read(bytes, 0, 4);

            // 检查 BOM
            if (read >= 3 && bytes[0] == (byte)0xEF && bytes[1] == (byte)0xBB && bytes[2] == (byte)0xBF) {
                return "UTF-8";
            }
            if (read >= 2 && bytes[0] == (byte)0xFE && bytes[1] == (byte)0xFF) {
                return "UTF-16BE";
            }
            if (read >= 2 && bytes[0] == (byte)0xFF && bytes[1] == (byte)0xFE) {
                return "UTF-16LE";
            }
        }

        // 默认返回 UTF-8
        return "UTF-8";
    }

    /**
     * 读取资源内容
     */
    private String readResourceContent(Resource resource, String encoding) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            byte[] bytes = IOUtils.toByteArray(is);
            return new String(bytes, encoding);
        }
    }

    /**
     * 检查常见问题
     */
    private void checkCommonIssues(String content, TemplateValidationResult result, Resource resource) {
        String[] lines = content.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int lineNum = i + 1;

            // 1. 检查模板外部是否使用了 XML 注释
            if (isOutsideTemplate(content, i) && line.trim().startsWith("<!--")) {
                result.addError(String.format("第 %d 行: 在模板外部使用了 XML 注释 (<!-- -->)，应使用 // 或 /* */", lineNum));
            }

            // 2. 检查未转义的 ? 字符
            /*if (line.contains("<?xml") && !line.contains("<\\?xml")) {
                result.addError(String.format("第 %d 行: XML 声明中的 ? 字符未转义，应使用 <\\?xml", lineNum));
            }*/

            // 3. 检查是否包含 BOM
            if (i == 0 && line.startsWith("\uFEFF")) {
                result.addWarning(String.format("第 %d 行: 文件包含 UTF-8 BOM，可能导致兼容性问题", lineNum));
            }

            // 4. 检查中文字符
            if (containsChinese(line) && !"UTF-8".equalsIgnoreCase(result.getEncoding())) {
                result.addWarning(String.format("第 %d 行: 包含中文字符但编码不是 UTF-8，可能导致乱码", lineNum));
            }

            // 5. 检查不匹配的分隔符
            checkDelimiterBalance(line, lineNum, result);

            // 6. 检查未闭合的模板定义
            checkTemplateDefinition(line, lineNum, result);
        }

        // 7. 检查整体结构
        checkOverallStructure(content, result);
    }

    /**
     * 检查是否在模板定义之外
     */
    private boolean isOutsideTemplate(String content, int lineIndex) {
        String[] lines = content.split("\n");

        int templateDepth = 0;
        for (int i = 0; i <= lineIndex; i++) {
            String line = lines[i].trim();

            if (line.contains("::= <<")) {
                templateDepth++;
            }
            if (line.endsWith(">>") && !line.contains("::= <<")) {
                templateDepth--;
            }
        }

        return templateDepth <= 0;
    }

    /**
     * 检查是否包含中文字符
     */
    private boolean containsChinese(String text) {
        for (char c : text.toCharArray()) {
            if (c >= 0x4E00 && c <= 0x9FFF) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查分隔符平衡
     */
    private void checkDelimiterBalance(String line, int lineNum, TemplateValidationResult result) {
        int openCount = countOccurrences(line, "\\$");
        int closeCount = countOccurrences(line, "\\$");

        if (openCount % 2 != 0 || closeCount % 2 != 0) {
            result.addWarning(String.format("第 %d 行: 分隔符 $ 可能不平衡", lineNum));
        }
    }

    /**
     * 检查模板定义
     */
    private void checkTemplateDefinition(String line, int lineNum, TemplateValidationResult result) {
        if (line.contains("::= <<") && !line.contains(">>")) {
            result.addWarning(String.format("第 %d 行: 模板定义开始但未在同一行结束", lineNum));
        }
    }

    /**
     * 检查整体结构
     */
    private void checkOverallStructure(String content, TemplateValidationResult result) {
        // 检查分隔符定义
        if (!content.contains("delimiter")) {
            result.addWarning("未找到分隔符定义 (delimiter)");
        }

        // 检查模板定义
        int templateCount = countOccurrences(content, "::= <<");
        if (templateCount == 0) {
            result.addError("未找到有效的模板定义");
        }

        // 检查模板结束标记
        int endCount = countOccurrences(content, ">>");
        if (templateCount > endCount) {
            result.addError(String.format("模板定义数量 (%d) 大于结束标记数量 (%d)", templateCount, endCount));
        }
    }

    /**
     * 统计字符串出现次数
     */
    private int countOccurrences(String text, String pattern) {
        Pattern p = Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(text);
        int count = 0;
        while (m.find()) {
            count++;
        }
        return count;
    }

    /**
     * 编译模板
     */
    private void compileTemplate(String content, Resource resource, TemplateValidationResult result) {
        try {
            // 创建错误监听器
            List<String> compileErrors = new ArrayList<>();
            STErrorListener errorListener = new STErrorListener() {
                @Override
                public void compileTimeError(STMessage msg) {
                    compileErrors.add(msg.cause.getMessage());
                }

                @Override
                public void runTimeError(STMessage msg) {
                    compileErrors.add(msg.cause.getMessage());
                }

                @Override
                public void IOError(STMessage msg) {
                    String error = String.format("IO错误: %s", msg.toString());
                    compileErrors.add(error);
                }

                @Override
                public void internalError(STMessage msg) {
                    String error = String.format("内部错误: %s", msg.toString());
                    compileErrors.add(error);
                }
            };

            // 创建临时文件来加载模板
            File tempFile = File.createTempFile("st4-template-", ".stg");
            try {
                FileUtils.writeStringToFile(tempFile, content, result.getEncoding());

                // 创建模板组
                STGroup group = new STGroupFile(tempFile.getAbsolutePath());
                group.setListener(errorListener);

                // 尝试获取模板名称列表
                List<String> templateNames = new ArrayList<>(group.getTemplateNames());
                result.setAvailableTemplates(templateNames);

                // 如果有编译错误，添加到结果
                for (String error : compileErrors) {
                    result.addError(error);
                }

            } finally {
                // 清理临时文件
                if (tempFile.exists()) {
                    try {
                        Files.delete(tempFile.toPath());
                    } catch (IOException e) {
                        log.warn("删除临时文件失败: {}", tempFile.getAbsolutePath(), e);
                    }
                }
            }

        } catch (Exception e) {
            result.addError("编译失败: " + e.getMessage());

            // 解析错误信息，提取有用的信息
            String errorMsg = e.getMessage();
            if (errorMsg != null) {
                if (errorMsg.contains("invalid character '?'")) {
                    result.addError("包含未转义的 ? 字符，XML 声明应使用 <\\?xml");
                } else if (errorMsg.contains("Nonterminated comment")) {
                    result.addError("注释未正确关闭，请检查注释语法");
                } else if (errorMsg.contains("missing '>'")) {
                    result.addError("缺少结束标记 >");
                }
            }
        }
    }

    /**
     * 生成校验报告
     */
    private void generateReports(BatchValidationResult batchResult) {
        try {
            // 创建报告目录
            Path reportDir = Paths.get(properties.getValidationReportDir());
            Files.createDirectories(reportDir);

            // 生成时间戳
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            String baseName = "st4-validation-" + timestamp;

            // 生成文本报告
            Path textReportPath = reportDir.resolve(baseName + ".txt");
            Files.write(textReportPath, generateTextReport(batchResult).getBytes(StandardCharsets.UTF_8));
            log.info("文本报告已生成: {}", textReportPath.toAbsolutePath());

            // 生成 HTML 报告
            if (properties.isGenerateHtmlReport()) {
                Path htmlReportPath = reportDir.resolve(baseName + ".html");
                Files.write(htmlReportPath, generateHtmlReport(batchResult).getBytes(StandardCharsets.UTF_8));
                log.info("HTML 报告已生成: {}", htmlReportPath.toAbsolutePath());
            }

            // 生成 JSON 报告
            if (properties.isGenerateJsonReport()) {
                Path jsonReportPath = reportDir.resolve(baseName + ".json");
                Files.write(jsonReportPath, generateJsonReport(batchResult).getBytes(StandardCharsets.UTF_8));
                log.info("JSON 报告已生成: {}", jsonReportPath.toAbsolutePath());
            }

        } catch (Exception e) {
            log.error("生成校验报告失败", e);
        }
    }

    /**
     * 生成文本报告
     */
    private String generateTextReport(BatchValidationResult batchResult) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("ST4 模板校验报告\n");
        sb.append("=".repeat(60)).append("\n");
        sb.append("生成时间: ").append(batchResult.getValidationTime()).append("\n");
        sb.append("总文件数: ").append(batchResult.getTotalFiles()).append("\n");
        sb.append("有效文件: ").append(batchResult.getValidFiles()).append(" (")
                .append(String.format("%.1f", batchResult.getSummary().get("validPercentage"))).append("%)\n");
        sb.append("无效文件: ").append(batchResult.getInvalidFiles()).append(" (")
                .append(String.format("%.1f", batchResult.getSummary().get("invalidPercentage"))).append("%)\n");
        sb.append("有警告文件: ").append(batchResult.getFilesWithWarnings()).append("\n");
        sb.append("总耗时: ").append(batchResult.getTotalDuration()).append("ms\n");
        sb.append("平均耗时: ").append(String.format("%.1f", batchResult.getSummary().get("averageDuration"))).append("ms\n");
        sb.append("=".repeat(60)).append("\n\n");

        if (batchResult.getInvalidFiles() > 0) {
            sb.append("无效文件列表:\n");
            sb.append("-".repeat(40)).append("\n");
            for (TemplateValidationResult invalid : batchResult.getInvalidResults()) {
                sb.append(invalid.getTemplatePath()).append("\n");
                for (String error : invalid.getErrors()) {
                    sb.append("  - ").append(error).append("\n");
                }
            }
            sb.append("\n");
        }

        if (batchResult.getFilesWithWarnings() > 0) {
            sb.append("有警告的文件列表:\n");
            sb.append("-".repeat(40)).append("\n");
            for (TemplateValidationResult warning : batchResult.getResultsWithWarnings()) {
                sb.append(warning.getTemplatePath()).append("\n");
                for (String warn : warning.getWarnings()) {
                    sb.append("  - ").append(warn).append("\n");
                }
            }
        }

        return sb.toString();
    }

    /**
     * 生成 HTML 报告
     */
    private String generateHtmlReport(BatchValidationResult batchResult) {
        StringBuilder html = new StringBuilder();
        html.append("""
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>ST4 模板校验报告</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { 
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                        line-height: 1.6; 
                        color: #333; 
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        min-height: 100vh;
                        padding: 20px;
                    }
                    .container { 
                        max-width: 1200px; 
                        margin: 0 auto; 
                    }
                    .report-card { 
                        background: white; 
                        border-radius: 10px; 
                        box-shadow: 0 20px 40px rgba(0,0,0,0.1); 
                        overflow: hidden; 
                        margin-bottom: 20px;
                    }
                    .header { 
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); 
                        color: white; 
                        padding: 30px; 
                        text-align: center; 
                    }
                    .header h1 { 
                        font-size: 2.5em; 
                        margin-bottom: 10px; 
                    }
                    .summary { 
                        display: grid; 
                        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); 
                        gap: 20px; 
                        padding: 30px; 
                        background: #f8f9fa; 
                    }
                    .summary-item { 
                        background: white; 
                        padding: 20px; 
                        border-radius: 8px; 
                        text-align: center; 
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1); 
                    }
                    .summary-item .number { 
                        font-size: 2.5em; 
                        font-weight: bold; 
                        margin-bottom: 10px; 
                    }
                    .summary-item.valid .number { color: #28a745; }
                    .summary-item.invalid .number { color: #dc3545; }
                    .summary-item.total .number { color: #007bff; }
                    .results { 
                        padding: 30px; 
                    }
                    .file-list { 
                        margin-top: 20px; 
                    }
                    .file-item { 
                        background: #f8f9fa; 
                        border-left: 4px solid #007bff; 
                        margin-bottom: 15px; 
                        padding: 20px; 
                        border-radius: 0 8px 8px 0; 
                    }
                    .file-item.invalid { 
                        border-left-color: #dc3545; 
                        background: #ffe6e6; 
                    }
                    .file-header { 
                        display: flex; 
                        justify-content: space-between; 
                        align-items: center; 
                        margin-bottom: 10px; 
                    }
                    .file-name { 
                        font-weight: bold; 
                        color: #333; 
                        word-break: break-all;
                    }
                    .file-status { 
                        padding: 5px 15px; 
                        border-radius: 20px; 
                        font-size: 0.9em; 
                        font-weight: bold; 
                        white-space: nowrap;
                    }
                    .status-valid { 
                        background: #d4edda; 
                        color: #155724; 
                    }
                    .status-invalid { 
                        background: #f8d7da; 
                        color: #721c24; 
                    }
                    .file-details { 
                        margin-top: 10px; 
                        font-size: 0.9em;
                    }
                    .error-list, .warning-list { 
                        margin-top: 10px; 
                    }
                    .error-item, .warning-item { 
                        padding: 8px 12px; 
                        margin-bottom: 5px; 
                        border-radius: 4px; 
                    }
                    .error-item { 
                        background: #f8d7da; 
                        border-left: 3px solid #dc3545; 
                    }
                    .warning-item { 
                        background: #fff3cd; 
                        border-left: 3px solid #ffc107; 
                    }
                    .toggle-details { 
                        background: none; 
                        border: none; 
                        color: #007bff; 
                        cursor: pointer; 
                        font-size: 0.9em; 
                        margin-top: 10px;
                    }
                    .details-content { 
                        display: none; 
                        margin-top: 10px; 
                    }
                    .details-content.show { 
                        display: block; 
                    }
                    .templates-list { 
                        display: flex; 
                        flex-wrap: wrap; 
                        gap: 5px; 
                        margin-top: 10px; 
                    }
                    .template-tag { 
                        background: #e3f2fd; 
                        color: #1976d2; 
                        padding: 3px 8px; 
                        border-radius: 12px; 
                        font-size: 0.85em; 
                    }
                    .no-results { 
                        text-align: center; 
                        color: #666; 
                        padding: 40px; 
                        font-style: italic;
                    }
                </style>
            </head>
            <body>
            """);

        html.append("<div class='container'>");
        html.append("<div class='report-card'>");

        // 头部
        html.append("<div class='header'>");
        html.append("<h1>📄 ST4 模板校验报告</h1>");
        html.append("<p>生成时间: ").append(batchResult.getValidationTime()).append("</p>");
        html.append("</div>");

        // 汇总信息
        html.append("<div class='summary'>");
        html.append(String.format("""
            <div class="summary-item total">
                <div class="number">%d</div>
                <div>总文件数</div>
            </div>
            """, batchResult.getTotalFiles()));
        html.append(String.format("""
            <div class="summary-item valid">
                <div class="number">%d</div>
                <div>有效文件</div>
            </div>
            """, batchResult.getValidFiles()));
        html.append(String.format("""
            <div class="summary-item invalid">
                <div class="number">%d</div>
                <div>无效文件</div>
            </div>
            """, batchResult.getInvalidFiles()));
        html.append(String.format("""
            <div class="summary-item">
                <div class="number">%dms</div>
                <div>总耗时</div>
            </div>
            """, batchResult.getTotalDuration()));
        html.append("</div>");

        // 详细结果
        html.append("<div class='results'>");
        html.append("<h2>详细校验结果</h2>");

        if (batchResult.getResults().isEmpty()) {
            html.append("<div class='no-results'>未找到任何模板文件</div>");
        } else {
            html.append("<div class='file-list'>");

            for (TemplateValidationResult result : batchResult.getResults()) {
                String statusClass = result.isValid() ? "" : "invalid";
                String statusText = result.isValid() ? "有效" : "无效";
                String statusSpanClass = result.isValid() ? "status-valid" : "status-invalid";

                html.append(String.format("""
                    <div class="file-item %s">
                        <div class="file-header">
                            <div class="file-name">%s</div>
                            <span class="file-status %s">%s</span>
                        </div>
                        <div class="file-details">
                            <div><strong>编码:</strong> %s</div>
                            <div><strong>大小:</strong> %s</div>
                    """,
                        statusClass,
                        escapeHtml(result.getTemplatePath()),
                        statusSpanClass,
                        statusText,
                        result.getEncoding() != null ? result.getEncoding() : "未知",
                        formatFileSize(result.getFileSize())
                ));

                if (result.getLastModified() != null) {
                    html.append(String.format("<div><strong>修改时间:</strong> %s</div>", result.getLastModified()));
                }

                if (result.getAvailableTemplates() != null && !result.getAvailableTemplates().isEmpty()) {
                    html.append("<div><strong>可用模板:</strong></div>");
                    html.append("<div class='templates-list'>");
                    for (String template : result.getAvailableTemplates()) {
                        html.append(String.format("<span class='template-tag'>%s</span>", escapeHtml(template)));
                    }
                    html.append("</div>");
                }

                if (result.hasErrors()) {
                    html.append("<button class='toggle-details' onclick='toggleDetails(this)'>显示错误详情 (" + result.getErrors().size() + ")</button>");
                    html.append("<div class='details-content'>");
                    html.append("<div class='error-list'>");
                    for (String error : result.getErrors()) {
                        html.append(String.format("<div class='error-item'>%s</div>", escapeHtml(error)));
                    }
                    html.append("</div>");
                    html.append("</div>");
                }

                if (result.hasWarnings()) {
                    html.append("<button class='toggle-details' onclick='toggleDetails(this)'>显示警告详情 (" + result.getWarnings().size() + ")</button>");
                    html.append("<div class='details-content'>");
                    html.append("<div class='warning-list'>");
                    for (String warning : result.getWarnings()) {
                        html.append(String.format("<div class='warning-item'>%s</div>", escapeHtml(warning)));
                    }
                    html.append("</div>");
                    html.append("</div>");
                }

                html.append("</div></div>");
            }

            html.append("</div>");
        }

        html.append("</div></div></div>");

        // JavaScript
        html.append("""
            <script>
            function toggleDetails(button) {
                const details = button.nextElementSibling;
                details.classList.toggle('show');
                button.textContent = details.classList.contains('show') ? 
                    '隐藏详情' : button.textContent.replace('隐藏', '显示');
            }
            </script>
            </body>
            </html>
            """);

        return html.toString();
    }

    /**
     * 生成 JSON 报告
     */
    private String generateJsonReport(BatchValidationResult batchResult) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("totalFiles", batchResult.getTotalFiles());
        json.put("validFiles", batchResult.getValidFiles());
        json.put("invalidFiles", batchResult.getInvalidFiles());
        json.put("filesWithWarnings", batchResult.getFilesWithWarnings());
        json.put("totalDuration", batchResult.getTotalDuration());
        json.put("validationTime", batchResult.getValidationTime());
        json.put("summary", batchResult.getSummary());

        List<Map<String, Object>> fileResults = new ArrayList<>();
        for (TemplateValidationResult result : batchResult.getResults()) {
            Map<String, Object> fileJson = new LinkedHashMap<>();
            fileJson.put("path", result.getTemplatePath());
            fileJson.put("valid", result.isValid());
            fileJson.put("encoding", result.getEncoding());
            fileJson.put("size", result.getFileSize());
            fileJson.put("lastModified", result.getLastModified());
            fileJson.put("errors", result.getErrors());
            fileJson.put("warnings", result.getWarnings());
            fileJson.put("availableTemplates", result.getAvailableTemplates());
            fileResults.add(fileJson);
        }
        json.put("files", fileResults);

        // 使用 Jackson 或其他 JSON 库序列化
        // 这里使用简单的字符串拼接
        return toJsonString(json);
    }

    /**
     * 简单的 JSON 序列化
     */
    private String toJsonString(Object obj) {
        if (obj == null) {
            return "null";
        }

        if (obj instanceof String) {
            return "\"" + escapeJson((String) obj) + "\"";
        }

        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }

        if (obj instanceof Date) {
            return "\"" + obj.toString() + "\"";
        }

        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) {
                    sb.append(",");
                }
                sb.append("\"").append(entry.getKey()).append("\":");
                sb.append(toJsonString(entry.getValue()));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }

        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : list) {
                if (!first) {
                    sb.append(",");
                }
                sb.append(toJsonString(item));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        }

        return "\"" + obj.toString() + "\"";
    }

    /**
     * 转义 JSON 特殊字符
     */
    private String escapeJson(String str) {
        if (str == null){
            return "";
        }
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * 转义 HTML 特殊字符
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        }
        if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        }
        return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }
}
