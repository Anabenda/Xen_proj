package com.cjs.proj.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@Table(name = "department")
public class Department implements Serializable {

    @Id
    private Integer id;

    private String departName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", departName='" + departName + '\'' +
                '}';
    }
}
