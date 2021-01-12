package com.cjs.proj.pojo;



import org.springframework.stereotype.Repository;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@Repository
@Table(name="employee")
public class Employee implements Serializable {

    @Id
    private Integer id;

    private String email;

    private String name;

    private String password;

    private String ipKey;

    private String phone;

    private Integer departmentId;

    private String floor;

    private Integer role;

    private String cpuIp;

    private String vmIp;

    private String uuid;

    public String getCpuIp() {
        return cpuIp;
    }

    public void setCpuIp(String cpuIp) {
        this.cpuIp = cpuIp;
    }

    public String getVmIp() {
        return vmIp;
    }

    public void setVmIp(String vmIp) {
        this.vmIp = vmIp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIpKey() {
        return ipKey;
    }

    public void setIpKey(String ipKey) {
        this.ipKey = ipKey;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", ipKey='" + ipKey + '\'' +
                ", phone='" + phone + '\'' +
                ", departmentId=" + departmentId +
                ", floor='" + floor + '\'' +
                ", role=" + role +
                '}';
    }
}
