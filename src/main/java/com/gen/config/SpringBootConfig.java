package com.gen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 项目自定义配置项
 *
 * @author ZhangZhiHong
 */
@Data
@Component
@ConfigurationProperties(prefix = "springboot")
public class SpringBootConfig {

    private String jdkVersions;
    /** SpringBoot版本列表 **/
    private String versionUrls;
}
