package com.cjs.proj.controller;


import com.cjs.proj.entity.PageResult;
import com.cjs.proj.entity.Result;
import com.cjs.proj.pojo.Audit;
import com.cjs.proj.pojo.AuditDepartment;
import com.cjs.proj.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/audit")
public class AuditController {
    @Autowired
    private AuditService auditService;

    @PostMapping("/save")
    public Result save(@RequestBody Map<String, Object> map){
        try {
            auditService.save(map);
            return new Result(0, "操作成功");
        }catch(RuntimeException e){
            e.printStackTrace();
            return new Result(1, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(1, "操作失败");
        }

    }

    @GetMapping("/sendEmail")
    public Result sendEmail(){
        try {
            auditService.sendEmail();
            return new Result(0, "操作成功");
        }catch(RuntimeException e){
            e.printStackTrace();
            return new Result(1, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(1, "操作失败");
        }

    }

    @GetMapping("/findPageByDepartmenId")
    public PageResult<AuditDepartment> findPageByDepartmenId(int page, int size){
        return auditService.findPageByDepartmenId(page, size);
    }

    @GetMapping("/findPageByDepartmenIdExclude")
    public PageResult<AuditDepartment> findPageByDepartmenIdExclude(int page, int size){
        return auditService.findPageByDepartmenIdExclude(page, size);
    }

    @GetMapping("/findByEmail")
    public PageResult<Audit> findByEmail(int page, int size){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return auditService.findByEmail(email, page, size);
    }

    @GetMapping("/cancel")
    public void cancel(Integer id){
        auditService.cancel(id);
    }

    @GetMapping("/agree")
    public Result agree(Integer id){
        try {
            auditService.agree(id);
            return new Result(0, "审核成功");
        } catch (RuntimeException e) {
            return new Result(1, e.getMessage());
        }
    }

    @GetMapping("/refuse")
    public void refuse(Integer id){
        auditService.refuse(id);
    }
}
