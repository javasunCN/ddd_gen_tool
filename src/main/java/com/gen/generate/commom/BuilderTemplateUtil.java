package com.gen.generate.commom;

import cn.hutool.v7.core.io.file.FileUtil;
import cn.hutool.v7.core.io.resource.Resource;
import cn.hutool.v7.core.io.resource.ResourceUtil;
import cn.hutool.v7.core.text.StrUtil;
import com.gen.generate.dto.GenerateReqDto;
import org.stringtemplate.v4.*;
import org.stringtemplate.v4.misc.STMessage;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 模板工具
 * @author ZhangZhiHong
 */
public class BuilderTemplateUtil {

    /**
     * pom.xml文件生成
     * @param generateReqDto
     */
    public static void pomGenerator(
            GenerateReqDto generateReqDto
    ) {
        // 1. 加载模板组
        String templateFileName = generateReqDto.getTemplateFileName();
        STGroup group = loadSTGroup(templateFileName);

        // 2. 渲染模板
        String templateGroupName = generateReqDto.getTemplateGroupName();
        // 获取模板实例前检查模板是否存在
        if (!group.isDefined(templateGroupName)) {
            System.err.println("错误：模板 '"+templateGroupName+"' 未在模板文件中定义！");
            System.err.println("可用的模板有：" + group.getTemplateNames());
            return;
        }
        ST template = loadTemplate(group, templateGroupName);
        // ST template = group.getInstanceOf(templateGroupName);
        if (template == null) {
            System.err.println("错误：无法创建模板实例，请检查模板语法！");
            return;
        }
        // 准备数据
        template.add("data", generateReqDto);


        String codeContent = template.render();

        // 3. 生成输出路径

        String fileName = "pom.xml";
        String outputDir = generateReqDto.getProjectAbsolutePath();
        String moduleName = generateReqDto.getModuleName();
        if (StrUtil.isNotBlank(moduleName)) {
            outputDir = outputDir + File.separator + moduleName;
            // 检查目录是否存在
            boolean isExist1 = FileUtil.exists(outputDir);
            // 目录不存在
            if (!isExist1) {
                // 创建目录
                FileUtil.mkdir(outputDir);
            }
        }
        Path outputPath = Paths.get(outputDir, fileName);
        generatorFile(outputPath, codeContent, templateFileName);
    }


    /**
     * 生成Mapper
     */
    public static void mapperGenerator(
            GenerateReqDto generateReqDto,
            String outputDir,
            String fileName) {
        // 1. 加载模板组
        String templateFileName = generateReqDto.getTemplateFileName();
        STGroup group = loadSTGroup(templateFileName);

        // 2. 渲染模板
        String templateGroupName = generateReqDto.getTemplateGroupName();
        // 获取模板实例前检查模板是否存在
        if (!group.isDefined(templateGroupName)) {
            System.err.println("错误：模板 '"+templateGroupName+"' 未在模板文件中定义！");
            System.err.println("可用的模板有：" + group.getTemplateNames());
            return;
        }
        ST template = loadTemplate(group, templateGroupName);
        if (template == null) {
            System.err.println("错误：无法创建模板实例，请检查模板语法！");
            return;
        }
        // 准备数据
        template.add("data", generateReqDto);

        String codeContent = template.render();

        // 3. 生成输出路径
        Path outputPath = Paths.get(outputDir, fileName);
        generatorFile(outputPath, codeContent, templateFileName);

    }


    private static void generatorFile(Path outputPath, String codeContent, String classTypeName) {
        try {
            if (Files.exists(outputPath)) {
                System.out.println("文件已存在 ~ " + outputPath.toString());
                return;
            }
            // 4. 创建目录并写入文件
            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, codeContent.getBytes());
            System.out.println(classTypeName+"已生成至: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ST loadTemplate(STGroup group, String templateGroupName) {
        ST template = group.getInstanceOf(templateGroupName);
        StringWriter writer = new StringWriter();
        STWriter stWriter = new AutoIndentWriter(writer) ;
        try {
            stWriter.writeSeparator("\n");
            template.write(stWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return template;
    }

    private static STGroup loadSTGroup(String templateFileName) {
        // 生成模版写死(防止乱传)
        Resource resource = ResourceUtil.getResource(templateFileName);
        URL fileUrl = resource.getUrl();
        String path = fileUrl.getPath();


        // 1. 加载模板组
        STGroup group = new STGroupFile(path);
        group.setListener(new STErrorListener() {
            @Override
            public void compileTimeError(STMessage msg) {
                System.err.println("模板编译错误: " + msg);
            }
            @Override
            public void runTimeError(STMessage msg) {
                // 处理运行时错误
            }
            @Override
            public void IOError(STMessage msg) {
                // 处理IO错误
            }
            @Override
            public void internalError(STMessage msg) {
                // 处理内部错误
            }
        });
        return group;
    }


    /**
     * 生成README.md文件
     * @param generateReqDto
     */
    public static void readmeGenerator(GenerateReqDto generateReqDto) {
        // 1. 加载模板组
        String templateFileName = generateReqDto.getTemplateFileName();
        STGroup group = loadSTGroup(templateFileName);

        // 2. 渲染模板
        String templateGroupName = generateReqDto.getTemplateGroupName();
        // 获取模板实例前检查模板是否存在
        if (!group.isDefined(templateGroupName)) {
            System.err.println("错误：模板 '"+templateGroupName+"' 未在模板文件中定义！");
            System.err.println("可用的模板有：" + group.getTemplateNames());
            return;
        }
        ST template = loadTemplate(group, templateGroupName);
        // ST template = group.getInstanceOf(templateGroupName);
        if (template == null) {
            System.err.println("错误：无法创建模板实例，请检查模板语法！");
            return;
        }
        // 准备数据
        template.add("data", generateReqDto);


        String codeContent = template.render();

        // 3. 生成输出路径

        String fileName = StrUtil.isBlank(generateReqDto.getFileName())?"README.md":generateReqDto.getFileName();
        String moduleName = generateReqDto.getModuleName();
        String outputDir = generateReqDto.getProjectAbsolutePath();
        if (StrUtil.isNotBlank(moduleName)) {
            outputDir = outputDir + File.separator + moduleName;
            // 检查目录是否存在
            boolean isExist1 = FileUtil.exists(outputDir);
            // 目录不存在
            if (!isExist1) {
                // 创建目录
                FileUtil.mkdir(outputDir);
            }
        }
        Path outputPath = Paths.get(outputDir, fileName);
        generatorFile(outputPath, codeContent, templateFileName);
    }

    /**
     * 按照模板生成Java类
     * @param generateReqDto
     * @param path Java文件存放的目录路径
     * @param fileName 文件名称
     */
    public static void javaGenerator(
            GenerateReqDto generateReqDto,
            String path,
            String fileName
            ) {
        // 1. 加载模板组
        String templateFileName = generateReqDto.getTemplateFileName();
        STGroup group = loadSTGroup(templateFileName);

        // 2. 渲染模板
        String templateGroupName = generateReqDto.getTemplateGroupName();
        // 获取模板实例前检查模板是否存在
        if (!group.isDefined(templateGroupName)) {
            System.err.println("错误：模板 '"+templateGroupName+"' 未在模板文件中定义！");
            System.err.println("可用的模板有：" + group.getTemplateNames());
            return;
        }
        ST template = loadTemplate(group, templateGroupName);
        // ST template = group.getInstanceOf(templateGroupName);
        if (template == null) {
            System.err.println("错误：无法创建模板实例，请检查模板语法！");
            return;
        }
        // 准备数据
        template.add("data", generateReqDto);

        String codeContent = template.render();

        // 检查目录是否存在
        boolean isExist1 = FileUtil.exists(path);
        // 目录不存在
        if (!isExist1) {
            // 创建目录
            FileUtil.mkdir(path);
        }
        Path outputPath = Paths.get(path, fileName);
        generatorFile(outputPath, codeContent, templateFileName);
    }

    /**
     * 按照模板生成Yaml配置文件
     */
    public static void yamlGenerator(
            GenerateReqDto generateReqDto,
            String path,
            String fileName) {
        // 1. 加载模板组
        String templateFileName = generateReqDto.getTemplateFileName();
        STGroup group = loadSTGroup(templateFileName);

        // 2. 渲染模板
        String templateGroupName = generateReqDto.getTemplateGroupName();
        // 获取模板实例前检查模板是否存在
        if (!group.isDefined(templateGroupName)) {
            System.err.println("错误：模板 '"+templateGroupName+"' 未在模板文件中定义！");
            System.err.println("可用的模板有：" + group.getTemplateNames());
            return;
        }
        ST template = loadTemplate(group, templateGroupName);
        // ST template = group.getInstanceOf(templateGroupName);
        if (template == null) {
            System.err.println("错误：无法创建模板实例，请检查模板语法！");
            return;
        }
        // 准备数据
        template.add("data", generateReqDto);

        String codeContent = template.render();

        // 检查目录是否存在
        boolean isExist1 = FileUtil.exists(path);
        // 目录不存在
        if (!isExist1) {
            // 创建目录
            FileUtil.mkdir(path);
        }
        Path outputPath = Paths.get(path, fileName);
        generatorFile(outputPath, codeContent, templateFileName);
    }
}
