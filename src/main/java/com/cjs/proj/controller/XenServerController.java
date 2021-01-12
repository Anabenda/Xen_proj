package com.cjs.proj.controller;

import com.cjs.proj.service.XenServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/xenServer")
public class XenServerController {
    @Autowired
    private XenServerService xenServerService;

    @GetMapping("/startup")
    public void startup(){
        try {
            xenServerService.startup("192.168.1.147", "root", "Admin518");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
