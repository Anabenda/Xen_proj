package com.cjs.proj.service;

import com.cjs.proj.pojo.Gpuvm;

import java.util.List;

public interface GpuvmService {

    // 查询是否有空闲的机器
    public List<Gpuvm> findIsUsing();

    // 检测正在使用的状态
    public List<Gpuvm> findIsUsing2();

    // 根据主键更新条目
    public void updateByPrimaryKey(Gpuvm gpuvm);

}
