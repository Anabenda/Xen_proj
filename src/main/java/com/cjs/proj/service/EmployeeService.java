package com.cjs.proj.service;


import com.cjs.proj.entity.PageResult;
import com.cjs.proj.pojo.Employee;
import com.cjs.proj.pojo.EmployeeDepartment;

import java.util.List;
import java.util.Map;

/**
 * 员工业务逻辑层
 */
public interface EmployeeService {

    public List<Employee> findAll();

    public List<EmployeeDepartment> findAllChanged();


    // 分页查询
    public PageResult<EmployeeDepartment> findPage(int page, int size);

    // 条件分页查询
    public PageResult<EmployeeDepartment> findPage(Map<String, Object> searchMap, int page, int size);


    // 根据邮箱查信息
    public Employee findByEmail(String email);


    public Employee findByPrimaryKey(Integer id);


    public void valid(Map<String, Object> map);

    // 新增员工
    void add(Map<String, Object> employeeMap);

    EmployeeDepartment findByPrimaryKeyChanged(Integer id);

    // 编辑员工
    void update(Map<String, Object> employeeMap);

    // 删除
    void delete(Integer id);

    PageResult<EmployeeDepartment> findPageOrderBy(Map<String, Object> searchMap, int page, int size);

    PageResult<EmployeeDepartment> findPageOrderByTest(int page, int size);

    // 根据部门查询
    PageResult<EmployeeDepartment> findByDepartmenId(Map<String, Object> searchMap, int page, int size);


    Employee findSelf();

    void adAdd(Map map);

    void adChange(Map map);
}
