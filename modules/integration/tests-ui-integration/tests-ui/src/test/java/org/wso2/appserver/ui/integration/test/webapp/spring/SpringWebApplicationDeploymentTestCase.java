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
package org.wso2.appserver.ui.integration.test.webapp.spring;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.ui.page.LoginPage;
import org.wso2.appserver.integration.common.ui.page.main.WebAppListPage;
import org.wso2.appserver.integration.common.ui.page.main.WebAppUploadingPage;
import org.wso2.appserver.integration.common.utils.ASIntegrationUITest;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import static org.testng.AssertJUnit.assertTrue;

/**
 * This class tests the deployment and accessibility  of a web application which use spring framework
 */
public class SpringWebApplicationDeploymentTestCase extends ASIntegrationUITest {
    private WebDriver driver;
    private final String context = "/booking-faces";

    @BeforeClass(alwaysRun = true, enabled = false)
    public void setUp() throws Exception {
        super.init();
        driver = BrowserManager.getWebDriver();
        driver.get(getLoginURL());
        LoginPage test = new LoginPage(driver);
        test.loginAs(userInfo.getUserName(), userInfo.getPassword());
    }

    @Test(groups = "wso2.as", description = "Uploading the web app which use spring", enabled = false)
    public void uploadSpringWebApplicationTest() throws Exception {
        String filePath = TestConfigurationProvider.getResourceLocation("AS")
                          + File.separator + "war" + File.separator + "spring" + File.separator + "booking-faces.war";
        WebAppUploadingPage uploadPage = new WebAppUploadingPage(driver);
        Assert.assertTrue(uploadPage.uploadWebApp(filePath), "Web Application uploading failed");

    }

    @Test(groups = "wso2.as", description = "Verifying Deployment the web app which use spring"
            , dependsOnMethods = "uploadSpringWebApplicationTest", enabled = false)
    public void webApplicationDeploymentTest() throws Exception {
        WebAppListPage webAppListPage = new WebAppListPage(driver);
        assertTrue("Web Application Deployment Failed. Web Application /booking-faces not found in Web application List"
                , isWebAppDeployed(webAppListPage, context));
        driver.findElement(By.id("webappsTable")).findElement(By.linkText("/booking-faces")).click();

    }

    @Test(groups = "wso2.as", description = "Access the spring application"
            , dependsOnMethods = "webApplicationDeploymentTest", enabled = false)
    public void invokeSpringApplicationTest() throws Exception {
        WebDriver driverForApp = null;
        try {
            driverForApp = BrowserManager.getWebDriver();
            //Go  to application
            driverForApp.get(webAppURL + "/booking-faces/spring/intro");
            driverForApp.findElement(By.linkText("Start your Spring Travel experience")).click();

            //searching hotels to reserve
            driverForApp.findElement(By.xpath("//*[@id=\"j_idt13:searchString\"]")).sendKeys("Con");
            driverForApp.findElement(By.xpath("//*[@id=\"j_idt13:findHotels\"]")).click();

            //view hotel information
            driverForApp.findElement(By.xpath("//*[@id=\"j_idt12:hotels:0:viewHotelLink\"]")).click();
            //go to book hotel
            driverForApp.findElement(By.xpath("//*[@id=\"hotel:book\"]")).click();

            //providing user name and password
            driverForApp.findElement(By.xpath("/html/body/div/div[3]/div[2]/div[2]/form/fieldset/p[1]/input"))
                    .sendKeys("keith");
            driverForApp.findElement(By.xpath("/html/body/div/div[3]/div[2]/div[2]/form/fieldset/p[2]/input"))
                    .sendKeys("melbourne");
            //authenticating
            driverForApp.findElement(By.xpath("/html/body/div/div[3]/div[2]/div[2]/form/fieldset/p[4]/input"))
                    .click();

            //booking hotel
            driverForApp.findElement(By.xpath("//*[@id=\"hotel:book\"]")).click();

            //providing payments information
            driverForApp.findElement(By.xpath("//*[@id=\"bookingForm:creditCard\"]")).sendKeys("1234567890123456");
            driverForApp.findElement(By.xpath("//*[@id=\"bookingForm:creditCardName\"]")).sendKeys("xyz");

            //proceed transaction
            driverForApp.findElement(By.xpath("//*[@id=\"bookingForm:proceed\"]")).click();

            //confirm booking
            driverForApp.findElement(By.xpath("//*[@id=\"j_idt13:confirm\"]")).click();

            //verify whether the hotel booked is in the booked hotel tabled
            Assert.assertEquals(driverForApp.findElement(By.xpath("//*[@id=\"bookings_header\"]")).getText()
                    , "Your Hotel Bookings", "Booked Hotel table Not Found");

            //verify the hotel name is exist in the booked hotel table
            Assert.assertEquals(driverForApp.findElement(By.xpath("//*[@id=\"j_idt23:j_idt24_data\"]/tr/td[1]"))
                                        .getText(), "Conrad Miami\n" +
                                                    "1395 Brickell Ave\n" +
                                                    "Miami, FL", "Hotel Name mismatch");
        } finally {
            if (driverForApp != null) {
                driverForApp.quit();
            }
        }
    }

    @AfterClass(alwaysRun = true, enabled = false)
    public void deleteWebApplication() throws Exception {
        try {
            WebAppListPage webAppListPage = new WebAppListPage(driver);
            if (webAppListPage.findWebApp(context)) {
                Assert.assertTrue(webAppListPage.deleteWebApp(context), "Web Application Deletion failed");
                Assert.assertTrue(isWebAppUnDeployed(webAppListPage, context));
            }
        } finally {
            driver.quit();
        }

    }

    private boolean isWebAppDeployed(WebAppListPage listPage, String webAppContext)
            throws IOException {

        boolean isServiceDeployed = false;
        Calendar startTime = Calendar.getInstance();
        long time;
        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < 1000 * 60 * 2) {
            listPage = new WebAppListPage(driver);
            if (listPage.findWebApp(webAppContext)) {
                isServiceDeployed = true;
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {

            }

        }

        return isServiceDeployed;

    }

    private boolean isWebAppUnDeployed(WebAppListPage listPage, String webAppContext)
            throws IOException {

        boolean isServiceUnDeployed = false;
        Calendar startTime = Calendar.getInstance();
        long time;
        while ((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < 1000 * 60 * 2) {
            listPage = new WebAppListPage(driver);
            if (!listPage.findWebApp(webAppContext)) {
                isServiceUnDeployed = true;
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {

            }

        }

        return isServiceUnDeployed;

    }
}
