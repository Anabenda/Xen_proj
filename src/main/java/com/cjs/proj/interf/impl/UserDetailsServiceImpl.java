package com.cjs.proj.interf.impl;

import com.cjs.proj.pojo.Employee;
import com.cjs.proj.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 这个方法要返回一个UserDetails对象
     * 其中包括用户名，密码，授权信息等
     *
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        /**
         * 以下是登录逻辑
         */
//        System.out.println(email);
        // 1. 如果未输入用户名，返回null
        if (email == null) {
            //返回null时，后边就会抛出异常，就会登录失败。但这个异常并不需要我们处理
            return null;
        }
        // 2. 搜索数据库有无该用户名
        Employee employee = employeeService.findByEmail(email);
        Integer role = employee.getRole();
        System.out.println(role);
        if(employee!=null){
            // 封装传回
            List<SimpleGrantedAuthority> list = new ArrayList<>();
            SimpleGrantedAuthority authority_user = new SimpleGrantedAuthority("ROLE_USER");
            SimpleGrantedAuthority authority_leader = new SimpleGrantedAuthority("ROLE_LEADER");
            SimpleGrantedAuthority authority_admin = new SimpleGrantedAuthority("ROLE_ADMIN");
            list.add(authority_user);
            if(role == 1){
                list.add(authority_leader);
            }
            if(role == 2){
                list.add(authority_leader);
                list.add(authority_admin);
            }
            User user = new User(email, "{noop}"+employee.getPassword(), list);
            return user;
        }
//        if("admin".equals(email)){
//            List<SimpleGrantedAuthority> list = new ArrayList<>();
//            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
//            list.add(authority);
//            User user = new User(email, "{noop}admin", list);
//            return user;
//        }
        return null;
    }
}
