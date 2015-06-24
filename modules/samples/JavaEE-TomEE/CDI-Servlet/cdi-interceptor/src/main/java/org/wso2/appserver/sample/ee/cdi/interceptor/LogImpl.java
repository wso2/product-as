package org.wso2.appserver.sample.ee.cdi.interceptor;

import org.apache.commons.logging.LogFactory;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@Log
public class LogImpl {
    private static final org.apache.commons.logging.Log log = LogFactory.getLog(Receptionist.class);

    @AroundInvoke
    public Object log(InvocationContext context) throws Exception {
        log.info("Before greeting");
        context.proceed();
        log.info("After greeting");
        return null;
    }
}
