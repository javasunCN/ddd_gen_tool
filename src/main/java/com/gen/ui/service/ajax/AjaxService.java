package com.gen.ui.service.ajax;

import cn.hutool.v7.json.JSONObject;

import java.util.List;

/**
 *
 *
 * @author ZhangZhiHong
 */
public interface AjaxService {

    /**
     * 获取SpringBoot版本列表
     * @return
     */
    List<String> getSpringBootVersion();


}
