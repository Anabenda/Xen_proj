package com.cjs.proj.service;

public interface XenServerService {

    public void startup() throws Exception;

    void shutdown() throws Exception;

    String getStatus() throws Exception;
}
