package org.wso2.appserver.sample.ee.cdi.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@Log
public class LogImpl {

    @AroundInvoke
    public Object log(InvocationContext context) throws Exception {
        System.out.println("Before greeting");
        context.proceed();
        System.out.println("After greeting");
        return null;
    }
}
