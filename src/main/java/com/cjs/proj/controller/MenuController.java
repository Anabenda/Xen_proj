package com.cjs.proj.controller;


import com.cjs.proj.pojo.Menu;
import com.cjs.proj.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @RequestMapping("/findAll")
    public List<Menu> findAll(){
        return menuService.findAll();
    }

    @RequestMapping("/findAllMenu")
    public List<Map> findAllMenu(){
        return menuService.findAllMenu();
    }

}
