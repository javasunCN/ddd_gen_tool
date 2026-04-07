package com.gen.generate.build.model;

import com.gen.generate.commom.BuilderTemplateUtil;
import com.gen.generate.dto.GenerateReqDto;

/**
 * 构建readme文件
 *
 * @author ZhangZhiHong
 */
public class BuilderReadmeModule {

    /**
     * 执行构建readme文件模块
     * @param generateReqDto
     */
    public static void execute(GenerateReqDto generateReqDto) {
        // 模板文件
        generateReqDto.setTemplateFileName("templates/st4/ReadmeStg.stg");
        // 模板文件组名
        generateReqDto.setTemplateGroupName("readmeTemplate");
        generateReqDto.setFileName("README.md");
        // 生成文件
        BuilderTemplateUtil.readmeGenerator(generateReqDto);
    }
}
