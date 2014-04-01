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
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.test.utils.http.client.HttpsResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpsURLConnectionClient;

import static org.testng.Assert.assertEquals;

/**
 * https://wso2.org/jira/browse/IDENTITY-1161
 */
public class POXSecurityBasicAuthChallenge extends ASIntegrationTest {
    private static final String SERVICE_NAME = "Axis2Service";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        applySecurity("1", SERVICE_NAME, FrameworkConstants.ADMIN_ROLE);
    }


    @Test(enabled=false, groups = {"wso2.as"}, description = "GET request without basic auth")
    public void testPOSTRequestBySuperAdmin() throws
            Exception {
        String securedRestURL = getSecuredServiceEndpoint(SERVICE_NAME) + "/echoString";
        HttpsResponse response =
                HttpsURLConnectionClient.getRequest(securedRestURL, "s=TestAutomation");
        assertEquals(response.getResponseCode(), 401, "Basic Auth challenge failed - expected response " +
                                                      "code not returned");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        securityAdminServiceClient.disableSecurity(SERVICE_NAME);
        super.cleanup();
    }
}
