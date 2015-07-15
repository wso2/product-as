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

import javax.activation.DataHandler;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.testng.Assert.assertTrue;

/**
 * A test case which tests registry non xml resource add operation
 */

public class NonXMLResourceAddTestCase extends ASIntegrationTest{

    private static final Log log = LogFactory.getLog(NonXMLResourceAddTestCase.class);
    private static final String PARENT_PATH = "/_system/config/";
    private static final String RES_FILE_FOLDER = "TextFiles";
    private static final String TEXT_FILE_NAME = "sampleText.txt";
    private ResourceAdminServiceClient resourceAdminServiceClient;

    @BeforeClass(groups = {"wso2.as"}, alwaysRun = true)
    public void init() throws Exception {

        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        log.info("Initializing Add Non-XML Resource Tests");
        log.debug("Add Non-XML Resource Test Initialised");
        resourceAdminServiceClient =
                new ResourceAdminServiceClient(backendURL,
                                               asServer.getContextTenant().getContextUser().getUserName(), asServer.getContextTenant().getContextUser().getPassword());
    }

    @Test(groups = {"wso2.as"})
    public void testAddNoneXmlResource()
            throws ResourceAdminServiceExceptionException, IOException, XPathExpressionException {

        //add a collection to the registry
        String collectionPath =
                resourceAdminServiceClient.addCollection(PARENT_PATH, RES_FILE_FOLDER, "",
                                                         "contains Text Res Files");
        String authorUserName =
                resourceAdminServiceClient.getResource(
                        (PARENT_PATH + RES_FILE_FOLDER))[0].getAuthorUserName();
        assertTrue(asServer.getContextTenant().getContextUser().getUserName().equalsIgnoreCase(authorUserName),
                   PARENT_PATH + RES_FILE_FOLDER + " creation failure");
        log.info("collection added to " + collectionPath);

        // Changing media type
        collectionPath =
                resourceAdminServiceClient.addCollection(
                        PARENT_PATH, RES_FILE_FOLDER,
                        "application/vnd.wso2.esb",
                        "application/vnd.wso2.esb media type collection");
        authorUserName =
                resourceAdminServiceClient.getResource(
                        (PARENT_PATH + RES_FILE_FOLDER))[0].getAuthorUserName();
        assertTrue(asServer.getContextTenant().getContextUser().getUserName().equalsIgnoreCase(authorUserName),
                   PARENT_PATH + RES_FILE_FOLDER + " updating failure");
        log.info("collection updated in " + collectionPath);

        String resource = FrameworkPathUtil.getSystemResourceLocation() +
                          "artifacts" + File.separator
                          + "AS" + File.separator + "txt" + File.separator + "sampleText.txt";

        DataHandler dh = new DataHandler(new URL("file:///" + resource));


        resourceAdminServiceClient.addResource(PARENT_PATH + RES_FILE_FOLDER + "/" + TEXT_FILE_NAME,
                                               "text/html", "txtDesc",
                                               dh);

        String textContent =
                resourceAdminServiceClient.getTextContent(PARENT_PATH + RES_FILE_FOLDER + "/" +
                                                          TEXT_FILE_NAME);

        assertTrue(dh.getContent().toString().equalsIgnoreCase(textContent),
                   "Text file has not been added properly ");
    }

    //cleanup code
    @AfterClass(groups = {"wso2.as"}, alwaysRun = true)
    public void cleanUp()
            throws Exception {
        resourceAdminServiceClient.deleteResource(PARENT_PATH+RES_FILE_FOLDER);
        resourceAdminServiceClient=null;
    }
}
