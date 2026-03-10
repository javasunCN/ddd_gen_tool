package com.gen.generate.build.model;

import com.gen.generate.dto.GenerateReqDto;

/**
 * 构建项目模块
 * @author ZhangZhiHong
 */
public class BuilderModule {

    /**
     * 构建项目模块
     *  注：不要轻易调整生成顺序
     * modules
     *  - common
     *  - application
     *  - domain
     *  - dto
     *  - north_gateway
     *  - south_gateway
     *  - doc
     *  - pom.xml
     *  - README.md
     * @param generateReqDto 生成项目请求参数
     */
    public static void execute(GenerateReqDto generateReqDto) {
        // 文件：pom.xml
        BuilderPomModule.execute(generateReqDto);

        // 文件：README.md
        BuilderReadmeModule.execute(generateReqDto);

        // 模块：common
        BuilderCommonModule.execute(generateReqDto);

        // 模块：application
        BuilderApplicationModule.execute(generateReqDto);

        // 模块：domain
        BuilderDomainModule.execute(generateReqDto);

        // 模块：domain
        BuilderDtoModule.execute(generateReqDto);

        // 模块：north_gateway
        BuilderNorthGatewayModule.execute(generateReqDto);

        // 模块：south_gateway
        BuilderSouthGatewayModule.execute(generateReqDto);

        // 模块：doc
        BuilderDocModule.execute(generateReqDto);


    }
}
