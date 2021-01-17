package com.cjs.proj.service;

import com.cjs.proj.pojo.Audit;

public interface XenServerService {

    // cpu机器开机
    public void startup() throws Exception;

    // 关掉绑定的cpu的机器
    void shutdown() throws Exception;

    // 获取绑定的cpu机器的状态
    String getStatus() throws Exception;

    String getStatus(String gpu_ip, String vm_uuid) throws Exception;

    // gpu机器开机
    public void gpuStartup(String gpuIp, String gpu_uuid, String uuid, Audit audit) throws Exception;

    // 定时关掉gpu的机器
    public void shutDownGpuVm(String gpuIp, String uuid, String email) throws Exception;
}
