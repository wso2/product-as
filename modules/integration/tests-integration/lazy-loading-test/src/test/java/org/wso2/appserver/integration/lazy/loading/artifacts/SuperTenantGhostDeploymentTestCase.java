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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test the ghost deployment of web application in Super  tenant (carbon.super). For this  two  web applications
 * will be deployed.
 */
public class SuperTenantGhostDeploymentTestCase extends LazyLoadingBaseTest {

    private static final Log log = LogFactory.getLog(SuperTenantGhostDeploymentTestCase.class);

    private static final String WEB_APP_FILE_NAME1 = "appServer-valied-deploymant-1.0.0.war";
    private static final String WEB_APP_NAME1 = "appServer-valied-deploymant-1.0.0";
    private static final String WEB_APP_FILE_NAME2 = "helloworld.war";
    private static final String WEB_APP_NAME2 = "helloworld";
    private static final String WEB_APP1_RESPONSE = "<status>success</status>";
    private static final String WEB_APP2_RESPONSE = "<h2>Hello, World.</h2>";
    private String webApp1URL;
    private String webApp2URL;
    private static volatile List<String> responseDataList = new ArrayList<String>();
    private static volatile List<String> responseDetailedInfoList = new ArrayList<String>();
    private static String WEB_APP1_LOCATION;
    private static String WEB_APP2_LOCATION;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();

        WEB_APP1_LOCATION = ARTIFACTS_LOCATION + WEB_APP_FILE_NAME1;
        WEB_APP2_LOCATION = ARTIFACTS_LOCATION + WEB_APP_FILE_NAME2;

        webApp1URL = webAppURL + "/" + WEB_APP_NAME1 + "/";
        webApp2URL = webAppURL + "/" + WEB_APP_NAME2 + "/hi.jsp";

    }

    @Test(groups = "wso2.as.lazy.loading", description = "Deploying web application in Ghost Deployment enable" +
            " environment. Each Web application should fully loaded (non Ghost format) soon after the deployment",
            alwaysRun = true)
    public void testDeployWebApplicationInGhostDeploymentOnSuperTenant() throws Exception {
        log.info("deployment of  web application started");

        loginAsTenantAdmin(SUPER_TENANT_DOMAIN_KEY);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);

        webAppAdminClient.warFileUplaoder(WEB_APP1_LOCATION);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, WEB_APP_NAME1),
                "Web Application deployment failed: " + WEB_APP_NAME1 + "on " + TENANT_DOMAIN_1);

        WebAppStatus webAppStatusWebApp1 = getWebAppStatus(SUPER_TENANT_DOMAIN, WEB_APP_FILE_NAME1);
        assertEquals(webAppStatusWebApp1.isWebAppStarted(), true, "Web-App: " + WEB_APP_FILE_NAME1 +
                " is not started after deployment in  Supper Tenant");
        assertEquals(webAppStatusWebApp1.isWebAppGhost(), false, "Web-App: " + WEB_APP_FILE_NAME1 +
                " is in ghost mode after deployment in Supper Tenant");

        webAppAdminClient.warFileUplaoder(WEB_APP2_LOCATION);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, WEB_APP_NAME2),
                "Web Application deployment failed: " + WEB_APP_NAME2 + "on " + TENANT_DOMAIN_1);


        WebAppStatus webAppStatusWebApp2 = getWebAppStatus(SUPER_TENANT_DOMAIN, WEB_APP_FILE_NAME2);
        assertEquals(webAppStatusWebApp2.isWebAppStarted(), true, "Web-App: " + WEB_APP_FILE_NAME2 +
                " is not started after deployment in  Supper Tenant");
        assertEquals(webAppStatusWebApp2.isWebAppGhost(), false, "Web-App: " + WEB_APP_FILE_NAME2 +
                " is in ghost mode after deployment in Supper Tenant");


        log.info("deployment of web application finished");

    }

    @Test(groups = "wso2.as.lazy.loading", description = "Invoke web application in Ghost Deployment enable" +
            " environment.First test will restart the server gracefully.After the restart  all web apps should be in" +
            " ghost format.Then,  it invokes the first web app on first tenant. After the invoke, only that web app " +
            "should loaded fully and all other web apps should be in Ghost format.",
            dependsOnMethods = "testDeployWebApplicationInGhostDeploymentOnSuperTenant")
    public void testInvokeWebAppInGhostDeploymentOnSuperTenant() throws Exception {

        serverManager.restartGracefully();


        WebAppStatus webAppStatusWebApp1 = getWebAppStatus(SUPER_TENANT_DOMAIN, WEB_APP_FILE_NAME1);
        assertEquals(webAppStatusWebApp1.isWebAppStarted(), true, "Web-App: " + WEB_APP_FILE_NAME1 +
                " is not started in  Supper Tenant");
        assertEquals(webAppStatusWebApp1.isWebAppGhost(), true, "Web-App: " + WEB_APP_FILE_NAME1 +
                " is not in ghost mode before invoking in Supper Tenant");


        WebAppStatus webAppStatusWebApp2 = getWebAppStatus(SUPER_TENANT_DOMAIN, WEB_APP_FILE_NAME2);
        assertEquals(webAppStatusWebApp2.isWebAppStarted(), true, "Web-App: " + WEB_APP_FILE_NAME2 +
                " is not started in  SupperTenant");
        assertEquals(webAppStatusWebApp2.isWebAppGhost(), true, "Web-App: " + WEB_APP_FILE_NAME2 +
                " is not in ghost mode before invoking in Supper Tenant");


        HttpResponse httpResponse = HttpURLConnectionClient.sendGetRequest(webApp1URL, null);
        assertEquals(httpResponse.getData(), WEB_APP1_RESPONSE, "Web app invocation fail. web app URL:" + webApp1URL);


        webAppStatusWebApp1 = getWebAppStatus(SUPER_TENANT_DOMAIN, WEB_APP_FILE_NAME1);
        assertEquals(webAppStatusWebApp1.isWebAppStarted(), true, "Web-App: " + WEB_APP_FILE_NAME1 +
                " is not started in  Supper Tenant");
        assertEquals(webAppStatusWebApp1.isWebAppGhost(), false, "Web-App: " + WEB_APP_FILE_NAME1 +
                " is  in ghost mode after invoking in Supper Tenant");


        webAppStatusWebApp2 = getWebAppStatus(SUPER_TENANT_DOMAIN, WEB_APP_FILE_NAME2);
        assertEquals(webAppStatusWebApp2.isWebAppStarted(), true, "Web-App: " + WEB_APP_FILE_NAME2 +
                " is not started in  SupperTenant");
        assertEquals(webAppStatusWebApp2.isWebAppGhost(), true, "Web-App: " + WEB_APP_FILE_NAME2 +
                " is not in ghost mode before invoking the other web-app of in Supper Tenant");

    }

    @Test(groups = "wso2.as.lazy.loading", description = "Send a Get request after a web app is auto unload  and reload" +
            " in to Ghost form. After access web app, it should be in fully load form  the Ghost form",
            dependsOnMethods = "testInvokeWebAppInGhostDeploymentOnSuperTenant")
    public void testWebAppAutoUnLoadAndInvokeInGhostDeploymentOnSupperTenant() throws LazyLoadingTestException {

        assertEquals(checkWebAppAutoUnloadingToGhostState(SUPER_TENANT_DOMAIN, WEB_APP_FILE_NAME1), true,
                "Web-app is not un-loaded ane re-deployed in Ghost form after idle time pass in super tenant " +
                        "Web_app Name: " + WEB_APP_FILE_NAME1);
        HttpResponse httpResponse;
        try {
            httpResponse = HttpURLConnectionClient.sendGetRequest(webApp1URL, null);
        } catch (IOException ioException) {
            String customErrorMessage = "IOException Exception when  send a GET request to" + webApp1URL + "\n" + ioException.getMessage();
            log.error(customErrorMessage);
            throw new LazyLoadingTestException(customErrorMessage, ioException);
        }
        assertEquals(httpResponse.getData(), WEB_APP1_RESPONSE, "Web app invocation fail");


        WebAppStatus webAppStatusWebApp1 = getWebAppStatus(SUPER_TENANT_DOMAIN, WEB_APP_FILE_NAME1);
        assertEquals(webAppStatusWebApp1.isWebAppStarted(), true, "Web-App: " + WEB_APP_FILE_NAME1 +
                " is not started in  Supper Tenant");
        assertEquals(webAppStatusWebApp1.isWebAppGhost(), false, "Web-App: " + WEB_APP_FILE_NAME1 +
                " is  in ghost mode after invoking in Supper Tenant");


    }


    @Test(groups = "wso2.as.lazy.loading", description = "Test web application auto unload  and reload in Ghost" +
            " format. After access web app, it should be in fully load form  but after configured web app idle time pass" +
            " it should get auto unload ne reload in Ghost form.",
            dependsOnMethods = "testWebAppAutoUnLoadAndInvokeInGhostDeploymentOnSupperTenant")
    public void testWebAppAutoUnLoadAndReloadInGhostFormInGhostDeploymentOnSuperTenant() throws Exception {
        serverManager.restartGracefully();

        HttpResponse httpResponse = HttpURLConnectionClient.sendGetRequest(webApp1URL, null);
        assertEquals(httpResponse.getData(), WEB_APP1_RESPONSE, "Web app invocation fail");


        WebAppStatus webAppStatusWebApp1 = getWebAppStatus(SUPER_TENANT_DOMAIN, WEB_APP_FILE_NAME1);
        assertEquals(webAppStatusWebApp1.isWebAppStarted(), true, "Web-App: " + WEB_APP_FILE_NAME1 +
                " is not started in  Supper Tenant");
        assertEquals(webAppStatusWebApp1.isWebAppGhost(), false, "Web-App: " + WEB_APP_FILE_NAME1 +
                " is  in ghost mode after invoking in Supper Tenant");


        assertEquals(checkWebAppAutoUnloadingToGhostState(SUPER_TENANT_DOMAIN, WEB_APP_FILE_NAME1), true,
                "Web-app is not un-loaded ane re-deployed in Ghost form after idle time pass in super tenant " +
                        "Web_app Name: " + WEB_APP_FILE_NAME1);
    }


    @Test(groups = "wso2.as.lazy.loading",
            description = "Send concurrent requests  when Web-App is in Ghost form. " + "All request should  get expected output",
            dependsOnMethods = "testWebAppAutoUnLoadAndReloadInGhostFormInGhostDeploymentOnSuperTenant", enabled = false)
    public void testConcurrentWebAPPInvocationsWhenWebAppIsInGhostFormInGhostDeploymentOnSuperTenant() throws Exception {
        //This test method case disable because of CARBON-15036
        serverManager.restartGracefully();
        HttpResponse httpResponseApp2 = HttpURLConnectionClient.sendGetRequest(webApp2URL, null);
        assertTrue(httpResponseApp2.getData().contains(WEB_APP2_RESPONSE), "Invocation of Web-App fail :" + webApp2URL);
        WebAppStatus webAppStatusTenant1WebApp2 = getWebAppStatus(SUPER_TENANT_DOMAIN, WEB_APP_FILE_NAME2);
        assertEquals(webAppStatusTenant1WebApp2.isWebAppStarted(), true, "Web-App: " + WEB_APP_FILE_NAME2 +
                " is not started in Tenant:" + SUPER_TENANT_DOMAIN);
        assertEquals(webAppStatusTenant1WebApp2.isWebAppGhost(), false, "Web-App: " + WEB_APP_FILE_NAME2 +
                " is in ghost mode after invoking in Tenant:" + SUPER_TENANT_DOMAIN);


        WebAppStatus webAppStatusTenant1WebApp1 = getWebAppStatus(SUPER_TENANT_DOMAIN, WEB_APP_FILE_NAME1);
        assertEquals(webAppStatusTenant1WebApp1.isWebAppStarted(), true, "Web-App: " + WEB_APP_FILE_NAME1 +
                " is not started in Tenant:" + SUPER_TENANT_DOMAIN);
        assertEquals(webAppStatusTenant1WebApp1.isWebAppGhost(), true, "Web-App: " + WEB_APP_FILE_NAME1 +
                " is in not ghost mode before invoking in Tenant:" + SUPER_TENANT_DOMAIN);


        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_THREAD_COUNT);
        log.info("Concurrent invocation Start");
        log.info("Expected Response Data:" + WEB_APP1_RESPONSE);
        for (int i = 0; i < CONCURRENT_THREAD_COUNT; i++) {
            executorService.execute(new Runnable() {

                public void run() {
                    HttpResponse httpResponse = null;
                    try {
                        httpResponse = HttpURLConnectionClient.sendGetRequest(webApp1URL, null);
                    } catch (IOException e) {
                        log.error("Error  when sending a  get request  for :" + webApp1URL, e);
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
        webAppStatusTenant1WebApp1 = getWebAppStatus(SUPER_TENANT_DOMAIN, WEB_APP_FILE_NAME1);
        assertEquals(webAppStatusTenant1WebApp1.isWebAppStarted(), true, "Web-App: " + WEB_APP_FILE_NAME1 +
                " is not started in Tenant:" + SUPER_TENANT_DOMAIN);
        assertEquals(webAppStatusTenant1WebApp1.isWebAppGhost(), false, "Web-App: " + WEB_APP_FILE_NAME1 +
                " is in ghost mode after invoking in Tenant:" + SUPER_TENANT_DOMAIN);

        assertEquals(correctResponseCount, CONCURRENT_THREAD_COUNT, allDetailResponse +
                "All the concurrent requests not get correct response.");


    }

    @AfterClass
    public void cleanWebApplications() throws Exception {

        loginAsTenantAdmin(SUPER_TENANT_DOMAIN_KEY);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);

        webAppAdminClient.deleteWebAppFile(WEB_APP_FILE_NAME1, hostURL);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, WEB_APP_NAME1),
                "Web Application un-deployment failed : Web app :" + WEB_APP_NAME1 + " on super tenant");
        webAppAdminClient.deleteWebAppFile(WEB_APP_FILE_NAME2, hostURL);
        assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(backendURL, sessionCookie, WEB_APP_NAME2),
                "Web Application un-deployment failed: Web app :" + WEB_APP_NAME2 + " on super tenant");


    }


}
