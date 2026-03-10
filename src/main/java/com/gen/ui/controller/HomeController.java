package com.gen.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页
 *
 * @author ZhangZhiHong
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "欢迎使用 Spring Boot 与 Thymeleaf！");
        model.addAttribute("currentYear", java.time.Year.now().getValue());
        // 对应 templates/index.html
        return "home";
    }
}
