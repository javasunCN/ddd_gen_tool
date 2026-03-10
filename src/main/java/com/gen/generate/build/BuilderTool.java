package com.gen.generate.build;

import com.gen.generate.build.model.BuilderModule;
import com.gen.generate.dto.GenerateReqDto;

/**
 * 生成器工具类
 *
 * @author ZhangZhiHong
 */
public class BuilderTool {

    /**
     * 生成项目入口
     * @param generateReqDto
     */
    public static void execute(GenerateReqDto generateReqDto) {
        // 传入路径进行校验
        BuilderValidate.validate(generateReqDto);
        // 创建项目模块
        BuilderModule.execute(generateReqDto);


    }
}
