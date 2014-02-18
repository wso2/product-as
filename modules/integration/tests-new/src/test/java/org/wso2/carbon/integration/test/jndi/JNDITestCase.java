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


package org.wso2.carbon.integration.test.jndi;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.ndatasource.NDataSourceAdminServiceClient;
import org.wso2.carbon.automation.api.clients.webapp.mgt.WebAppAdminClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.HttpRequestUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.wso2.carbon.automation.utils.as.WebApplicationDeploymentUtil;
import org.wso2.carbon.automation.utils.httpclient.HttpClientUtil;
import org.wso2.carbon.integration.test.ASIntegrationTest;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceMetaInfo;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceMetaInfo_WSDataSourceDefinition;
import org.wso2.carbon.ndatasource.ui.stub.core.xsd.JNDIConfig;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * This class uploads webApp-context-lookup.war
 * and carbon-datasource-lookup.war.
 * .
 */

public class JNDITestCase extends ASIntegrationTest {


    private WebAppAdminClient webAppAdminClient;
    private String webAppNameOne;
    private String webAppNameTwo;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(ProductConstant.ADMIN_USER_ID);
        webAppAdminClient = new WebAppAdminClient(asServer.getBackEndUrl(), asServer.getSessionCookie());
        webAppNameOne = "wso2appserver-samples-tomcat-webapps-generic-javabean-1.0";
        webAppNameTwo = "carbon-datasource-lookup";
    }

    @AfterClass(alwaysRun = true)
    public void deteleteWebApp() throws Exception {
        webAppAdminClient = new WebAppAdminClient(asServer.getBackEndUrl(), asServer.getSessionCookie());
        webAppAdminClient.deleteWebAppFile(webAppNameOne + ".war");
        webAppAdminClient.deleteWebAppFile(webAppNameTwo + ".war");
    }


    @Test(groups = "wso2.as", description = "Deploying web application")
    public void testWebAppContextLookupWebApplicationDeployment() throws Exception {

        webAppAdminClient.warFileUplaoder(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                "artifacts" + File.separator + "AS" + File.separator + "war"
                + File.separator + webAppNameOne + ".war");

        assertTrue(WebApplicationDeploymentUtil.isWebApplicationDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(), webAppNameOne)
                , "Web Application Deployment failed: " + webAppNameOne);
    }


    @Test(groups = "wso2.as", description = "test JNDI look up on web application's context",
            dependsOnMethods = "testWebAppContextLookupWebApplicationDeployment")
    public void testJNDILookupOnWebAppContext() throws Exception {
        String webAppURL =
                asServer.getWebAppURL() + "/wso2appserver-samples-tomcat-webapps-generic-javabean-1.0/bean";
        HttpClientUtil client = new HttpClientUtil();
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURL, null);

        Assert.assertEquals(response.getData(), "foo = 68, bar = 23");
    }


    @Test(groups = "wso2.as", description = "Deploying carbon datasource lookup web application")
    public void testCarbonDataSourceLookupWebApplicationDeployment() throws Exception {

        webAppAdminClient.warFileUplaoder(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                "artifacts" + File.separator + "AS" + File.separator + "war"
                + File.separator + webAppNameTwo + ".war");

        assertTrue(WebApplicationDeploymentUtil.isWebApplicationDeployed(
                asServer.getBackEndUrl(), asServer.getSessionCookie(), webAppNameTwo)
                , "Web Application Deployment failed: " + webAppNameTwo);
    }


    @Test(groups = "wso2.as", description = "test JNDI lookup for default carbon datasource ",
            dependsOnMethods = "testCarbonDataSourceLookupWebApplicationDeployment")
    public void testJNDILookupOnDefaultCarbonDataSource() throws Exception {

        String webAppURL =
                asServer.getWebAppURL() + "/carbon-datasource-lookup/dslookup";
        HttpClientUtil client = new HttpClientUtil();
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURL, "jndi_name=jdbc/WSO2CarbonDB");

        Assert.assertEquals(response.getData(),
                "Driver:org.h2.Driver " +
                        "URL:jdbc:h2:repository/database/WSO2CARBON_DB;DB_CLOSE_ON_EXIT=FALSE;LOCK_TIMEOUT=60000");

    }


    @Test(groups = "wso2.as", description = "Create a new Carbon data source and test JNDI lookup",
            dependsOnMethods = "testCarbonDataSourceLookupWebApplicationDeployment")
    public void testJNDILookupOnCustomCarbonDataSource() throws Exception {

        NDataSourceAdminServiceClient dataSourceAdminServiceClient =
                new NDataSourceAdminServiceClient(asServer.getBackEndUrl(), asServer.getSessionCookie());

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

        String webAppURL =
                asServer.getWebAppURL() + "/carbon-datasource-lookup/dslookup";
        HttpClientUtil client = new HttpClientUtil();
        HttpResponse response = HttpRequestUtil.sendGetRequest(webAppURL, "jndi_name=jdbc/myCarbonDS");
        Assert.assertEquals(response.getData(),
                "Driver:com.mysql.jdbc.Driver URL:jdbc:mysql://localhost:3306/carbonDB");

    }


}
