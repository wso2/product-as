/*
* Copyright 2004,2013 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.appserver.test.integration.javaee.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;
import org.wso2.appserver.sample.ee.cdi.jpa.jaxws.ContactDTO;
import org.wso2.appserver.sample.ee.cdi.jpa.jaxws.ContactsDTO;
import org.wso2.appserver.sample.ee.cdi.jpa.jaxws.CustomersDatabase;
import org.wso2.appserver.test.integration.TestBase;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class JpaJaxWsTestCase extends TestBase {

    private static final Log log = LogFactory.getLog(JpaJaxWsTestCase.class);
    private static final String webAppLocalURL = "/jpa-contacts-database-" + System.getProperty("appserver.version");

    @Test(description = "test jpa and jax-ws")
    public void testJpaWsGet() throws Exception {
        String serviceName = "CustomersDatabaseServiceService";
        String wsdlUrl = getBaseUrl() + webAppLocalURL + "/" + serviceName + "?wsdl";

        Service customerDataService = Service.create(
                new URL(wsdlUrl),
                new QName("http://jaxws.jpa.cdi.ee.sample.appserver.wso2.org/", serviceName));

        assertNotNull(customerDataService);

        CustomersDatabase customersDatabase = customerDataService.getPort(CustomersDatabase.class);
        ContactDTO contact = new ContactDTO("Bob", "(012)345-6789", 25, "bob@bob.com", new Date());

        String response = customersDatabase.addContact(contact);
        log.info("Response : " + response);

        assertEquals("Contact was saved successfully.", response,
                "AddContact response doesn't contain the expected success message");

        ContactsDTO contacts = customersDatabase.getContacts();

        ContactDTO contact1 = contacts.getContacts().get(0);
        log.info("Contact details retrieved, name: " + contact1.getName() + ", email: " + contact1.getEmail());

        assertEquals(contact.getName(), contact1.getName(), "Contact name doesn't match");
    }
}
