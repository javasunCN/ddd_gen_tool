package com.gen.generate.build;

import cn.hutool.v7.core.io.file.FileUtil;
import cn.hutool.v7.core.text.StrUtil;
import com.gen.config.common.ResponseCode;
import com.gen.config.exception.BusinessException;
import com.gen.generate.dto.GenerateReqDto;

import java.io.File;


/**
 * 生成器校验类
 *
 * @author ZhangZhiHong
 */
public class BuilderValidate {

    /**
     * 校验生成器参数
     * @param generateReqDto
     */
    public static void validate(GenerateReqDto generateReqDto) {
        // 传入参数Null校验
        validateNull(generateReqDto);

        // 生成项目的路径等检验
        validateProject(generateReqDto);
    }

    private static void validateProject(GenerateReqDto generateReqDto) {
        String projectName = generateReqDto.getProjectName();
        String projectPath = generateReqDto.getProjectPath();
        String projectAbsolutePath = projectPath + File.separator + projectName;
        // 检查项目是否存在
        boolean isExist = FileUtil.exists(projectAbsolutePath);
        // 项目不存在
        if (!isExist) {
            // 创建项目目录
            FileUtil.mkdir(projectAbsolutePath);
        } else {
            // 项目存在，校验是否为目录
            if (!FileUtil.isDirectory(projectAbsolutePath)) {
                throw new BusinessException(ResponseCode.BAD_REQUEST.getCode(), "项目路径不是目录");
            }
            // 清空项目目录下的文件
            // FileUtil.clean(projectAbsolutePath);
        }
        generateReqDto.setProjectAbsolutePath(projectAbsolutePath);
    }

    private static void validateNull(GenerateReqDto generateReqDto) {
        if (generateReqDto == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST.getCode(), ResponseCode.BAD_REQUEST.getMessage());
        }

        if (StrUtil.isBlank(generateReqDto.getSpringBootVersion())) {
            throw new BusinessException(ResponseCode.BAD_REQUEST.getCode(), ResponseCode.BAD_REQUEST.getMessage());
        }

        if (StrUtil.isBlank(generateReqDto.getProjectName())) {
            throw new BusinessException(ResponseCode.BAD_REQUEST.getCode(), ResponseCode.BAD_REQUEST.getMessage());
        }

        if (StrUtil.isBlank(generateReqDto.getProjectPath())) {
            throw new BusinessException(ResponseCode.BAD_REQUEST.getCode(), ResponseCode.BAD_REQUEST.getMessage());
        }
    }
}
