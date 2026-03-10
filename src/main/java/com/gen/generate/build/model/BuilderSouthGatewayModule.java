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
 * 构建南向网关模块
 *
 * @author ZhangZhiHong
 */
public class BuilderSouthGatewayModule {

    /**
     * 执行构建南向网关模块
     * @param generateReqDto
     */
    public static void execute(GenerateReqDto generateReqDto) {
        // 生成Mode基础的结构
        String modelName = "south_gateway";
        BaseInfoDto baseInfoDto = BuildBaseInfo.buildBasicStructure(generateReqDto, modelName);


        // 模板文件
        generateReqDto.setTemplateFileName("templates/st4/SouthGatewayPomStg.stg");
        // 模板文件组名
        generateReqDto.setTemplateGroupName("southGatewayPomTemplate");
        generateReqDto.setModuleName(modelName);
        // 生成文件
        BuilderTemplateUtil.pomGenerator(generateReqDto);

        /* 创建如下包：
            client
            external_service
            mybatis
            repository
         **/
        southGatewayPackageGenerator(generateReqDto, baseInfoDto);

        /**
         * mapper.xml
         */
        southGatewayMapperPackageGenerator(generateReqDto, baseInfoDto);
    }

    private static void southGatewayMapperPackageGenerator(GenerateReqDto generateReqDto, BaseInfoDto baseInfoDto) {
        // src/main/resources
        String resourceSrc = baseInfoDto.getResourcesSrc();
        List<String> packageList = List.of("mybatis");

        for (String packageName : packageList) {
            String path = resourceSrc + File.separator + packageName;
            // 检查目录是否存在
            boolean isExist1 = FileUtil.exists(path);
            // 目录不存在
            if (!isExist1) {
                // 创建目录
                FileUtil.mkdir(path);
            }

            String fileName = "OrderMapper.xml";
            // 模板文件
            generateReqDto.setTemplateFileName("templates/st4/south_gateway/MapperPomStg.stg");
            // 模板文件组名
            generateReqDto.setTemplateGroupName("OrderMapperTemplate");
            // 生成文件
            BuilderTemplateUtil.mapperGenerator(generateReqDto, path, fileName);

        }
    }


    private static void southGatewayPackageGenerator(
            GenerateReqDto generateReqDto,
            BaseInfoDto baseInfoDto
    ) {
        String packageSrc = baseInfoDto.getPackageSrc();
        List<String> packageList = List.of("client", "external_service", "infrastructure", "repository");

        for (String packageName : packageList) {
            String path = packageSrc + File.separator + packageName;
            // 检查目录是否存在
            boolean isExist1 = FileUtil.exists(path);
            // 目录不存在
            if (!isExist1) {
                // 创建目录
                FileUtil.mkdir(path);
            }


            if (StrUtil.equalsAnyIgnoreCase("client", packageName)) {

            } else if (StrUtil.equalsAnyIgnoreCase("external_service", packageName)) {
                // 生成类
                List<String> enumsList = List.of("CustomerServiceImpl", "InventoryServiceImpl", "PaymentServiceImpl");
                for (String className : enumsList) {
                    String templateFileName = className+".stg";
                    String templateGroupName = className+"Template";
                    generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path, packageName);
                }
            } else if (StrUtil.equalsAnyIgnoreCase("infrastructure", packageName)) {
                packageList = List.of("mybatis");
                for (String packageName1 : packageList) {
                    String path1 = path + File.separator + packageName1;
                    // 检查目录是否存在
                    isExist1 = FileUtil.exists(path1);
                    // 目录不存在
                    if (!isExist1) {
                        // 创建目录
                        FileUtil.mkdir(path1);
                    }
                    packageList = List.of("tables", "mapper");
                    for (String packageName2 : packageList) {
                        String path2 = path1 + File.separator + packageName2;
                        // 检查目录是否存在
                        isExist1 = FileUtil.exists(path2);
                        // 目录不存在
                        if (!isExist1) {
                            // 创建目录
                            FileUtil.mkdir(path2);
                        }

                        if (StrUtil.equalsAnyIgnoreCase("tables", packageName2)) {
                            // 生成类
                            List<String> enumsList = List.of("Order");
                            for (String className : enumsList) {
                                String templateFileName = className+".stg";
                                String templateGroupName = className+"Template";
                                generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path2, packageName+".mybatis.tables");
                            }
                        } else if (StrUtil.equalsAnyIgnoreCase("mapper", packageName2)) {
                            // 生成类
                            List<String> enumsList = List.of("OrderMapper");
                            for (String className : enumsList) {
                                String templateFileName = className+".stg";
                                String templateGroupName = className+"Template";
                                generatorJavaFile(generateReqDto, templateFileName, templateGroupName, className, path2, packageName+".mybatis.mapper");
                            }
                        }
                    }
                }

            } else if (StrUtil.equalsAnyIgnoreCase("repository", packageName)) {
                // 生成类
                List<String> enumsList = List.of("OrderRepositoryImpl");
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
        generateReqDto.setTemplateFileName("templates/st4/south_gateway/"+templateFileName);
        // 模板文件组名
        generateReqDto.setTemplateGroupName(templateGroupName);
        generateReqDto.setPackagePath(generateReqDto.getGroupId()+"."+pkg);

        String fileName = className+".java";
        BuilderTemplateUtil.javaGenerator(generateReqDto, path, fileName);
    }
}
