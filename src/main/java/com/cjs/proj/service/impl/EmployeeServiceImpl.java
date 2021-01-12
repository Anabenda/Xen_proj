package com.cjs.proj.service.impl;

import com.cjs.proj.entity.PageResult;
import com.cjs.proj.mapper.EmployeeMapper;
import com.cjs.proj.pojo.Department;
import com.cjs.proj.pojo.Employee;
import com.cjs.proj.pojo.EmployeeDepartment;
import com.cjs.proj.service.EmployeeService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private static Map<String, Integer> departmentMap;

    static {
        departmentMap = new HashMap<>();
        departmentMap.put("中心领导", 1);
        departmentMap.put("办公室", 2);
        departmentMap.put("党群办", 3);
        departmentMap.put("财务部", 4);
        departmentMap.put("业务管理部", 5);
        departmentMap.put("技术支持和保障部", 6);
        departmentMap.put("技术开发和服务部", 7);
        departmentMap.put("智能船", 8);
        departmentMap.put("船体结构室", 9);
        departmentMap.put("电气室", 10);
        departmentMap.put("轮机室", 11);
        departmentMap.put("船体法定室", 12);


    }


    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public List<Employee> findAll() {
        return employeeMapper.selectAll();
    }

    @Override
    public List<EmployeeDepartment> findAllChanged() {
        return employeeMapper.findAllChanged();
    }

    @Override
    public PageResult<EmployeeDepartment> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<EmployeeDepartment> employees = (Page<EmployeeDepartment>) employeeMapper.findAllChanged();
        return new PageResult<EmployeeDepartment>(employees.getTotal(), employees.getResult());
    }

    @Override
    public PageResult<EmployeeDepartment> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        String name = "";
        //   System.out.println(searchMap.get("departmentName"));
        Integer id = 100;
        List<Integer> ids = new ArrayList<>();
        if (searchMap != null) {
            if (searchMap.containsKey("name") && !"".equals(searchMap.get("name"))) {
                name = (String) searchMap.get("name");
            }
            if (searchMap.containsKey("departmentName") && !"".equals(searchMap.get("departmentName"))) {
                for (String key : departmentMap.keySet()) {
                    if (key.indexOf((String) searchMap.get("departmentName")) != -1) {
                        id = departmentMap.get(key);
                        ids.add(id);
                    }
                }
            }
        }
        if (!searchMap.containsKey("departmentName") || "".equals(searchMap.get("departmentName"))) {
            Page<EmployeeDepartment> employee_fin = (Page<EmployeeDepartment>) employeeMapper.findAllChangedByCondition2(name);
            return new PageResult<EmployeeDepartment>(employee_fin.getTotal(), employee_fin.getResult());
        } else {
//            System.out.println(ids);
            Page<EmployeeDepartment> employee_fin = (Page<EmployeeDepartment>) employeeMapper.findAllChangedByCondition1(name, ids);
            return new PageResult<EmployeeDepartment>(employee_fin.getTotal(), employee_fin.getResult());
        }
    }

    @Override
    public Employee findByEmail(String email) {
        Example example = new Example(Employee.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("email", email);
        return employeeMapper.selectOneByExample(example);
    }

    @Override
    public Employee findByPrimaryKey(Integer id) {
        return employeeMapper.selectByPrimaryKey(id);
    }


    @Override
    public void valid(Map<String, Object> map) {
        String email = (String) map.get("email");
        Employee employee = findByEmail(email);
        if (employee == null) {
            throw new RuntimeException("该账户不存在!");
        }
        if (map.get("message") == null || !map.get("message").equals(redisTemplate.boundValueOps("code_" + email).get())) {
            throw new RuntimeException("验证码错误!");
        }
        employee.setPassword((String) map.get("password"));
        employeeMapper.updateByPrimaryKey(employee);
        redisTemplate.boundValueOps("code_" + email).expire(0, TimeUnit.MINUTES);
    }

    // 将员工新增数据库
    @Override
    public void add(Map<String, Object> employeeMap) {
        if(employeeMap == null || !employeeMap.containsKey("email") || !employeeMap.containsKey("name") || !employeeMap.containsKey("phone")){
            throw new RuntimeException("请填写必要的员工信息");
        }
        Employee employee = new Employee();
        employee.setEmail((String) employeeMap.get("email"));
        employee.setName((String) employeeMap.get("name"));
        employee.setPhone((String) employeeMap.get("phone"));

        if(employeeMap.containsKey("ipKey")){
            employee.setIpKey((String) employeeMap.get("ipKey"));
        }
        if(employeeMap.containsKey("departmentId")){
            employee.setDepartmentId(Integer.parseInt((String)employeeMap.get("departmentId")));
        }
        if(employeeMap.containsKey("floor")){
            employee.setFloor((String) employeeMap.get("floor"));
        }
        employee.setPassword("123456");
        employeeMapper.insert(employee);
    }

    @Override
    public EmployeeDepartment findByPrimaryKeyChanged(Integer id) {

        return employeeMapper.findByPrimaryKeyChanged(id);
    }

    @Override
    public void update(Map<String, Object> employeeMap) {
        if(employeeMap == null || !employeeMap.containsKey("email") || !employeeMap.containsKey("name") || !employeeMap.containsKey("phone")){
            throw new RuntimeException("请填写必要的员工信息");
        }
//        System.out.println(employeeMap);
        Integer id = (Integer) employeeMap.get("id");
//        System.out.println(id);
        Employee employee = new Employee();
        employee.setId(id);
        employee.setEmail((String) employeeMap.get("email"));
        employee.setName((String) employeeMap.get("name"));
        employee.setPhone((String) employeeMap.get("phone"));

        if(employeeMap.containsKey("ipKey")){
            employee.setIpKey((String) employeeMap.get("ipKey"));
        }
        if(employeeMap.containsKey("departmentId")){
            employee.setDepartmentId(Integer.parseInt((String)employeeMap.get("departmentId")));
        }
        if(employeeMap.containsKey("floor")){
            employee.setFloor((String) employeeMap.get("floor"));
        }
        employee.setPassword("123456");
        employeeMapper.updateByPrimaryKeySelective(employee);
    }

    @Override
    public void delete(Integer id) {
        employeeMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PageResult<EmployeeDepartment> findPageOrderBy(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        String name = "";
        //   System.out.println(searchMap.get("departmentName"));
        Integer id = 100;
        List<Integer> ids = new ArrayList<>();
        if (searchMap != null) {
            if (searchMap.containsKey("name") && !"".equals(searchMap.get("name"))) {
                name = (String) searchMap.get("name");
            }
            if (searchMap.containsKey("departmentName") && !"".equals(searchMap.get("departmentName"))) {
                for (String key : departmentMap.keySet()) {
                    if (key.indexOf((String) searchMap.get("departmentName")) != -1) {
                        id = departmentMap.get(key);
                        ids.add(id);
                    }
                }
            }
        }
        if (!searchMap.containsKey("departmentName") || "".equals(searchMap.get("departmentName"))) {
            Page<EmployeeDepartment> employee_fin = (Page<EmployeeDepartment>) employeeMapper.findAllChangedByCondition4(name);
            return new PageResult<EmployeeDepartment>(employee_fin.getTotal(), employee_fin.getResult());
        } else {
//            System.out.println(ids);
            Page<EmployeeDepartment> employee_fin = (Page<EmployeeDepartment>) employeeMapper.findAllChangedByCondition3(name, ids);
            return new PageResult<EmployeeDepartment>(employee_fin.getTotal(), employee_fin.getResult());
        }
    }

    @Override
    public PageResult<EmployeeDepartment> findPageOrderByTest(int page, int size) {
        PageHelper.startPage(page, size);
        String name = "";
        //   System.out.println(searchMap.get("departmentName"));
        Integer id = 100;
        List<Integer> ids = new ArrayList<>();
        Map searchMap = new HashMap<>();
        if (searchMap != null) {
            if (searchMap.containsKey("name") && !"".equals(searchMap.get("name"))) {
                name = (String) searchMap.get("name");
            }
            if (searchMap.containsKey("departmentName") && !"".equals(searchMap.get("departmentName"))) {
                for (String key : departmentMap.keySet()) {
                    if (key.indexOf((String) searchMap.get("departmentName")) != -1) {
                        id = departmentMap.get(key);
                        ids.add(id);
                    }
                }
            }
        }
        if (!searchMap.containsKey("departmentName") || "".equals(searchMap.get("departmentName"))) {
            Page<EmployeeDepartment> employee_fin = (Page<EmployeeDepartment>) employeeMapper.findAllChangedByCondition4(name);
            return new PageResult<EmployeeDepartment>(employee_fin.getTotal(), employee_fin.getResult());
        } else {
//            System.out.println(ids);
            Page<EmployeeDepartment> employee_fin = (Page<EmployeeDepartment>) employeeMapper.findAllChangedByCondition3(name, ids);
            return new PageResult<EmployeeDepartment>(employee_fin.getTotal(), employee_fin.getResult());
        }
    }



    @Override
    public PageResult<EmployeeDepartment> findByDepartmenId(Map<String, Object> searchMap, int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Example example = new Example(Employee.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("email", username);
        Employee employee = employeeMapper.selectOneByExample(example);
        PageHelper.startPage(page, size);

        String name = "";
        //   System.out.println(searchMap.get("departmentName"));
        Integer id = 100;
        List<Integer> ids = new ArrayList<>();
        if (searchMap != null) {
            if (searchMap.containsKey("name") && !"".equals(searchMap.get("name"))) {
                name = (String) searchMap.get("name");
            }
            if (searchMap.containsKey("departmentName") && !"".equals(searchMap.get("departmentName"))) {
                for (String key : departmentMap.keySet()) {
                    if (key.indexOf((String) searchMap.get("departmentName")) != -1) {
                        id = departmentMap.get(key);
                        ids.add(id);
                    }
                }
            }
        }
        if (!searchMap.containsKey("departmentName") || "".equals(searchMap.get("departmentName"))) {
            Page<EmployeeDepartment> employee_fin = (Page<EmployeeDepartment>) employeeMapper.findByDepartmentId_2(name, employee.getDepartmentId());
            return new PageResult<EmployeeDepartment>(employee_fin.getTotal(), employee_fin.getResult());
        } else {
//            System.out.println(ids);
            Page<EmployeeDepartment> employee_fin = (Page<EmployeeDepartment>) employeeMapper.findByDepartmentId_1(name, ids, employee.getDepartmentId());
            return new PageResult<EmployeeDepartment>(employee_fin.getTotal(), employee_fin.getResult());
        }
    }



    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Employee.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            if (searchMap.containsKey("name") && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            if (searchMap.containsKey("departmentName") && !"".equals(searchMap.get("departmentName"))) {
                for (String key : departmentMap.keySet()) {
                    if (key.indexOf((String) searchMap.get("departmentName")) != -1) {
                        criteria.andEqualTo("departmentId", departmentMap.get(key));
                    }
                }
            }
        }
        return example;
    }
}
