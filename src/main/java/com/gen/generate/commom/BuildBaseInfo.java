package com.gen.generate.commom;

import cn.hutool.v7.core.io.file.FileUtil;
import com.gen.generate.dto.BaseInfoDto;
import com.gen.generate.dto.GenerateReqDto;

import java.io.File;

/**
 * 构建项目基础包
 *
 * @author ZhangZhiHong
 */
public class BuildBaseInfo {

    /**
     * 生成Mode基础的结构
     * @param generateReqDto 请求对象
     * @param modelName 模块名
     */
    public static BaseInfoDto buildBasicStructure(
            GenerateReqDto generateReqDto,
            String modelName
    ) {
        String projectAbsolutePath = generateReqDto.getProjectAbsolutePath();
        // 级联创建目录 src/main/java/com/yiqian
        String src = projectAbsolutePath + File.separator + modelName + File.separator + "src";
        String mainSrc = src + File.separator +"main";
        String testSrc = src + File.separator +"test";
        String javaSrc = mainSrc + File.separator +"java";
        String resourcesSrc = mainSrc + File.separator +"resources";

        String groupId = generateReqDto.getGroupId();
        String packageSrc = javaSrc + File.separator + groupId.replaceAll("\\.", File.separator);

        // 检查目录是否存在
        boolean isExist1 = FileUtil.exists(testSrc);
        // 目录不存在
        if (!isExist1) {
            // 创建目录
            FileUtil.mkdir(testSrc);
        }

        // 检查目录是否存在
        boolean isExist2 = FileUtil.exists(packageSrc);
        // 目录不存在
        if (!isExist2) {
            // 创建目录
            FileUtil.mkdir(packageSrc);
        }

        // 检查目录是否存在
        boolean isExist3 = FileUtil.exists(resourcesSrc);
        // 目录不存在
        if (!isExist3) {
            // 创建目录
            FileUtil.mkdir(resourcesSrc);
        }

        return BaseInfoDto.builder()
                .src(src)
                .mainSrc(mainSrc)
                .testSrc(testSrc)
                .javaSrc(javaSrc)
                .resourcesSrc(resourcesSrc)
                .packageSrc(packageSrc)
                .build();
    }
}
