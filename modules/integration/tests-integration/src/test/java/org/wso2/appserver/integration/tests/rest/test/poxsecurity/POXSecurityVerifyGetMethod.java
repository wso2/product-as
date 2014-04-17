/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.appserver.integration.tests.rest.test.poxsecurity;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.tests.ASTestConstants;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpsResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpsURLConnectionClient;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/*
check GET methods with pox security by using credentials of admin, user, invalid user and invalid group.
*/
public class POXSecurityVerifyGetMethod extends ASIntegrationTest {
    private static final String SERVICE_NAME = "SimpleStockQuoteService";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        applySecurity("1", SERVICE_NAME, FrameworkConstants.ADMIN_ROLE);
    }

    @Test(groups = {"wso2.as"}, description = "test pox security with super tenant credentials")
    public void testGetQuote() throws IOException,
            LoginAuthenticationExceptionException,
            XMLStreamException, XPathExpressionException {
        String securedRestURL = getSecuredServiceEndpoint(SERVICE_NAME) + "/getSimpleQuote";
        HttpsResponse response = HttpsURLConnectionClient.getWithBasicAuth(securedRestURL, "symbol=IBM",
                asServer.getSuperTenant().getTenantAdmin().getUserName(),
                asServer.getSuperTenant().getTenantAdmin().getPassword());
        assertTrue(response.getData().contains("IBM Company"),
                "getQuote doesn't return expected values");
        assertTrue(response.getData().contains("IBM"),
                "getQuote doesn't return expected values");
    }

    @Test(groups = {"wso2.as"}, description = "test pox security with user credentials", dependsOnMethods = "testGetQuote")
    public void testGetQuoteByUser() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_USER);

        applySecurity("1", SERVICE_NAME, FrameworkConstants.ADMIN_ROLE);
        String securedRestURL = getSecuredServiceEndpoint(SERVICE_NAME) + "/getSimpleQuote";

        HttpsResponse response =
                HttpsURLConnectionClient.getWithBasicAuth(securedRestURL, "symbol=IBM",
                        asServer.getSuperTenant().getTenantUser("userKey1").getUserName(),
                        asServer.getSuperTenant().getTenantUser("userKey1").getPassword());

        assertTrue(response.getData().contains("IBM Company"), "getQuote doesn't return expected values");
        assertTrue(response.getData().contains("IBM"), "getQuote doesn't return expected values");
    }

    @Test(groups = {"wso2.as"}, description = "test pox security with invalid user credentials",
            dependsOnMethods = "testGetQuoteByUser")
    public void testGetQuoteWithInvalidCredentials() throws Exception {
        String securedRestURL = getSecuredServiceEndpoint(SERVICE_NAME) + "/getSimpleQuote";
        boolean status = false;
        HttpsResponse response = null;
        try {
            response =
                    HttpsURLConnectionClient.getWithBasicAuth(securedRestURL, "symbol=IBM", "invalidUserName",
                            "InvalidPassword");
        } catch (IOException ignored) {
            status = true; // invalid users cannot read the resource
        }
        assertTrue(status, "Invalid user was able to get the resource");
        assertNull(response, "Response cannot be null");
    }

    @Test(groups = {"wso2.as"}, description = "test pox security with invalid user group",
            dependsOnMethods = "testGetQuoteWithInvalidCredentials")
    public void testGetQuoteWithInvalidGroup() throws Exception {
        String securedRestURL = getSecuredServiceEndpoint(SERVICE_NAME) + "/getSimpleQuote";
        String adminUserGroup = FrameworkConstants.ADMIN_ROLE; //user POX_USER doesn't belong to admin group thus test should throws IOException
        applySecurity("1", SERVICE_NAME, adminUserGroup);
        HttpsResponse response = null;
        boolean status = false;
        try {
            response =
                    HttpsURLConnectionClient.getWithBasicAuth(securedRestURL, "symbol=IBM",
                            ASTestConstants.POX_USER, ASTestConstants.POX_USER_PASSWORD);
            if (response != null) {
                if (response.getResponseCode() == 401) {
                    status = true;
                }
            }
        } catch (IOException ignored) {
            status = true; // invalid users cannot read the resource
        }
        assertTrue(status, "User belongs to invalid group was able to get the resource");
        assertNull(response, "Response cannot be null");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        securityAdminServiceClient.disableSecurity(SERVICE_NAME);
        super.cleanup();
    }
}
