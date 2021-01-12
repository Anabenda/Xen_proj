package com.cjs.proj.service.impl;

import com.cjs.proj.mapper.MenuMapper;
import com.cjs.proj.pojo.Menu;
import com.cjs.proj.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<Menu> findAll() {
        return menuMapper.selectAll();
    }

    /**
     * 查询所有，构成树状结构
     * @return
     */
    @Override
    public List<Map> findAllMenu() {
        List<Menu> menuList = findAll();

        return findMenuByParentId(menuList, "0");
    }

    /**
     * 查询下级菜单
     * @param menuList
     * @param parentId
     * @return
     */
    private List<Map> findMenuByParentId(List<Menu> menuList, String parentId){
        List<Map> mapList = new ArrayList<>();
        for(Menu menu : menuList){
            if(menu.getParentId().equals(parentId)){
                Map map = new HashMap();
                map.put("id", menu.getId());
                map.put("title", menu.getName());
                map.put("icon", menu.getIcon());
                map.put("children", findMenuByParentId(menuList, menu.getId()));
                map.put("linkUrl", menu.getUrl());
                mapList.add(map);
            }
        }
        return mapList;
    }
}
