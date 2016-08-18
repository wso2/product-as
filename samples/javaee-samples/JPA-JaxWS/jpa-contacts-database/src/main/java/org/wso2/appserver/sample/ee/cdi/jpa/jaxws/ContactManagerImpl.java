/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.appserver.sample.ee.cdi.jpa.jaxws;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.util.List;
import javax.ejb.Stateful;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

/**
 * ContactManagerImpl class.
 */
@Stateful
@Named
public class ContactManagerImpl implements ContactManager {
    private static final Log log = LogFactory.getLog(ContactManagerImpl.class);

    @PersistenceContext(name = "ContactsUnit", type = PersistenceContextType.EXTENDED)
    EntityManager entityManager;

    @Override
    public String addContact(Contact contact) throws Exception {
        try {
            entityManager.persist(contact);
            return "Contact was saved successfully.";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Error Occurred : " + e.getMessage();
        }
    }

    @Override
    public List<Contact> getContacts() {
        Query query = entityManager.createQuery("SELECT Contact contact FROM Contact contact");
        return query.getResultList();
    }

    /*@PostConstruct
    private void addDefaultContacts() {
        Calendar calendar = new GregorianCalendar();
        Contact contact1 = new Contact("Bob", "0711234567", 20, "bob@bob.com", new Date());
        try {
            addContact(contact1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }*/
}
