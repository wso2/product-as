package org.wso2.appserver.sample.ee.cdi.inject;

public class SayHi implements Greet {
    @Override
    public String greet() {
        return "Hi, this is CDI-Servlet sample in WSO2 Application Server";
    }
}
