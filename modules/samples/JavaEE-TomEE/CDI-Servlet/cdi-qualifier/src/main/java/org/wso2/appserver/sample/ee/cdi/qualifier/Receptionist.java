package org.wso2.appserver.sample.ee.cdi.qualifier;

@Entrance
public class Receptionist implements Greeter {
    public String greet() {
        return "Hi, Good morning";
    }
}


