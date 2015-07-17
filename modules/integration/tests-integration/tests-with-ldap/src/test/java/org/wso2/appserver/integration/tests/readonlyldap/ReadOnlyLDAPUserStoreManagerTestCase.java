/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.appserver.integration.tests.readonlyldap;

import org.apache.axis2.AxisFault;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;
import org.wso2.carbon.user.mgt.stub.types.carbon.FlaggedName;

import java.io.File;

/**
 * This test class will test the Application Server with Read Only Ldap server as the primary user store
 */
public class ReadOnlyLDAPUserStoreManagerTestCase extends ASIntegrationTest {

    private ServerConfigurationManager scm;
    private UserManagementClient userMgtClient;
    private AuthenticatorClient authenticatorClient;
    private final String newUserName = "ReadOnlyLDAPUserName";
    private final String newUserRole = "ReadOnlyLDAPUserRole";
    private final String newUserPassword = "ReadOnlyLDAPUserPass";

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @BeforeClass(alwaysRun = true)
    public void configureServer() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        userMgtClient = new UserManagementClient(backendURL, sessionCookie);
        authenticatorClient = new AuthenticatorClient(backendURL);
        //Populate roles and users in to ReadWrite Ldap
        userMgtClient.addRole(newUserRole, null, new String[]{"/permission/admin/login"});
        userMgtClient.addUser(newUserName, newUserPassword, new String[]{newUserRole}, null);
        Assert.assertTrue(userMgtClient.roleNameExists(newUserRole), "Role name doesn't exists");
        Assert.assertTrue(userMgtClient.userNameExists(newUserRole, newUserName), "User name doesn't exists");

        String newUserSessionCookie = authenticatorClient.login(newUserName
                , newUserPassword, asServer.getInstance().getHosts().get("default"));
        Assert.assertTrue(newUserSessionCookie.contains("JSESSIONID"), "Session Cookie not found. Login failed");
        authenticatorClient.logOut();

        File userMgtConfigFile = new File(TestConfigurationProvider.getResourceLocation("AS")
                                          + File.separator + "configs" + File.separator
                                          + "readonlyldap" + File.separator + "user-mgt.xml");

        scm = new ServerConfigurationManager(asServer);
        //Enable ReadOnly User Store in user-mgt.xml
        scm.applyConfiguration(userMgtConfigFile);
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        userMgtClient = new UserManagementClient(backendURL, sessionCookie);
    }

    @Test(groups = "wso2.as", description = "Test login of a user already exist in the ReadOnly ldap")
    public void userLoginTest() throws Exception {
        String userSessionCookie = authenticatorClient.login(newUserName, newUserPassword
                , asServer.getInstance().getHosts().get("default"));
        Assert.assertTrue(userSessionCookie.contains("JSESSIONID"), "Session Cookie not found. Login failed user "
                                                                + newUserName);
        authenticatorClient.logOut();
    }

    @Test(groups = "wso2.as", description = "Getting users of a role")
    public void getUsersOfRoleTest() throws Exception {
        Assert.assertTrue(nameExists(userMgtClient.getUsersOfRole(newUserRole, newUserName, 10), newUserName)
                , "List does not contains the expected user name");
    }

    @Test(groups = "wso2.as", description = "Get roles of a particular user")
    public void getRolesOfUser() throws Exception {
        Assert.assertTrue(nameExists(userMgtClient.getRolesOfUser(newUserName, newUserRole, 10), newUserRole)
                , "List does not contains the expected role name");
    }

    @Test(groups = "wso2.as", description = "get all the roles in ldap")
    public void getAllRolesNamesTest() throws Exception {
        Assert.assertTrue(userMgtClient.getAllRolesNames("*", 10).length >= 1, "No role listed in Ldap");
    }

    @Test(groups = "wso2.as", description = "Check new role addition failure in readonly Ldap"
            , expectedExceptions = AxisFault.class
            , expectedExceptionsMessageRegExp = "Read only user store or Role creation is disabled")
    public void testAddNewRole() throws Exception {
        Assert.assertFalse(nameExists(userMgtClient.getAllRolesNames(newUserRole + "1", 100), newUserRole + "1")
                , "User Role trying to add already exist");
       userMgtClient.addRole(newUserRole + "1", null, new String[]{"login"}, false);
       Assert.assertFalse(nameExists(userMgtClient.getAllRolesNames(newUserRole + "1", 100), newUserRole + "1")
                , "Role creation success. New role must not be allowed to add in ReadOnly Ldap");

    }

    @Test(groups = "wso2.as", description = "Check new user addition failure in readonly Ldap",
          expectedExceptions = UserAdminUserAdminException.class, expectedExceptionsMessageRegExp =
            "UserAdminUserAdminException")
    public void addNewUserTest() throws Exception {
        userMgtClient.addUser(newUserName + "1", newUserPassword, new String[]{newUserRole}, null);
        Assert.assertFalse(userMgtClient.userNameExists(newUserName, newUserRole),"New user must not" +
                                                                                  " be allowed to add in ReadOnly Ldap");
    }

    @Test(groups = "wso2.as", description = "Check update role name failure", expectedExceptions =
            AxisFault.class, expectedExceptionsMessageRegExp =
            "Read-only UserStoreManager. Roles cannot be added or modified.")
    public void updateRoleNameTest() throws Exception {
        String updatedUserRole =  newUserRole + "updated";
        userMgtClient.updateRoleName(newUserRole, updatedUserRole);
        Assert.assertFalse(nameExists(userMgtClient.getAllRolesNames(newUserRole + "1", 100), updatedUserRole)
                , "Role has been updated. New role must not be allowed to update in ReadOnly Ldap");
    }

    @Test(groups = "wso2.as", description = "Check update users of role failure", expectedExceptions =
            UserAdminUserAdminException.class, expectedExceptionsMessageRegExp = "UserAdminUserAdminException")
    public void updateUsersOfRoleTest() throws Exception {

        String[] userList = new String[]{newUserName};
        FlaggedName[] userFlagList = new FlaggedName[userList.length];

        for (int i = 0; i < userFlagList.length; i++) {
            FlaggedName flaggedName = new FlaggedName();
            flaggedName.setItemName(userList[i]);
            flaggedName.setSelected(true);
            userFlagList[i] = flaggedName;
        }
        userMgtClient.updateUsersOfRole(asServer.getSuperTenant().getTenantAdmin().getUserName(), userFlagList);
        Assert.fail("Roles of user must not be allowed to add in ReadOnly Ldap");

    }

    @Test(groups = "wso2.as", description = "Check add remove roles of user failure", expectedExceptions =
            AxisFault.class, expectedExceptionsMessageRegExp = "Error occurred while getting" +
                                                                                 " database type from DB connection")
    public void addRemoveRolesOfUserTest() throws Exception {

        String[] newRoles = new String[]{FrameworkConstants.ADMIN_ROLE};
        String[] deletedRoles = new String[]{newUserRole};
        userMgtClient.addRemoveRolesOfUser(newUserName, newRoles, deletedRoles);
        Assert.fail("Roles of user must not be allowed to remove in ReadOnly Ldap");
    }

    @Test(groups = "wso2.as", description = "Check add remove users of role failure", expectedExceptions =
            AxisFault.class, expectedExceptionsMessageRegExp = "Read-only user store.Roles cannot be added or modfified")
    public void addRemoveUsersOfRoleTest() throws Exception {

        String[] newUsers = new String[]{asServer.getSuperTenant().getTenantAdmin().getUserName()};
        String[] deletedUsers = new String[]{newUserName};
        //https://wso2.org/jira/browse/IDENTITY-3433
        userMgtClient.addRemoveUsersOfRole(newUserRole, newUsers, deletedUsers);
        Assert.fail("User roles must not be allowed to remove in ReadOnly Ldap");
    }

    @Test(groups = "wso2.as", description = "Listing all available users")
    public void listAllUsersTest() throws Exception {
        FlaggedName[] userList = userMgtClient.listAllUsers("*", 100);
        Assert.assertTrue(userList.length > 0, "List all users return empty list");
        Assert.assertTrue(nameExists(userList, newUserName), "User Not Exist in the user list");
    }

    @Test(groups = "wso2.as", description = "Check list users")
    public void listUsersTest() throws Exception {
        String[] usersList = userMgtClient.listUsers("*", 100);
        Assert.assertNotNull(usersList, "UserList null");
        Assert.assertTrue(usersList.length > 0, "List users return empty list");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @AfterClass(alwaysRun = true)
    public void restoreServer() throws Exception {

        scm.restoreToLastConfiguration();
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        userMgtClient = new UserManagementClient(backendURL, sessionCookie);
        if (nameExists(userMgtClient.listAllUsers(newUserName, 10), newUserName)) {
            userMgtClient.deleteUser(newUserName);
        }
        if (userMgtClient.roleNameExists(newUserRole)) {
            userMgtClient.deleteRole(newUserRole);
        }
    }

    /**
     * Check where given input is existing on FlaggedName array
     * @param allNames FlaggedName array
     * @param inputName input string to search
     * @return true if input name exist, else false
     */
    private boolean nameExists(FlaggedName[] allNames, String inputName) {
        boolean exists = false;

        for (FlaggedName flaggedName : allNames) {
            String name = flaggedName.getItemName();

            if (name.equals(inputName)) {
                exists = true;
                break;
            } else {
                exists = false;
            }
        }

        return exists;
    }

}
