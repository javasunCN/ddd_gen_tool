package com.gen.generate.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 基础信息类
 *
 * @author ZhangZhiHong
 */
@Builder
@Data
public class BaseInfoDto {
    /** src **/
    private String src;
    /** src/main **/
    private String mainSrc;
    /** src/test **/
    private String testSrc;
    /** src/main/java **/
    private String javaSrc;
    /** src/main/resources **/
    private String resourcesSrc;
    /** src/main/java/com/yiqian **/
    private String packageSrc;
}
