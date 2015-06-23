package org.wso2.appserver.sample.ee.jpa.jaxrs;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Student")
public class StudentDTO {

    private int index;
    private String name;

    public StudentDTO(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public StudentDTO() {
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
