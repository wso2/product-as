package org.wso2.appserver.sample.ee.jpa.jaxrs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import java.util.Date;

@Entity
public class Student {

    @Id
    private int index;

    @Column(length = 25)
    private String name;

    @Version
    @Column(length = 45)
    private Date timestamp;

    public Student(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public Student() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
