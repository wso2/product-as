package org.wso2.appserver.integration.common.artifacts.hostinfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Path("/")

public class HostInfoService {

    /**
     * Provide the host information.
     */
    @Path("get/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HostInfo getHostInfo() throws HostInfoException {
        HostInfo hostInfo = new HostInfo();
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();
            hostInfo.setHostAddress(ip.getHostAddress());
            hostInfo.setHostName(ip.getHostName());
        } catch (UnknownHostException e) {
            throw new HostInfoException("", e);
        }
        return hostInfo;
    }
}
