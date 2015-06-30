package org.wso2.appserver.sample.ee.cdi.qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.inject.Qualifier;


import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface Entrance {
}
