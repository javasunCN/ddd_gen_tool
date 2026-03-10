package com.gen.generate.build.model;

import com.gen.generate.commom.BuilderTemplateUtil;
import com.gen.generate.dto.GenerateReqDto;

/**
 * 构建主项目目录：pom文件
 *
 * @author ZhangZhiHong
 */
public class BuilderPomModule {
    /**
     * 执行构建主项目目录：pom文件模块
     * @param generateReqDto
     */
    public static void execute(GenerateReqDto generateReqDto) {

        // 模板文件
        generateReqDto.setTemplateFileName("templates/st4/ParentPomStg.stg");
        // 模板文件组名
        generateReqDto.setTemplateGroupName("parentPomTemplate");
        // 生成文件
        BuilderTemplateUtil.pomGenerator(generateReqDto);
    }
}
