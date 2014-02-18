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

package org.wso2.carbon.integration.test.sampleservices.chadservice;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.aar.services.AARServiceUploaderClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import java.io.File;
import java.rmi.RemoteException;

import static org.testng.Assert.assertTrue;

/**
 * This class uploads the Chad.aar verify deployment and invoke services
 */
public class ChadTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(ChadTestCase.class);
    private static OMFactory factory = OMAbstractFactory.getOMFactory();
    private static OMNamespace omNs = factory.createOMNamespace("http://www.wso2.org/types", "ns");
    private static AxisServiceClient axisServiceClient = new AxisServiceClient();
    private static String endpoint;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        endpoint = asServer.getServiceUrl() + "/Chad";
    }

    @AfterClass(alwaysRun = true)
    public void chadDelete() throws Exception {   // deletes Chad.aar
        deleteService("Chad");
        log.info("Chad service deleted");
    }

    @Test(groups = "wso2.as", description = "Upload Chad.aar service and verify deployment")
    public void chadServiceUpload() throws Exception {
        AARServiceUploaderClient aarServiceUploaderClient
                = new AARServiceUploaderClient(asServer.getBackEndUrl(),
                asServer.getSessionCookie());

        aarServiceUploaderClient.uploadAARFile("Chad.aar",
                ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        "Chad.aar", "");

        isServiceDeployed("Chad");
        log.info("Chad.aar service uploaded and deployed successfully");
    }

    @Test(groups = "wso2.as", description = "Login authorization", dependsOnMethods = "chadServiceUpload")
    public void login() throws AxisFault {
        OMElement response = axisServiceClient.sendReceive(createPayLoadForLoginAndAddAdmin("admin", "admin"),
                endpoint, "login");
        assertTrue(response.toString().equals("<ns:loginResponse xmlns:ns=\"http://www.wso2.org/types\">" +
                "<return>true</return></ns:loginResponse>"));
    }

    @Test(groups = "wso2.as", description = "Add new admin user", dependsOnMethods = "login",
            expectedExceptions = AxisFault.class)
    public void addAdminUser() throws RemoteException {
        axisServiceClient.sendReceive(createPayLoadForLoginAndAddAdmin("Billy", "password"), endpoint,
                "addAdminUser");
        // This operation sends a null response as the first response even though an admin user has been added successfully.
    }

    @Test(groups = "wso2.as", description = "Check added admin user availability",
            dependsOnMethods = "addAdminUser")
    public void listAdminUsers() throws RemoteException {
        OMElement responseAdminUserList = axisServiceClient.sendReceive(createPayLoadForResponseList(),
                endpoint, "listAdminUsers");
        assertTrue(responseAdminUserList.toString().contains("Billy"));
        // This is to check whether the addAdminUser() produces the expected output
    }

    @Test(groups = "wso2.as", description = "Creating a poll", dependsOnMethods = "listAdminUsers")
    public void createPoll() throws AxisFault {
        axisServiceClient.sendReceive(createPayLoadForCreatePoll(), endpoint, "createPoll");
        OMElement responsePollList = axisServiceClient.sendReceive(createPayLoadForResponseList(),
                endpoint, "listPolls");
        assertTrue(responsePollList.toString().contains("My first poll"));  // checking for poll description
    }

    private static OMElement createPayLoadForLoginAndAddAdmin(String userName, String password) {
        OMElement getOme = factory.createOMElement("login", omNs);
        OMElement getOmeUserName = factory.createOMElement("username", omNs);
        OMElement getOmePassword = factory.createOMElement("password", omNs);

        getOmeUserName.setText(userName); // input value for username
        getOme.addChild(getOmeUserName);
        getOmePassword.setText(password); // input value for password
        getOme.addChild(getOmePassword);
        return getOme;
    }

    private static OMElement createPayLoadForResponseList() {
        OMElement getOme;
        getOme = factory.createOMElement("login", omNs);
        return getOme;
    }

    private static OMElement createPayLoadForCreatePoll() {

        OMElement getOme = factory.createOMElement("login", omNs);
        OMElement getOmeTitle = factory.createOMElement("title", omNs);
        OMElement getOmeDescription = factory.createOMElement("description", omNs);
        OMElement getOmeIsSingleVote = factory.createOMElement("isSingleVote", omNs);
        OMElement getOmeFirstChoice = factory.createOMElement("choices", omNs);
        OMElement getOmeSecondChoice = factory.createOMElement("choices", omNs);

        getOmeTitle.setText("First poll"); //  title
        getOmeDescription.setText("My first poll"); // description
        getOmeIsSingleVote.setText("true");
        getOmeFirstChoice.setText("01"); // first choice
        getOmeSecondChoice.setText("02"); // second choice

        getOme.addChild(getOmeTitle);
        getOme.addChild(getOmeDescription);
        getOme.addChild(getOmeIsSingleVote);
        getOme.addChild(getOmeFirstChoice);
        getOme.addChild(getOmeSecondChoice);

        return getOme;
    }
}
