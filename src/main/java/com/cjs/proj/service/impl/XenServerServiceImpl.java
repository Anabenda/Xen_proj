package com.cjs.proj.service.impl;

import com.cjs.proj.entity.TargetServer;
import com.cjs.proj.service.XenServerService;

import com.xensource.xenapi.APIVersion;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Session;
import com.xensource.xenapi.VM;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Map;

@Service
public class XenServerServiceImpl implements XenServerService {

    protected Connection connection;
    private String connectionName;

    @Override
    public void startup(String hostname, String username, String password) throws Exception {
        TargetServer server = new TargetServer(hostname, username, password);
        Session session = connect(server);
        try {
            System.out.println("1");
            System.out.println("--------------------------");
            Map<VM, VM.Record> allRecords = VM.getAllRecords(connection);
            System.out.println(allRecords.size());

            VM B508 = VM.getByUuid(connection, "859aa27f-f5ec-4a18-751c-064fa3b339b9");
            B508.start(connection, false, true);
        }
        finally {
            disconnect();
        }
    }

    private Session connect(TargetServer target) throws Exception
    {
        connection = new Connection(new URL("http://" + target.Hostname));
        // log(String.format("logging in to '%s'...", target.Hostname));
        Session session = Session.loginWithPassword(connection, target.Username, target.Password, APIVersion.latest().toString());
        // logf("Success! Session API version is %s", connection.getAPIVersion().toString());

        connectionName = target.Hostname;
        return session;
    }

    private void disconnect() throws Exception
    {
      //  logf("disposing connection for %s", connectionName);
        Session.logout(connection);
    }
}
