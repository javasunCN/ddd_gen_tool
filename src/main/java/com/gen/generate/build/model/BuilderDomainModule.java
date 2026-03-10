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
 * 构建领域模块
 *
 * @author ZhangZhiHong
 */
public class BuilderDomainModule {

    /**
     * 执行构建领域模块
     * @param generateReqDto
     */
    public static void execute(GenerateReqDto generateReqDto) {
        // 生成Mode基础的结构
        String modelName = "domain";
        BaseInfoDto baseInfoDto = BuildBaseInfo.buildBasicStructure(generateReqDto, modelName);

        // 模板文件
        generateReqDto.setTemplateFileName("templates/st4/DomainPomStg.stg");
        // 模板文件组名
        generateReqDto.setTemplateGroupName("domainPomTemplate");
        generateReqDto.setModuleName(modelName);
        // 生成文件
        BuilderTemplateUtil.pomGenerator(generateReqDto);

        /* 创建如下包：
            aggregate
            entity
            event
            repository
            service
            valueobject
         **/
        domainPackageGenerator(generateReqDto, baseInfoDto);
    }


    private static void domainPackageGenerator(
            GenerateReqDto generateReqDto,
            BaseInfoDto baseInfoDto
    ) {
        String packageSrc = baseInfoDto.getPackageSrc();
        List<String> packageList = List.of("aggregate", "entity", "event", "external_service", "factory", "repository", "service", "valueobject");

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
            if (StrUtil.equalsAnyIgnoreCase("aggregate", packageName)) {
                // 生成类
                List<String> enumsList = List.of("OrderAggregate");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, packageName);
                }
            } else if (StrUtil.equalsAnyIgnoreCase("entity", packageName)) {
                // 生成类
                List<String> enumsList = List.of("OrderEntity", "OrderProductEntity");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, packageName);
                }
            } else if (StrUtil.equalsAnyIgnoreCase("event", packageName)) {
                // 生成类
                List<String> enumsList = List.of("OrderCreatedEvent", "OrderDeliveredEvent", "OrderPaidEvent", "OrderStatusChangedEvent");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, packageName);
                }
            } else if (StrUtil.equalsAnyIgnoreCase("external_service", packageName)) {
                // 生成类
                List<String> enumsList = List.of("CustomerService", "InventoryService", "PaymentService");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, packageName);
                }
            } else if (StrUtil.equalsAnyIgnoreCase("factory", packageName)) {
                // 生成类
                List<String> enumsList = List.of("GeneralOrderFactory", "OrderFactory", "VipOrderFactory");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, packageName);
                }
            } else if (StrUtil.equalsAnyIgnoreCase("repository", packageName)) {
                // 生成类
                List<String> enumsList = List.of("OrderRepository");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, packageName);
                }
            } else if (StrUtil.equalsAnyIgnoreCase("service", packageName)) {
                // 生成类
                List<String> enumsList = List.of("OrderDomainService", "OrderDomainServiceImpl", "OrderValidateService", "OrderValidateServiceImpl");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, packageName);
                }
            } else if (StrUtil.equalsAnyIgnoreCase("valueobject", packageName)) {
                // 生成类
                List<String> enumsList = List.of("Address", "CustomerCode", "Money", "OrderCode", "ProductId");
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
        generateReqDto.setTemplateFileName("templates/st4/domain/"+templateFileName);
        // 模板文件组名
        generateReqDto.setTemplateGroupName(templateGroupName);
        generateReqDto.setPackagePath(generateReqDto.getGroupId()+"."+pkg);

        String fileName = className+".java";
        BuilderTemplateUtil.javaGenerator(generateReqDto, path, fileName);
    }
}
