package com.cjs.proj.service.impl;

import com.cjs.proj.entity.TargetServer;
import com.cjs.proj.mapper.EmployeeMapper;
import com.cjs.proj.pojo.Employee;
import com.cjs.proj.service.EmployeeService;
import com.cjs.proj.service.XenServerService;

import com.xensource.xenapi.APIVersion;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Session;
import com.xensource.xenapi.VM;
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

    private void disconnect(Connection connection) throws Exception
    {
      //  logf("disposing connection for %s", connectionName);
        Session.logout(connection);
    }
}
