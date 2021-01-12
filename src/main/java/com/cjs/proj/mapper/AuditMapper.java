package com.cjs.proj.mapper;

import com.cjs.proj.pojo.Audit;
import com.cjs.proj.pojo.AuditDepartment;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AuditMapper extends Mapper<Audit> {



    @Select("SELECT * FROM audit WHERE department_id = #{department_id} and state = '待审核' order by time desc")
    @Results({
            @Result(property = "isGpu", column = "is_gpu"),
            @Result(property = "vCpu", column = "v_cpu"),
            @Result(property = "applyTime", column = "apply_time"),
            @Result(property = "department", column = "department_id", one = @One(select =
                    "com.cjs.proj.mapper.DepartmentMapper.getCompanyById")),
    })
    public List<AuditDepartment> findPageByDepartmentId(@Param("department_id") Integer departmentId);


    @Select("SELECT * FROM audit WHERE email = #{email} order by time desc")
    @Results({
            @Result(property = "isGpu", column = "is_gpu"),
            @Result(property = "vCpu", column = "v_cpu"),
            @Result(property = "applyTime", column = "apply_time"),
    })
    public List<Audit> findByEmail(@Param("email") String email);

    @Select("SELECT * FROM audit WHERE department_id = #{department_id} and state != '待审核' order by time desc")
    @Results({
            @Result(property = "isGpu", column = "is_gpu"),
            @Result(property = "vCpu", column = "v_cpu"),
            @Result(property = "applyTime", column = "apply_time"),
            @Result(property = "department", column = "department_id", one = @One(select =
                    "com.cjs.proj.mapper.DepartmentMapper.getCompanyById")),
    })
    public List<AuditDepartment> findPageByDepartmenIdExclude(@Param("department_id") Integer departmentId);
}


