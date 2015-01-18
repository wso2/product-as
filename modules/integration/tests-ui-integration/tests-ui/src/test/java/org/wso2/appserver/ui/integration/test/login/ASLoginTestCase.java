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

package org.wso2.appserver.ui.integration.test.login;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationUITest;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.appserver.integration.common.ui.page.LoginPage;
import org.wso2.appserver.integration.common.ui.page.main.HomePage;


public class ASLoginTestCase extends ASIntegrationUITest {
    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        super.init();
        driver = BrowserManager.getWebDriver();
        driver.get(getLoginURL());
    }

    @Test(groups = "wso2.as", description = "verify login to AS Server")
    public void testLogin() throws Exception {
        LoginPage test = new LoginPage(driver);
        HomePage home = test.loginAs(asServer.getContextTenant().getContextUser().getUserName()
                , asServer.getContextTenant().getContextUser().getPassword());
        home.logout();
        driver.close();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {

        driver.quit();
    }
}
