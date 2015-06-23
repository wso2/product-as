package org.wso2.appserver.sample.ee.cdi.decoder;


public class Receptionist implements Greeter {

    @Override
    public String greet() {
        return "Hi, this is the Receptionist class";
    }
}
