package org.wso2.appserver.sample.ee.cdi.produces;

public class GreeterImplOne implements Greeter {
    @Override
    public String greet() {
        return "Hi, greetings from implementation one";
    }
}
