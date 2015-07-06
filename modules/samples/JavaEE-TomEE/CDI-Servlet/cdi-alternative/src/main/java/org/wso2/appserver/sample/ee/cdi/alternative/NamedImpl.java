package org.wso2.appserver.sample.ee.cdi.alternative;

import javax.inject.Named;

@Named("NamedImplementation")
public class NamedImpl implements Greeter {
    @Override
    public String greet() {
        return "Hi, this is the named implementation";
    }
}
