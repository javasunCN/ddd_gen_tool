package com.gen.generate.controller;

import com.gen.config.common.ApiResponse;
import com.gen.generate.build.BuilderTool;
import com.gen.generate.dto.GenerateReqDto;
import org.springframework.web.bind.annotation.*;

/**
 * 生成器接口
 *
 * @author ZhangZhiHong
 */
@RestController
@RequestMapping(("/api"))
public class GenerateController {

    /**
     * 生成项目
     * @return
     */
    @PostMapping("/generate")
    public ApiResponse<GenerateReqDto> generate(
            @RequestBody GenerateReqDto generateReqDto
    ) {
        // 执行生成逻辑
        BuilderTool.execute(generateReqDto);
        return ApiResponse.success(generateReqDto);
    }
}
