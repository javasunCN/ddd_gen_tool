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
 * 构建北向网关模块
 *
 * @author ZhangZhiHong
 */
public class BuilderNorthGatewayModule {

    /**
     * 执行构建北向网关模块
     * @param generateReqDto
     */
    public static void execute(GenerateReqDto generateReqDto) {
        // 生成Mode基础的结构
        String modelName = "north_gateway";
        BaseInfoDto baseInfoDto = BuildBaseInfo.buildBasicStructure(generateReqDto, modelName);

        // 模板文件
        generateReqDto.setTemplateFileName("templates/st4/NorthGatewayPomStg.stg");
        // 模板文件组名
        generateReqDto.setTemplateGroupName("northGatewayPomTemplate");
        generateReqDto.setModuleName(modelName);
        // 生成文件
        BuilderTemplateUtil.pomGenerator(generateReqDto);

        /* 创建如下包：
            application
            local_gateway
            remote_gateway
         **/
        northGatewayPackageGenerator(generateReqDto, baseInfoDto);
    }

    private static void northGatewayPackageGenerator(
            GenerateReqDto generateReqDto,
            BaseInfoDto baseInfoDto
    ) {
        String packageSrc = baseInfoDto.getPackageSrc();
        List<String> packageList = List.of("application", "local_gateway", "remote_gateway");

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
            if (StrUtil.equalsAnyIgnoreCase("application", packageName)) {
                // application/order
                String path1 = packageSrc + File.separator + packageName + File.separator + "order";
                // 检查目录是否存在
                boolean isExist2 = FileUtil.exists(path1);
                // 目录不存在
                if (!isExist2) {
                    // 创建目录
                    FileUtil.mkdir(path1);
                }
                // 生成类
                List<String> javaList = List.of("OrderApplicationService", "OrderApplicationServiceImpl");
                for (String className : javaList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    // packageName = com.yiqian.application.order
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path1, packageName+".order");


                    List<String> packageList1 = List.of("converter", "strategy");
                    for (String packageName1 : packageList1) {
                        // application/order/converter、strategy
                        String path2 = path1 + File.separator + packageName1;
                        // 检查目录是否存在
                        boolean isExist3 = FileUtil.exists(path2);
                        // 目录不存在
                        if (!isExist3) {
                            // 创建目录
                            FileUtil.mkdir(path2);
                        }

                        if (StrUtil.equalsAnyIgnoreCase("converter", packageName1)) {
                            List<String> javaList1 = List.of("OrderConverter");
                            for (String className1 : javaList1) {
                                String templateFileName1 = className1+".stg";
                                String templateGroupName1 = className1+"Template";
                                // com.yiqian.application.order.converter
                                generatorJavaFile(generateReqDto, templateFileName1, templateGroupName1, className1, path2, packageName+".order."+packageName1);
                            }
                        } else if (StrUtil.equalsAnyIgnoreCase("strategy", packageName1)) {
                            List<String> javaList1 = List.of("OrderStrategy", "OrderStrategyFactory");
                            for (String className1 : javaList1) {
                                String templateFileName1 = className1+".stg";
                                String templateGroupName1 = className1+"Template";
                                // com.yiqian.application.order.converter
                                generatorJavaFile(generateReqDto, templateFileName1, templateGroupName1, className1, path2, packageName+".order."+packageName1);
                            }

                            List<String> packageList2 = List.of("impl");
                            for (String packageName2 : packageList2) {
                                // application/order/strategy/impl
                                String path4 = path2 + File.separator + packageName2;
                                // 检查目录是否存在
                                boolean isExist4 = FileUtil.exists(path4);
                                // 目录不存在
                                if (!isExist4) {
                                    // 创建目录
                                    FileUtil.mkdir(path4);
                                }

                                javaList = List.of("GeneralOrder", "VipOrder");
                                for (String className1 : javaList) {
                                    String templateFileName1 = className1+".stg";
                                    String templateGroupName1 = className1+"Template";
                                    generatorJavaFile(generateReqDto, templateFileName1, templateGroupName1, className1, path4, packageName+".order.strategy."+packageName2);
                                }

                            }
                        }
                    }
                }
            } else if (StrUtil.equalsAnyIgnoreCase("local_gateway", packageName)) {

            } else if (StrUtil.equalsAnyIgnoreCase("remote_gateway", packageName)) {
                path = packageSrc + File.separator + packageName + File.separator + "order";
                // 检查目录是否存在
                isExist1 = FileUtil.exists(path);
                // 目录不存在
                if (!isExist1) {
                    // 创建目录
                    FileUtil.mkdir(path);
                }
                // 生成类
                List<String> enumsList = List.of("OrderController");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, packageName+".order");
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
        generateReqDto.setTemplateFileName("templates/st4/north_gateway/"+templateFileName);
        // 模板文件组名
        generateReqDto.setTemplateGroupName(templateGroupName);
        generateReqDto.setPackagePath(generateReqDto.getGroupId()+"."+pkg);

        String fileName = className+".java";
        BuilderTemplateUtil.javaGenerator(generateReqDto, path, fileName);
    }
}
