package org.wso2.appserver.sample.ee.cdi.alternative;


import javax.enterprise.inject.Alternative;

@Alternative
public class LiftOperator implements Greeter {
    @Override
    public String greet() {
        return "Have a good day, Bye!";
    }

}
