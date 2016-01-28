package com.focustech.jmx.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JmxController {
	@RequestMapping(value = "/index.html")
    public String index(Model model, String server, String app) {
        model.addAttribute("common", "1");
        return "/new/index";
    }
	
	@RequestMapping(value = "/detail.html")
    public String detail(Model model, String server, String app) {
        model.addAttribute("common", "1");
        return "/new/detail";
    }
}
