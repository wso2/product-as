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
package org.wso2.appserver.integration.tests.jndi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.SkipException;
import org.testng.annotations.*;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.testng.Assert;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.integration.common.admin.client.NDataSourceAdminServiceClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceMetaInfo;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceMetaInfo_WSDataSourceDefinition;
import org.wso2.carbon.ndatasource.ui.stub.core.xsd.JNDIConfig;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * This class test JNDI resource lookup functionality in Application server.
 * .
 */
public class JNDIResourceLookupTestCase extends ASIntegrationTest {
    private static final Log log = LogFactory.getLog(JNDIResourceLookupTestCase.class);

    private WebAppAdminClient webAppAdminClient;
    private String tomcatJNDIResourceLookupWebApp;
    private String tomcatWebAppContext;
    private String carbonDataSourceLookupWebApp;
    private String carbonWebAppContext;
    private TestUserMode userMode;
    private static ServerConfigurationManager serverConfigurationManager;
    private static int isRestarted = 0;

    @Factory(dataProvider = "userModeProvider")
    public JNDIResourceLookupTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);
        if (isRestarted == 0) {
            serverConfigurationManager =
                    new ServerConfigurationManager(new AutomationContext("AS", TestUserMode.SUPER_TENANT_ADMIN));
            String sourceFilePath = TestConfigurationProvider.getResourceLocation("AS") + File.separator + "configs" +
                                    File.separator + "tomcat" + File.separator + "context.xml";
            String targetFilePath = FrameworkPathUtil.getCarbonHome() + File.separator + "repository" + File.separator +
                                    "conf" + File.separator + "tomcat" + File.separator + "context.xml";

            serverConfigurationManager.applyConfiguration(new File(sourceFilePath),
                                                          new File(targetFilePath), true, true);

        }
        ++isRestarted;
        sessionCookie = loginLogoutClient.login();

        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        tomcatJNDIResourceLookupWebApp = "tomcat-jndi-resource-lookup";
        tomcatWebAppContext = "/tomcat-jndi-resource-lookup";
        carbonDataSourceLookupWebApp = "carbon-datasource-lookup";
        carbonWebAppContext = "/carbon-datasource-lookup";
    }

    @AfterClass(alwaysRun = true)
    public void deleteWebApp() throws Exception {

        sessionCookie = loginLogoutClient.login();
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        String defaultHost = asServer.getDefaultInstance().getHosts().get("default");
        webAppAdminClient.deleteWebAppFile(tomcatJNDIResourceLookupWebApp + ".war", defaultHost);
        webAppAdminClient.deleteWebAppFile(carbonDataSourceLookupWebApp + ".war", defaultHost);

        //Revert and restart only once
        --isRestarted;
        if (isRestarted == 0) {
            serverConfigurationManager.restoreToLastConfiguration();
        }
    }

    @Test(groups = "wso2.as", description = "Deploying web application")
    public void testTomcatJNDIResourceWebApplicationDeployment() throws Exception {
        webAppAdminClient.uploadWarFile(
                FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator + "AS" + File.separator +
                "war" + File.separator + tomcatJNDIResourceLookupWebApp + ".war");
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie,
                                                                 tomcatJNDIResourceLookupWebApp),
                   "Web Application Deployment failed: " + tomcatJNDIResourceLookupWebApp);
    }

    @Test(groups = "wso2.as", description = "test JNDI look up on web application's context",
            dependsOnMethods = "testTomcatJNDIResourceWebApplicationDeployment")
    public void testJNDILookupOnWebAppContext() throws Exception {

        String webAppURLLocal = "";
        if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
            webAppURLLocal = webAppURL + tomcatWebAppContext + "/jndi/tomcat-resource-lookup";

        } else if (userMode == TestUserMode.TENANT_ADMIN) {
            webAppURLLocal = webAppURL + "/webapps" + tomcatWebAppContext + "/jndi/tomcat-resource-lookup";
        }

        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, "dsName=jdbc/WebappContextDB");
        Assert.assertEquals(response.getData(), "DataSourceAvailable");
    }

    //TODO Check whether this behaviour is correct in Tenant mode.
    @Test(groups = "wso2.as", description = "test JNDI look up on Tomcat's context",
            dependsOnMethods = "testTomcatJNDIResourceWebApplicationDeployment")
    public void testJNDILookupOnTomcatContext() throws Exception {

        String webAppURLLocal = "";
        if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
            webAppURLLocal = webAppURL + tomcatWebAppContext + "/jndi/tomcat-resource-lookup";

        } else if (userMode == TestUserMode.TENANT_ADMIN) {
            throw new SkipException("Skipping becauese of a product bug - https://wso2.org/jira/browse/WSAS-1994");
//            webAppURLLocal = webAppURL + "/webapps" + tomcatWebAppContext + "/jndi/tomcat-resource-lookup";
        }

        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, "dsName=jdbc/TomcatContextDB");
        Assert.assertEquals(response.getData(), "DataSourceAvailable");
    }

    @Test(groups = "wso2.as", description = "Deploying carbon data source lookup web application")
    public void testCarbonDataSourceLookupWebApplicationDeployment() throws Exception {
        webAppAdminClient.uploadWarFile(
                FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator + "AS" + File.separator +
                "war" + File.separator + carbonDataSourceLookupWebApp + ".war");
        assertTrue(
                WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, carbonDataSourceLookupWebApp),
                "Web Application Deployment failed: " + carbonDataSourceLookupWebApp);
    }

    @Test(groups = "wso2.as", description = "test JNDI lookup for default carbon datasource ",
            dependsOnMethods = "testCarbonDataSourceLookupWebApplicationDeployment")
    public void testJNDILookupOnDefaultCarbonDataSource() throws Exception {

        String webAppURLLocal;
        if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
            webAppURLLocal = webAppURL + carbonWebAppContext + "/jndi/carbon-datasource-lookup";
            HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, "dsName=jdbc/WSO2CarbonDB");
            Assert.assertEquals(response.getData(), "DataSourceAvailable");

        } else if (userMode == TestUserMode.TENANT_ADMIN) {
            webAppURLLocal = webAppURL + "/webapps" + carbonWebAppContext + "/jndi/carbon-datasource-lookup";
            HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, "dsName=jdbc/WSO2CarbonDB");
            Assert.assertEquals(response.getData(), "DataSourceNotFound");
        }
    }

    @Test(groups = "wso2.as", description = "test JNDI lookup for a resource registered in another webapp's context.xml",
            dependsOnMethods = "testCarbonDataSourceLookupWebApplicationDeployment")
    public void testJNDILookupForResourceInAnotherWebAppContext() throws Exception {

        String webAppURLLocal = "";
        if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
            webAppURLLocal = webAppURL + carbonWebAppContext + "/jndi/tomcat-resource-lookup";

        } else if (userMode == TestUserMode.TENANT_ADMIN) {
            webAppURLLocal = webAppURL + "/webapps" + carbonWebAppContext + "/jndi/tomcat-resource-lookup";
        }

        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, "dsName=jdbc/WebappContextDB");
        Assert.assertEquals(response.getData(), "DataSourceNotFound");
    }

    @Test(groups = "wso2.as", description = "Create a new Carbon data source and test JNDI lookup",
            dependsOnMethods = "testCarbonDataSourceLookupWebApplicationDeployment")
    public void testJNDILookupOnCustomCarbonDataSource() throws Exception {
        NDataSourceAdminServiceClient dataSourceAdminServiceClient =
                new NDataSourceAdminServiceClient(backendURL, sessionCookie);
        JNDIConfig jndiConfig = new JNDIConfig();
        jndiConfig.setName("jdbc/myCarbonDS");
        WSDataSourceMetaInfo wsDataSourceMetaInfo = new WSDataSourceMetaInfo();
        wsDataSourceMetaInfo.setJndiConfig(jndiConfig);
        WSDataSourceMetaInfo_WSDataSourceDefinition wsDataSourceDefinition =
                new WSDataSourceMetaInfo_WSDataSourceDefinition();
        wsDataSourceDefinition.setDsXMLConfiguration(
                "<configuration xmlns:svns=\"http://org.wso2.securevault/configuration\"" +
                        " xmlns:xml=\"http://www.w3.org/XML/1998/namespace\">\n" +
                        "    <url>jdbc:mysql://localhost:3306/carbonDB</url>\n" +
                        "    <username>admin</username>\n" +
                        "    <password>admin</password>\n" +
                        "    <driverClassName>com.mysql.jdbc.Driver</driverClassName>\n" +
                        "    <maxActive>50</maxActive>\n" +
                        "    <maxWait>60000</maxWait>\n" +
                        "    <testOnBorrow>true</testOnBorrow>\n" +
                        "    <validationQuery>SELECT 1</validationQuery>\n" +
                        "    <validationInterval>30000</validationInterval>\n" +
                        "</configuration>");
        wsDataSourceDefinition.setType("RDBMS");
        wsDataSourceMetaInfo.setDefinition(wsDataSourceDefinition);
        wsDataSourceMetaInfo.setName("MyCarbonDS");
        dataSourceAdminServiceClient.addDataSource(wsDataSourceMetaInfo);

        String webAppURLLocal = "";
        if (userMode == TestUserMode.SUPER_TENANT_ADMIN) {
            webAppURLLocal = webAppURL + carbonWebAppContext + "/jndi/carbon-datasource-lookup";

        } else if (userMode == TestUserMode.TENANT_ADMIN) {
            webAppURLLocal = webAppURL + "/webapps" + carbonWebAppContext + "/jndi/carbon-datasource-lookup";
        }

        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURLLocal, "dsName=jdbc/myCarbonDS");
        Assert.assertEquals(response.getData(), "DataSourceAvailable");
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN},
                new TestUserMode[]{TestUserMode.TENANT_ADMIN},
        };
    }
}
