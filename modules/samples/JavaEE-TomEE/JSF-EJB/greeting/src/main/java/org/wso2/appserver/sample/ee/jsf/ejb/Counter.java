package org.wso2.appserver.sample.ee.jsf.ejb;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class Counter implements Serializable {

    int count;

    public int getCount() {
        return count;
    }

    public void increaseCount() {
        count++;
    }

}
