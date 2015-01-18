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

package org.wso2.appserver.ui.integration.test.webapp;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.ui.page.LoginPage;
import org.wso2.appserver.integration.common.ui.page.main.WebAppListPage;
import org.wso2.appserver.integration.common.utils.ASIntegrationUITest;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class ExampleTestCase extends ASIntegrationUITest {

    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        super.init();
        driver = BrowserManager.getWebDriver();
        driver.get(getLoginURL());
        LoginPage test = new LoginPage(driver);
        test.loginAs(userInfo.getUserName(), userInfo.getPassword());
        WebAppListPage webAppListPage = new WebAppListPage(driver);
        assertTrue("Web app context not found", webAppListPage.findWebApp("/example"));
        driver.findElement(By.linkText("/example")).click();
    }

    @Test(groups = "wso2.as", description = "Verify example:servlet HelloWorldExample")
    public void testHelloWorldExample() throws Exception {
        driver.get(webAppURL+ "/example/servlets/");
        assertEquals(driver.getTitle(), "Servlet Examples");
        driver.findElement(By.xpath("/html/body/p[5]/table/tbody/tr/td[2]/a[2]")).click();
        assertEquals(driver.getCurrentUrl(), webAppURL +
                                             "/example/servlets/servlet/HelloWorldExample");
        assertTrue(driver.getPageSource().contains("Hello World!"));

    }

    @Test(groups = "wso2.as", description = "Verify example:servlet RequestInfoExample")
    public void testRequestInfoExample() throws Exception {
        driver.get(asServer.getContextUrls().getWebAppURL() + "/example/servlets/");
        assertEquals(driver.getTitle(), "Servlet Examples");
        driver.findElement(By.xpath("/html/body/p[5]/table/tbody/tr[2]/td[2]/a[2]")).click();
        assertEquals(driver.getCurrentUrl(), asServer.getContextUrls().getWebAppURL() +
                                             "/example/servlets/servlet/RequestInfoExample");
        assertTrue(driver.getPageSource().contains("Request Information Example"));
        assertTrue(driver.getPageSource().contains("/example/servlets/servlet/RequestInfoExample"));
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        driver.quit();
    }
}
