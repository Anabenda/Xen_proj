package com.cjs.proj.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "template")
public class template implements Serializable {

    @Id
    private Integer id;

    private String system;

    private String isGpu;

    private String memory;

    private Integer vCpu;

    private String space;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getIsGpu() {
        return isGpu;
    }

    public void setIsGpu(String isGpu) {
        this.isGpu = isGpu;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public Integer getvCpu() {
        return vCpu;
    }

    public void setvCpu(Integer vCpu) {
        this.vCpu = vCpu;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }
}
