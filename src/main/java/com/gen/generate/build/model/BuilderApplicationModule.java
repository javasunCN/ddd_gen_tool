package com.gen.generate.build.model;

import cn.hutool.v7.core.text.StrUtil;
import com.gen.generate.commom.BuildBaseInfo;
import com.gen.generate.commom.BuilderTemplateUtil;
import com.gen.generate.dto.BaseInfoDto;
import com.gen.generate.dto.GenerateReqDto;

import java.util.List;

/**
 * 构建应用模块
 *
 * @author ZhangZhiHong
 */
public class BuilderApplicationModule {

    /**
     * 执行构建应用模块
     * @param generateReqDto
     */
    public static void execute(GenerateReqDto generateReqDto) {
        // 生成Mode基础的结构
        String modelName = "application";
        BaseInfoDto baseInfoDto = BuildBaseInfo.buildBasicStructure(generateReqDto, modelName);

        // 模板文件
        generateReqDto.setTemplateFileName("templates/st4/ApplicationPomStg.stg");
        // 模板文件组名
        generateReqDto.setTemplateGroupName("applicationPomTemplate");
        generateReqDto.setModuleName(modelName);
        // 生成文件
        BuilderTemplateUtil.pomGenerator(generateReqDto);

        // 生成启动类 Application.java
        // 模板文件
        generateReqDto.setTemplateFileName("templates/st4/application/Application.stg");
        // 模板文件组名
        generateReqDto.setTemplateGroupName("applicationTemplate");
        generateReqDto.setModuleName(modelName);

        String javaPath = baseInfoDto.getPackageSrc();
        String fileName = "Application.java";
        BuilderTemplateUtil.javaGenerator(generateReqDto, javaPath, fileName);


        // 生成配置文件
        List<String> yamlList = List.of("application", "application-dev", "application-prod", "application-test");
        for (String yaml : yamlList) {
            // 模板文件
            if (!StrUtil.equalsAnyIgnoreCase("application", yaml)) {
                // 模板文件组名
                generateReqDto.setTemplateGroupName("applicationResourceEnvTemplate");
                generateReqDto.setTemplateFileName("templates/st4/applicationResourceEnv.stg");
            } else {
                // 模板文件组名
                generateReqDto.setTemplateGroupName("applicationResourceTemplate");
                generateReqDto.setTemplateFileName("templates/st4/applicationResource.stg");
            }


            String resourcesPath = baseInfoDto.getResourcesSrc();
            String fileName1 = yaml + ".yaml";
            BuilderTemplateUtil.yamlGenerator(generateReqDto, resourcesPath, fileName1);
        }



    }
}
