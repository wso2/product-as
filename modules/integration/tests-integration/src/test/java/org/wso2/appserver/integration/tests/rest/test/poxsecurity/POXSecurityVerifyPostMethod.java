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
import org.wso2.appserver.integration.tests.ASTestConstants;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.test.utils.http.client.HttpsResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpsURLConnectionClient;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class POXSecurityVerifyPostMethod extends ASIntegrationTest {
    private static final String SERVICE_NAME = "Axis2Service";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        applySecurity("1", SERVICE_NAME, FrameworkConstants.ADMIN_ROLE);
    }


    @Test(groups = {"wso2.as"}, description = "POST request by admin")
    public void testPOSTRequestBySuperAdmin()
            throws IOException, LoginAuthenticationExceptionException,
            XMLStreamException, XPathExpressionException {
        String securedRestURL = getSecuredServiceEndpoint(SERVICE_NAME) + "/echoString";
        HttpsResponse response = HttpsURLConnectionClient.postWithBasicAuth(securedRestURL, "s=TestAutomation",
                asServer.getSuperTenant().getTenantAdmin().getUserName(),
                asServer.getSuperTenant().getTenantAdmin().getPassword());
        assertTrue(response.getData().contains("<ns:echoStringResponse xmlns:ns=\"http://service.carbon.wso2.org\">" +
                                               "<ns:return>TestAutomation</ns:return></ns:echoStringResponse>")
                , "response doesn't contain the expected output");
    }

    @Test(groups = {"wso2.as"}, description = "POST request by user/tenant", dependsOnMethods = "testPOSTRequestBySuperAdmin")
    public void testPOSTRequestByUser() throws Exception {
        super.init();
        applySecurity("1", SERVICE_NAME, ASTestConstants.POX_ROLE_NAME);
        String securedRestURL = getSecuredServiceEndpoint(SERVICE_NAME) + "/echoString";
        HttpsResponse response = HttpsURLConnectionClient.postWithBasicAuth(securedRestURL, "s=TestAutomation",
                ASTestConstants.POX_USER, ASTestConstants.POX_USER_PASSWORD);

        assertTrue(response.getData().contains("<ns:echoStringResponse xmlns:ns=\"http://service.carbon.wso2.org\">" +
                                               "<ns:return>TestAutomation</ns:return></ns:echoStringResponse>")
                , "response doesn't contain the expected output");
    }

    @Test(groups = {"wso2.as"}, description = "POST request by invalid user",
          dependsOnMethods = "testPOSTRequestByUser", expectedExceptions = IOException.class)
    public void testPOSTRequestByInvalidUser() throws Exception {
        super.init();
        applySecurity("1", SERVICE_NAME, FrameworkConstants.ADMIN_ROLE);

        String securedRestURL = getSecuredServiceEndpoint(SERVICE_NAME) + "/echoString";
        HttpsResponse response =
                HttpsURLConnectionClient.postWithBasicAuth(securedRestURL, "s=TestAutomation",
                                                           "invalidUser", "InvalidPassword");
        assertFalse(response.getData().contains("<ns:echoStringResponse xmlns:ns=\"http://service.carbon.wso2.org\">" +
                                                "<ns:return>TestAutomation</ns:return></ns:echoStringResponse>")
                , "response doesn't contain the expected output");
    }

    @Test(groups = {"wso2.as"}, description = "Test post request by user belongs to unauthorized group",
          dependsOnMethods = "testPOSTRequestByInvalidUser", expectedExceptions = IOException.class)
    public void testPOSTRequestByGroup() throws Exception {
        String adminUserGroup = FrameworkConstants.ADMIN_ROLE;
        applySecurity("1", SERVICE_NAME, adminUserGroup);
        String securedRestURL = getSecuredServiceEndpoint(SERVICE_NAME) + "/echoString";
        HttpsResponse response =
                HttpsURLConnectionClient.postWithBasicAuth(securedRestURL, "s=TestAutomation",
                                                           ASTestConstants.POX_USER, ASTestConstants.POX_USER_PASSWORD);

        assertFalse(response.getData().contains("<ns:echoStringResponse xmlns:ns=\"http://service.carbon.wso2.org\">" +
                                                "<ns:return>TestAutomation</ns:return></ns:echoStringResponse>")
                , "response doesn't contain the expected output");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        securityAdminServiceClient.disableSecurity(SERVICE_NAME);
        super.cleanup();
    }
}
