package com.cjs.proj.service;

import com.cjs.proj.entity.PageResult;
import com.cjs.proj.pojo.Audit;
import com.cjs.proj.pojo.AuditDepartment;

import java.util.List;
import java.util.Map;

public interface AuditService {


    List<Audit> findAll();

    // 按部门分页查询申请记录 （查未审核的）
    PageResult<AuditDepartment> findPageByDepartmenId(int page, int size);

    // 保存欸
    void save(Map<String, Object> map);

    // 根据email来查申请信息
    public PageResult<Audit> findByEmail(String email, int page, int size);

    // 根据id取消
    public void cancel(Integer id);

    // 根据id同意
    public void agree(Integer id) throws RuntimeException;

    // 根据id拒绝
    public void refuse(Integer id);

    // 按部门分页查询申请记录（查已审核的）
    PageResult<AuditDepartment> findPageByDepartmenIdExclude(int page, int size);

    // 给管理员发送邮件
    void sendEmail();
}
