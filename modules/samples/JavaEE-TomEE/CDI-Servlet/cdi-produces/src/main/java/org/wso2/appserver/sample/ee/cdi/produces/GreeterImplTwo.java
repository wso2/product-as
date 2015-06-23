package org.wso2.appserver.sample.ee.cdi.produces;

public class GreeterImplTwo implements Greeter {
    @Override
    public String greet() {
        return "Bye !";
    }
}
