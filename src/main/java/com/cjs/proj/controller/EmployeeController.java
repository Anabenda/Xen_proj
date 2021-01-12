package com.cjs.proj.controller;


import com.cjs.proj.entity.PageResult;
import com.cjs.proj.entity.Result;
import com.cjs.proj.pojo.Employee;
import com.cjs.proj.pojo.EmployeeDepartment;
import com.cjs.proj.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @RequestMapping("/findAll")
    public List<Employee> findAll() {
        return employeeService.findAll();
    }

    @RequestMapping("/findAllChanged")
    public List<EmployeeDepartment> findAllChanged() {
        return employeeService.findAllChanged();
    }

    @GetMapping("/findByEmail")
    public Employee findAll(String email) {
        // System.out.println(email);
        return employeeService.findByEmail(email);
    }

    @GetMapping("/findByPrimaryKey")
    public Employee findByPrimaryKey(Integer id) {
//        System.out.println(email);
        return employeeService.findByPrimaryKey(id);
    }

    @GetMapping("/findByPrimaryKeyChanged")
    public EmployeeDepartment findByPrimaryKeyChanged(Integer id) {
//        System.out.println(email);
        return employeeService.findByPrimaryKeyChanged(id);
    }


    @GetMapping("/findPage")
    public PageResult<EmployeeDepartment> findPage(int page, int size) {
        return employeeService.findPageOrderByTest(page, size);
    }

    @PostMapping("/findPage")
    public PageResult<EmployeeDepartment> findPage(@RequestBody Map<String, Object> searchMap, int page, int size) {
        return employeeService.findPage(searchMap, page, size);
    }

    @PostMapping("/findPageOrderBy")
    public PageResult<EmployeeDepartment> findPageOrderBy(@RequestBody Map<String, Object> searchMap, int page, int size) {
        return employeeService.findPageOrderBy(searchMap, page, size);
    }

    @PostMapping("/add")
    public Result save(@RequestBody Map<String, Object> employeeMap) {
        try {
            employeeService.add(employeeMap);
            return new Result(0, "添加成功");
        }catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(1, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(1, "新增失败!");
        }
    }

    @PostMapping("/update")
    public Result update(@RequestBody Map<String, Object> employeeMap) {
        try {
            employeeService.update(employeeMap);
            return new Result(0, "编辑成功");
        }catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(1, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(1, "编辑失败!");
        }
    }

    @GetMapping("/delete")
    public Result delete(Integer id) {
        try {
            employeeService.delete(id);
            return new Result(0, "编辑成功");
        }catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(1, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(1, "编辑失败!");
        }
    }

    @PostMapping("/findByDepartmenId")
    public PageResult<EmployeeDepartment> findByDepartmenId(@RequestBody Map<String, Object> searchMap, int page, int size){
        return employeeService.findByDepartmenId(searchMap, page, size);
    }

    @GetMapping("/findSelf")
    public Employee findSelf() {
        return employeeService.findSelf();
    }
}
