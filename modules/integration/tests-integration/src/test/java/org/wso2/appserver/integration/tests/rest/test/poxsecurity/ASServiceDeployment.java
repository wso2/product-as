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

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.tests.ASTestConstants;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

public class ASServiceDeployment extends ASIntegrationTest {
    private String[] fileNames =
            {"StudentService.aar", "Axis2Service.aar", "SimpleStockQuoteService.aar"};
    private UserManagementClient userManagementClient;
    private String userName = ASTestConstants.POX_USER;
    private String password = ASTestConstants.POX_USER_PASSWORD;
    private String roleName = ASTestConstants.POX_ROLE_NAME;

    @BeforeTest(alwaysRun = true)
    public void testDeployService() throws Exception {
        super.init();
        for (String fileName : fileNames) {
            String studentServiceFilePath = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" +
                                            File.separator + "AS" + File.separator + "aar" +
                                            File.separator + fileName;
            deployAarService(fileName.replace(".aar", ""), fileName, studentServiceFilePath, "");
        }
        createUserAndRole();
    }

    @AfterTest(alwaysRun = true)
    public void testUnDeployService() throws RemoteException, UserAdminUserAdminException {
        for (String fileName : fileNames) {
            deleteService(fileName.replace(".aar", ""));
        }
        deleteUserAndRole();
    }

    private void createUserAndRole() throws XPathExpressionException, IOException,
            LoginAuthenticationExceptionException, UserAdminUserAdminException, URISyntaxException,
            SAXException, XMLStreamException {
        LoginLogoutClient loginLogoutClient = new LoginLogoutClient(asServer);
        userManagementClient = new UserManagementClient(asServer.getContextUrls().getBackEndUrl(),
                loginLogoutClient.login());

        String[] permissions = {"/permission/admin/configure/",
                "/permission/admin/login",
                "/permission/admin/manage/",
                "/permission/admin/monitor",
                "/permission/protected"};

        userManagementClient.addUser(userName, password,null, null);
        userManagementClient.addRole(roleName, new String [] {userName}, permissions);
    }

    private void deleteUserAndRole() throws RemoteException, UserAdminUserAdminException {
        userManagementClient.deleteRole(roleName);
        userManagementClient.deleteUser(userName);

    }

}
