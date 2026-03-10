package com.gen.generate.build.model;

import cn.hutool.v7.core.io.file.FileUtil;
import cn.hutool.v7.core.text.StrUtil;
import com.gen.generate.commom.BuildBaseInfo;
import com.gen.generate.commom.BuilderTemplateUtil;
import com.gen.generate.dto.BaseInfoDto;
import com.gen.generate.dto.GenerateReqDto;

import java.io.File;
import java.util.List;

/**
 * 构建公共模块
 * @author ZhangZhiHong
 */
public class BuilderCommonModule {


    public static void execute(GenerateReqDto generateReqDto) {
        // 生成Mode基础的结构
        String modelName = "common";
        BaseInfoDto baseInfoDto = BuildBaseInfo.buildBasicStructure(generateReqDto, modelName);

        // 模板文件
        generateReqDto.setTemplateFileName("templates/st4/CommonPomStg.stg");
        // 模板文件组名
        generateReqDto.setTemplateGroupName("commonPomTemplate");
        generateReqDto.setModuleName(modelName);
        // 生成文件
        BuilderTemplateUtil.pomGenerator(generateReqDto);


       /* 创建如下包：
        * annotation
        * common
        * config
        * enums
        * exception
        **/
        commonPackageGenerator(generateReqDto, baseInfoDto);


    }

    private static void commonPackageGenerator(
            GenerateReqDto generateReqDto,
            BaseInfoDto baseInfoDto
    ) {
        String packageSrc = baseInfoDto.getPackageSrc();
        List<String> packageList = List.of("annotation", "common", "config", "enums", "exception");

        for (String packageName : packageList) {
            String path = packageSrc + File.separator + packageName;
            // 检查目录是否存在
            boolean isExist1 = FileUtil.exists(path);
            // 目录不存在
            if (!isExist1) {
                // 创建目录
                FileUtil.mkdir(path);
            }

            // 不同包生成基础的Java类
            if (StrUtil.equalsAnyIgnoreCase("annotation", packageName)) {
                // 生成注解类 AggregateRoot、Entity、ValueObject
                List<String> enumsList = List.of("AggregateRoot", "Entity", "ValueObject");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, packageName);
                }
            } else if (StrUtil.equalsAnyIgnoreCase("common", packageName)) {
                // 生成java文件
                List<String> enumsList = List.of("ApiResponse", "PageData", "ResponseCode");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, packageName);
                }
            } else if (StrUtil.equalsAnyIgnoreCase("config", packageName)) {

            } else if (StrUtil.equalsAnyIgnoreCase("enums", packageName)) {
                // 生成java文件
                List<String> enumsList = List.of("OrderCodeEnum", "OrderStatus", "UserTypeEnum");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, packageName);
                }
            } else if (StrUtil.equalsAnyIgnoreCase("exception", packageName)) {
                // 生成java文件
                List<String> enumsList = List.of("GlobalExceptionHandler", "BusinessException");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, packageName);
                }
            }
        }
    }

    public static void generatorJavaFile(GenerateReqDto generateReqDto,
                                         String templateFileName,
                                         String templateGroupName,
                                         String className,
                                         String path,
                                         String pkg
    ) {
        // 模板文件
        generateReqDto.setTemplateFileName("templates/st4/common/"+templateFileName);
        // 模板文件组名
        generateReqDto.setTemplateGroupName(templateGroupName);
        generateReqDto.setPackagePath(generateReqDto.getGroupId()+"."+pkg);

        String fileName = className+".java";
        BuilderTemplateUtil.javaGenerator(generateReqDto, path, fileName);
    }
}
