package com.gen.st4;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import java.util.Arrays;
import java.util.List;

/**
 * ST4 模板配置属性
 */
@Component
@Validated
@ConfigurationProperties(prefix = "st4.template")
public class St4TemplateProperties {

    /**
     * 是否启用模板校验
     */
    private boolean enableValidation = true;

    /**
     * 启动时是否进行模板校验
     */
    private boolean validateOnStartup = true;

    /**
     * 校验失败时是否阻止应用启动
     */
    private boolean failFast = false;

    /**
     * 模板文件目录，支持 classpath: 和 file: 前缀
     */
    @NotEmpty
    private String templateDir = "classpath:templates/";

    /**
     * 模板文件扩展名
     */
    private List<String> extensions = Arrays.asList(".stg", ".st");

    /**
     * 校验报告输出目录
     */
    private String validationReportDir = "./logs/st4-validation";

    /**
     * 是否生成详细的 HTML 报告
     */
    private boolean generateHtmlReport = true;

    /**
     * 是否生成 JSON 报告
     */
    private boolean generateJsonReport = true;

    /**
     * 模板文件编码
     */
    @NotEmpty
    private String encoding = "UTF-8";

    /**
     * 模板热重载
     */
    private boolean hotReload = true;

    /**
     * 热重载检查间隔（秒）
     */
    private int hotReloadInterval = 5;

    /**
     * 模板缓存
     */
    private boolean cacheTemplates = true;

    // Getters and Setters
    public boolean isEnableValidation() {
        return enableValidation;
    }

    public void setEnableValidation(boolean enableValidation) {
        this.enableValidation = enableValidation;
    }

    public boolean isValidateOnStartup() {
        return validateOnStartup;
    }

    public void setValidateOnStartup(boolean validateOnStartup) {
        this.validateOnStartup = validateOnStartup;
    }

    public boolean isFailFast() {
        return failFast;
    }

    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }

    public String getTemplateDir() {
        return templateDir;
    }

    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
    }

    public String getValidationReportDir() {
        return validationReportDir;
    }

    public void setValidationReportDir(String validationReportDir) {
        this.validationReportDir = validationReportDir;
    }

    public boolean isGenerateHtmlReport() {
        return generateHtmlReport;
    }

    public void setGenerateHtmlReport(boolean generateHtmlReport) {
        this.generateHtmlReport = generateHtmlReport;
    }

    public boolean isGenerateJsonReport() {
        return generateJsonReport;
    }

    public void setGenerateJsonReport(boolean generateJsonReport) {
        this.generateJsonReport = generateJsonReport;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean isHotReload() {
        return hotReload;
    }

    public void setHotReload(boolean hotReload) {
        this.hotReload = hotReload;
    }

    public int getHotReloadInterval() {
        return hotReloadInterval;
    }

    public void setHotReloadInterval(int hotReloadInterval) {
        this.hotReloadInterval = hotReloadInterval;
    }

    public boolean isCacheTemplates() {
        return cacheTemplates;
    }

    public void setCacheTemplates(boolean cacheTemplates) {
        this.cacheTemplates = cacheTemplates;
    }
}
