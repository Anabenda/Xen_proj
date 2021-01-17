package com.cjs.proj.service.impl;

import com.cjs.proj.config.DelayQueueManager;
import com.cjs.proj.entity.PageResult;
import com.cjs.proj.entity.TaskBase;
import com.cjs.proj.interf.impl.DelayTask;
import com.cjs.proj.mapper.AuditMapper;
import com.cjs.proj.mapper.EmployeeMapper;
import com.cjs.proj.pojo.*;
import com.cjs.proj.service.AuditService;
import com.cjs.proj.service.EmployeeService;
import com.cjs.proj.service.GpuvmService;
import com.cjs.proj.service.XenServerService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import tk.mybatis.mapper.entity.Example;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class AuditServiceImpl implements AuditService {

    @Value("${self.sendingEmail}")
    private String sendingEmail;

    @Value("${self.emailSuffix}")
    private String emailSuffix;

    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private GpuvmService gpuvmService;

    @Autowired
    private XenServerService xenServerService;

    @Autowired
    private DelayQueueManager delayQueueManager;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<Audit> findAll() {
        return auditMapper.selectAll();
    }

    @Override
    public PageResult<AuditDepartment> findPageByDepartmenId(int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeService.findByEmail(email);
        PageHelper.startPage(page, size);
        Page<AuditDepartment> auditDepartments = (Page<AuditDepartment>) auditMapper.findPageByDepartmentId(employee.getDepartmentId());
        return new PageResult<AuditDepartment>(auditDepartments.getTotal(), auditDepartments.getResult());
    }

    @Override
    public void save(Map<String, Object> map) {
        Audit audit = new Audit();
        // 存邮箱
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        audit.setEmail(email);
        Employee employee = employeeService.findByEmail(email);

        // 存姓名, 部门
        audit.setName(employee.getName());
        audit.setDepartmentId(employee.getDepartmentId());
        // 存申请表单数据
        if(map.containsKey("memory") && !"".equals(map.get("memory")) &&
                map.containsKey("vCpu") && !"".equals(map.get("vCpu")) &&
                map.containsKey("space") && !"".equals(map.get("space")) &&
                map.containsKey("applyTime") && !"".equals(map.get("applyTime"))){

            audit.setMemory((String) map.get("memory"));
            audit.setSpace((String) map.get("space"));
            audit.setApplyTime((String) map.get("applyTime"));
            audit.setvCpu((Integer) map.get("vCpu"));
        } else{
            throw new RuntimeException("申请信息不全");
        }
        // 存固定信息
        audit.setSystem("Windows 10 专业版");
        audit.setIsGpu("是");
        // 存当前时间
        audit.setTime(sdf.format(new Date()));
        // 存审核状态，初始为 未审核
        audit.setState("待审核");
        // 存备注信息
        if(map.containsKey("info") && !"".equals(map.get("info"))){
            audit.setInfo((String) map.get("info"));
        }

        // over
        auditMapper.insert(audit);

        // 2. 更改自己的gpu审核状态
        employee.setGpustatus("待审核");
        employeeMapper.updateByPrimaryKeySelective(employee);
    }

    @Override
    public PageResult<Audit> findByEmail(String email, int page, int size) {
        PageHelper.startPage(page, size);
        Page<Audit> auditDepartments = (Page<Audit>) auditMapper.findByEmail(email);
        return new PageResult<Audit>(auditDepartments.getTotal(), auditDepartments.getResult());
    }

    @Override
    public void cancel(Integer id) {
        Audit audit = auditMapper.selectByPrimaryKey(id);
        audit.setState("已取消");
        auditMapper.updateByPrimaryKeySelective(audit);
    }

    @Override
    public void agree(Integer id) throws RuntimeException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeService.findByEmail(email);

        Audit audit = auditMapper.selectByPrimaryKey(id);
        // 先找有没有空闲的机器
        // 从数据库里读取所有机器编号，查看是否有空闲的
        List<Gpuvm> gpuVms = gpuvmService.findIsUsing();
        if(gpuVms == null || gpuVms.size() == 0) {
            // 没有空闲的
            // 抛出异常
            throw new RuntimeException("没有空闲的机器");
        }
        boolean flag = false;
        int num = gpuVms.size();
        int i = 0;
        // 有空闲的，
        for(Gpuvm gpuvm: gpuVms) {
            if(!flag) {
                // 1. 获得第一台的服务器ip
                // 2. 获得第一台的uuid
                // 3. 获得host的uuid
                i++;
                String gpuIp = gpuvm.getGpuIp();
                String uuid = gpuvm.getUuid();
                String gpu_uuid = gpuvm.getGpuUuid();
                String gpuvm_ip = gpuvm.getVmIp();
                // 3. 开机
                try {
                    // 成功开机（方法里包含了配置的修改）
                    xenServerService.gpuStartup(gpuIp, gpu_uuid, uuid, audit);
                    // 状态为至1(使用中)
                    gpuvm.setIsUsing(1);
                    gpuvmService.updateByPrimaryKey(gpuvm);
                    // flag至true
                    flag = true;
                    // 同时添加一个延时任务， 延时关机
                    long delayTime = 90000l;   // 1.5 min
                    // 开任务时，传个email过去，这样就可以到时候修改gpu申请状态字段
                    delayQueueManager.put(new DelayTask(new TaskBase(gpuIp, uuid, xenServerService, email), delayTime));
                    // 同时将gpu使用状态为置为 使用中
                    employee.setGpustatus("使用中");
                    employee.setGpuip(gpuIp);
                    employee.setGpuvmip(gpuvm_ip);
                    employeeMapper.updateByPrimaryKey(employee);
                    i = -1;
                } catch (Exception e) {
                    System.out.println("内存不足");
                }
            } else {
                break;
            }
        }
        if(i != -1) {
            throw new RuntimeException("服务器所剩内存不足");
        } else{
            audit.setState("已审核");
            auditMapper.updateByPrimaryKeySelective(audit);
        }

    }

    @Override
    public void refuse(Integer id) {
        Audit audit = auditMapper.selectByPrimaryKey(id);
        audit.setState("已拒绝");
        auditMapper.updateByPrimaryKeySelective(audit);
    }

    @Override
    public PageResult<AuditDepartment> findPageByDepartmenIdExclude(int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeService.findByEmail(email);
        PageHelper.startPage(page, size);
        Page<AuditDepartment> auditDepartments = (Page<AuditDepartment>) auditMapper.findPageByDepartmenIdExclude(employee.getDepartmentId());
        return new PageResult<AuditDepartment>(auditDepartments.getTotal(), auditDepartments.getResult());
    }

    @Override
    public void sendEmail() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeService.findByEmail(email);
        // 发邮件给管理员
        // 1. 先查出本部门所有管理员
        Example example = new Example(Employee.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("role", 1);
        criteria.andEqualTo("departmentId", employee.getDepartmentId());
        List<Employee> admins = employeeMapper.selectByExample(example);
        // 2. 挨个发邮件
        for(Employee admin: admins){
            try {
//                System.out.println(admin);
                contextLoads(admin.getEmail(), employee);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }


    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    public void contextLoads(String email, Employee employee) throws MessagingException {

        Employee user = employeeService.findByEmail(email);
        if(user == null){
            throw new RuntimeException("该账户不存在!");
        }
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(sendingEmail);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("员工申请审核通知");
        // 利用 Thymeleaf 模板构建 html 文本
        Context ctx = new Context();
        ctx.setVariable("title", "申请审核");
        String content_1 = "您部门下的员工： "+employee.getName()+"于"+sdf.format(new Date())+"申请使用资源，请尽快网上审核!"; //10*****@qq.com 重置密码。"
        ctx.setVariable("content_1", content_1);

        ctx.setVariable("content_2", "");
        ctx.setVariable("content_hhh", "");
        ctx.setVariable("content_3", "谢谢!");
        ctx.setVariable("content_4", "CSS团队");
        String emailText = templateEngine.process("email", ctx);
        mimeMessageHelper.setText(emailText, true);
        javaMailSender.send(mimeMessage);
    }
}
