package com.cjs.proj.controller;

import com.cjs.proj.entity.Result;
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
    public Result startup(){
        try {
            xenServerService.startup();
            return new Result(0, "开机成功，请稍后接入机器");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(1, "机器正在启动，请勿重复操作");
        }
    }

    @GetMapping("/shutdown")
    public Result shutdown(){
        try {
            xenServerService.shutdown();
            return new Result(0, "关机成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(1, "机器正在关闭，请勿重复操作");
        }
    }

    @GetMapping("/getStatus")
    public String getStatus() {
        try {
            return xenServerService.getStatus();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
