package org.wso2.appserver.integration.tests.usermgt;
/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
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

import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;
import org.wso2.carbon.user.mgt.stub.types.carbon.FlaggedName;
import org.wso2.carbon.user.mgt.stub.types.carbon.UIPermissionNode;

import javax.activation.DataHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;

import static org.testng.Assert.*;

/**
 * This class will test the functionality of the UserAdmin service with Admin user
 */
public class UserManagementWithAdminUserTestCase extends ASIntegrationTest {
	private static final String USER_CSV_FILE_NAME = "users.csv";
	private static final String TEST_ROLE_NAME = "testrole"; // this is defined in the automation.xml
	private static final String TEST_NON_ADMIN_ROLE_NAME = "rolenonadmin"; // this is defined in the automation.xml

	private String[] permissions = { "/permission/admin/login" };
	private UserManagementClient userManagementClient;

	private TestUserMode userMode;
	private String username;
	private String password;
	private String roleName;

	@Factory(dataProvider = "userModeDataProvider")
	public UserManagementWithAdminUserTestCase(TestUserMode userMode) {
		this.userMode = userMode;
	}

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		super.init(userMode);
		userManagementClient = new UserManagementClient(backendURL, sessionCookie);
		initializeDefaultValues();
	}

	@DataProvider
	protected static TestUserMode[][] userModeDataProvider() {
		return new TestUserMode[][] { { TestUserMode.SUPER_TENANT_ADMIN }, { TestUserMode.TENANT_ADMIN } };
	}

	@Test(groups = "wso2.as", description = "Adding a new role")
	public void testCreateNewRole() throws Exception {
		assertFalse(userManagementClient.roleNameExists(roleName), "Role already exists");
		userManagementClient.addRole(roleName, null, permissions);
		assertTrue(userManagementClient.roleNameExists(roleName), "Role creation failed");
	}

	@Test(groups = "wso2.as", description = "Adding a new user", dependsOnMethods = { "testCreateNewRole" })
	public void testCreateNewUser() throws Exception {
		assertFalse(userManagementClient.userNameExists("", username), "User already exists");
		userManagementClient.addUser(username, password, null, username);
		assertTrue(userManagementClient.userNameExists("", username), "User creation failed");
	}

	@Test(groups = "wso2.as", description = "Check if user exist", dependsOnMethods = { "testCreateNewUser" })
	public void testUserExist() throws Exception {
		assertTrue(userManagementClient.userNameExists(roleName, username), "Created username doesn't exist");
	}

	@Test(groups = "wso2.as", description = "Adding a role to user", dependsOnMethods = { "testUserExist" })
	public void testAddRoleToUser() throws Exception {
		FlaggedName[] rolesOfUser = userManagementClient.getRolesOfUser(username, roleName, 10);
		for (FlaggedName flaggedName : rolesOfUser) {
			if (roleName.equals(flaggedName.getItemName())) {
				assertFalse(flaggedName.getSelected(), "User already has this role");
				break;
			}
		}

		userManagementClient.addRemoveRolesOfUser(username, new String[] { roleName }, null);
		rolesOfUser = userManagementClient.getRolesOfUser(username, roleName, 10);
		for (FlaggedName flaggedName : rolesOfUser) {
			if (roleName.equals(flaggedName.getItemName())) {
				assertTrue(flaggedName.getSelected(), "Assign role failed");
				return;
			}
		}
		fail("Assign role failed");
	}

	@Test(groups = "wso2.as", description = "Login with new user", dependsOnMethods = { "testUpdateRoleName" })
	public void testLoginWithNewUser() throws Exception {
		String oldUsername = username;
		if (userMode == TestUserMode.TENANT_ADMIN) {
			username += "@" + asServer.getContextTenant().getDomain();
		}
		String newSessionCookie =
				loginLogoutClient.login(username, password, asServer.getInstance().getHosts().get("default"));
		assertNotNull(newSessionCookie, "Can't login with new user");
		if (userMode == TestUserMode.TENANT_ADMIN) {
			username = oldUsername;
		}
	}

	@Test(groups = "wso2.as", description = "Removing a role from user", dependsOnMethods = { "testLoginWithNewUser" })
	public void testRemoveRoleFromUser() throws Exception {
		FlaggedName[] rolesOfUser = userManagementClient.getRolesOfUser(username, roleName, 10);
		for (FlaggedName flaggedName : rolesOfUser) {
			if (roleName.equals(flaggedName.getItemName())) {
				assertTrue(flaggedName.getSelected(), "User don't have this role");
				break;
			}
		}

		userManagementClient.addRemoveRolesOfUser(username, null, new String[] { roleName });
		rolesOfUser = userManagementClient.getRolesOfUser(username, roleName, 10);
		for (FlaggedName flaggedName : rolesOfUser) {
			if (roleName.equals(flaggedName.getItemName())) {
				assertFalse(flaggedName.getSelected(), "Remove role failed");
				return;
			}
		}
		fail("Remove role failed");
	}

	@Test(groups = "wso2.as", description = "Add already existing user", dependsOnMethods = {
			"testRemoveRoleFromUser" }, expectedExceptions = UserAdminUserAdminException.class,
			expectedExceptionsMessageRegExp = "UserAdminUserAdminException")
	public void testCreateExistingUser() throws Exception {
		userManagementClient.addUser(username, password, null, username);
	}

	@Test(groups = "wso2.as", description = "Add already existing role", dependsOnMethods = {
			"testCreateExistingUser" }, expectedExceptions = RemoteException.class,
			expectedExceptionsMessageRegExp = "Role name:.*in the system. Please pick another role name.")
	public void testCreateExistingRole() throws Exception {
		userManagementClient.addRole(roleName, null, permissions);
	}

	@Test(groups = "wso2.as", description = "Get permissions of the Admin role")
	public void testGetPermissionsOfRole() throws Exception {
		UIPermissionNode rolePermissions = userManagementClient.getRolePermissions(TEST_NON_ADMIN_ROLE_NAME);
		UIPermissionNode loginPermissionNode;
		if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
			loginPermissionNode = rolePermissions.getNodeList()[0].getNodeList()[1];
		} else {
			loginPermissionNode = rolePermissions.getNodeList()[1];
		}
		assertEquals(loginPermissionNode.getResourcePath(), "/permission/admin/login",
		             "/permission/admin/login/ in not in the requested position");
		assertTrue(loginPermissionNode.getSelected(),
		           "/permission/admin/login/ is not available for " + TEST_NON_ADMIN_ROLE_NAME);
	}

	@Test(groups = "wso2.as", description = "Get list of users and check if that is equal to the no of users define in automation.xml")
	public void testListAllUsers() throws Exception {
		FlaggedName[] listOfUsers = userManagementClient.listAllUsers("*", 10);
		// Get user count defined in automation.xml + last element is false when call through the UserAdminClient
		if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
			assertTrue(listOfUsers.length >= 6, "User count is differ from the expected count");
		} else {
			assertTrue(listOfUsers.length >= 5, "User count is differ from the expected count");
		}
	}

        //TODO: Fix this test case - WSAS-2077
	@Test(groups = "wso2.as", description = "Change password of current user defined in automation.xml", enabled = false)
	public void testChangePasswordOfCurrentUser() throws Exception {
		String oldPassword = userInfo.getPassword();
		String newPassword = "admin123";
		String loggedInUsername = "admin";
		userManagementClient.changePasswordByUser(oldPassword, newPassword);
		// Try to login with new password
		if (userMode == TestUserMode.TENANT_ADMIN) {
			loggedInUsername += "@" + asServer.getContextTenant().getDomain();
		}
		loginLogoutClient = new LoginLogoutClient(asServer);
		assertNotNull(loginLogoutClient
				              .login(loggedInUsername, newPassword, asServer.getInstance().getHosts().get("default")),
		              "Couldn't login with new password");

		//try to login with old password
		Exception ex = null;
		try {
			assertNull(loginLogoutClient
					           .login(loggedInUsername, oldPassword, asServer.getInstance().getHosts().get("default")),
			           "Couldn't login with new password");
		} catch (Exception e) {
			ex = e;
		}
		assertNotNull(ex, "User able to login to system using old password");
		assertEquals(ex.getMessage(), "Error while login as " + loggedInUsername,
		             "User able to login to system using old password");

		//Revert back to old password
		userManagementClient.changePasswordByUser(newPassword, oldPassword);
		loginLogoutClient = new LoginLogoutClient(asServer);
		assertNotNull(loginLogoutClient
				              .login(loggedInUsername, oldPassword, asServer.getInstance().getHosts().get("default")),
		              "Failed to revert old password");
	}

	@Test(groups = "wso2.as", description = "Update role name", dependsOnMethods = { "testAddRoleToUser" })
	public void testUpdateRoleName() throws Exception {
		String newRoleName = "new_" + roleName;
		userManagementClient.updateRoleName(roleName, newRoleName);
		assertTrue(userManagementClient.roleNameExists(newRoleName), "Role rename failed");

		FlaggedName[] rolesOfUser = userManagementClient.getRolesOfUser(username, "*", 10);
		for (FlaggedName flaggedName : rolesOfUser) {
			if (roleName.equals(flaggedName.getItemName())) {
				assertFalse(flaggedName.getSelected(), "User still has previous role name");
			} else if (newRoleName.equals(flaggedName.getItemName())) {
				assertTrue(flaggedName.getSelected(), "User don't have renamed role");
			}
		}
		roleName = newRoleName;
	}

	@Test(groups = "wso2.as", description = "Change password by username", dependsOnMethods = {
			"testLoginWithNewUser" })
	public void testChangePasswordByUsername() throws Exception {
		String newPassword = "new_" + password;
		userManagementClient.changePassword(username, newPassword);
		String oldUsername = username;
		if (userMode == TestUserMode.TENANT_ADMIN) {
			username += "@" + asServer.getContextTenant().getDomain();
		}
		String newSessionCookie =
				loginLogoutClient.login(username, newPassword, asServer.getInstance().getHosts().get("default"));
		assertNotNull(newSessionCookie, "Can't login after change password");

		//try to login with old password
		Exception ex = null;
		try {
			assertNull(loginLogoutClient.login(username, password, asServer.getInstance().getHosts().get("default")),
			           "Couldn't login with new password");
		} catch (Exception e) {
			ex = e;
		}
		assertNotNull(ex, "User able to login to system using old password");
		assertEquals(ex.getMessage(), "Error while login as " + username,
		             "User able to login to system using old password");

		if (userMode == TestUserMode.TENANT_ADMIN) {
			username = oldUsername;
		}
		password = newPassword;
	}

	@Test(groups = "wso2.as", description = "Deleting a user", dependsOnMethods = { "testAddRemoveUsersOfRole" })
	public void testDeleteUser() throws Exception {
		assertTrue(userManagementClient.userNameExists("", username), "User doesn't exist");
		userManagementClient.deleteUser(username);
		assertFalse(userManagementClient.userNameExists("", username), "User deletion failed");
	}

	@Test(groups = "wso2.as", description = "Deleting a role", dependsOnMethods = { "testDeleteUser" })
	public void testDeleteRole() throws Exception {
		assertTrue(userManagementClient.roleNameExists(roleName), "Role doesn't exist");
		userManagementClient.deleteRole(roleName);
		assertFalse(userManagementClient.roleNameExists(roleName), "Role deletion failed");
	}

	@Test(groups = "wso2.as", description = "Upload users in bulk")
	public void testBulkUserUpload() throws Exception {
		Path filePath = Paths.get(FrameworkPathUtil.getSystemResourceLocation(), "artifacts", "AS", "usermgt",
		                          USER_CSV_FILE_NAME);
		DataHandler handler = new DataHandler(filePath.toUri().toURL());
		userManagementClient.bulkImportUsers(filePath.toString(), handler, "abc123");
		ArrayList<String> users = (ArrayList<String>) FileUtils.readLines(filePath.toFile());
		users.remove(0); // Remove the username
		HashSet<String> userList = userManagementClient.getUserList();
		for (String user : users) {
			assertTrue(userList.contains(user), "Username " + user + " doesn't exist");
			//assertNotNull(loginLogoutClient.login(user, "abc123", asServer.getInstance().getHosts().get("default")));
			userManagementClient.deleteUser(user);
		}
	}

	@Test(groups = "wso2.as", description = "Remove users of a role", dependsOnMethods = { "testCreateExistingRole" })
	public void testAddRemoveUsersOfRole() throws Exception {
		FlaggedName[] rolesOfUser = userManagementClient.getRolesOfUser(username, TEST_ROLE_NAME, 10);
		for (FlaggedName flaggedName : rolesOfUser) {
			if (TEST_ROLE_NAME.equals(flaggedName.getItemName())) {
				assertTrue(flaggedName.getSelected(), "User doesn't have this role");//TOD prnt username + role name
				break;
			}
		}

		userManagementClient.addRemoveUsersOfRole(TEST_ROLE_NAME, null, new String[] { username });
		rolesOfUser = userManagementClient.getRolesOfUser(username, TEST_ROLE_NAME, 10);
		for (FlaggedName flaggedName : rolesOfUser) {
			if (TEST_ROLE_NAME.equals(flaggedName.getItemName())) {
				assertFalse(flaggedName.getSelected(), "Remove role " + TEST_ROLE_NAME + " failed");
				return;
			}
		}
		fail("Remove role " + TEST_ROLE_NAME + " failed");
	}

	@Test(groups = "wso2.as", description = "Get roles of current user")
	public void testGetRolesOfCurrentUser() throws Exception {
		String checkingRoleName = "admin";
		FlaggedName[] rolesOfUser = userManagementClient.getRolesOfCurrentUser();
		for (FlaggedName flaggedName : rolesOfUser) {
			if (checkingRoleName.equals(flaggedName.getItemName())) {
				assertTrue(flaggedName.getSelected(), "Current user doesn't have " + checkingRoleName + " role");
				return;
			}
		}
		fail("Current user doesn't have " + checkingRoleName + " role");
	}

	@Test(groups = "wso2.as", description = "Update users of a role", dependsOnMethods = { "testCreateNewUser" })
	public void testUpdateUsersOfRole() throws Exception {
		FlaggedName flaggedName = new FlaggedName();
		flaggedName.setSelected(true);
		flaggedName.setItemName(username);
		flaggedName.setItemDisplayName(username);
		userManagementClient.updateUsersOfRole(TEST_ROLE_NAME, new FlaggedName[] { flaggedName });

		FlaggedName[] rolesOfUser = userManagementClient.getRolesOfUser(username, TEST_ROLE_NAME, 10);
		for (FlaggedName role : rolesOfUser) {
			if (TEST_ROLE_NAME.equals(role.getItemName())) {
				assertTrue(role.getSelected(), "Assigning role " + TEST_ROLE_NAME + " failed");
				return;
			}
		}
		fail("Assigning role " + TEST_ROLE_NAME + " failed");
	}

	@Test(groups = "wso2.as", description = "Adding a new internal role")
	public void testAddNewInternalRole() throws Exception {
		String internalRole = "testInternalRole";
		assertFalse(userManagementClient.roleNameExists("Internal/" + internalRole),
		            "Internal role " + internalRole + "already exists");
		userManagementClient.addInternalRole(internalRole, null, permissions);
		assertTrue(userManagementClient.roleNameExists("Internal/" + internalRole), "Internal role creation failed");
	}

	@Test(groups = "wso2.as", description = "Check if shared roles is enable")
	public void testIsSharedRolesEnable() throws Exception {
		assertFalse(userManagementClient.isSharedRolesEnabled(), "Shared roles is enable by default");
	}

	@Test(groups = "wso2.as", description = "Add role to a user using updateRolesOfUser")
	public void testUpdateRolesOfUser() throws Exception {
		String userName = (userMode == TestUserMode.SUPER_TENANT_ADMIN) ? "testu3" : "testuser21";
		userManagementClient
				.updateRolesOfUser(userName, new String[] { TEST_NON_ADMIN_ROLE_NAME, "Internal/everyone" });
		FlaggedName[] rolesOfUser = userManagementClient.getRolesOfUser(userName, "*", 10);
		boolean isOtherRolesAvailable = false;
		boolean isAssignedRolesAvailable = true;
		for (FlaggedName flaggedName : rolesOfUser) {
			if (TEST_NON_ADMIN_ROLE_NAME.equals(flaggedName.getItemName())) {
				assertTrue(flaggedName.getSelected(), "Updating roles of users failed");
				isAssignedRolesAvailable &= flaggedName.getSelected();
				continue;
			}else if ("Internal/everyone".equals(flaggedName.getItemName())) {
				assertTrue(flaggedName.getSelected(), "User don't have Internal/everyone role");
				isAssignedRolesAvailable &= flaggedName.getSelected();
				continue;
			}
			isOtherRolesAvailable |= flaggedName.getSelected();
		}
		if(isOtherRolesAvailable || !isAssignedRolesAvailable) {
			fail("Assign role failed");
		}
	}

	private void initializeDefaultValues() {
		switch (userMode) {
			case SUPER_TENANT_ADMIN:
				username = "STA_testuser";
				password = "STA_testuser";
				roleName = "STA_testuserrole";
				break;
			case TENANT_ADMIN:
				username = "TA_testuser";
				password = "TA_testuser";
				roleName = "TA_testuserrole";
				break;
		}
	}
}
