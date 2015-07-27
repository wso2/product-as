/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.appserver.integration.tests.usermgt.profile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.UserProfileMgtServiceClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.identity.user.profile.stub.types.UserFieldDTO;
import org.wso2.carbon.identity.user.profile.stub.types.UserProfileAdmin;
import org.wso2.carbon.identity.user.profile.stub.types.UserProfileDTO;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * User profile management related functionality will be tested.
 * A user will be created and the user's profile related operations are tested
 * Here the admin service exposed operations are tested and
 * default and custom user profiles update operation also tested using setUserProfile
 */
public class UserProfileMgtTestCase extends ASIntegrationTest {

    private final Log log = LogFactory.getLog(UserProfileMgtTestCase.class);
    private TestUserMode userMode;
    private UserProfileMgtServiceClient userProfileMgtClient;
    private UserManagementClient userMgtClient;

    private final String defaultProfileName = "default";
    private final String testUser = "UserProfileMgt_User";
    private final String testUserProfile = "UserProfileMgt_UserProfile";
    private final String testUserProfileConfiguration = "default";
    private final String[] testUserRoles = { FrameworkConstants.ADMIN_ROLE };
    private final String testUserProfileLastName = "NewProfileLastName";
    private final String testUserProfileGivenName = "NewProfileGivenName";
    private final String testUserProfileEmailAddress = "newprofile@mymail.com";

    @Factory(dataProvider = "userModeDataProvider")
    public UserProfileMgtTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    public static Object[][] userModeDataProvider() {
        return new Object[][] { new Object[] { TestUserMode.SUPER_TENANT_ADMIN },
                new Object[] { TestUserMode.TENANT_ADMIN }, };
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {

        super.init(userMode);
        userProfileMgtClient = new UserProfileMgtServiceClient(backendURL, sessionCookie);
        userMgtClient = new UserManagementClient(backendURL, sessionCookie);

        char[] testUserPassword = { 'p', 'a', 's', 's', 'W', 'o', 'r', 'd', '1' };

        userMgtClient.addUser(testUser, String.valueOf(testUserPassword), testUserRoles, testUserProfileConfiguration);

        assertTrue(userMgtClient.userNameExists(testUserRoles[0], testUser), "Add user operation failed");

    }

    @Test(groups = "wso2.as", description = "Check whether add profile is enabled")
    public void testIsAddProfileEnabled() throws Exception {

        boolean isAddProfileEnabled = userProfileMgtClient.isAddProfileEnabled();
        assertTrue(isAddProfileEnabled, "Add profile is disabled");
    }

    @Test(groups = "wso2.as", description = "Check whether add profile is disabled for domain",
            dependsOnMethods = "org.wso2.appserver.integration.tests.usermgt.profile.UserProfileMgtTestCase." +
                    "testIsAddProfileEnabled")
    public void testIsAddProfileEnabledForDomain() throws Exception {

        boolean isAddProfileEnabledForDomain = userProfileMgtClient
                .isAddProfileEnabledForDomain(asServer.getContextTenant().getDomain());
        assertFalse(isAddProfileEnabledForDomain, "Add profile is enabled for domain");
    }

    @Test(groups = "wso2.as", description = "Check whether UserStore is not read only",
            dependsOnMethods = "org.wso2.appserver.integration.tests.usermgt.profile.UserProfileMgtTestCase." +
                    "testIsAddProfileEnabledForDomain")
    public void testIsReadOnlyUserStore() throws Exception {

        boolean isReadOnlyUserStore = userProfileMgtClient.isReadOnlyUserStore();
        assertFalse(isReadOnlyUserStore, "User Store is read only");
    }

    @Test(groups = "wso2.as", description = "Check add new user profile",
            dependsOnMethods = "org.wso2.appserver.integration.tests.usermgt.profile.UserProfileMgtTestCase." +
                    "testIsReadOnlyUserStore")
    public void testAddNewUserProfile() throws Exception {

        // Create another user profile than the default profile for the same user
        UserProfileDTO userProfileDTO = createUserProfile(testUserProfile, testUserProfileLastName,
                testUserProfileGivenName, testUserProfileEmailAddress);

        userProfileMgtClient.setUserProfile(testUser, userProfileDTO);

        UserProfileDTO userProfile = userProfileMgtClient.getUserProfile(testUser, testUserProfile);

        assertEquals(userProfile.getProfileName(), testUserProfile, "Add new user profile failed");

        log.info(" ----- " + testUser + " profile created " + testUserProfile + " / " + testUserProfileGivenName +
                " " + testUserProfileLastName + " / " + testUserProfileEmailAddress + " at " +
                asServer.getContextTenant().getDomain());

    }

    @Test(groups = "wso2.as", description = "Check get user profile",
            dependsOnMethods = "org.wso2.appserver.integration.tests.usermgt.profile.UserProfileMgtTestCase." +
                    "testAddNewUserProfile")
    public void testGetUserProfileForGivenUser() throws Exception {

        UserProfileDTO userProfileDTO = userProfileMgtClient.getUserProfile(testUser, testUserProfile);
        UserFieldDTO[] userFieldDTOs = userProfileDTO.getFieldValues();

        // Validate basic details from the test get user profile response
        assertEquals(userProfileDTO.getProfileName(), testUserProfile, "Get user profile name mismatch");
        assertEquals(userProfileDTO.getProfileConifuration(), testUserProfileConfiguration,
                "Get user profile configuration mismatch");

        // Populate UserProfile object from the test get user profile fields
        UserProfile userProfile = populateUserProfileData(userFieldDTOs);

        assertEquals(userProfile.getUserFieldLastName(), testUserProfileLastName,
                "User profile field Last Name mismatch in getUserProfile");
        assertEquals(userProfile.getUserFieldGivenName(), testUserProfileGivenName,
                "User profile field Given Name mismatch in getUserProfile");
        assertEquals(userProfile.getUserFieldEmailAddress(), testUserProfileEmailAddress,
                "User profile field Email Address mismatch in getUserProfile");
    }

    @Test(groups = "wso2.as", description = "Check get multiple user profiles",
            dependsOnMethods = "org.wso2.appserver.integration.tests.usermgt.profile.UserProfileMgtTestCase." +
                    "testGetUserProfileForGivenUser")
    public void testGetMultipleUserProfilesForGivenUser() throws Exception {

        boolean isDefaultProfilePresent = false;
        boolean isNewProfilePresent = false;

        UserProfileDTO[] userProfileDTOs = userProfileMgtClient.getUserProfiles(testUser);
        UserProfile userProfile = null;

        // Validate default and newly added profile are present in the response
        for (UserProfileDTO userProfileDTO : userProfileDTOs) {
            if (userProfileDTO.getProfileName().equals(defaultProfileName)) {
                log.info(" ----- " + defaultProfileName + " profile located for user " + testUser + " at " +
                        asServer.getContextTenant().getDomain());

                isDefaultProfilePresent = true;

            }
            if (userProfileDTO.getProfileName().equals(testUserProfile)) {
                log.info(" ----- " + testUserProfile + " profile located for user " + testUser + " at " +
                        asServer.getContextTenant().getDomain());

                isNewProfilePresent = true;
                UserFieldDTO[] userFieldDTOs = userProfileDTO.getFieldValues();

                // Populate UserProfile object from the test get user profile fields
                userProfile = populateUserProfileData(userFieldDTOs);
            }
        }

        assertTrue(isDefaultProfilePresent && isNewProfilePresent, "Getting user profiles has failed");
        assertEquals(userProfile.getUserFieldLastName(), testUserProfileLastName,
                "User profile field Last Name mismatch in getUserProfiles");
        assertEquals(userProfile.getUserFieldGivenName(), testUserProfileGivenName,
                "User profile field Given Name mismatch in getUserProfiles");
        assertEquals(userProfile.getUserFieldEmailAddress(), testUserProfileEmailAddress,
                "User profile field Email Address mismatch in getUserProfiles");

    }

    @Test(groups = "wso2.as", description = "Check update new user profile",
            dependsOnMethods = "org.wso2.appserver.integration.tests.usermgt.profile.UserProfileMgtTestCase" +
                    ".testGetMultipleUserProfilesForGivenUser")
    public void testUpdateNewUserProfile() throws Exception {

        // Following fields of the existing testUserProfile will be updated
        String updatedNewUserProfileLastName = "NewProfileModifiedLastName";
        String updatedNewUserProfileGivenName = "NewProfileModifiedGivenName";
        String updatedNewUserProfileEmailAddress = "newprofilemodified@mymail.com";

        UserProfileDTO userProfileDTO = createUserProfile(testUserProfile, updatedNewUserProfileLastName,
                updatedNewUserProfileGivenName, updatedNewUserProfileEmailAddress);

        userProfileMgtClient.setUserProfile(testUser, userProfileDTO);

        UserProfileDTO updatedUserProfileDTO = userProfileMgtClient.getUserProfile(testUser, testUserProfile);
        UserFieldDTO[] updatedUserFieldDTOs = updatedUserProfileDTO.getFieldValues();

        // Populate UserProfile object from the test get user profile fields
        UserProfile userProfile = populateUserProfileData(updatedUserFieldDTOs);

        assertEquals(userProfile.getUserFieldLastName(), updatedNewUserProfileLastName,
                "User profile field Last Name mismatch in testUpdateNewUserProfile");
        assertEquals(userProfile.getUserFieldGivenName(), updatedNewUserProfileGivenName,
                "User profile field Given Name mismatch in testUpdateNewUserProfile");
        assertEquals(userProfile.getUserFieldEmailAddress(), updatedNewUserProfileEmailAddress,
                "User profile field Email Address mismatch in testUpdateNewUserProfile");

        log.info(" ----- " + testUser + " profile updated " + testUserProfile + " / " + updatedNewUserProfileGivenName +
                " " + updatedNewUserProfileLastName + " / " + updatedNewUserProfileEmailAddress + " at " +
                asServer.getContextTenant().getDomain());

    }

    @Test(groups = "wso2.as", description = "Check update default user profile",
            dependsOnMethods = "org.wso2.appserver.integration.tests.usermgt.profile.UserProfileMgtTestCase" +
                    ".testUpdateNewUserProfile")
    public void testUpdateDefaultUserProfile() throws Exception {

        // Following fields of the default user profile will be updated
        String updatedDefaultUserProfileLastName = "DefaultProfileModifiedLastName";
        String updatedDefaultUserProfileGivenName = "DefaultProfileModifiedGivenName";
        String updatedDefaultUserProfileEmailAddress = "defaultprofilemodified@mymail.com";

        UserProfileDTO userProfileDTO = createUserProfile(defaultProfileName, updatedDefaultUserProfileLastName,
                updatedDefaultUserProfileGivenName, updatedDefaultUserProfileEmailAddress);

        userProfileMgtClient.setUserProfile(testUser, userProfileDTO);

        UserProfileDTO updatedUserProfileDTO = userProfileMgtClient.getUserProfile(testUser, defaultProfileName);
        UserFieldDTO[] updatedUserFieldDTOs = updatedUserProfileDTO.getFieldValues();

        UserProfile userProfile = populateUserProfileData(updatedUserFieldDTOs);

        assertEquals(userProfile.getUserFieldLastName(), updatedDefaultUserProfileLastName,
                "User profile field Last Name mismatch in testUpdateDefaultUserProfile");
        assertEquals(userProfile.getUserFieldGivenName(), updatedDefaultUserProfileGivenName,
                "User profile field Given Name mismatch in testUpdateDefaultUserProfile");
        assertEquals(userProfile.getUserFieldEmailAddress(), updatedDefaultUserProfileEmailAddress,
                "User profile field Email Address mismatch in testUpdateDefaultUserProfile");

        log.info(" ----- " + testUser + " profile updated " + defaultProfileName + " / " +
                updatedDefaultUserProfileGivenName + " " + updatedDefaultUserProfileLastName + " / " +
                updatedDefaultUserProfileEmailAddress + " at " + asServer.getContextTenant().getDomain());

    }

    @Test(groups = "wso2.as", description = "Check get profile fields definition",
            dependsOnMethods = "org.wso2.appserver.integration.tests.usermgt.profile.UserProfileMgtTestCase." +
                    "testUpdateDefaultUserProfile")
    public void testGetProfileFieldsDescription() throws Exception {

        UserProfileDTO userProfileDTO = userProfileMgtClient.getProfileFieldsForInternalStore();
        UserFieldDTO[] userFieldDTOs = userProfileDTO.getFieldValues();

        // Existence of the user profile fields are checked in the
        // received UserProfileDTO returned by getProfileFieldsForInternalStore
        UserProfileField userProfileField = validateUserFieldDefinitions(userFieldDTOs);

        assertTrue(userProfileField.isProfileFieldResponseLastNamePresent(),
                "User profile claim URI mismatch in getProfileFieldsForInternalStore for " +
                        UserProfileMgtConstants.CLAIM_URI_LAST_NAME);
        assertTrue(userProfileField.isProfileFieldResponseGivenNamePresent(),
                "User profile claim URI mismatch in getProfileFieldsForInternalStore for " +
                        UserProfileMgtConstants.CLAIM_URI_GIVEN_NAME);
        assertTrue(userProfileField.isProfileFieldResponseEmailAddressPresent(),
                "User profile claim URI mismatch in getProfileFieldsForInternalStore for " +
                        UserProfileMgtConstants.CLAIM_URI_EMAIL_ADDRESS);

    }

    @Test(groups = "wso2.as", description = "Check get user profile admin instance definition",
            dependsOnMethods = "org.wso2.appserver.integration.tests.usermgt.profile.UserProfileMgtTestCase." +
                    "testGetProfileFieldsDescription")
    public void testGetUserProfileAdminInstance() throws Exception {

        UserProfileAdmin userProfileAdmin = userProfileMgtClient.getInstance();
        UserProfileDTO userProfileDTO = userProfileAdmin.getProfileFieldsForInternalStore();
        UserFieldDTO[] userFieldDTOs = userProfileDTO.getFieldValues();

        assertNotNull(userProfileAdmin, "Error getting user profile instance from getInstance");

        assertTrue(userProfileAdmin.getAddProfileEnabled(), "Add profile is disabled");
        assertFalse(userProfileAdmin.getReadOnlyUserStore(), "User store is read only");

        // Existence of the user profile fields are checked in the
        // received UserProfileDTO returned by getInstance
        UserProfileField userProfileField = validateUserFieldDefinitions(userFieldDTOs);

        assertTrue(userProfileField.isProfileFieldResponseLastNamePresent(),
                "User profile claim URI mismatch in getInstance -> getProfileFieldsForInternalStore for " +
                        UserProfileMgtConstants.CLAIM_URI_LAST_NAME);
        assertTrue(userProfileField.isProfileFieldResponseGivenNamePresent(),
                "User profile claim URI mismatch in getInstance -> getProfileFieldsForInternalStore for " +
                        UserProfileMgtConstants.CLAIM_URI_GIVEN_NAME);
        assertTrue(userProfileField.isProfileFieldResponseEmailAddressPresent(),
                "User profile claim URI mismatch in getInstance -> getProfileFieldsForInternalStore for " +
                        UserProfileMgtConstants.CLAIM_URI_EMAIL_ADDRESS);

    }

    @Test(groups = "wso2.as", description = "Check delete manually created user profile",
            dependsOnMethods = "org.wso2.appserver.integration.tests.usermgt.profile.UserProfileMgtTestCase." +
                    "testGetUserProfileAdminInstance")
    public void testDeleteUserProfile() throws Exception {

        // Delete the previously created new user profile of the testUser
        userProfileMgtClient.deleteUserProfile(testUser, testUserProfile);

        UserProfileDTO userProfile = userProfileMgtClient.getUserProfile(testUser, testUserProfile);
        assertNull(userProfile, "Manually created user profile delete failed");

        log.info(" ----- user profile " + testUserProfile + " deleted for user " + testUser + " at " +
                asServer.getContextTenant().getDomain());

    }

    @AfterClass(alwaysRun = true)
    public void clean() throws Exception {
        userMgtClient.deleteUser(testUser);

    }

    /**
     * Here we check for the existence of the user profile fields in the
     * received UserProfileDTO returned by getProfileFieldsForInternalStore
     * @param userFieldDTOs UserFieldDTO[] object array
     */
    private UserProfileField validateUserFieldDefinitions(UserFieldDTO[] userFieldDTOs) {

        UserProfileField userProfileField = new UserProfileField();

        for (UserFieldDTO userFieldDTO : userFieldDTOs) {
            if (userFieldDTO.getClaimUri().equals(UserProfileMgtConstants.CLAIM_URI_LAST_NAME)) {
                userProfileField.setProfileFieldResponseLastNamePresent(true);
            }
            if (userFieldDTO.getClaimUri().equals(UserProfileMgtConstants.CLAIM_URI_GIVEN_NAME)) {
                userProfileField.setProfileFieldResponseGivenNamePresent(true);

            }
            if (userFieldDTO.getClaimUri().equals(UserProfileMgtConstants.CLAIM_URI_EMAIL_ADDRESS)) {
                userProfileField.setProfileFieldResponseEmailAddressPresent(true);
            }
        }

        return userProfileField;

    }

    /**
     * This is used to create a new UserProfileDTO object
     * @param profileName Name of the user profile
     * @param lastName User profile field for Last Name
     * @param givenName User profile field for Given Name
     * @param emailAddress User profile field for Email Address
     * @return UserProfileDTO object
     */
    private UserProfileDTO createUserProfile(String profileName, String lastName, String givenName,
            String emailAddress) {

        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setProfileName(profileName);

        UserFieldDTO userFieldLastName = new UserFieldDTO();
        userFieldLastName.setClaimUri(UserProfileMgtConstants.CLAIM_URI_LAST_NAME);
        userFieldLastName.setFieldValue(lastName);

        UserFieldDTO userFieldGivenName = new UserFieldDTO();
        userFieldGivenName.setClaimUri(UserProfileMgtConstants.CLAIM_URI_GIVEN_NAME);
        userFieldGivenName.setFieldValue(givenName);

        UserFieldDTO userFieldEmailAddress = new UserFieldDTO();
        userFieldEmailAddress.setClaimUri(UserProfileMgtConstants.CLAIM_URI_EMAIL_ADDRESS);
        userFieldEmailAddress.setFieldValue(emailAddress);

        UserFieldDTO[] userFieldDTOs = new UserFieldDTO[3];
        userFieldDTOs[0] = userFieldLastName;
        userFieldDTOs[1] = userFieldGivenName;
        userFieldDTOs[2] = userFieldEmailAddress;

        userProfileDTO.setFieldValues(userFieldDTOs);

        return userProfileDTO;

    }

    /**
     * This will pupulate the UserProfile object with user profile field values which is to be validated
     * @param userFieldDTOs UserFieldDTO[] object array
     * @return userProfile object
     */
    private UserProfile populateUserProfileData(UserFieldDTO[] userFieldDTOs) {

        UserProfile userProfile = new UserProfile();

        for (UserFieldDTO userFieldDTO : userFieldDTOs) {
            if (UserProfileMgtConstants.CLAIM_URI_LAST_NAME.equals(userFieldDTO.getClaimUri())) {
                userProfile.setUserFieldLastName(userFieldDTO.getFieldValue());
            }
            if (UserProfileMgtConstants.CLAIM_URI_GIVEN_NAME.equals(userFieldDTO.getClaimUri())) {
                userProfile.setUserFieldGivenName(userFieldDTO.getFieldValue());
            }
            if (UserProfileMgtConstants.CLAIM_URI_EMAIL_ADDRESS.equals(userFieldDTO.getClaimUri())) {
                userProfile.setUserFieldEmailAddress(userFieldDTO.getFieldValue());
            }
        }

        return userProfile;

    }

    /**
     * To store user profile field data
     */
    class UserProfile {

        private String userFieldLastName;
        private String userFieldGivenName;
        private String userFieldEmailAddress;

        public String getUserFieldLastName() {
            return userFieldLastName;
        }

        public void setUserFieldLastName(String userFieldLastName) {
            this.userFieldLastName = userFieldLastName;
        }

        public String getUserFieldGivenName() {
            return userFieldGivenName;
        }

        public void setUserFieldGivenName(String userFieldGivenName) {
            this.userFieldGivenName = userFieldGivenName;
        }

        public String getUserFieldEmailAddress() {
            return userFieldEmailAddress;
        }

        public void setUserFieldEmailAddress(String userFieldEmailAddress) {
            this.userFieldEmailAddress = userFieldEmailAddress;
        }

    }

    /**
     * To store user profile field presence check variables
     */
    class UserProfileField {

        private boolean isProfileFieldResponseLastNamePresent = false;
        private boolean isProfileFieldResponseGivenNamePresent = false;
        private boolean isProfileFieldResponseEmailAddressPresent = false;

        public boolean isProfileFieldResponseLastNamePresent() {
            return isProfileFieldResponseLastNamePresent;
        }

        public void setProfileFieldResponseLastNamePresent(boolean isProfileFieldResponseLastNamePresent) {
            this.isProfileFieldResponseLastNamePresent = isProfileFieldResponseLastNamePresent;
        }

        public boolean isProfileFieldResponseGivenNamePresent() {
            return isProfileFieldResponseGivenNamePresent;
        }

        public void setProfileFieldResponseGivenNamePresent(boolean isProfileFieldResponseGivenNamePresent) {
            this.isProfileFieldResponseGivenNamePresent = isProfileFieldResponseGivenNamePresent;
        }

        public boolean isProfileFieldResponseEmailAddressPresent() {
            return isProfileFieldResponseEmailAddressPresent;
        }

        public void setProfileFieldResponseEmailAddressPresent(boolean isProfileFieldResponseEmailAddressPresent) {
            this.isProfileFieldResponseEmailAddressPresent = isProfileFieldResponseEmailAddressPresent;
        }

    }

}
