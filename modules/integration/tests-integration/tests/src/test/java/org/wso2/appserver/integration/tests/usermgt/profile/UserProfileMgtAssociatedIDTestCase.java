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

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.IdentityProviderMgtServiceClient;
import org.wso2.appserver.integration.common.clients.UserProfileMgtServiceClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.identity.application.common.model.idp.xsd.IdentityProvider;
import org.wso2.carbon.identity.user.profile.stub.types.AssociatedAccountDTO;
import org.wso2.carbon.idp.mgt.stub.IdentityProviderMgtServiceIdentityApplicationManagementExceptionException;

import java.rmi.RemoteException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Associate ID related test cases of the user profile management tests.
 * An IDP is created and the relevant Associated Account operations are tested.
 * Associated account is associated with the logged in tenant admin user.
 */
public class UserProfileMgtAssociatedIDTestCase extends ASIntegrationTest {

    private TestUserMode userMode;
    private UserProfileMgtServiceClient userProfileMgtClient;
    private IdentityProviderMgtServiceClient idpMgtServiceClient;

    private final String testIDPName = "UsrProfMgtAssoID_TestIDPProvider";
    private final String testIDPAssociatedNameWithAdmin = "UsrProfMgtAssoID_testAdmin";
    private String nameAssociatedWithAssociatedAccount;

    @Factory(dataProvider = "userModeDataProvider")
    public UserProfileMgtAssociatedIDTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    public static Object[][] userModeDataProvider() {
        return new Object[][] { new Object[] { TestUserMode.SUPER_TENANT_ADMIN },
                new Object[] { TestUserMode.TENANT_ADMIN }, };
    }

    @BeforeClass(alwaysRun = true)
    public void testInit() throws Exception {

        super.init(userMode);
        userProfileMgtClient = new UserProfileMgtServiceClient(backendURL, sessionCookie);
        idpMgtServiceClient = new IdentityProviderMgtServiceClient(backendURL, sessionCookie);
        nameAssociatedWithAssociatedAccount = asServer.getContextTenant().getContextUser().getUserNameWithoutDomain();

        // This is a dummy IDP entry to check the Associated ID functionality
        IdentityProvider identityProvider = createIdpObject();
        idpMgtServiceClient.addIdP(identityProvider);
    }

    @Test(groups = "wso2.as", description = "add associated account to the given IDP")
    public void testAddAssociatedAccountToIDP() throws Exception {
        String associatedAccountIDPName = null;
        String associatedAccountIDPUserName = null;

        userProfileMgtClient.addAssociatedID(testIDPName, testIDPAssociatedNameWithAdmin);
        AssociatedAccountDTO[] associatedAccountDTOArray = userProfileMgtClient.getAssociatedIDs();

        for (AssociatedAccountDTO associatedAccountDTO : associatedAccountDTOArray) {
            if (testIDPName.equalsIgnoreCase(associatedAccountDTO.getIdentityProviderName())) {
                associatedAccountIDPName = associatedAccountDTO.getIdentityProviderName();
                associatedAccountIDPUserName = associatedAccountDTO.getUsername();
            }
        }

        assertEquals(associatedAccountIDPName, testIDPName, "Add associated ID failed, invalid IDP name");
        assertEquals(associatedAccountIDPUserName, testIDPAssociatedNameWithAdmin,
                "Add associated ID failed, invalid associated user");
    }

    @Test(groups = "wso2.as", description = "get Name associated with the associated account",
            dependsOnMethods = "org.wso2.appserver.integration.tests.usermgt.profile" +
                    ".UserProfileMgtAssociatedIDTestCase.testAddAssociatedAccountToIDP")
    public void testGetNamesAssociatedWithAssociatedAccount() throws Exception {

        String nameAssociatedWithIDP = userProfileMgtClient
                .getNameAssociatedWith(testIDPName, testIDPAssociatedNameWithAdmin);

        assertEquals(nameAssociatedWithIDP, nameAssociatedWithAssociatedAccount, "Get name associated with IDP failed");

    }

    @Test(groups = "wso2.as", description = "delete associated ID",
            dependsOnMethods = "org.wso2.appserver.integration.tests.usermgt.profile" +
                    ".UserProfileMgtAssociatedIDTestCase.testGetNamesAssociatedWithAssociatedAccount")
    public void testDeleteAssociatedAccountFromIDP() throws Exception {

        userProfileMgtClient.removeAssociateID(testIDPName, testIDPAssociatedNameWithAdmin);

        // Since we deleted the only Associated ID, this should return null
        assertNull(userProfileMgtClient.getAssociatedIDs(), "Delete Associated ID failed");

    }

    @AfterClass(alwaysRun = true)
    public void clean() throws Exception {
        idpMgtServiceClient.deleteIdP(testIDPName);

    }

    /**
     * To test the Associated ID functionality a new IDP needs to be created
     * with basic IDP details which will be associated with a local user account.
     * In this case associated with admin account, who is the logged in user
     * @throws RemoteException
     * @throws IdentityProviderMgtServiceIdentityApplicationManagementExceptionException
     */
    private IdentityProvider createIdpObject()
            throws RemoteException, IdentityProviderMgtServiceIdentityApplicationManagementExceptionException {

        IdentityProvider idProvider = new IdentityProvider();

        String testIdpDescription = "This is test identity provider";
        String testIdpRealmId = "localhost";
        String testIdpAlias = "https://localhost:9443/oauth2/token/";
        String testIdpProvisioningRole = "test";

        // This is a sample certificate, since to create a dummy IDP to test the Associated ID functionality
        String sampleCertificate = "MIIBuTCCASKgAwIBAgIQNdNhtuV5GbNHYZsf+LvM0zANBgkqhkiG9w0BAQUFADAb\n" +
                "MRkwFwYDVQQDExBFZGlkZXYgU21va2VUZXN0MB4XDTA4MTExMjE5NTEzNVoXDTM5\n" +
                "MTIzMTIzNTk1OVowGzEZMBcGA1UEAxMQRWRpZGV2IFNtb2tlVGVzdDCBnzANBgkq\n" +
                "hkiG9w0BAQEFAAOBjQAwgYkCgYEAm6zGzqxejwswWTNLcSsa7P8xqODspX9VQBuq\n" +
                "5W1RoTgQ0LNR64+7ywLjH8+wrb/lB6QV7s2SFUiWDeduVesvMJkWtZ5zzQyl3iUa\n" +
                "CBpT4S5AaO3/wkYQSKdI108pXH7Aue0e/ZOwgEEX1N6OaPQn7AmAB4uq1h+ffw+r\n" +
                "RKNHqnsCAwEAATANBgkqhkiG9w0BAQUFAAOBgQCZmj+pgRsN6HpoICawK3XXNAmi\n" +
                "cgfQkailX9akIjD3xSCwEQx4nG6tZjTz30u4NoSffW7pch58SxuZQDqW5NsJcQNq\n" +
                "Ngo/dMoqqpXdi2/0BYEcJ8pjsngrFm+fM2BnyGpXH7aWuKsWjVFGlWlF+yi8I35Q\n" + "8wFJt2Z/XGA7WWDjvw==";

        // Alias name is a string, URL status not considered
        idProvider.setAlias(testIdpAlias);

        // Sample BASE 64 encoded string is set as the certificate value
        idProvider.setCertificate(sampleCertificate);

        idProvider.setDisplayName(testIDPName);
        idProvider.setEnable(true);
        idProvider.setFederationHub(false);
        idProvider.setHomeRealmId(testIdpRealmId);
        idProvider.setIdentityProviderDescription(testIdpDescription);
        idProvider.setIdentityProviderName(testIDPName);
        idProvider.setPrimary(true);
        idProvider.setProvisioningRole(testIdpProvisioningRole);

        return idProvider;

    }

}
