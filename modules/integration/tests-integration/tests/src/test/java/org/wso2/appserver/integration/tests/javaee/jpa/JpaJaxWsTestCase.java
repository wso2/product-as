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
package org.wso2.appserver.integration.tests.javaee.jpa;

import java.io.File;
import java.net.URL;
import java.util.Date;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.appserver.sample.ee.cdi.jpa.jaxws.ContactDTO;
import org.wso2.appserver.sample.ee.cdi.jpa.jaxws.ContactsDTO;
import org.wso2.appserver.sample.ee.cdi.jpa.jaxws.CustomersDatabase;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class JpaJaxWsTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(JpaJaxWsTestCase.class);
    private static final String webAppFileName = "jpa-contacts-database.war";
    private static final String webAppName = "jpa-contacts-database";
    private static final String webAppLocalURL = "/jpa-contacts-database";
    private TestUserMode userMode;
    private String hostname;

    @Factory(dataProvider = "userModeProvider")
    public JpaJaxWsTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN},
                //todo enable tenant mode after fixing sample issue with tenant
                //jira : https://wso2.org/jira/browse/WSAS-1998
                new TestUserMode[]{TestUserMode.TENANT_USER},
        };
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);

        hostname = asServer.getInstance().getHosts().get("default");
        webAppURL = getWebAppURL(WebAppTypes.WEBAPPS) + webAppLocalURL;

        String webAppFilePath = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator +
                "AS" + File.separator + "javaee" + File.separator + "jpa" + File.separator + webAppFileName;
        WebAppDeploymentUtil.deployWebApplication(backendURL, sessionCookie, webAppFilePath);

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, webAppName),
                "Web Application Deployment failed");
    }

    @Test(groups = "wso2.as", description = "test jpa and jax-ws", enabled = true)
    public void testJpaWsGet() throws Exception {

        String serviceName = "CustomersDatabaseServiceService";
        String wsdlUrl = webAppURL + "/" + serviceName + "?wsdl";

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

    @AfterClass(alwaysRun = true)
    public void cleanupWebApps() throws Exception {
        WebAppDeploymentUtil.unDeployWebApplication(backendURL, hostname, sessionCookie, webAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, webAppName),
                "Web Application unDeployment failed");
    }

}
