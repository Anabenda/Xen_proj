package com.cjs.proj.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "gpuvm")
public class Gpuvm implements Serializable {
    @Id
    private Integer id;

    private String gpuIp;

    private String gpuUuid;

    private String vmIp;

    private String uuid;

    private Integer isUsing;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGpuIp() {
        return gpuIp;
    }

    public void setGpuIp(String gpuIp) {
        this.gpuIp = gpuIp;
    }

    public String getGpuUuid() {
        return gpuUuid;
    }

    public void setGpuUuid(String gpuUuid) {
        this.gpuUuid = gpuUuid;
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

    public Integer getIsUsing() {
        return isUsing;
    }

    public void setIsUsing(Integer isUsing) {
        this.isUsing = isUsing;
    }

    @Override
    public String toString() {
        return "Gpuvm{" +
                "id=" + id +
                ", gpuIp='" + gpuIp + '\'' +
                ", gpuUuid='" + gpuUuid + '\'' +
                ", vmIp='" + vmIp + '\'' +
                ", uuid='" + uuid + '\'' +
                ", isUsing=" + isUsing +
                '}';
    }
}
