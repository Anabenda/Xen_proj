package com.cjs.proj.service;

import com.cjs.proj.pojo.Menu;

import java.util.List;
import java.util.Map;

public interface MenuService {

    // 查询所有
    public List<Menu> findAll();

    // 查询所有，构成树状结构
    public List<Map> findAllMenu();
}
