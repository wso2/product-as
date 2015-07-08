/*
* Copyright 2004,2005 The Apache Software Foundation.
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

package org.wso2.appserver.integration.resources.resource.test;

import org.apache.abdera.model.AtomDate;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.util.base64.Base64Utils;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.ResourceAdminServiceClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.configurations.UrlGenerationUtil;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.carbon.registry.resource.stub.common.xsd.ResourceData;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.util.Date;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class CollectionInCollectionFeedTestCase extends ASIntegrationTest {

    private ResourceAdminServiceClient resourceAdminClient;

    private static final String PATH = "/c3/";
    private static final String COLL_NAME = "TestFolder";
    private static final String COLL_NAME_AFTER_RENAME = "RenamedTestFolder";
    private static final String COLL_NAME_AFTER_MOVING = "MovedTestFolder";
    private static final String COLL_MOVED_LOCATION = "/c1/c2/";
    private static final String COLL_COPIED_LOCATION = "/c1/";
    private static final String COLL_NAME_AFTER_COPYING = "CopiedTestFolder";
    private static final String COLL_DESC = "A test collection";
    private static final String REGISTRY_NAMESPACE = "http://wso2.org/registry";

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        resourceAdminClient =
                new ResourceAdminServiceClient(backendURL,
                                               sessionCookie);
    }

    @Test(groups = "wso2.as")
    public void testAddCollection()
            throws ResourceAdminServiceExceptionException, RemoteException, XPathExpressionException {

        String fileType = "other";
        resourceAdminClient.addCollection(PATH, COLL_NAME, fileType, COLL_DESC);

        String authorUserName = resourceAdminClient.getResource(PATH + COLL_NAME)[0].getAuthorUserName();
        assertTrue(asServer.getContextTenant().getContextUser().getUserName().equalsIgnoreCase(authorUserName), "Collection creation failure");
    }

    @Test(groups = "wso2.as", dependsOnMethods = "testAddCollection")
    public void testFeed()
            throws ResourceAdminServiceExceptionException, IOException, XMLStreamException, XPathExpressionException {

        ResourceData[] rData = resourceAdminClient.getResource(PATH + COLL_NAME);

        OMElement atomFeedOMElement = getAtomFeedContent(constructAtomUrl(PATH + COLL_NAME));

        assertNotNull(atomFeedOMElement, "No feed data available");

        //checking whether the created time is correct
        OMElement createdElement = atomFeedOMElement.getFirstChildWithName(
                new QName(REGISTRY_NAMESPACE, "createdTime"));

        assertTrue(createdElement.getText().equalsIgnoreCase(getAtomDateString(rData[0].getCreatedOn().getTime())),
                   "Created time is incorrect");
    }

    @Test(groups = "wso2.as", dependsOnMethods = "testFeed")
    public void testRenameCollection()
            throws ResourceAdminServiceExceptionException, RemoteException {

        resourceAdminClient.renameResource(PATH, PATH + COLL_NAME, COLL_NAME_AFTER_RENAME);
        boolean found = false;
        ResourceData[] rData = resourceAdminClient.getResource(PATH + COLL_NAME_AFTER_RENAME);

        for (ResourceData resource : rData) {
            if (COLL_NAME_AFTER_RENAME.equalsIgnoreCase(resource.getName())) {
                found = true;
            }
        }

        assertTrue(found, "Rename collection error");
    }

    @Test(groups = "wso2.as", dependsOnMethods = "testRenameCollection")
    public void testFeedAfterRename()
            throws ResourceAdminServiceExceptionException, IOException, XMLStreamException, XPathExpressionException {

        ResourceData[] rData = resourceAdminClient.getResource(PATH + COLL_NAME_AFTER_RENAME);

        OMElement atomFeedOMElement = getAtomFeedContent(constructAtomUrl(PATH + COLL_NAME_AFTER_RENAME));

        assertNotNull(atomFeedOMElement, "No feed data available");

        //checking whether the created time is correct
        OMElement createdElement = atomFeedOMElement.getFirstChildWithName(
                new QName(REGISTRY_NAMESPACE, "createdTime"));

        assertTrue(createdElement.getText().equalsIgnoreCase(getAtomDateString(rData[0].getCreatedOn().getTime())),
                   "Created time is incorrect after renaming");
    }

    @Test(groups = "wso2.as", dependsOnMethods = "testFeedAfterRename")
    public void testCopyCollection()
            throws ResourceAdminServiceExceptionException, RemoteException {

        //copy resource
        resourceAdminClient.copyResource(PATH,
                                         PATH + COLL_NAME_AFTER_RENAME, COLL_COPIED_LOCATION, COLL_NAME_AFTER_COPYING);

        //check that the collection has been moved
        String copiedDesc =
                resourceAdminClient.getResource(COLL_COPIED_LOCATION + COLL_NAME_AFTER_COPYING)[0].getDescription();

        assertTrue(COLL_DESC.equalsIgnoreCase(copiedDesc), "Resource has not being copied");

    }

    @Test(groups = "wso2.as", dependsOnMethods = "testCopyCollection")
    public void testFeedAfterCopying()
            throws ResourceAdminServiceExceptionException, IOException, XMLStreamException, XPathExpressionException {

        ResourceData[] rData = resourceAdminClient.getResource(COLL_COPIED_LOCATION + COLL_NAME_AFTER_COPYING);

        OMElement atomFeedOMElement =
                getAtomFeedContent(constructAtomUrl(COLL_COPIED_LOCATION + COLL_NAME_AFTER_COPYING));

        assertNotNull(atomFeedOMElement, "No feed data available");

        //checking whether the created time is correct
        OMElement createdElement = atomFeedOMElement.getFirstChildWithName(
                new QName(REGISTRY_NAMESPACE, "createdTime"));

        assertTrue(createdElement.getText().equalsIgnoreCase(getAtomDateString(rData[0].getCreatedOn().getTime())),
                   "Created time is incorrect after copying collection");
    }

    @Test(groups = "wso2.as", dependsOnMethods = "testFeedAfterCopying")
    public void testMoveCollection()
            throws ResourceAdminServiceExceptionException, RemoteException, InterruptedException {


        //move the resource
        resourceAdminClient.moveResource(PATH, PATH + COLL_NAME_AFTER_RENAME,
                                         COLL_MOVED_LOCATION, COLL_NAME_AFTER_MOVING);
        Thread.sleep(2000);


        //check that the collection has been moved
        String movedDesc =
                resourceAdminClient.getResource(COLL_MOVED_LOCATION + COLL_NAME_AFTER_MOVING)[0].getDescription();

        assertTrue(COLL_DESC.equalsIgnoreCase(movedDesc), "Resource has not being moved");
    }

    @Test(groups = "wso2.as", dependsOnMethods = "testMoveCollection")
    public void testFeedAfterMoving()
            throws ResourceAdminServiceExceptionException, IOException, XMLStreamException, XPathExpressionException {

        ResourceData[] rData = resourceAdminClient.getResource(COLL_MOVED_LOCATION + COLL_NAME_AFTER_MOVING);

        OMElement atomFeedOMElement =
                getAtomFeedContent(constructAtomUrl(COLL_MOVED_LOCATION + COLL_NAME_AFTER_MOVING));

        assertNotNull(atomFeedOMElement, "No feed data available");

        //checking whether the created time is correct
        OMElement createdElement = atomFeedOMElement.getFirstChildWithName(
                new QName(REGISTRY_NAMESPACE, "createdTime"));

        assertTrue(createdElement.getText().equalsIgnoreCase(getAtomDateString(rData[0].getCreatedOn().getTime())),
                   "Created time is incorrect after moving the collection");
    }

    @Test(groups = "wso2.as", dependsOnMethods = "testFeedAfterMoving", expectedExceptions = AxisFault.class)
    public void testDeleteCollection()
            throws ResourceAdminServiceExceptionException, RemoteException {

        resourceAdminClient.deleteResource(COLL_COPIED_LOCATION + COLL_NAME_AFTER_COPYING);
        resourceAdminClient.getResource(COLL_COPIED_LOCATION + COLL_NAME_AFTER_COPYING);

        resourceAdminClient.deleteResource(PATH + COLL_NAME_AFTER_RENAME);
        resourceAdminClient.getResource(PATH + COLL_NAME_AFTER_RENAME);

        resourceAdminClient.deleteResource(COLL_MOVED_LOCATION + COLL_NAME_AFTER_MOVING);
        resourceAdminClient.getResource(COLL_MOVED_LOCATION + COLL_NAME_AFTER_MOVING);

    }

    private String getAtomDateString(Date date) {
        AtomDate atomDate = new AtomDate(date);
        return atomDate.getValue();
    }

    private String constructAtomUrl(String feedPath) throws XPathExpressionException {

        String registryURL;

            registryURL = UrlGenerationUtil.getRemoteRegistryURL(asServer
                    .getDefaultInstance());

        return registryURL + "atom" + feedPath;
    }

    private OMElement getAtomFeedContent(String registryUrl) throws IOException,
            XMLStreamException, XPathExpressionException {
        StringBuilder sb;
        InputStream inputStream = null;
        BufferedReader reader = null;
        URL url = new URL(registryUrl);
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            String userPassword = asServer.getContextTenant().getContextUser().getUserName() + ":" + asServer.getContextTenant().getContextUser().getPassword();
            String encodedAuthorization = Base64Utils.encode(userPassword.getBytes(Charset.forName("UTF-8")));
            connection.setRequestProperty("Authorization", "Basic " +
                                                           encodedAuthorization);
            connection.connect();

            inputStream = connection.getInputStream();
            sb = new StringBuilder();
            String line;

            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } finally {
            assert inputStream != null;
            assert reader != null;
            reader.close();
            inputStream.close();
        }

        return AXIOMUtil.stringToOM(sb.toString());

    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws ResourceAdminServiceExceptionException, RemoteException {

        resourceAdminClient.deleteResource(PATH);
        resourceAdminClient.deleteResource(COLL_COPIED_LOCATION);

        resourceAdminClient = null;
    }
}

