package org.wso2.appserver.sample.ee.cdi.qualifier;


@Departure
public class LiftOperator implements Greeter {
    @Override
    public String greet() {
        return "Have a good day, Bye!";
    }

}
