package org.wso2.appserver.sample.ee.cdi.produces;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.inject.Qualifier;


import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Retention(RUNTIME)
@Target({FIELD, TYPE, METHOD})
public @interface Greetings {
    GreetingType value();
}
