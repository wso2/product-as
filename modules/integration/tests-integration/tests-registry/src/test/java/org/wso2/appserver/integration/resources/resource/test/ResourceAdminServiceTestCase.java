/*
*  Copyright (c) WSO2 Inc. (http://wso2.com) All Rights Reserved.

  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
*/

package org.wso2.appserver.integration.resources.resource.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.ResourceAdminServiceClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;

import javax.xml.xpath.XPathExpressionException;
import java.rmi.RemoteException;

import static org.testng.Assert.assertTrue;

/**
 * A test case which tests registry resource admin service operation
 */

public class ResourceAdminServiceTestCase extends ASIntegrationTest{

    private static final Log log = LogFactory.getLog(ResourceAdminServiceTestCase.class);
    private ResourceAdminServiceClient resourceAdminServiceClient;

    @BeforeClass(groups = {"wso2.as"}, alwaysRun = true)
    public void init() throws Exception {

       super.init(TestUserMode.SUPER_TENANT_ADMIN);
        log.debug("Running SuccessCase");


        resourceAdminServiceClient =
                new ResourceAdminServiceClient(backendURL,
                                               asServer.getContextTenant().getContextUser().getUserName(), asServer.getContextTenant().getContextUser().getPassword());
    }

    @Test(groups = {"wso2.as"})
    public void runSuccessCase() throws ResourceAdminServiceExceptionException, RemoteException, XPathExpressionException {

        String collectionPath = resourceAdminServiceClient.addCollection("/", "Test", "", "");
        String authorUserName =
                resourceAdminServiceClient.getResource("/Test")[0].getAuthorUserName();
        assertTrue(asServer.getContextTenant().getContextUser().getUserName().equalsIgnoreCase(authorUserName),
                   "/Test creation failure");
        log.debug("collection added to " + collectionPath);
        // resourceAdminServiceStub.addResource("/Test/echo_back.xslt", "application/xml", "xslt files", null,null);

        String content = "Hello world";
        resourceAdminServiceClient.addTextResource("/Test", "Hello", "text/plain", "sample",
                                                   content);
        String textContent = resourceAdminServiceClient.getTextContent("/Test/Hello");

        assertTrue(content.equalsIgnoreCase(textContent), "Text content does not match");

    }

    @AfterClass(groups = {"wso2.as"}, alwaysRun = true)
    public void cleanUp() throws ResourceAdminServiceExceptionException, RemoteException {
        resourceAdminServiceClient.deleteResource("/Test");
        resourceAdminServiceClient=null;
    }
}
