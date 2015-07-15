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
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.carbon.registry.resource.stub.beans.xsd.CollectionContentBean;

import javax.activation.DataHandler;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * A test case which tests registry XML resource add operation
 */
public class XMLResourceAddTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(XMLResourceAddTestCase.class);
    private static final String PARENT_PATH = "/_system/config";
    private static final String RES_FILES_COLLECTION = "ResFiles";
    private static final String RESOURCE_NAME = "synapse.xml";
    private static final String ESB_MEDIATYPE = "application/vnd.wso2.esb";
    private ResourceAdminServiceClient resourceAdminServiceClient;

    @BeforeClass(groups = {"wso2.as"}, alwaysRun = true)
    public void init () throws Exception {

        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        resourceAdminServiceClient =
                new ResourceAdminServiceClient(backendURL,
                        asServer.getContextTenant().getContextUser().getUserName(),
                        asServer.getContextTenant().getContextUser().getPassword());
    }

    @Test (groups = {"wso2.as"})
    public void testAddArtifacts ()
            throws ResourceAdminServiceExceptionException, RemoteException, MalformedURLException,
            XPathExpressionException {

        log.debug("Running SuccessCase");
        //add a collection to the registry
        String collectionPath =
                resourceAdminServiceClient.addCollection(PARENT_PATH, RES_FILES_COLLECTION, "",
                        "contains ResFiles");
        String authorUserName =
                resourceAdminServiceClient.getResource(PARENT_PATH +
                        "/" + RES_FILES_COLLECTION)[0].getAuthorUserName();
        assertTrue(asServer.getContextTenant().getContextUser().getUserName().equalsIgnoreCase(authorUserName),
                PARENT_PATH + "/" + RES_FILES_COLLECTION + " creation failure");
        log.info("collection added to " + collectionPath);
        // Changing media type
        resourceAdminServiceClient.addCollection(PARENT_PATH,
                RES_FILES_COLLECTION,
                ESB_MEDIATYPE,
                ESB_MEDIATYPE + " type collection");
        String resource = FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator
                + "AS" + File.separator + "xml" + File.separator + RESOURCE_NAME;
        DataHandler dh = new DataHandler(new URL("file:///" + resource));
        resourceAdminServiceClient.addResource(PARENT_PATH + "/" +
                RES_FILES_COLLECTION + "/" + RESOURCE_NAME,
                "application/xml", "resDesc",
                dh);
        String textContent =
                resourceAdminServiceClient.getTextContent(PARENT_PATH + "/" +
                        RES_FILES_COLLECTION +
                        "/" + RESOURCE_NAME);
        assertNotNull(textContent, "Unable to get text content");

    }

    @Test (groups = {"wso2.as"}, dependsOnMethods = "testAddArtifacts")
    public void testDeleteArtifacts ()
            throws ResourceAdminServiceExceptionException, RemoteException {

        resourceAdminServiceClient.deleteResource(PARENT_PATH + "/" +
                RES_FILES_COLLECTION + "/" + RESOURCE_NAME);
        boolean isResourceExist = false;
        CollectionContentBean collectionContentBean =
                resourceAdminServiceClient.getCollectionContent(PARENT_PATH +
                        "/" + RES_FILES_COLLECTION);
        if (collectionContentBean.getChildCount() > 0) {
            String[] childPath = collectionContentBean.getChildPaths();
            for (int i = 0; i <= childPath.length - 1; i++) {
                if (childPath[i].equalsIgnoreCase(PARENT_PATH +
                        "/" + RES_FILES_COLLECTION +
                        "/" + RESOURCE_NAME)) {
                    isResourceExist = true;
                }

            }
        }
        assertFalse(isResourceExist, "Resource exists even after deleting");
    }

    @AfterClass(groups = {"wso2.as"}, alwaysRun = true)
    public void cleanUp () throws ResourceAdminServiceExceptionException, RemoteException {

        resourceAdminServiceClient.deleteResource(PARENT_PATH + "/" + RES_FILES_COLLECTION);
        resourceAdminServiceClient = null;
    }
}
