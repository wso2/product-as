/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.appserver.sample.ee.cdi.jpa.jaxws;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * CustomersDatabaseService class.
 */
@WebService
public class CustomersDatabaseService implements CustomersDatabase {

    @EJB
    ContactManager contactManager;

    @WebMethod
    public ContactsDTO getContacts() throws Exception {

        List<ContactDTO> contactsList = new ArrayList<ContactDTO>();

        for (Contact contact : contactManager.getContacts()) {
            contactsList.add(new ContactDTO(contact.getName(), contact.getContactNumber(), contact.getAge(),
                    contact.getEmail(), contact.getBirthday()));
        }

        ContactsDTO contactsDTO = new ContactsDTO(contactsList);
        return contactsDTO;
    }

    @WebMethod
    public String addContact(ContactDTO contact) throws Exception {
        Contact contactEntity = new Contact(contact.getName(), contact.getContactNumber(), contact.getAge(),
                contact.getEmail(), contact.getBirthday());
        return contactManager.addContact(contactEntity);
    }
}
