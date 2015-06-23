package org.wso2.appserver.sample.ee.cdi.jpa.jaxws;

import java.util.List;

public interface ContactManager {

    public String addContact(Contact contact) throws Exception;

    public List<Contact> getContacts();

}
