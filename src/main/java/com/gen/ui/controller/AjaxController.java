package com.gen.ui.controller;

import cn.hutool.v7.json.JSONObject;
import cn.hutool.v7.json.JSONUtil;
import com.gen.ui.service.ajax.AjaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 生成器接口
 *
 * @author ZhangZhiHong
 */
@RestController
@RequestMapping(("/ajax"))
@RequiredArgsConstructor
public class AjaxController {

    private final AjaxService ajaxService;



    /**
     * 获取SpringBoot版本列表
     * @return
     */
    @GetMapping("/getSpringBootVersion")
    public String getSpringBootVersion() {
        List<String> springBootVersionList = ajaxService.getSpringBootVersion();
        return JSONUtil.toJsonStr(springBootVersionList);
    }

}
