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
import org.wso2.carbon.registry.info.stub.RegistryExceptionException;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.carbon.registry.resource.stub.beans.xsd.CollectionContentBean;
import org.wso2.carbon.registry.resource.stub.beans.xsd.ResourceTreeEntryBean;

import javax.activation.DataHandler;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * A test case which tests registry resource
 */
public class RegistryResourceTestCase extends ASIntegrationTest{

    private static final Log log = LogFactory.getLog(RegistryResourceTestCase.class);

    private static final String PARENT_PATH = "/TestAutomation";
    private static final String WSO2_COLL = "wso2";
    private ResourceAdminServiceClient resourceAdminServiceClient;
    private String TEXT_FILE_NAME = "hello";

    @BeforeClass(groups = {"wso2.as"}, alwaysRun = true)
    public void init() throws Exception {

        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        log.debug("Running SuccessCase");
        resourceAdminServiceClient =
                new ResourceAdminServiceClient(backendURL,
                                               asServer.getContextTenant().getContextUser().getUserName(), asServer.getContextTenant().getContextUser().getPassword());
    }

    @Test(groups = {"wso2.as"})
    public void testCreateCollection()
            throws ResourceAdminServiceExceptionException, RemoteException, XPathExpressionException {
        log.debug("Running SuccessCase");

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
                resourceAdminServiceClient.addCollection("/TestAutomation", "wso2", "", "");
        authorUserName =
                resourceAdminServiceClient.getResource(PARENT_PATH +
                                                       "/" + WSO2_COLL)[0].getAuthorUserName();
        assertTrue(asServer.getContextTenant().getContextUser().getUserName().equalsIgnoreCase(authorUserName),
                   PARENT_PATH + "/" + WSO2_COLL + " creation failure");
        log.info("collection added to " + collectionPath);


    }

    @Test(groups = {"wso2.as"}, dependsOnMethods = "testCreateCollection")
    public void testAddResourceFromLocalFile()
            throws IOException, ResourceAdminServiceExceptionException {

        String RESOURCE_NAME = "sampleText.txt";
        String resource =
                FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator
                + "AS" + File.separator + "txt" + File.separator + RESOURCE_NAME;
                

        DataHandler dh = new DataHandler(new URL("file:///" + resource));

        resourceAdminServiceClient.addResource(PARENT_PATH + "/" + WSO2_COLL + "/" + RESOURCE_NAME,
                                               "text/html", "txtDesc",
                                               dh);
        String textContent = resourceAdminServiceClient.getTextContent(PARENT_PATH +
                                                                       "/" + WSO2_COLL +
                                                                       "/" + RESOURCE_NAME);

        assertTrue(dh.getContent().toString().equalsIgnoreCase(textContent), "Added resource not found");
        log.info("Resource successfully added to the registry and retrieved contents successfully");

    }

    @Test(groups = {"wso2.as"}, dependsOnMethods = "testCreateCollection")
    public void testAddResourceFromURL()
            throws MalformedURLException, ResourceAdminServiceExceptionException, RemoteException,
                   RegistryExceptionException {
        boolean isFound = false;
        String JAR_URL = "http://dist.wso2.org/maven2/org/wso2/carbon/org.wso2.carbon." +
                "registry.profiles.ui/3.0.0/org.wso2.carbon.registry.profiles." +
                "ui-3.0.0.jar";
        String JAR_NAME = "org.wso2.carbon.registry.profiles.ui-3.0.0.jar";
        resourceAdminServiceClient.addResource(PARENT_PATH + "/" + WSO2_COLL + "/" + JAR_NAME,
                                               "application/java-archive",
                                               "resource added from external URL",
                                               new DataHandler(new URL(
                                                       JAR_URL)));
        ResourceTreeEntryBean resourceTreeEntryBean =
                resourceAdminServiceClient.getResourceTreeEntryBean(PARENT_PATH + "/" + WSO2_COLL);
        String[] resourceChild = resourceTreeEntryBean.getChildren();
        for (int childCount = 0; childCount <= resourceChild.length; childCount++) {
            if (resourceChild[childCount].equalsIgnoreCase(
                    PARENT_PATH + "/" + WSO2_COLL + "/" + JAR_NAME)) {
                isFound = true;
                break;
            }
        }

        assertTrue(isFound, "uploaded resource not found in " + PARENT_PATH + "/" + WSO2_COLL);
    }

    @Test(groups = {"wso2.as"}, dependsOnMethods = "testCreateCollection")
    public void testAddTextContent()
            throws ResourceAdminServiceExceptionException, RemoteException {

        String text = "Hello world";
        resourceAdminServiceClient.addTextResource(PARENT_PATH + "/" + WSO2_COLL, TEXT_FILE_NAME,
                                                   "text/plain", "sample", text);
        String textContent = resourceAdminServiceClient.getTextContent(PARENT_PATH + "/" +
                                                                       WSO2_COLL + "/" +
                                                                       TEXT_FILE_NAME);
        assertTrue(text.equalsIgnoreCase(textContent), "Added text resource not found");
        log.info("Resource successfully added to the registry and retrieved contents successfully");


    }

    @Test(groups = {"wso2.as"}, dependsOnMethods = "testCreateCollection")
    public void testResourceBoundary() {

        // some characters may fail due to the CARBON-8352
        String[] charBuffer = {"~", "!", "@", "#", "%", "^", "*", "+", "=", "{", "}",
                               "|", "\\", "<", ">", "\"", "\'", ";"};
        for (String aCharBuffer : charBuffer) {
            try {
                resourceAdminServiceClient.addTextResource(PARENT_PATH + "/" + WSO2_COLL,
                                                           TEXT_FILE_NAME + aCharBuffer,
                                                           "text/plain", "sample", "Hello world");
                log.error("Invalid resource added with illigal character " + aCharBuffer);
                resourceAdminServiceClient.deleteResource("/TestAutomation");
                fail("Invalid resource added with illigal character " + aCharBuffer);
            } catch (Exception e) {

                assertTrue(e.getMessage().contains(
                        "contains one or more illegal characters (~!@#;%^*()+={}|\\<>\"',)" +
                        "" +
                        ""), "Invalid resource added with illigal character " + aCharBuffer);
            }
        }
    }

    @AfterClass(groups = {"wso2.as"}, alwaysRun = true)
    public void cleanUp() throws ResourceAdminServiceExceptionException, RemoteException {
        resourceAdminServiceClient.deleteResource("/TestAutomation");

        resourceAdminServiceClient=null;
    }
}
//ToDo create custom content testcase need to be added
