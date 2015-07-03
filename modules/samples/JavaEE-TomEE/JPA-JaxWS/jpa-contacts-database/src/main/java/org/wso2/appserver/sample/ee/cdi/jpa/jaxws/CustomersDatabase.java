package org.wso2.appserver.sample.ee.cdi.jpa.jaxws;

import javax.jws.WebService;

@WebService
public interface CustomersDatabase {

    public ContactsDTO getContacts() throws Exception;

    public String addContact(ContactDTO contact) throws Exception;

}
