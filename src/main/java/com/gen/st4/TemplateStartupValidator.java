package com.gen.st4;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 启动时校验模板
 */
@Slf4j
@Component
@Order(1)  // 设置高优先级，尽早执行
public class TemplateStartupValidator implements CommandLineRunner {

    private final TemplateValidationService validationService;
    private final St4TemplateProperties properties;

    public TemplateStartupValidator(TemplateValidationService validationService,
                                    St4TemplateProperties properties) {
        this.validationService = validationService;
        this.properties = properties;
    }

    @Override
    public void run(String... args) {
        if (!properties.isEnableValidation() || !properties.isValidateOnStartup()) {
            log.info("模板启动校验已禁用");
            return;
        }

        log.info("开始执行 ST4 模板启动校验...");

        try {
            BatchValidationResult result = validationService.validateAllTemplates();

            if (result.getInvalidFiles() > 0) {
                String errorMessage = String.format("发现 %d 个无效模板文件，请检查！", result.getInvalidFiles());

                if (properties.isFailFast()) {
                    log.error(errorMessage);
                    throw new TemplateValidationException(errorMessage);
                } else {
                    log.warn(errorMessage);
                }
            } else {
                log.info("模板启动校验通过，所有模板文件有效！");
            }

        } catch (TemplateValidationException e) {
            throw e;  // 重新抛出
        } catch (Exception e) {
            log.error("模板启动校验过程中发生异常", e);
            if (properties.isFailFast()) {
                throw new TemplateValidationException("模板启动校验失败: " + e.getMessage(), e);
            }
        }
    }
}
