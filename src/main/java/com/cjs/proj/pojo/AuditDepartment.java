package com.cjs.proj.pojo;

import java.io.Serializable;

public class AuditDepartment extends Audit implements Serializable {

    private Department department;

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
