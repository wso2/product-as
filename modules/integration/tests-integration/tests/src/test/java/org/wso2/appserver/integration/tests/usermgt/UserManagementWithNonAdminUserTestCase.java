package org.wso2.appserver.integration.tests.usermgt;
/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*mi
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.user.mgt.stub.types.carbon.FlaggedName;

import javax.activation.DataHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;

import static org.testng.Assert.*;
/**
 * This class will test the functionality of the UserAdmin service in NonAdmin mode
 */
public class UserManagementWithNonAdminUserTestCase extends ASIntegrationTest {
	private static final String NON_ADMIN_EXCEPTION_MESSAGE = "Access Denied.";
	private static final String USER_CSV_FILE_NAME = "users.csv";
	private static final String TEST_ROLE_NAME = "testrole"; // this is defined in the automation.xml

	private String[] permissions = { "/permission/admin/login/" };
	private UserManagementClient userManagementClient;

	private TestUserMode userMode;
	private String username;
	private String password;

	@Factory(dataProvider = "userModeDataProvider")
	public UserManagementWithNonAdminUserTestCase(TestUserMode userMode) {
		this.userMode = userMode;
	}

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		//Since if we don't impose roles in the automation.xml all the users have admin privileges created 2 new users and a role and use that for TENANT and SUPER_TENANT users
		if (userMode == TestUserMode.SUPER_TENANT_USER) {
			super.init("superTenant", "userKey4");
		} else if (userMode == TestUserMode.TENANT_USER) {
			super.init("wso2.com", "user3");
		}
		userManagementClient = new UserManagementClient(backendURL, sessionCookie);
		initializeDefaultValues();
	}

	@DataProvider
	protected static TestUserMode[][] userModeDataProvider() {
		return new TestUserMode[][] { { TestUserMode.SUPER_TENANT_USER }, { TestUserMode.TENANT_USER } };
	}

	@Test(groups = "wso2.as", description = "Creating a new role", expectedExceptions = RemoteException.class,
			expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testCreateNewRole() throws Exception {
		String roleName = "TmpTestRole";
		userManagementClient.addRole(roleName, null, permissions);
		fail("Creating a new role with non admin user executed without an exception");
	}

	@Test(groups = "wso2.as", description = "Creating a new user", dependsOnMethods = { "testCreateNewRole" },
			expectedExceptions = RemoteException.class, expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testCreateNewUser() throws Exception {
		String tmpUserName = (userMode == TestUserMode.SUPER_TENANT_USER) ? "ST_Username" : "T_Username";
		String tmpPassword = (userMode == TestUserMode.SUPER_TENANT_USER) ? "ST_Password" : "T_Password";
		userManagementClient.addUser(tmpUserName, tmpPassword, null, tmpUserName);
		fail("Creating a new user with non admin user executed without an exception");
	}

	@Test(groups = "wso2.as", description = "Check if user exist", dependsOnMethods = { "testCreateNewUser" },
			expectedExceptions = RemoteException.class, expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testUserExist() throws Exception {
		userManagementClient.userNameExists(TEST_ROLE_NAME, username);
		fail("Non admin user able to check if user exist without an exception");
	}

	@Test(groups = "wso2.as", description = "Adding a role to user", dependsOnMethods = { "testUserExist" },
			expectedExceptions = RemoteException.class, expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testAddRoleToUser() throws Exception {
		userManagementClient.addRemoveRolesOfUser(username, new String[] { TEST_ROLE_NAME }, null);
		fail("Non admin user able to add a role to user");
	}

	@Test(groups = "wso2.as", description = "Login with new user", dependsOnMethods = { "testAddRoleToUser" })
	public void testLoginWithNewUser() throws Exception {
		String oldUsername = username;
		if (userMode == TestUserMode.TENANT_USER) {
			username += "@" + asServer.getContextTenant().getDomain();
		}
		String newSessionCookie =
				loginLogoutClient.login(username, password, asServer.getInstance().getHosts().get("default"));
		if (userMode == TestUserMode.TENANT_USER) {
			username = oldUsername;
		}
		assertNotNull(newSessionCookie, "Login failed while login as " + username);
	}

	@Test(groups = "wso2.as", description = "Removing a role from user", dependsOnMethods = { "testLoginWithNewUser" },
			expectedExceptions = RemoteException.class, expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testRemoveRoleFromUser() throws Exception {
		userManagementClient.addRemoveRolesOfUser(username, null, new String[] { TEST_ROLE_NAME });
		fail("Non admin user able to remove a role from a user");
	}

	@Test(groups = "wso2.as", description = "Add already existing user", dependsOnMethods = {
			"testRemoveRoleFromUser" }, expectedExceptions = RemoteException.class,
			expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testCreateExistingUser() throws Exception {
		userManagementClient.addUser(username, password, null, username);
		fail("Non admin user able to create existing user");
	}

	@Test(groups = "wso2.as", description = "Add already existing role", dependsOnMethods = {
			"testCreateExistingUser" }, expectedExceptions = RemoteException.class,
			expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testCreateExistingRole() throws Exception {
		userManagementClient.addRole(TEST_ROLE_NAME, null, permissions);
		fail("Non admin user able to create existing role");
	}

	@Test(groups = "wso2.as", description = "Get permissions of the Admin role",
			expectedExceptions = RemoteException.class, expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testGetPermissionsOfRole() throws Exception {
		userManagementClient.getRolePermissions(TEST_ROLE_NAME);
		fail("Non admin user able to get permission list of a role");
	}

	@Test(groups = "wso2.as", description = "Update role name", dependsOnMethods = { "testCreateNewRole" },
			expectedExceptions = RemoteException.class, expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testUpdateRoleName() throws Exception {
		String newRoleName = "new_" + TEST_ROLE_NAME;
		userManagementClient.updateRoleName(TEST_ROLE_NAME, newRoleName);
		fail("Non admin user able to update role name");
	}

	@Test(groups = "wso2.as", description = "Change password by username", dependsOnMethods = {
			"testLoginWithNewUser" }, expectedExceptions = RemoteException.class,
			expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testChangePasswordByUsername() throws Exception {
		String newPassword = "new_" + password;
		userManagementClient.changePassword(username, newPassword);
		fail("Non admin user able to change password by username");
	}

	@Test(groups = "wso2.as", description = "Deleting a user", dependsOnMethods = { "testAddRemoveUsersOfRole" },
			expectedExceptions = RemoteException.class, expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testDeleteUser() throws Exception {
		String tmpUserName = (userMode == TestUserMode.SUPER_TENANT_USER) ? "testu2" : "testuser21";
		userManagementClient.deleteUser(tmpUserName);
		fail("Non admin user able to delete a user");
	}

	@Test(groups = "wso2.as", description = "Deleting a role", dependsOnMethods = { "testDeleteUser" },
			expectedExceptions = RemoteException.class, expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testDeleteRole() throws Exception {
		userManagementClient.deleteRole(TEST_ROLE_NAME);
		fail("Non admin user able to delete a role");
	}

	@Test(groups = "wso2.as", description = "Upload users in bulk", expectedExceptions = RemoteException.class,
			expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testBulkUserUpload() throws Exception {
		Path filePath = Paths.get(FrameworkPathUtil.getSystemResourceLocation(), "artifacts", "AS", "usermgt",
		                          USER_CSV_FILE_NAME);
		DataHandler handler = new DataHandler(filePath.toUri().toURL());
		userManagementClient.bulkImportUsers(filePath.toString(), handler, "abc123");
		fail("Non admin user able to do bulk user import");
	}

	@Test(groups = "wso2.as", description = "Remove users of a role", dependsOnMethods = { "testCreateExistingRole" },
			expectedExceptions = RemoteException.class, expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testAddRemoveUsersOfRole() throws Exception {
		userManagementClient.addRemoveUsersOfRole(TEST_ROLE_NAME, null, new String[] { username });
		fail("Non admin user able to execute addRemoveUsersOfRole");
	}

	@Test(groups = "wso2.as", description = "Get roles of current user")
	public void testGetRolesOfCurrentUser() throws Exception {
		String checkingRoleName = "rolenonadmin";
		FlaggedName[] rolesOfUser = userManagementClient.getRolesOfCurrentUser();
		for (FlaggedName flaggedName : rolesOfUser) {
			if (checkingRoleName.equals(flaggedName.getItemName())) {
				assertEquals(true, flaggedName.getSelected(),
				             "Current user doesn't have " + checkingRoleName + " role");
				return;
			}
		}
		fail("Current user doesn't have " + checkingRoleName + " role");
	}

	@Test(groups = "wso2.as", description = "Update users of a role", dependsOnMethods = { "testCreateNewUser" },
			expectedExceptions = RemoteException.class, expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testUpdateUsersOfRole() throws Exception {
		FlaggedName flaggedNameUser = new FlaggedName();
		flaggedNameUser.setSelected(true);
		flaggedNameUser.setItemName(username);
		flaggedNameUser.setItemDisplayName(username);
		userManagementClient.updateUsersOfRole(TEST_ROLE_NAME, new FlaggedName[] { flaggedNameUser });
		fail("Non admin user able update roles of users");
	}

	@Test(groups = "wso2.as", description = "Get All UI permissions", expectedExceptions = RemoteException.class,
			expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testAllUIGetPermissions() throws Exception {
		userManagementClient.getAllUIPermissions();
		fail("Non admin user able to get all UI permissions");
	}

	@Test(groups = "wso2.as", description = "Get list of users and check if that is equal to the no of users define in automation.xml",
			expectedExceptions = RemoteException.class, expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testListAllUsers() throws Exception {
		userManagementClient.listAllUsers("", 10);
		fail("Non admin user able to get list of users");
	}

	@Test(groups = "wso2.as", description = "Adding a new internal role", expectedExceptions = RemoteException.class,
			expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testAddCreateNewRole() throws Exception {
		String internalRole = "testInternalRole";
		userManagementClient.addInternalRole(internalRole, null, permissions);
		fail("Non admin user able to add internal role");
	}

	@Test(groups = "wso2.as", description = "Get all shared role names", expectedExceptions = RemoteException.class,
			expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testGetAllSharedRoles() throws Exception {
		userManagementClient.getAllSharedRoleNames("*", 10);
		fail("Non admin user able to get all shared roles");
	}

	@Test(groups = "wso2.as", description = "Check if multiple users stores available",
			expectedExceptions = RemoteException.class, expectedExceptionsMessageRegExp = NON_ADMIN_EXCEPTION_MESSAGE)
	public void testIsSharedRolesAvailable() throws Exception {
		userManagementClient.isSharedRolesEnabled();
		fail("Non admin user able to check if shared roles available");
	}

	private void initializeDefaultValues() {
		switch (userMode) {
			case SUPER_TENANT_USER:
				username = "testu4";
				password = "testu4pass";
				break;
			case TENANT_USER:
				username = "testuser31";
				password = "testuser31";
				break;
		}
	}
}
