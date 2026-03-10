package com.gen.generate.build.model;

import com.gen.generate.commom.BuildBaseInfo;
import com.gen.generate.commom.BuilderTemplateUtil;
import com.gen.generate.dto.GenerateReqDto;

import java.io.File;

/**
 * 构建文档模块
 *
 * @author ZhangZhiHong
 */
public class BuilderDocModule {

    /**
    * 执行构建文档模块
     */
    public static void execute(GenerateReqDto generateReqDto) {

        generateReqDto.setModuleName("doc");

        // 文档目录创建README.md
        // 模板文件
        generateReqDto.setTemplateFileName("templates/st4/ReadmeStg.stg");
        // 模板文件组名
        generateReqDto.setTemplateGroupName("readmeTemplate");
        generateReqDto.setDescription("领域驱动文档归档包");
        // 生成文件
        BuilderTemplateUtil.readmeGenerator(generateReqDto);
    }
}
