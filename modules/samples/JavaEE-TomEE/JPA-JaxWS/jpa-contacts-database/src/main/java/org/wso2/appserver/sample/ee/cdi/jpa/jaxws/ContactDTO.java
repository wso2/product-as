package org.wso2.appserver.sample.ee.cdi.jpa.jaxws;

import java.util.Date;

public class ContactDTO {
    private String name;
    private String contactNumber;
    private int age;
    private String email;
    private Date birthday;

    public ContactDTO(String name, String contactNumber, int age, String email, Date birthday) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.age = age;
        this.email = email;
        this.birthday = birthday;
    }

    public ContactDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
