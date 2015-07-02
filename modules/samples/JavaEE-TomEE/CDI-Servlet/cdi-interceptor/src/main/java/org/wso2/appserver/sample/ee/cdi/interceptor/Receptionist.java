package org.wso2.appserver.sample.ee.cdi.interceptor;

import org.apache.commons.logging.LogFactory;

@Log
public class Receptionist implements Greeter {
    private static final org.apache.commons.logging.Log log = LogFactory.getLog(Receptionist.class);

    @Override
    public void greet() {
        log.info("Inside greet method");
    }
}
