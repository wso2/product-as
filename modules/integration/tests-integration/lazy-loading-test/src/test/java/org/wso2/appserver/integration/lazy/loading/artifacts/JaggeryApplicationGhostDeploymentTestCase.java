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

package org.wso2.appserver.integration.lazy.loading.artifacts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.JaggeryApplicationUploaderClient;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.lazy.loading.LazyLoadingBaseTest;
import org.wso2.appserver.integration.lazy.loading.util.LazyLoadingTestException;
import org.wso2.appserver.integration.lazy.loading.util.WebAppStatus;
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
 * Test the ghost deployment of Jaggery application. For this test two tenants will be used and in each tenant two
 * Jaggery applications will be deployed.
 */
public class JaggeryApplicationGhostDeploymentTestCase extends LazyLoadingBaseTest {

    private static final Log log = LogFactory.getLog(JaggeryApplicationGhostDeploymentTestCase.class);

    private static final String JAGGERY_APP_FILE_NAME1 = "hello.jag.zip";
    private static final String JAGGERY_APP_FILE_NAME2 = "bye.jag.zip";
    private static final String JAGGERY_APP_NAME1 = "hello";
    private static final String JAGGERY_APP_NAME2 = "bye";
    private static final String JAGG_APP1_RESPONSE = "Hello";
    private static final String JAGG_APP2_RESPONSE = "Bye";
    private String jagAppFilePath1;
    private String jagAppFilePath2;
    private String tenant1JaggApp1Url;
    private String tenant1JaggApp2Url;
    private volatile List<String> responseDataList = new ArrayList<String>();
    private volatile List<String> responseDetailedInfoList = new ArrayList<String>();

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        jagAppFilePath1 = artifactsLocation + JAGGERY_APP_FILE_NAME1;
        jagAppFilePath2 = artifactsLocation + JAGGERY_APP_FILE_NAME2;
        tenant1JaggApp1Url =
                webAppURL + "/t/" + tenantDomain1 + "/jaggeryapps/" + JAGGERY_APP_NAME1 + "/" + JAGGERY_APP_NAME1 + ".jag";
        tenant1JaggApp2Url =
                webAppURL + "/t/" + tenantDomain1 + "/jaggeryapps/" + JAGGERY_APP_NAME2 + "/" + JAGGERY_APP_NAME2 + ".jag";
    }

    @Test(groups = "wso2.as.lazy.loading", description = "Deploying Jaggery application in Ghost Deployment enable" +
            "environment. Each Jaggery application should fully loaded (non Ghost format) soon after the deployment",
            alwaysRun = true)
    public void testJaggeryApplicationInGhostDeployment()
            throws Exception {
        log.info("deployment of  Jaggery Application started");
        JaggeryApplicationUploaderClient jaggeryApplicationUploaderClient;
        //Tenant1
        loginAsTenantAdmin(TENANT_DOMAIN_1_KEY);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        jaggeryApplicationUploaderClient = new JaggeryApplicationUploaderClient(backendURL, sessionCookie);

        jaggeryApplicationUploaderClient.uploadJaggeryFile(JAGGERY_APP_FILE_NAME1, jagAppFilePath1);
        assertTrue(isJaggeryAppDeployed(JAGGERY_APP_NAME1),
                "Jaggery application  is not deployed correctly. App name: " + JAGGERY_APP_NAME1 + " Tenant :"
                        + tenantDomain1);
        WebAppStatus jaggeryAppStatusTenant1WebApp1 = getWebAppStatus(tenantDomain1, JAGGERY_APP_NAME1);
        assertTrue(jaggeryAppStatusTenant1WebApp1.getTenantStatus().isTenantContextLoaded(),
                " Tenant Context is not loaded. Tenant:" + tenantDomain1);
        assertTrue(jaggeryAppStatusTenant1WebApp1.isWebAppStarted(), "Jaggery application: " +
                JAGGERY_APP_NAME1 + " is not started after deployment in Tenant:" + tenantDomain1);
        assertFalse(jaggeryAppStatusTenant1WebApp1.isWebAppGhost(), "Jaggery application: " +
                JAGGERY_APP_NAME1 + " is in ghost mode after deployment in Tenant:" + tenantDomain1);
        jaggeryApplicationUploaderClient.uploadJaggeryFile(JAGGERY_APP_FILE_NAME2, jagAppFilePath2);
        assertTrue(isJaggeryAppDeployed(JAGGERY_APP_NAME2),
                "Jaggery application  is not deployed correctly. App name: " + JAGGERY_APP_NAME2 + " Tenant :"
                        + tenantDomain1);
        WebAppStatus jaggeryAppStatusTenant1WebApp2 = getWebAppStatus(tenantDomain1, JAGGERY_APP_NAME2);
        assertTrue(jaggeryAppStatusTenant1WebApp2.getTenantStatus().isTenantContextLoaded(),
                " Tenant Context is not loaded. Tenant:" + tenantDomain1);
        assertTrue(jaggeryAppStatusTenant1WebApp2.isWebAppStarted(), "Jaggery application: " +
                JAGGERY_APP_NAME2 + " is not started after deployment in Tenant:" + tenantDomain1);
        assertFalse(jaggeryAppStatusTenant1WebApp2.isWebAppGhost(), "Jaggery application: " +
                JAGGERY_APP_NAME2 + " is in ghost mode after deployment in Tenant:" + tenantDomain1);
        //Tenant2
        loginAsTenantAdmin(TENANT_DOMAIN_2_KEY);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        jaggeryApplicationUploaderClient = new JaggeryApplicationUploaderClient(backendURL, sessionCookie);

        jaggeryApplicationUploaderClient.uploadJaggeryFile(JAGGERY_APP_FILE_NAME1, jagAppFilePath1);
        assertTrue(isJaggeryAppDeployed(JAGGERY_APP_NAME1),
                "Jaggery application  is not deployed correctly. App name: " + JAGGERY_APP_NAME1 + " Tenant :"
                        + tenantDomain2);
        WebAppStatus jaggeryAppStatusTenant2WebApp1 = getWebAppStatus(tenantDomain2, JAGGERY_APP_NAME1);
        assertTrue(jaggeryAppStatusTenant2WebApp1.getTenantStatus().isTenantContextLoaded(),
                " Tenant Context is not loaded. Tenant:" + tenantDomain2);
        assertTrue(jaggeryAppStatusTenant2WebApp1.isWebAppStarted(), "Jaggery application: " +
                JAGGERY_APP_NAME1 + " is not started after deployment in Tenant:" + tenantDomain2);
        assertFalse(jaggeryAppStatusTenant2WebApp1.isWebAppGhost(), "Jaggery application: " +
                JAGGERY_APP_NAME1 + " is in ghost mode after deployment in Tenant:" + tenantDomain2);
        jaggeryApplicationUploaderClient.uploadJaggeryFile(JAGGERY_APP_FILE_NAME2, jagAppFilePath2);
        assertTrue(isJaggeryAppDeployed(JAGGERY_APP_NAME2),
                "Jaggery application  is not deployed correctly. App name: " + JAGGERY_APP_NAME2 + " Tenant :"
                        + tenantDomain2);
        WebAppStatus jaggeryAppStatusTenant2WebApp2 = getWebAppStatus(tenantDomain2, JAGGERY_APP_NAME2);
        assertTrue(jaggeryAppStatusTenant2WebApp2.getTenantStatus().isTenantContextLoaded(),
                " Tenant Context is not loaded. Tenant:" + tenantDomain2);
        assertTrue(jaggeryAppStatusTenant2WebApp2.isWebAppStarted(), "Jaggery application: " +
                JAGGERY_APP_NAME2 + " is not started after deployment in Tenant:" + tenantDomain2);
        assertFalse(jaggeryAppStatusTenant2WebApp2.isWebAppGhost(), "Jaggery application: " +
                JAGGERY_APP_NAME2 + " is in ghost mode after deployment in Tenant:" + tenantDomain2);


    }

    @Test(groups = "wso2.as.lazy.loading", description = "Invoke Jaggery application in Ghost Deployment enable " +
            "environment. First test will restart the server gracefully. After the restart  all tenant context should" +
            " not be loaded. Then the it invokes the first Jaggery app on first tenant. After the invoke, only that " +
            "Jaggery app should loaded.", dependsOnMethods = "testJaggeryApplicationInGhostDeployment")
    public void testInvokeJaggeryAppInGhostDeployment()
            throws Exception {
        serverManager.restartGracefully();
        assertFalse(getTenantStatus(tenantDomain1).isTenantContextLoaded(), " Tenant Name:" +
                tenantDomain1 + "loaded before access.");
        assertFalse(getTenantStatus(tenantDomain2).isTenantContextLoaded(), " Tenant Name:" +
                tenantDomain2 + "loaded before access.");
        HttpResponse httpResponse = HttpURLConnectionClient.sendGetRequest(tenant1JaggApp1Url, null);
        assertEquals(httpResponse.getData(), JAGG_APP1_RESPONSE,
                "Jaggery application invocation fail: " + tenant1JaggApp1Url);

        WebAppStatus webAppStatusTenant1WebApp1 = getWebAppStatus(tenantDomain1, JAGGERY_APP_NAME1);
        assertTrue(webAppStatusTenant1WebApp1.getTenantStatus().isTenantContextLoaded(),
                " Tenant Context is not loaded. Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp1.isWebAppStarted(), "Jaggery-app " + JAGGERY_APP_NAME1 +
                " is not started in Tenant:" + tenantDomain1);
        assertFalse(webAppStatusTenant1WebApp1.isWebAppGhost(), "Jaggery-app: " + JAGGERY_APP_NAME1 +
                " is in ghost mode after invoking in Tenant:" + tenantDomain1);

        WebAppStatus webAppStatusTenant1WebApp2 = getWebAppStatus(tenantDomain1, JAGGERY_APP_NAME2);
        assertTrue(webAppStatusTenant1WebApp2.getTenantStatus().isTenantContextLoaded(),
                " Tenant Context is not loaded. Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp2.isWebAppStarted(), "Jaggery-app : " + JAGGERY_APP_NAME2 +
                " is not started  in Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp2.isWebAppGhost(), "Jaggery-app : " + JAGGERY_APP_NAME2 +
                " is loaded before access and after access other web app in same Tenant:" + tenantDomain1);


        assertFalse(getTenantStatus(tenantDomain2).isTenantContextLoaded(), " Tenant Name:" +
                tenantDomain2 + "loaded before access.");

    }


    @Test(groups = "wso2.as.lazy.loading", description = "Send a Get request after a Jaggery application is auto " +
            "unload  and reload in to Ghost form. After access Jaggery application, it should be in fully load form" +
            " the Ghost form", dependsOnMethods = "testInvokeJaggeryAppInGhostDeployment")
    public void testJaggeryAppAutoUnLoadAndInvokeInGhostDeployment()
            throws LazyLoadingTestException {
        assertTrue(checkWebAppAutoUnloadingToGhostState(tenantDomain1, JAGGERY_APP_NAME1),
                "Web-app is not un-loaded ane re-deployed in Ghost form after idle time pass. Tenant Name:"
                        + tenantDomain1 + " Web_app Name: " + JAGGERY_APP_NAME1);
        HttpResponse httpResponse;
        try {
            httpResponse = HttpURLConnectionClient
                    .sendGetRequest(tenant1JaggApp1Url, null);
        } catch (IOException ioException) {
            String customErrorMessage = "IOException Exception when  send a GET request to" + tenant1JaggApp1Url +
                    "\n" + ioException.getMessage();
            log.error(customErrorMessage);
            throw new LazyLoadingTestException(customErrorMessage, ioException);
        }
        assertEquals(httpResponse.getData(), JAGG_APP1_RESPONSE,
                "Jaggery application invocation fail: " + tenant1JaggApp1Url);
        WebAppStatus webAppStatusTenant1WebApp1 = getWebAppStatus(tenantDomain1, JAGGERY_APP_NAME1);
        assertTrue(webAppStatusTenant1WebApp1.getTenantStatus().isTenantContextLoaded(),
                " Tenant Context is not loaded. Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp1.isWebAppStarted(), "Jaggery-app " + JAGGERY_APP_NAME1 +
                " is not started in Tenant:" + tenantDomain1);
        assertFalse(webAppStatusTenant1WebApp1.isWebAppGhost(), "Jaggery-app: " + JAGGERY_APP_NAME1 +
                " is in ghost mode after invoking in Tenant:" + tenantDomain1);

    }


    @Test(groups = "wso2.as.lazy.loading", description = "Test Jaggery application auto unload  and reload in Ghost" +
            " format. After access Jaggery app, it should be in fully load form  but after configured Jaggery app" +
            " idle time pass it should get auto unload ne reload in Ghost form.",
            dependsOnMethods = "testJaggeryAppAutoUnLoadAndInvokeInGhostDeployment")
    public void testJaggeryAppAutoUnLoadAndReloadInGhostFormInGhostDeployment()
            throws Exception {
        serverManager.restartGracefully();
        HttpResponse httpResponse = HttpURLConnectionClient.sendGetRequest(tenant1JaggApp1Url, null);
        assertEquals(httpResponse.getData(), JAGG_APP1_RESPONSE,
                "Jaggery application invocation fail: " + tenant1JaggApp1Url);
        WebAppStatus webAppStatusTenant1WebApp1 = getWebAppStatus(tenantDomain1, JAGGERY_APP_NAME1);
        assertTrue(webAppStatusTenant1WebApp1.getTenantStatus().isTenantContextLoaded(),
                " Tenant Context is not loaded. Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp1.isWebAppStarted(), "Jaggery-app " + JAGGERY_APP_NAME1 +
                " is not started in Tenant:" + tenantDomain1);
        assertFalse(webAppStatusTenant1WebApp1.isWebAppGhost(), "Jaggery-app: " + JAGGERY_APP_NAME1 +
                " is in ghost mode after invoking in Tenant:" + tenantDomain1);
        assertTrue(checkWebAppAutoUnloadingToGhostState(tenantDomain1, JAGGERY_APP_NAME1),
                "Web-app is not un-loaded ane re-deployed in Ghost form after idle time pass. Tenant Name:"
                        + tenantDomain1 + " Web_app Name: " + JAGGERY_APP_NAME1);

    }

    @Test(groups = "wso2.as.lazy.loading", description = "Test Unload of tenant configuration context  after tenant "
            + "idle time pass without any action with that tenant",
            dependsOnMethods = "testJaggeryAppAutoUnLoadAndReloadInGhostFormInGhostDeployment")
    public void testTenantUnloadInIdleTimeAfterJaggeryAPPUsageInGhostDeployment()
            throws Exception {
        serverManager.restartGracefully();

        assertFalse(getTenantStatus(tenantDomain1).isTenantContextLoaded(),
                "Tenant context is  loaded before access. Tenant name: " + tenantDomain1);
        HttpResponse httpResponse = HttpURLConnectionClient.sendGetRequest(tenant1JaggApp1Url, null);
        assertEquals(httpResponse.getData(), JAGG_APP1_RESPONSE,
                "Jaggery application invocation fail: " + tenant1JaggApp1Url);
        assertTrue(getTenantStatus(tenantDomain1).isTenantContextLoaded(),
                "Tenant context is  not loaded after access. Tenant name: " + tenantDomain1);
        assertTrue(checkTenantAutoUnloading(tenantDomain1),
                "Tenant context is  not unloaded after idle time. Tenant name: " + tenantDomain1);

    }


    @Test(groups = "wso2.as.lazy.loading", description = "Send concurrent requests  when tenant context is not loaded." +
            "All request should  get expected output",
            dependsOnMethods = "testTenantUnloadInIdleTimeAfterJaggeryAPPUsageInGhostDeployment", enabled = false)
    public void testConcurrentJaggeryAPPInvocationsWhenTenantContextNotLoadedInGhostDeployment() throws Exception {
        //This test method case disable because of CARBON-15036
        serverManager.restartGracefully();
        assertFalse(getTenantStatus(tenantDomain1).isTenantContextLoaded(),
                "Tenant context is  loaded before access. Tenant name: " + tenantDomain1);
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_THREAD_COUNT);
        log.info("Concurrent invocation Start");
        log.info("Expected Response Data:" + JAGG_APP1_RESPONSE);
        for (int i = 0; i < CONCURRENT_THREAD_COUNT; i++) {
            executorService.execute(new Runnable() {

                public void run() {
                    HttpResponse httpResponse = null;
                    try {
                        httpResponse = HttpURLConnectionClient.sendGetRequest(tenant1JaggApp1Url, null);
                    } catch (IOException e) {
                        log.error("Error  when sending a  get request  for :" + tenant1JaggApp1Url, e);
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
            if (JAGG_APP1_RESPONSE.equals(responseData)) {
                correctResponseCount += 1;
            }
        }
        StringBuffer allDetailResponseStringBuffer = new StringBuffer();
        allDetailResponseStringBuffer.append("\n");
        for (String responseInfo : responseDetailedInfoList) {
            allDetailResponseStringBuffer.append(responseInfo);
            allDetailResponseStringBuffer.append("\n");
        }
        String allDetailResponse = allDetailResponseStringBuffer.toString();
        WebAppStatus webAppStatusTenant1WebApp1 = getWebAppStatus(tenantDomain1, JAGGERY_APP_NAME1);
        assertTrue(webAppStatusTenant1WebApp1.getTenantStatus().isTenantContextLoaded(),
                " Tenant Context is not loaded. Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp1.isWebAppStarted(), "Web-App: " + JAGGERY_APP_NAME1 +
                " is not started in Tenant:" + tenantDomain1);
        assertFalse(webAppStatusTenant1WebApp1.isWebAppGhost(), "Web-App: " + JAGGERY_APP_NAME1 +
                " is in ghost mode after invoking in Tenant:" + tenantDomain1);
        assertEquals(correctResponseCount, CONCURRENT_THREAD_COUNT, allDetailResponse + "All the concurrent requests " +
                "not get correct response.");


    }


    @Test(groups = "wso2.as.lazy.loading", description = "Send concurrent requests  when tenant context is loaded." +
            " But Jaggery application is in Ghost form. All request should  get expected output",
            dependsOnMethods = "testConcurrentJaggeryAPPInvocationsWhenTenantContextNotLoadedInGhostDeployment", enabled = false)
    public void testConcurrentJaggeryAPPInvocationsWhenTenantContextLoadedInGhostDeployment() throws Exception {
        //This test method case disable because of CARBON-15036
        serverManager.restartGracefully();
        assertFalse(getTenantStatus(tenantDomain1).isTenantContextLoaded(),
                "Tenant context is  loaded before access. Tenant name: " + tenantDomain1);
        HttpResponse httpResponseApp2 = HttpURLConnectionClient.sendGetRequest(tenant1JaggApp2Url, null);
        assertTrue(httpResponseApp2.getData().contains(JAGG_APP2_RESPONSE), "Invocation of Web-App fail :" +
                tenant1JaggApp2Url);
        assertTrue(getTenantStatus(tenantDomain1).isTenantContextLoaded(),
                "Tenant context is  not loaded after access. Tenant name: " + tenantDomain1);
        WebAppStatus webAppStatusTenant1WebApp2 = getWebAppStatus(tenantDomain1, JAGGERY_APP_NAME2);
        assertTrue(webAppStatusTenant1WebApp2.isWebAppStarted(), "Web-App: " + JAGGERY_APP_NAME2 +
                " is not started in Tenant:" + tenantDomain1);
        assertFalse(webAppStatusTenant1WebApp2.isWebAppGhost(), "Web-App: " + JAGGERY_APP_NAME2 +
                " is in ghost mode after invoking in Tenant:" + tenantDomain1);
        WebAppStatus webAppStatusTenant1WebApp1 = getWebAppStatus(tenantDomain1, JAGGERY_APP_NAME1);
        assertTrue(webAppStatusTenant1WebApp1.isWebAppStarted(), "Web-App: " + JAGGERY_APP_NAME1 +
                " is not started in Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp1.isWebAppGhost(), "Web-App: " + JAGGERY_APP_NAME1 +
                " is in not ghost mode before invoking in Tenant:" + tenantDomain1);
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_THREAD_COUNT);
        log.info("Concurrent invocation Start");
        log.info("Expected Response Data:" + JAGG_APP1_RESPONSE);
        for (int i = 0; i < CONCURRENT_THREAD_COUNT; i++) {
            executorService.execute(new Runnable() {

                public void run() {
                    HttpResponse httpResponse = null;
                    try {
                        httpResponse = HttpURLConnectionClient.sendGetRequest(tenant1JaggApp1Url, null);
                    } catch (IOException e) {
                        log.error("Error  when sending a  get request  for :" + tenant1JaggApp1Url, e);
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
            if (JAGG_APP1_RESPONSE.equals(responseData)) {
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
        webAppStatusTenant1WebApp1 = getWebAppStatus(tenantDomain1, JAGGERY_APP_NAME1);
        assertTrue(webAppStatusTenant1WebApp1.getTenantStatus().isTenantContextLoaded(),
                " Tenant Context is not loaded. Tenant:" + tenantDomain1);
        assertTrue(webAppStatusTenant1WebApp1.isWebAppStarted(), "Web-App: " + JAGGERY_APP_NAME1 +
                " is not started in Tenant:" + tenantDomain1);
        assertFalse(webAppStatusTenant1WebApp1.isWebAppGhost(), "Web-App: " + JAGGERY_APP_NAME1 +
                " is in ghost mode after invoking in Tenant:" + tenantDomain1);
        assertEquals(correctResponseCount, CONCURRENT_THREAD_COUNT, allDetailResponse + "All the concurrent requests" +
                " not get correct response.");


    }


    @AfterClass(alwaysRun = true)
    public void cleanJaggeryApplication() throws Exception {

        loginAsTenantAdmin(TENANT_DOMAIN_1_KEY);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.deleteWebAppFile(JAGGERY_APP_NAME1, "localhost");
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, JAGGERY_APP_NAME1),
                "Web Application un-deployment failed : Web app :" + JAGGERY_APP_FILE_NAME1 + " on " + tenantDomain1);
        webAppAdminClient.deleteWebAppFile(JAGGERY_APP_NAME2, "localhost");
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, JAGGERY_APP_NAME1),
                "Web Application un-deployment failed: Web app :" + JAGGERY_APP_FILE_NAME1 + " on " + tenantDomain1);

        loginAsTenantAdmin(TENANT_DOMAIN_2_KEY);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppAdminClient.deleteWebAppFile(JAGGERY_APP_NAME1, "localhost");
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, JAGGERY_APP_NAME1),
                "Web Application un-deployment failed : Web app :" + JAGGERY_APP_FILE_NAME1 + " on " + tenantDomain2);
        webAppAdminClient.deleteWebAppFile(JAGGERY_APP_NAME2, "localhost");
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, JAGGERY_APP_NAME1),
                "Web Application un-deployment failed: Web app :" + JAGGERY_APP_FILE_NAME1 + " on " + tenantDomain2);
    }

}
