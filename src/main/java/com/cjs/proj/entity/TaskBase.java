package com.cjs.proj.entity;

import com.alibaba.fastjson.JSON;
import com.cjs.proj.service.XenServerService;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskBase {

    private String gpuIp;

    private String uuid;

    private XenServerService xenServerService;

    private String email;

    public TaskBase(String gpuIp, String uuid, XenServerService xenServerService, String email) {
        this.gpuIp = gpuIp;
        this.uuid = uuid;
        this.xenServerService = xenServerService;
        this.email = email;
    }

    public String getGpuIp() {
        return gpuIp;
    }

    public void setGpuIp(String gpuIp) {
        this.gpuIp = gpuIp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public XenServerService getXenServerService() {
        return xenServerService;
    }

    public void setXenServerService(XenServerService xenServerService) {
        this.xenServerService = xenServerService;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public void shutDown() throws Exception {
        this.xenServerService.shutDownGpuVm(this.gpuIp, this.uuid, this.email);
    }
}
