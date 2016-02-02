package com.focustech.jmx.web.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.focustech.jmx.po.JmxApplication;
import com.focustech.jmx.po.JmxServer;
import com.google.common.collect.Lists;

@Controller
public class JmxController {
	@RequestMapping(value = "/index.html")
    public String index(Model model, String server, String app) {
        model.addAttribute("common", "1");
        List<JmxApplication> list = Lists.newArrayList();
        JmxApplication a = new JmxApplication();
        a.setAppId(1);
        a.setAppName("1");
        JmxApplication b = new JmxApplication();
        b.setAppId(2);
        b.setAppName("2");
        list.add(a);
        list.add(b);
        model.addAttribute("list", list);
        return "/new/index";
    }
	
	@RequestMapping(value = "/jmx0/getHosts")
	@ResponseBody
    public List<JmxServer> detail(Model model, String appId) {
		JmxServer s1 = new JmxServer();
		s1.setHostId(1);
		s1.setHostIp("192.168.0.1");
		JmxServer s2 = new JmxServer();
		s2.setHostId(2);
		s2.setHostIp("192.168.0.2");
		List<JmxServer> l = Lists.newArrayList();
		l.add(s1);
		l.add(s2);
		model.addAttribute("list",l);
        model.addAttribute("common", "1");
        return l;
    }
}

