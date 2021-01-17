package com.cjs.proj.service.impl;

import com.cjs.proj.entity.TargetServer;
import com.cjs.proj.mapper.EmployeeMapper;
import com.cjs.proj.pojo.Audit;
import com.cjs.proj.pojo.Employee;
import com.cjs.proj.service.EmployeeService;
import com.cjs.proj.service.XenServerService;

import com.xensource.xenapi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Map;

@Service
public class XenServerServiceImpl implements XenServerService {

    private String connectionName;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public void startup() throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeService.findByEmail(email);

        TargetServer server = new TargetServer(employee.getCpuIp(), "root", "Admin518");
        Connection connection = new Connection(new URL("http://" + server.Hostname));
        Session session = Session.loginWithPassword(connection, server.Username, server.Password, APIVersion.latest().toString());
        // connectionName = target.Hostname;
        try {
            VM vm = VM.getByUuid(connection, employee.getUuid());
            vm.start(connection, false, true);
        }
        finally {
            disconnect(connection);
        }
    }

    @Override
    public void shutdown() throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeService.findByEmail(email);
        TargetServer server = new TargetServer(employee.getCpuIp(), "root", "Admin518");
        Connection connection = new Connection(new URL("http://" + server.Hostname));
        Session session = Session.loginWithPassword(connection, server.Username, server.Password, APIVersion.latest().toString());
        try {
            VM vm = VM.getByUuid(connection, employee.getUuid());
            vm.shutdown(connection);
        }
        finally {
            disconnect(connection);
        }
    }

    @Override
    public String getStatus() throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeService.findByEmail(email);
        TargetServer server = new TargetServer(employee.getCpuIp(), "root", "Admin518");
        Connection connection = new Connection(new URL("http://" + server.Hostname));
        Session session = Session.loginWithPassword(connection, server.Username, server.Password, APIVersion.latest().toString());
        try {
            VM vm = VM.getByUuid(connection, employee.getUuid());
            String status = vm.getPowerState(connection).toString();
            return status;
        }
        finally {
            disconnect(connection);
        }
    }

    @Override
    public String getStatus(String gpu_ip, String vm_uuid) throws Exception {
        TargetServer server = new TargetServer(gpu_ip, "root", "Admin518");
        Connection connection = new Connection(new URL("http://" + server.Hostname));
        Session session = Session.loginWithPassword(connection, server.Username, server.Password, APIVersion.latest().toString());
        try {
            VM vm = VM.getByUuid(connection, vm_uuid);
            String status = vm.getPowerState(connection).toString();
            return status;
        }
        finally {
            disconnect(connection);
        }
    }

    @Override
    public void gpuStartup(String gpuIp, String gpu_uuid, String uuid, Audit audit) throws Exception {
        TargetServer server = new TargetServer(gpuIp, "root", "Admin518");
        Connection connection = new Connection(new URL("http://" + server.Hostname));
        Session session = Session.loginWithPassword(connection, server.Username, server.Password, APIVersion.latest().toString());
        try {
            // 1. 先判断该服务器有没有需要的内存大小
            // 1.1. 获得该host
            Host host = Host.getByUuid(connection, gpu_uuid);
            // 1.2. 获得剩余可用内存大小
            Long memoryLost = host.computeFreeMemory(connection);  // bit
            // 1.3. 比较是否够
            String memory = audit.getMemory();
            long needed = Long.parseLong(memory.split(" ")[0]);
            if(memoryLost/1024.0/1024/1024 < needed) {
                // 不够
                throw new RuntimeException("服务器所剩内存不够");
            }
            // 1.4. 修改内存
            VM vm = VM.getByUuid(connection, uuid);
            long needed_bit = needed*1024l*1024l*1024l;
            vm.setMemoryLimits(connection, needed_bit/2l, needed_bit, needed_bit, needed_bit);

            // 2. 开机
            vm.start(connection, false, true);
        } catch (Exception e) {
            throw new RuntimeException("服务器所剩内存不够");
        }
        finally {
            disconnect(connection);
        }
    }

    @Override
    public void shutDownGpuVm(String gpuIp, String uuid, String email) throws Exception {
        Employee employee = employeeService.findByEmail(email);
        TargetServer server = new TargetServer(gpuIp, "root", "Admin518");
        Connection connection = new Connection(new URL("http://" + server.Hostname));
        Session session = Session.loginWithPassword(connection, server.Username, server.Password, APIVersion.latest().toString());
        try {
            // 1. 先获取状态看是否已经关机
            VM vm = VM.getByUuid(connection, uuid);
            String status = vm.getPowerState(connection).toString();
            if(status != null && "Halted".equals(status)) {
                // 已经关机
                return;
            }
            // 没关机或者处于异常状态，不多bb，关机就行
            vm.shutdown(connection);
            // 同时将该人gpu申请状态清空
            employee.setGpustatus("未申请");
            employeeMapper.updateByPrimaryKey(employee);
        } catch (Exception e) {
            throw new RuntimeException("出bug了");
        }
        finally {
            disconnect(connection);
        }
    }


    private void disconnect(Connection connection) throws Exception
    {
      //  logf("disposing connection for %s", connectionName);
        Session.logout(connection);
    }
}
