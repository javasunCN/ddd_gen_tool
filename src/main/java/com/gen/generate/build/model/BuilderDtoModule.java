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
 * 构建DTO模块
 *
 * @author ZhangZhiHong
 */
public class BuilderDtoModule {

    /**
     * 执行构建DTO模块
     * @param generateReqDto
     */
    public static void execute(GenerateReqDto generateReqDto) {
        // 生成Mode基础的结构
        String modelName = "dto";
        BaseInfoDto baseInfoDto = BuildBaseInfo.buildBasicStructure(generateReqDto, modelName);

        // 模板文件
        generateReqDto.setTemplateFileName("templates/st4/DtoPomStg.stg");
        // 模板文件组名
        generateReqDto.setTemplateGroupName("dtoPomTemplate");
        generateReqDto.setModuleName(modelName);
        // 生成文件
        BuilderTemplateUtil.pomGenerator(generateReqDto);

        /* 创建如下包：
            common
            feign
            req
            resp
         **/
        dtoPackageGenerator(generateReqDto, baseInfoDto);
    }

    private static void dtoPackageGenerator(
            GenerateReqDto generateReqDto,
            BaseInfoDto baseInfoDto
    ) {
        String packageSrc = baseInfoDto.getPackageSrc();
        List<String> packageList = List.of("common", "feign", "req", "resp");

        for (String packageName : packageList) {
            String path = packageSrc + File.separator + "order" + File.separator + "dto" + File.separator + packageName;
            // 检查目录是否存在
            boolean isExist1 = FileUtil.exists(path);
            // 目录不存在
            if (!isExist1) {
                // 创建目录
                FileUtil.mkdir(path);
            }

            String orderPackage = "order.dto.";

            // 不同包生成基础的Java类
            if (StrUtil.equalsAnyIgnoreCase("common", packageName)) {
                // 生成类
                List<String> enumsList = List.of("AddressDTO", "MoneyDTO", "PaginationRequest");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, orderPackage + packageName);
                }
            } else if (StrUtil.equalsAnyIgnoreCase("feign", packageName)) {
                // 生成类
                List<String> enumsList = List.of("InventoryProduct");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, orderPackage + packageName);
                }
            } else if (StrUtil.equalsAnyIgnoreCase("req", packageName)) {
                // 生成类
                List<String> enumsList = List.of("CreateOrderRequest", "ProductItemRequest");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, orderPackage + packageName);
                }
            } else if (StrUtil.equalsAnyIgnoreCase("resp", packageName)) {
                // 生成类
                List<String> enumsList = List.of("OrderResponse");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, orderPackage + packageName);
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
        generateReqDto.setTemplateFileName("templates/st4/dto/"+templateFileName);
        // 模板文件组名
        generateReqDto.setTemplateGroupName(templateGroupName);
        generateReqDto.setPackagePath(generateReqDto.getGroupId()+"."+pkg);

        String fileName = className+".java";
        BuilderTemplateUtil.javaGenerator(generateReqDto, path, fileName);
    }
}
