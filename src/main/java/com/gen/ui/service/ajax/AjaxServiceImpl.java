package com.gen.ui.service.ajax;

import cn.hutool.v7.core.xml.XmlUtil;
import cn.hutool.v7.http.HttpUtil;
import cn.hutool.v7.json.JSON;
import cn.hutool.v7.json.JSONObject;
import cn.hutool.v7.json.JSONUtil;
import com.gen.config.SpringBootConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 异步请求服务实现类
 * @author: ZhangZhiHong
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AjaxServiceImpl implements AjaxService {

    private final SpringBootConfig springBootConfig;


    @Override
    public List<String> getSpringBootVersion() {
        String versionUrls = springBootConfig.getVersionUrls();
        // 1. 读取网络 XML 文件内容
        String xmlContent = HttpUtil.get(versionUrls);
        // 2. 解析 XML 内容
        NodeList versionList = XmlUtil.parseXml(xmlContent).getElementsByTagName("version");

        List<String> versionListStr = new ArrayList<>();
        for (int i = 0; i < versionList.getLength(); i++) {
            String version = versionList.item(i).getTextContent();
            if (!version.contains("-") && (version.startsWith("3") || version.startsWith("4"))) {
                versionListStr.add(version);
            }
        }
        versionListStr.sort((v1, v2) -> {
           return v1.contains(v2) ? 1 : -1;
        });
        return versionListStr;
    }
}
