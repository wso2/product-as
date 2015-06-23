package org.wso2.appserver.sample.ee.cdi.alternative;

import javax.enterprise.inject.Alternative;

@Alternative
public class Receptionist implements Greeter {
    public String greet() {
        return "Hi, Good morning";
    }
}
