package com.cjs.proj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 配置用户权限组和接口路径的关系
     * 和一些其他配置
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable().authorizeRequests()
                //处理跨域请求中的Preflight请求
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/css/**","/js/**","/img/**","/email/**","/error/**").permitAll()
                .antMatchers("/login.html").permitAll()
//                .antMatchers("/**").hasRole("USER")
                .antMatchers("/pages/**", "/admin/**").hasAnyRole("ADMIN", "LEADER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html")  //指定登录页的路径
                .loginProcessingUrl("/authentication/form") //指定自定义form表单请求的路径
                .defaultSuccessUrl("/main.html") // 指定登陆成功跳转到的页面
                .failureUrl("/login.html") // 登陆失败跳转到的页面
                .permitAll();
        http
                // ...
                .headers()
                .frameOptions().sameOrigin()
                .httpStrictTransportSecurity().disable();
        //默认都会产生一个hiden标签 里面有安全相关的验证 防止请求伪造 这边我们暂时不需要 可禁用掉

    }

}
