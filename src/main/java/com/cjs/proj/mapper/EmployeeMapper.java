package com.cjs.proj.mapper;

import com.cjs.proj.pojo.Employee;
import com.cjs.proj.pojo.EmployeeDepartment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface EmployeeMapper extends Mapper<Employee> {

    // 查询所有
    @Select("SELECT * FROM employee")
    @Results({
            @Result(property = "ipKey", column = "ip_key"),
            @Result(property = "cpuIp", column = "cpu_ip"),
            @Result(property = "vmIp", column = "vm_ip"),
            @Result(property = "department", column = "department_id", one = @One(select =
                    "com.cjs.proj.mapper.DepartmentMapper.getCompanyById")),
    })
    List<EmployeeDepartment> findAllChanged();


    // 根据id查找单个 组合对象
    @Select("SELECT * FROM employee where id=#{id}")
    @Results({
            @Result(property = "ipKey", column = "ip_key"),
            @Result(property = "cpuIp", column = "cpu_ip"),
            @Result(property = "vmIp", column = "vm_ip"),
            @Result(property = "department", column = "department_id", one = @One(select =
                    "com.cjs.proj.mapper.DepartmentMapper.getCompanyById")),
    })
    EmployeeDepartment findByPrimaryKeyChanged(Integer id);



    // 以下四个针对大领导
    // 两个非空或名字为空
    @Select("<script> " +
            "select * from employee where name like CONCAT(CONCAT('%', #{name}), '%') and department_id in " +
            "<foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'> " +
            "   #{item} " +
            "</foreach>" +
            "</script> ")
    @Results({
            @Result(property = "ipKey", column = "ip_key"),
            @Result(property = "cpuIp", column = "cpu_ip"),
            @Result(property = "vmIp", column = "vm_ip"),
            @Result(property = "department", column = "department_id", one = @One(select =
                    "com.cjs.proj.mapper.DepartmentMapper.getCompanyById")),
    })
    List<EmployeeDepartment> findAllChangedByCondition1(@Param("name") String name,@Param("ids") List<Integer> ids);

    // 有名字没部门
    @Select("SELECT * FROM employee WHERE name like CONCAT(CONCAT('%', #{name}), '%')")
    @Results({
            @Result(property = "ipKey", column = "ip_key"),
            @Result(property = "cpuIp", column = "cpu_ip"),
            @Result(property = "vmIp", column = "vm_ip"),
            @Result(property = "department", column = "department_id", one = @One(select =
                    "com.cjs.proj.mapper.DepartmentMapper.getCompanyById")),
    })
    List<EmployeeDepartment> findAllChangedByCondition2(String name);

    // 两个非空或名字为空 同时 按部门排序
    @Select("<script> " +
            "select * from employee where name like CONCAT(CONCAT('%', #{name}), '%') and department_id in " +
            "<foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'> " +
            "   #{item} " +
            "</foreach>" + "order by department_id" +
            "</script> ")
    @Results({
            @Result(property = "ipKey", column = "ip_key"),
            @Result(property = "cpuIp", column = "cpu_ip"),
            @Result(property = "vmIp", column = "vm_ip"),
            @Result(property = "department", column = "department_id", one = @One(select =
                    "com.cjs.proj.mapper.DepartmentMapper.getCompanyById")),
    })
    List<EmployeeDepartment> findAllChangedByCondition3(@Param("name") String name,@Param("ids") List<Integer> ids);

    // 有名字没部门 同时按 部门排序
    @Select("SELECT * FROM employee WHERE name like CONCAT(CONCAT('%', #{name}), '%') order by department_id")
    @Results({
            @Result(property = "ipKey", column = "ip_key"),
            @Result(property = "cpuIp", column = "cpu_ip"),
            @Result(property = "vmIp", column = "vm_ip"),
            @Result(property = "department", column = "department_id", one = @One(select =
                    "com.cjs.proj.mapper.DepartmentMapper.getCompanyById")),
    })
    List<EmployeeDepartment> findAllChangedByCondition4(String name);




    // 以下四个针对其他用户
    @Select("<script> " +
            "select * from employee where department_id = #{departmentId} and name like CONCAT(CONCAT('%', #{name}), '%') and department_id in " +
            "<foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'> " +
            "   #{item} " +
            "</foreach>" +
            "</script> ")
    @Results({
            @Result(property = "ipKey", column = "ip_key"),
            @Result(property = "cpuIp", column = "cpu_ip"),
            @Result(property = "vmIp", column = "vm_ip"),
            @Result(property = "department", column = "department_id", one = @One(select =
                    "com.cjs.proj.mapper.DepartmentMapper.getCompanyById")),
    })
    List<EmployeeDepartment> findByDepartmentId_1(String name, List<Integer> ids, Integer departmentId);

    @Select("SELECT * FROM employee where department_id = #{departmentId} and name like CONCAT(CONCAT('%', #{name}), '%')")
    @Results({
            @Result(property = "ipKey", column = "ip_key"),
            @Result(property = "cpuIp", column = "cpu_ip"),
            @Result(property = "vmIp", column = "vm_ip"),
            @Result(property = "department", column = "department_id", one = @One(select =
                    "com.cjs.proj.mapper.DepartmentMapper.getCompanyById")),
    })
    List<EmployeeDepartment> findByDepartmentId_2(String name, Integer departmentId);
}
