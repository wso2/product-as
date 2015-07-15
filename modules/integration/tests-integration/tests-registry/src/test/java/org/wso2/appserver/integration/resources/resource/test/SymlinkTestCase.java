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
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.carbon.registry.resource.stub.beans.xsd.CollectionContentBean;

import javax.activation.DataHandler;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertTrue;

public class SymlinkTestCase extends ASIntegrationTest{

    private static final Log log = LogFactory.getLog(SymlinkTestCase.class);

    private static final String PARENT_PATH = "/TestAutomation";
    private static final String SYMLINK_PARENT_COLL_NAME = "SymlinkTestCase";
    private static final String TEST_COLLECTION1 = "TestCollection1";
    private static final String TEST_COLLECTION2 = "TestCollection2";
    private static final String RESOURCE_NAME = "sampleText.txt";

    private ResourceAdminServiceClient resourceAdminServiceClient;

    @BeforeClass(groups = {"wso2.as"}, alwaysRun = true)
    public void init() throws Exception {

      super.init(TestUserMode.SUPER_TENANT_ADMIN);

        resourceAdminServiceClient =
                new ResourceAdminServiceClient(backendURL,
                                               asServer.getContextTenant().getContextUser().getUserName(), asServer.getContextTenant().getContextUser().getPassword());
    }

    @Test(groups = {"wso2.as"})
    public void testAddArtifacts()
            throws ResourceAdminServiceExceptionException, IOException, XPathExpressionException {
        CollectionContentBean collectionContentBean =
                resourceAdminServiceClient.getCollectionContent("/");
        if (collectionContentBean.getChildCount() > 0) {
            String[] childPath = collectionContentBean.getChildPaths();
            for (int i = 0; i <= childPath.length - 1; i++) {
                if (childPath[i].equalsIgnoreCase(PARENT_PATH)) {
                    resourceAdminServiceClient.deleteResource(PARENT_PATH);
                }
            }
        }
        String collectionPath =
                resourceAdminServiceClient.addCollection("/", "TestAutomation", "", "");
        String authorUserName =
                resourceAdminServiceClient.getResource(PARENT_PATH)[0].getAuthorUserName();
        assertTrue(asServer.getContextTenant().getContextUser().getUserName().equalsIgnoreCase(authorUserName),
                   PARENT_PATH + " creation failure");
        log.info("collection added to " + collectionPath);

        collectionPath =
                resourceAdminServiceClient.addCollection(PARENT_PATH,
                                                         SYMLINK_PARENT_COLL_NAME, "", "");
        authorUserName = resourceAdminServiceClient.getResource(
                PARENT_PATH + "/" + SYMLINK_PARENT_COLL_NAME)[0].getAuthorUserName();
        assertTrue(asServer.getContextTenant().getContextUser().getUserName().equalsIgnoreCase(authorUserName),
                   PARENT_PATH + "/" + SYMLINK_PARENT_COLL_NAME + " creation failure");
        log.info("collection added to " + collectionPath);

        collectionPath =
                resourceAdminServiceClient.addCollection(PARENT_PATH + "/" +
                                                         SYMLINK_PARENT_COLL_NAME,
                                                         TEST_COLLECTION1, "", "");
        authorUserName = resourceAdminServiceClient.getResource(
                PARENT_PATH + "/" + SYMLINK_PARENT_COLL_NAME + "/" +
                TEST_COLLECTION1)[0].getAuthorUserName();
        assertTrue(asServer.getContextTenant().getContextUser().getUserName().equalsIgnoreCase(authorUserName),
                   PARENT_PATH + "/" + SYMLINK_PARENT_COLL_NAME + "/" +
                   TEST_COLLECTION1 + " creation failure");
        log.info("collection added to " + collectionPath);

        collectionPath =
                resourceAdminServiceClient.addCollection(PARENT_PATH + "/" +
                                                         SYMLINK_PARENT_COLL_NAME,
                                                         TEST_COLLECTION2, "", "");
        authorUserName = resourceAdminServiceClient.getResource(
                PARENT_PATH + "/" + SYMLINK_PARENT_COLL_NAME + "/" +
                TEST_COLLECTION2)[0].getAuthorUserName();
        assertTrue(asServer.getContextTenant().getContextUser().getUserName().equalsIgnoreCase(authorUserName),
                   PARENT_PATH + "/" + SYMLINK_PARENT_COLL_NAME + "/" +
                   TEST_COLLECTION2 + " creation failure");

        log.info("collection added to " + collectionPath);

        String resource = FrameworkPathUtil.getSystemResourceLocation() +
                          "artifacts" + File.separator
                          + "AS" + File.separator +  "txt" + File.separator + "sampleText.txt";


        DataHandler dh = new DataHandler(new URL("file:///" + resource));

        resourceAdminServiceClient.addResource(PARENT_PATH + "/" + SYMLINK_PARENT_COLL_NAME + "/" +
                                               TEST_COLLECTION1 + "/" + RESOURCE_NAME,
                                               "text/html", "txtDesc", dh);

        String text = resourceAdminServiceClient.getTextContent(
                PARENT_PATH + "/" + SYMLINK_PARENT_COLL_NAME + "/" +
                TEST_COLLECTION1 + "/" + RESOURCE_NAME);
        String fileText = dh.getContent().toString();

        assertTrue(fileText.equalsIgnoreCase(text),
                   PARENT_PATH + "/" + SYMLINK_PARENT_COLL_NAME + "/" +
                   TEST_COLLECTION1 + "/" + RESOURCE_NAME +
                   " creation failure");
    }

    @Test(groups = {"wso2.as"}, dependsOnMethods = "testAddArtifacts")
    public void testAddSymlink() throws Exception {
        resourceAdminServiceClient.addSymbolicLink(PARENT_PATH + "/" +
                                                   SYMLINK_PARENT_COLL_NAME + "/" +
                                                   TEST_COLLECTION1,
                                                   "sampleTestSymlink",
                                                   PARENT_PATH + "/" + SYMLINK_PARENT_COLL_NAME +
                                                   "/" + TEST_COLLECTION2);
        String authorUserName =
                resourceAdminServiceClient.getResource(PARENT_PATH + "/" +
                                                       SYMLINK_PARENT_COLL_NAME
                                                       + "/" +TEST_COLLECTION1)[0].getAuthorUserName();
        assertTrue(asServer.getContextTenant().getContextUser().getUserName().equalsIgnoreCase(authorUserName),
                   "Symlink creation failure");

    }

    @AfterClass(groups = {"wso2.as"}, alwaysRun = true)
    public void cleanUp() throws ResourceAdminServiceExceptionException, RemoteException {
        resourceAdminServiceClient.deleteResource("/TestAutomation");
        resourceAdminServiceClient=null;
    }
}
