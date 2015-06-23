package org.wso2.appserver.sample.ee.cdi.scope;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;


@Named("Receptionist")
@RequestScoped
public class Receptionist implements Greeter {

    private int meetings;

    public Receptionist() {
        meetings = 0;
    }

    @Override
    public String greet() {
        String greeting = "";

        if (meetings == 0)
            greeting = "Receptionist: Hi, this is the first time I meet you";
        else
            greeting = "Receptionist: Hi, I met you for " + meetings + " time(s)";

        meetings++;
        return greeting;
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("Post construct of Receptionist");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("Pre destroy of Receptionist");
    }
}
