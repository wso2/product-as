package org.wso2.appserver.sample.ee.cdi.jpa.jaxws;

import java.util.List;

public class ContactsDTO {

    private List<ContactDTO> contacts;

    public ContactsDTO(List<ContactDTO> contacts) {
        this.contacts = contacts;
    }

    public ContactsDTO() {
    }

    public List<ContactDTO> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactDTO> contacts) {
        this.contacts = contacts;
    }
}
