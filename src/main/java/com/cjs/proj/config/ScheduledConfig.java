package com.cjs.proj.config;

import com.cjs.proj.pojo.Gpuvm;
import com.cjs.proj.service.GpuvmService;
import com.cjs.proj.service.XenServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ScheduledConfig {
    @Autowired
    private GpuvmService gpuvmService;

    @Autowired
    private XenServerService xenServerService;

    @Scheduled(cron="10 * * * * *")
    public void flushGpuVMStatus(){
        // 找到数据库里记录的正在用的
        List<Gpuvm> isUsing = gpuvmService.findIsUsing2();

        // 挨个看是否真的正在使用
        for(Gpuvm gpuvm: isUsing) {
            String gpuIp = gpuvm.getGpuIp();
            String uuid = gpuvm.getUuid();
            try {
                String status = xenServerService.getStatus(gpuIp, uuid);
                if(status!=null && "Halted".equals(status)) {
                    // 使用状态为清0
                    gpuvm.setIsUsing(0);
                    gpuvmService.updateByPrimaryKey(gpuvm);
                }
            } catch (Exception e) {
                System.out.println("状态无法获取");
            }

        }

        System.out.println("定时任务执行:"+new Date(System.currentTimeMillis()));
    }

}
