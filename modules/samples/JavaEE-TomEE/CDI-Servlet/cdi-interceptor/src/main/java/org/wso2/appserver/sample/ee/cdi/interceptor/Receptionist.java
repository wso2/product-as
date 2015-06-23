package org.wso2.appserver.sample.ee.cdi.interceptor;


@Log
public class Receptionist implements Greeter {

    @Override
    public void greet() {
        System.out.println("Inside greet method");
    }
}
