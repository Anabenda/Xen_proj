package com.cjs.proj.controller;

import com.cjs.proj.entity.Result;
import com.cjs.proj.pojo.Employee;
import com.cjs.proj.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.codec.json.KotlinSerializationJsonEncoder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RequestMapping("/email")
@RestController
public class EmailController {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    public void sendSimpleEmail(String email) {
        // 构造Email消息
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("375458221@qq.com");
        message.setTo(email);
        message.setSubject("邮件主题");
        message.setText("琳琳大可爱真是可爱到feiqi呢😏");
        javaMailSender.send(message);
    }

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void contextLoads(String email) throws MessagingException {

        Employee user = employeeService.findByEmail(email);
        if(user == null){
            throw new RuntimeException("该账户不存在!");
        }
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom("375458221@qq.com");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("CCS验证码邮件");
        // 利用 Thymeleaf 模板构建 html 文本
        Context ctx = new Context();
        ctx.setVariable("title", "密码重置");
        String[] split = email.split("@");
        String content_1 = "请使用此代码为帐户"+split[0].substring(0, 3)+"xxxxx@"+split[1]+"重置密码。"; //10*****@qq.com 重置密码。"
        ctx.setVariable("content_1", content_1);

        Random r = new Random();
        int code = r.nextInt(999999);
        if(code<100000){
            code+=100000;
        }

        // 2. 将验证码保存到redis中
        redisTemplate.boundValueOps("code_"+email).set(code+"");
        redisTemplate.boundValueOps("code_"+email).expire(5, TimeUnit.MINUTES);


        ctx.setVariable("content_2", "你的验证码如下:");
        ctx.setVariable("content_code", code+"");
        ctx.setVariable("content_hhh", "此验证码5min内有效，超时请重新获取🙂");
        ctx.setVariable("content_3", "谢谢!");
        ctx.setVariable("content_4", "CSS团队");
        String emailText = templateEngine.process("email", ctx);
        mimeMessageHelper.setText(emailText, true);
        javaMailSender.send(mimeMessage);
    }

    @GetMapping("/sendSms")
    public Result sendSms(String email){
        try {
            contextLoads(email);
        }catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(1, e.getMessage());
        }
        catch (MessagingException e) {
            e.printStackTrace();
            return new Result(1, "验证码发送失败");
        }
        return new Result(0, "验证码已发送");
    }

    @PostMapping("/valid")
    public Result valid(@RequestBody Map<String, Object> map){
        try {
            employeeService.valid(map);
            return new Result(0, "设置成功");
        }catch (RuntimeException e){
            e.printStackTrace();
            return new Result(1, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(1, e.getMessage());
        }
    }
}
