package com.cjs.proj.mapper;

import com.cjs.proj.pojo.Department;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface DepartmentMapper extends Mapper<Department> {


    @Select("SELECT * FROM department WHERE id = #{id}")
    @Results({
            @Result(property = "departName",  column = "depart_name")
    })
    Department getCompanyById(Integer id);

}
