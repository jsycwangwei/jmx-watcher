package com.focustech.jmx.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class CommonController {

    private String name = "jmx";
    private String password = "jmx2015";
    private String SIGN = "SIGN";

    @RequestMapping(value = "login.html")
    public String detail(HttpServletRequest request, HttpServletResponse response, Model model, String name, String pwd)
            throws Exception {
        model.addAttribute("common", "common");
        if (StringUtils.equals(name, this.name) && StringUtils.equals(pwd, this.password)) {
            request.getSession().setAttribute(SIGN, true);
            return "redirect:/jmx/overview.html";
        }
        return "/new/login";
    }

    @RequestMapping(value = "logout.html")
    public String detail(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        request.getSession().removeAttribute(SIGN);
        return "/common/login";
    }

}
