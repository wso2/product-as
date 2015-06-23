package org.wso2.appserver.sample.ee.cdi.qualifier;


import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created with IntelliJ IDEA.
 * User: kalpa
 * Date: 7/10/14
 * Time: 10:14 AM
 * To change this template use File | Settings | File Templates.
 */
@Qualifier
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface Entrance {
}
