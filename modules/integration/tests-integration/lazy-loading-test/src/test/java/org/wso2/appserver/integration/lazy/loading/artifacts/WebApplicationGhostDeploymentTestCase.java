/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.appserver.integration.lazy.loading.artifacts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.lazy.loading.LazyLoadingBaseTest;
import org.wso2.appserver.integration.lazy.loading.util.LazyLoadingTestException;
import org.wso2.appserver.integration.lazy.loading.util.WebAppStatusBean;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
 * Test the ghost deployment of web application. For this test two tenants will be used and
 * in each tenant two  web applications will be deployed.
 */
public class WebApplicationGhostDeploymentTestCase extends LazyLoadingBaseTest {

    private static final Log log = LogFactory.getLog(WebApplicationGhostDeploymentTestCase.class);

    private static final String WEB_APP_FILE_NAME1 = "appServer-valied-deploymant-1.0.0.war";
    private static final String WEB_APP_NAME1 = "appServer-valied-deploymant-1.0.0";
    private static final String WEB_APP_FILE_NAME2 = "helloworld.war";
    private static final String WEB_APP_NAME2 = "helloworld";
    private static final String WEB_APP1_RESPONSE = "<status>success</status>";
    private static final String WEB_APP2_RESPONSE = "<h2>Hello, World.</h2>";
    private String webApp1Location;
    private String webApp2Location;
    private String tenant1WebApp1URL;
    private String tenant1WebApp2URL;
    private volatile List<String> responseDataList = new ArrayList<String>();
    private volatile List<String> responseDetailedInfoList = new ArrayList<String>();

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        webApp1Location = artifactsLocation + WEB_APP_FILE_NAME1;
        webApp2Location = artifactsLocation + WEB_APP_FILE_NAME2;
        tenant1WebApp1URL =
                webAppURL + "/t/" + tenantDomain1 + "/webapps/" + WEB_APP_NAME1 + "/";
        tenant1WebApp2URL =
                webAppURL + "/t/" + tenantDomain1 + "/webapps/" + WEB_APP_NAME2 + "/hi.jsp";
    }

    @Test(groups = "wso2.as.lazy.loading", description = "Deploying web application in Ghost Deployment enable" +
            " environment. Each Web application should fully loaded (non Ghost format) soon after the deployment",
            alwaysRun = true)
    public void testDeployWebApplicationInGhostDeployment() throws Exception {
        log.info("deployment of  web application started");
        //Tenant 1
        init(TENANT_DOMAIN_1_KEY, ADMIN);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.uploadWarFile(webApp1Location);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, WEB_APP_NAME1),
                "Web Application deployment failed: " + WEB_APP_NAME1 + "on " + tenantDomain1);
        WebAppStatusBean webAppStatusTenant1WebApp1 = getWebAppStatus(tenantDomain1, WEB_APP_FILE_NAME1);
        assertTrue(webAppStatusTenant1WebApp1.getTenantStatus().isTenantContextLoaded(), " Tenant Context is" +
                " not loaded. Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp1.isWebAppStarted(), "Web-App: " + WEB_APP_FILE_NAME1 + " is not " +
                "started after deployment in Tenant:" + tenantDomain1);
        assertFalse(webAppStatusTenant1WebApp1.isWebAppGhost(), "Web-App: " + WEB_APP_FILE_NAME1 + " is in " +
                "ghost mode after deployment in Tenant:" + tenantDomain1);

        webAppAdminClient.uploadWarFile(webApp2Location);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, WEB_APP_NAME2),
                "Web Application deployment failed: " + WEB_APP_NAME2 + "on " + tenantDomain1);
        WebAppStatusBean webAppStatusTenant1WebApp2 = getWebAppStatus(tenantDomain1, WEB_APP_FILE_NAME2);
        assertTrue(webAppStatusTenant1WebApp2.getTenantStatus().isTenantContextLoaded(), " Tenant Context is" +
                " not loaded. Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp2.isWebAppStarted(), "Web-App: " + WEB_APP_FILE_NAME2 + " is not " +
                "started after deployment in Tenant:" + tenantDomain1);
        assertFalse(webAppStatusTenant1WebApp2.isWebAppGhost(), "Web-App: " + WEB_APP_FILE_NAME2 + " is in" +
                " ghost mode after deployment in Tenant:" + tenantDomain1);

        //Tenant2
        init(TENANT_DOMAIN_2_KEY, ADMIN);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);

        webAppAdminClient.uploadWarFile(webApp1Location);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, WEB_APP_NAME1),
                "Web Application deployment failed: " + WEB_APP_NAME1 + "on " + tenantDomain2);
        WebAppStatusBean webAppStatusTenant2WebApp1 = getWebAppStatus(tenantDomain2, WEB_APP_FILE_NAME1);
        assertTrue(webAppStatusTenant2WebApp1.getTenantStatus().isTenantContextLoaded(), " Tenant Context is" +
                " not loaded. Tenant:" + tenantDomain2);
        assertTrue(webAppStatusTenant2WebApp1.isWebAppStarted(), "Web-App: " + WEB_APP_FILE_NAME1 + " is not" +
                " started after deployment in Tenant:" + tenantDomain2);
        assertFalse(webAppStatusTenant2WebApp1.isWebAppGhost(), "Web-App: " + WEB_APP_FILE_NAME1 + " is in" +
                " ghost mode after deployment in Tenant:" + tenantDomain2);

        webAppAdminClient.uploadWarFile(webApp2Location);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, WEB_APP_NAME2),
                "Web Application deployment failed: " + WEB_APP_NAME2 + "on " + tenantDomain2);
        WebAppStatusBean webAppStatusTenant2WebApp2 = getWebAppStatus(tenantDomain2, WEB_APP_FILE_NAME2);
        assertTrue(webAppStatusTenant2WebApp2.getTenantStatus().isTenantContextLoaded(), " Tenant Context is" +
                " not loaded. Tenant:" + tenantDomain2);
        assertTrue(webAppStatusTenant2WebApp2.isWebAppStarted(), "Web-App: " + WEB_APP_FILE_NAME2 + " is not" +
                " started after deployment in Tenant:" + tenantDomain2);
        assertFalse(webAppStatusTenant2WebApp2.isWebAppGhost(), "Web-App: " + WEB_APP_FILE_NAME2 + " is in " +
                "ghost mode after deployment in Tenant:" + tenantDomain2);
        log.info("deployment of web application finished");
    }

    @Test(groups = "wso2.as.lazy.loading", description = "Invoke web application in Ghost Deployment " +
            "enable environment.First test will restart the server gracefully.After the restart all tenant context " +
            "should not be loaded.Then, it invokes the first web app on first tenant. After the invoke, only that" +
            " web app should loaded fully.",
            dependsOnMethods = "testDeployWebApplicationInGhostDeployment")
    public void testInvokeWebAppInGhostDeployment() throws Exception {
        serverManager.restartGracefully();
        assertFalse(getTenantStatus(tenantDomain1).isTenantContextLoaded(), "Tenant Name:" +
                tenantDomain1 + " loaded before access.");
        assertFalse(getTenantStatus(tenantDomain2).isTenantContextLoaded(), "Tenant Name:" +
                tenantDomain2 + " loaded before access.");
        HttpResponse httpResponse = HttpURLConnectionClient.sendGetRequest(tenant1WebApp1URL, null);
        assertEquals(httpResponse.getData(), WEB_APP1_RESPONSE,
                "Web app invocation fail. web app URL:" + tenant1WebApp1URL);
        WebAppStatusBean webAppStatusTenant1WebApp1 = getWebAppStatus(tenantDomain1, WEB_APP_FILE_NAME1);
        assertTrue(webAppStatusTenant1WebApp1.getTenantStatus().isTenantContextLoaded(), " Tenant Context is" +
                " not loaded. Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp1.isWebAppStarted(), "Web-App: " + WEB_APP_FILE_NAME1 + " is not" +
                " started in Tenant:" + tenantDomain1);
        assertFalse(webAppStatusTenant1WebApp1.isWebAppGhost(), "Web-App: " + WEB_APP_FILE_NAME1 + " is in " +
                "ghost mode after invoking in Tenant:" + tenantDomain1);
        WebAppStatusBean webAppStatusTenant1WebApp2 = getWebAppStatus(tenantDomain1, WEB_APP_FILE_NAME2);
        assertTrue(webAppStatusTenant1WebApp2.getTenantStatus().isTenantContextLoaded(), " Tenant Context is" +
                " not loaded. Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp2.isWebAppStarted(), "Web-App: " + WEB_APP_FILE_NAME2 + " is not " +
                "started  in Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp2.isWebAppGhost(), "Web-App: " + WEB_APP_FILE_NAME2 + " is loaded " +
                "before access and after access other web app in same Tenant:" + tenantDomain1);
        assertFalse(getTenantStatus(tenantDomain2).isTenantContextLoaded(), "Tenant Name:" + tenantDomain2
                + " loaded before access.");
    }


    @Test(groups = "wso2.as.lazy.loading", description = "Send a Get request after a web app is auto unload and reload" +
            " in to Ghost form. After access web app, it should be in fully load form  the Ghost form",
            dependsOnMethods = "testInvokeWebAppInGhostDeployment")
    public void testWebAppAutoUnLoadAndInvokeInGhostDeployment() throws LazyLoadingTestException {

        assertTrue(checkWebAppAutoUnloadingToGhostState(tenantDomain1, WEB_APP_FILE_NAME1),
                "Web-app is not un-loaded ane re-deployed in Ghost form after idle time pass. Tenant Name:"
                        + tenantDomain1 + " Web_app Name: " + WEB_APP_FILE_NAME1);
        HttpResponse httpResponse;
        try {
            httpResponse = HttpURLConnectionClient.sendGetRequest(tenant1WebApp1URL, null);
        } catch (IOException ioException) {
            String customErrorMessage = "IOException Exception when  send a GET request to" + tenant1WebApp1URL + "\n"
                    + ioException.getMessage();
            log.error(customErrorMessage);
            throw new LazyLoadingTestException(customErrorMessage, ioException);
        }
        assertEquals(httpResponse.getData(), WEB_APP1_RESPONSE, "Web app invocation fail");
        WebAppStatusBean webAppStatusTenant1WebApp1 = getWebAppStatus(tenantDomain1, WEB_APP_FILE_NAME1);
        assertTrue(webAppStatusTenant1WebApp1.getTenantStatus().isTenantContextLoaded(), " Tenant Context is" +
                " not loaded. Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp1.isWebAppStarted(), "Web-App: " + WEB_APP_FILE_NAME1 + " is not " +
                "started in Tenant:" + tenantDomain1);
        assertFalse(webAppStatusTenant1WebApp1.isWebAppGhost(), "Web-App: " + WEB_APP_FILE_NAME1 + " is in" +
                " ghost mode after invoking in Tenant:" + tenantDomain1);


    }


    @Test(groups = "wso2.as.lazy.loading", description = "Test web application auto unload  and reload in Ghost" +
            " form. After access web app, it should be in fully load form  but after configured web app idle time" +
            " pass it should get auto unload ne reload in Ghost form.",
            dependsOnMethods = "testWebAppAutoUnLoadAndInvokeInGhostDeployment")
    public void testWebAppAutoUnLoadAndReloadToGhostFormInGhostDeployment() throws Exception {
        serverManager.restartGracefully();
        HttpResponse httpResponse = HttpURLConnectionClient.sendGetRequest(tenant1WebApp1URL, null);
        assertEquals(httpResponse.getData(), WEB_APP1_RESPONSE, "Web app invocation fail");
        WebAppStatusBean webAppStatusTenant1WebApp1 = getWebAppStatus(tenantDomain1, WEB_APP_FILE_NAME1);
        assertTrue(webAppStatusTenant1WebApp1.getTenantStatus().isTenantContextLoaded(), " Tenant Context is" +
                " not loaded. Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp1.isWebAppStarted(), "Web-App: " + WEB_APP_FILE_NAME1 + " is not" +
                " started in Tenant:" + tenantDomain1);
        assertFalse(webAppStatusTenant1WebApp1.isWebAppGhost(), "Web-App: " + WEB_APP_FILE_NAME1 + " is in " +
                "ghost mode after invoking in Tenant:" + tenantDomain1);
        assertTrue(checkWebAppAutoUnloadingToGhostState(tenantDomain1, WEB_APP_FILE_NAME1),
                "Web-app is not un-loaded ane re-deployed in Ghost form after idle time pass. Tenant Name:"
                        + tenantDomain1 + " Web_app Name: " + WEB_APP_FILE_NAME1);
    }


    @Test(groups = "wso2.as.lazy.loading", description = "Test Unload of tenant configuration context  after tenant "
            + "idle time pass without any action with that tenant",
            dependsOnMethods = "testWebAppAutoUnLoadAndReloadToGhostFormInGhostDeployment")
    public void testTenantUnloadInIdleTimeAfterWebAPPUsageInGhostDeployment() throws Exception {
        serverManager.restartGracefully();
        assertFalse(getTenantStatus(tenantDomain1).isTenantContextLoaded(),
                "Tenant context is  loaded before access. Tenant name: " + tenantDomain1);
        HttpResponse httpResponse = HttpURLConnectionClient.sendGetRequest(tenant1WebApp1URL, null);
        assertEquals(httpResponse.getData(), WEB_APP1_RESPONSE, "Web app invocation fail");
        assertTrue(getTenantStatus(tenantDomain1).isTenantContextLoaded(),
                "Tenant context is  not loaded after access. Tenant name: " + tenantDomain1);
        assertTrue(checkTenantAutoUnloading(tenantDomain1),
                "Tenant context is  not unloaded after idle time. Tenant name: " + tenantDomain1);
    }


    @Test(groups = "wso2.as.lazy.loading", description = "Send concurrent requests  when tenant context is not loaded." +
            "All request should  get expected output",
            dependsOnMethods = "testTenantUnloadInIdleTimeAfterWebAPPUsageInGhostDeployment", enabled = false)
    public void testConcurrentWebAPPInvocationsWhenTenantContextNotLoadedInGhostDeployment() throws Exception {
        //This test method case disable because of CARBON-15036
        serverManager.restartGracefully();
        assertFalse(getTenantStatus(tenantDomain1).isTenantContextLoaded(),
                "Tenant context is  loaded before access. Tenant name: " + tenantDomain1);
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_THREAD_COUNT);
        log.info("Concurrent invocation Start");
        log.info("Expected Response Data:" + WEB_APP1_RESPONSE);
        for (int i = 0; i < CONCURRENT_THREAD_COUNT; i++) {
            executorService.execute(new Runnable() {

                public void run() {
                    HttpResponse httpResponse = null;
                    try {
                        httpResponse = HttpURLConnectionClient.sendGetRequest(tenant1WebApp1URL, null);
                    } catch (IOException e) {
                        log.error("Error  when sending a  get request  for :" + tenant1WebApp1URL, e);
                    }
                    synchronized (this) {
                        String responseDetailedInfo;
                        String responseData;
                        if (httpResponse != null) {
                            responseDetailedInfo = "Response Data :" + httpResponse.getData() +
                                    "\tResponse Code:" + httpResponse.getResponseCode();
                            responseData = httpResponse.getData();
                        } else {
                            responseDetailedInfo = "Response Data : NULL Object return from HttpURLConnectionClient";
                            responseData = "NULL Object return";
                        }
                        responseDataList.add(responseData);
                        log.info(responseDetailedInfo);
                        responseDetailedInfoList.add(responseDetailedInfo);
                    }
                }

            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.MINUTES);
        log.info("Concurrent invocation End");

        int correctResponseCount = 0;
        for (String responseData : responseDataList) {
            if (WEB_APP1_RESPONSE.equals(responseData)) {
                correctResponseCount += 1;
            }
        }
        StringBuilder allDetailResponseStringBuffer = new StringBuilder();
        allDetailResponseStringBuffer.append("\n");

        for (String responseInfo : responseDetailedInfoList) {
            allDetailResponseStringBuffer.append(responseInfo);
            allDetailResponseStringBuffer.append("\n");
        }
        String allDetailResponse = allDetailResponseStringBuffer.toString();
        WebAppStatusBean webAppStatusTenant1WebApp1 = getWebAppStatus(tenantDomain1, WEB_APP_FILE_NAME1);
        assertTrue(webAppStatusTenant1WebApp1.getTenantStatus().isTenantContextLoaded(), " Tenant Context is" +
                " not loaded. Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp1.isWebAppStarted(), "Web-App: " + WEB_APP_FILE_NAME1 + " is not" +
                " started in Tenant:" + tenantDomain1);
        assertFalse(webAppStatusTenant1WebApp1.isWebAppGhost(), "Web-App: " + WEB_APP_FILE_NAME1 + " is in " +
                "ghost mode after invoking in Tenant:" + tenantDomain1);
        assertEquals(correctResponseCount, CONCURRENT_THREAD_COUNT, allDetailResponse + "All the concurrent requests " +
                "not get correct response.");
    }


    @Test(groups = "wso2.as.lazy.loading", description = "Send concurrent requests  when tenant context is loaded." +
            " But Web-App is in Ghost form. All request should  get expected output",
            dependsOnMethods = "testConcurrentWebAPPInvocationsWhenTenantContextNotLoadedInGhostDeployment", enabled = false)
    public void testConcurrentWebAPPInvocationsWhenTenantContextLoadedInGhostDeployment() throws Exception {
        //This test method case disable because of CARBON-15036
        serverManager.restartGracefully();
        assertFalse(getTenantStatus(tenantDomain1).isTenantContextLoaded(),
                "Tenant context is  loaded before access. Tenant name: " + tenantDomain1);
        HttpResponse httpResponseApp2 = HttpURLConnectionClient.sendGetRequest(tenant1WebApp2URL, null);
        assertTrue(httpResponseApp2.getData().contains(WEB_APP2_RESPONSE), "Invocation of Web-App fail :"
                + tenant1WebApp2URL);
        assertTrue(getTenantStatus(tenantDomain1).isTenantContextLoaded(),
                "Tenant context is  not loaded after access. Tenant name: " + tenantDomain1);
        WebAppStatusBean webAppStatusTenant1WebApp2 = getWebAppStatus(tenantDomain1, WEB_APP_FILE_NAME2);
        assertTrue(webAppStatusTenant1WebApp2.isWebAppStarted(), "Web-App: " + WEB_APP_FILE_NAME2 +
                " is not started in Tenant:" + tenantDomain1);
        assertFalse(webAppStatusTenant1WebApp2.isWebAppGhost(), "Web-App: " + WEB_APP_FILE_NAME2 +
                " is in ghost mode after invoking in Tenant:" + tenantDomain1);
        WebAppStatusBean webAppStatusTenant1WebApp1 = getWebAppStatus(tenantDomain1, WEB_APP_FILE_NAME1);
        assertTrue(webAppStatusTenant1WebApp1.isWebAppStarted(), "Web-App: " + WEB_APP_FILE_NAME1 +
                " is not started in Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp1.isWebAppGhost(), "Web-App: " + WEB_APP_FILE_NAME1 +
                " is in not ghost mode before invoking in Tenant:" + tenantDomain1);

        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_THREAD_COUNT);
        log.info("Concurrent invocation Start");
        log.info("Expected Response Data:" + WEB_APP1_RESPONSE);
        for (int i = 0; i < CONCURRENT_THREAD_COUNT; i++) {
            executorService.execute(new Runnable() {

                public void run() {
                    HttpResponse httpResponse = null;
                    try {
                        httpResponse = HttpURLConnectionClient.sendGetRequest(tenant1WebApp1URL, null);
                    } catch (IOException e) {
                        log.error("Error  when sending a  get request  for :" + tenant1WebApp1URL, e);
                    }
                    synchronized (this) {
                        String responseDetailedInfo;
                        String responseData;
                        if (httpResponse != null) {
                            responseDetailedInfo = "Response Data :" + httpResponse.getData() +
                                    "\tResponse Code:" + httpResponse.getResponseCode();
                            responseData = httpResponse.getData();
                        } else {
                            responseDetailedInfo = "Response Data : NULL Object return from HttpURLConnectionClient";
                            responseData = "NULL Object return";
                        }
                        responseDataList.add(responseData);
                        log.info(responseDetailedInfo);
                        responseDetailedInfoList.add(responseDetailedInfo);
                    }
                }

            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.MINUTES);
        log.info("Concurrent invocation End");
        int correctResponseCount = 0;
        for (String responseData : responseDataList) {
            if (WEB_APP1_RESPONSE.equals(responseData)) {
                correctResponseCount += 1;
            }
        }
        StringBuilder allDetailResponseStringBuffer = new StringBuilder();
        allDetailResponseStringBuffer.append("\n");
        for (String responseInfo : responseDetailedInfoList) {
            allDetailResponseStringBuffer.append(responseInfo);
            allDetailResponseStringBuffer.append("\n");
        }
        String allDetailResponse = allDetailResponseStringBuffer.toString();
        webAppStatusTenant1WebApp1 = getWebAppStatus(tenantDomain1, WEB_APP_FILE_NAME1);
        assertTrue(webAppStatusTenant1WebApp1.getTenantStatus().isTenantContextLoaded(), " Tenant Context " +
                "is not loaded. Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp1.isWebAppStarted(), "Web-App: " + WEB_APP_FILE_NAME1 +
                " is not started in Tenant:" + tenantDomain1);
        assertFalse(webAppStatusTenant1WebApp1.isWebAppGhost(), "Web-App: " + WEB_APP_FILE_NAME1 +
                " is in ghost mode after invoking in Tenant:" + tenantDomain1);
        assertEquals(correctResponseCount, CONCURRENT_THREAD_COUNT, allDetailResponse + "All the concurrent" +
                " requests not get correct response.");


    }


    @AfterClass(alwaysRun = true)
    public void cleanWebApplications() throws Exception {
        //Tenant1
        init(TENANT_DOMAIN_1_KEY, ADMIN);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.deleteWebAppFile(WEB_APP_FILE_NAME1, hostURL);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, WEB_APP_NAME1),
                "Web Application un-deployment failed : Web app :" + WEB_APP_NAME1 + " on " + tenantDomain1);
        webAppAdminClient.deleteWebAppFile(WEB_APP_FILE_NAME2, hostURL);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, WEB_APP_NAME2),
                "Web Application un-deployment failed: Web app :" + WEB_APP_NAME2 + " on " + tenantDomain1);
        //Tenant2
        init(TENANT_DOMAIN_2_KEY, ADMIN);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.deleteWebAppFile(WEB_APP_FILE_NAME1, hostURL);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, WEB_APP_NAME1),
                "Web Application un-deployment failed : Web app :" + WEB_APP_NAME1 + " on " + tenantDomain2);
        webAppAdminClient.deleteWebAppFile(WEB_APP_FILE_NAME2, hostURL);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, WEB_APP_NAME2),
                "Web Application un-deployment failed: Web app :" + WEB_APP_NAME2 + " on " + tenantDomain2);

    }

}
