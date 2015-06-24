package org.wso2.appserver.sample.ee.cdi.scope;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;

@Named("LiftOperator")
public class LiftOperator implements Greeter {
    private static final Log log = LogFactory.getLog(LiftOperator.class);

    private int meetings;

    public LiftOperator() {
        meetings = 0;
    }

    @Override
    public String greet() {
        String greeting;

        if (meetings == 0)
            greeting = "Lift Operator: Hi, this is the first time I meet you";
        else
            greeting = "Lift Operator: Hi, I met you for " + meetings + " time(s)";

        meetings++;
        return greeting;
    }

    @PostConstruct
    public void postConstruct() {
        log.info("Post construct of LiftOperator");
    }

    @PreDestroy
    public void preDestroy() {
        log.info("Pre destroy of LifeOperator");
    }

}
