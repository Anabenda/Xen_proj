package com.cjs.proj.interf;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsService {
    /**
     * 在登录的时候，就会调用这个方法，它的返回结果是一个UserDetails接口类
     */
    UserDetails loadUserByUsername(String var1) throws UsernameNotFoundException;
}
