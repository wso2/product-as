package org.wso2.appserver.sample.ee.jsf.ejb;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

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
