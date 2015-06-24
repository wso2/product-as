package org.wso2.appserver.integration.common.artifacts.hostinfo;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Bean class for host information.
 */

@XmlRootElement(name = "hostInfo")

public class HostInfo {

    private String hostAddress;
    private String hostName;

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
