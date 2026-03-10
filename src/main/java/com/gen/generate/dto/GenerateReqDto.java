package com.gen.generate.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 生成项目请求参数
 * @author ZhangZhiHong
 */
@Data
public class GenerateReqDto {

    /** SpringBoot版本 **/
    private String springBootVersion;
    /** JDK版本 **/
    private String jdkVersion;

    /** 构建项目的名称 **/
    private String projectName;
    /** 构建项目的路径 **/
    private String projectPath;

    /******* Maven项目构建信息 *********/
    /** groupId **/
    private String groupId;
    /** artifactId **/
    private String artifactId;
    /** 微服务项目描述 **/
    private String description;






    /** 构建项目的绝对路径-传入后面使用 **/
    @JsonIgnore
    private String projectAbsolutePath;
    /** 模板: templates/*.stg **/
    @JsonIgnore
    private String templateFileName;
    /** 模板组名称 **/
    @JsonIgnore
    private String templateGroupName;
    /** 模块名称 **/
    @JsonIgnore
    private String moduleName;
    /** 导入包的路径 **/
    @JsonIgnore
    private String packagePath;
}
