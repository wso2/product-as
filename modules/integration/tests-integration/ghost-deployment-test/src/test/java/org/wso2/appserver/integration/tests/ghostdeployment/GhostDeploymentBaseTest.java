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

package org.wso2.appserver.integration.tests.ghostdeployment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.application.mgt.stub.ApplicationAdminExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Base class to hold the common variables and utility methods of Ghost deployment Test cases.
 * All Ghost deployment Test  Classes must inherit from this.
 * Replacing the carbon.xml with Ghost deployment configuration and deployment of the
 * tenant-info-service web application is implemented in side init() method.
 */
public class GhostDeploymentBaseTest extends ASIntegrationTest {

    protected static final String TENANT_DOMAIN_1 = "tenant1.com";
    protected static final String TENANT_DOMAIN_2 = "tenant2.com";
    protected static final String SUPPER_TENANT_DOMAIN = "superTenant";

    protected WebAppAdminClient webAppAdminClient;
    protected ServerConfigurationManager serverManager;
    protected String hostURL;
    protected static final String CARBON_HOME = System.getProperty(ServerConstants.CARBON_HOME);
    protected static final String ARTIFACTS_LOCATION = TestConfigurationProvider.getResourceLocation() + File.separator +
            "artifacts" + File.separator + "AS" + File.separator + "ghost" + File.separator;

    private static final Log log = LogFactory.getLog(GhostDeploymentBaseTest.class);
    private static long TENANT_IDLE_TIME;
    private static long WEB_APP_IDLE_TIME;
    private static final long MAX_LOOP_TIME = 60 * 1000;

    private static final String TENANT_INFO_SERVICE = "tenant-info-service-webapp";
    private static final String TENANT_INFO_SERVICE_FILE_NAME = TENANT_INFO_SERVICE + ".war";
    private static final String IS_TENANT_LOADED_METHOD_URL = TENANT_INFO_SERVICE + "/isTenantLoaded?";
    private static final String IS_WEB_APP_LOADED_METHOD_URL = TENANT_INFO_SERVICE + "/isWebAppGhostStatus?";
    private static final String IS_SUPPER_TENANT_WEB_APP_LOADED_METHOD_URL = TENANT_INFO_SERVICE +
            "/isSuperTenantWebAppLoaded?";

    private static final String TENANT_INFO_SERVICE_ARTIFACT_LOCATION = ARTIFACTS_LOCATION + TENANT_INFO_SERVICE_FILE_NAME;


    private static final String TENANT_IDLE_XPATH = "//listenerExtensions/platformExecutionManager/extentionClasses/" +
            "*[name()='class']/*[name()='parameter'][@name='-Dtenant.idle.time']/@value";
    private static final String WEB_APP_IDLE_XPATH = "//listenerExtensions/platformExecutionManager/extentionClasses/" +
            "*[name()='class']/*[name()='parameter'][@name='-Dwebapp.idle.time']/@value";

    private static final String TENANT_NAME = "tenantName";
    private static final String WEB_APP_NAME = "webAppName";
    private static final String CARBON_XML = "carbon.xml";

    private static final String CARBON_ARTIFACT_LOCATION = ARTIFACTS_LOCATION + CARBON_XML;
    private static final String CARBON_REPOSITORY_LOCATION = CARBON_HOME + File.separator + "repository" +
            File.separator + "conf" + File.separator + CARBON_XML;


    @Override
    /**
     * Login as supper admin and do the replacement of carbon.xml with Ghost deployment
     * configuration and deployment of the tenant-info-service web application.
     * At the end it will restart the server gracefully.
     * @throws Exception
     */
    public void init() throws Exception {
        super.init();

        serverManager = new ServerConfigurationManager(asServer);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        TENANT_IDLE_TIME = Long.parseLong(asServer.getConfigurationNode(TENANT_IDLE_XPATH).getNodeValue()) * 60 * 1000;
        WEB_APP_IDLE_TIME = Long.parseLong(asServer.getConfigurationNode(WEB_APP_IDLE_XPATH).getNodeValue()) * 60 * 1000;


        File sourceFile = new File(CARBON_ARTIFACT_LOCATION);
        File targetFile = new File(CARBON_REPOSITORY_LOCATION);

        serverManager.applyConfigurationWithoutRestart(sourceFile, targetFile, true);
        log.info("carbon.xml replaced with :" + CARBON_ARTIFACT_LOCATION);

        webAppAdminClient.warFileUplaoder(TENANT_INFO_SERVICE_ARTIFACT_LOCATION);
        serverManager.restartGracefully();
        log.info("Server Restarted after applying carbon.xml and tenant information utility web application");

    }

    /**
     * This method will do a REST  GET call to the tenant-info-service deployed by init() method.
     * It will check the configuration context of given tenant domain is  loaded in the carbon server.
     *
     * @param tenantDomain Domain Name of the tenant.
     * @return true if Tenant configuration context if given tenant is loaded to the system
     * @throws IOException
     */
    protected boolean isTenantLoaded(String tenantDomain) throws IOException {
        HttpResponse response = HttpURLConnectionClient.sendGetRequest(
                webAppURL + "/" + IS_TENANT_LOADED_METHOD_URL + TENANT_NAME + "="
                        + tenantDomain, null);
        return Boolean.valueOf(response.getData());
    }

    /**
     * This method will do a REST  GET call to the tenant-info-service deployed by init() method.
     * IT will check the deployment status of given web application of given tenant is loaded in Ghost form.
     *
     * @param tenantDomain Domain Name of the tenant.
     * @param webAppName   Name of the Web Application
     * @return true if  web app is fully loaded (not in ghost form)
     * @throws IOException
     */
    protected boolean isWebAppLoaded(String tenantDomain, String webAppName) throws IOException {
        HttpResponse response = HttpURLConnectionClient.sendGetRequest(
                webAppURL + "/" + IS_WEB_APP_LOADED_METHOD_URL + TENANT_NAME + "="
                        + tenantDomain + "&" + WEB_APP_NAME + "=" + webAppName, null);
        return Boolean.valueOf(response.getData());
    }

    protected boolean isSupperTenantWebAppLoaded(String webAppName) throws IOException {
        HttpResponse response = HttpURLConnectionClient.sendGetRequest(
                webAppURL + "/" + IS_SUPPER_TENANT_WEB_APP_LOADED_METHOD_URL + WEB_APP_NAME + "=" + webAppName, null);
        return Boolean.valueOf(response.getData());
    }


    /**
     * Check the given Jaggary application is deployed correctly. This method is wait for 90 seconds
     * for deployment of jaggery application and each 500 milliseconds  of wait it will check the
     * deployment status.
     *
     * @param appName Name of the application.
     * @return true if the application is get deployed before the maximum wait time of 90 seconds.
     * @throws RemoteException
     * @throws InterruptedException
     */
    protected boolean isJaggeryAppDeployed(String appName) throws RemoteException, InterruptedException {
        int deploymentDelay = 90 * 1000;
        log.info("waiting " + deploymentDelay + " millis for Service deployment " + appName);
        WebAppAdminClient webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        List<String> webAppList;
        List<String> faultyWebAppList;

        boolean isWebAppDeployed = false;
        Calendar startTime = Calendar.getInstance();
        long time;
        boolean doLoop = true;
        while (((time = (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis())) < deploymentDelay)
                && doLoop) {
            webAppList = webAppAdminClient.getWebApplist(appName);
            faultyWebAppList = webAppAdminClient.getFaultyWebAppList(appName);

            for (String faultWebAppName : faultyWebAppList) {
                if (faultWebAppName.equalsIgnoreCase(appName)) {
                    isWebAppDeployed = false;
                    log.info(appName + "- Jaggary Application is faulty");
                    doLoop = false;
                }
            }

            for (String name : webAppList) {
                if (name.equalsIgnoreCase(appName)) {
                    isWebAppDeployed = true;
                    log.info(appName + " Jaggary Application deployed in " + time + " millis");
                    doLoop = false;
                }
            }
            Thread.sleep(500);
        }
        return isWebAppDeployed;
    }

    /**
     * Check the given Carbon application is deployed correctly. This method is wait for 90 seconds
     * for deployment of Carbon application and each 500 milliseconds  of wait it will check the
     * deployment status.
     *
     * @param appName Name of the application.
     * @return true if the application is listed.
     * @throws ApplicationAdminExceptionException
     * @throws RemoteException
     * @throws InterruptedException
     */
    protected boolean isCarbonAppListed(String appName)
            throws ApplicationAdminExceptionException, RemoteException, InterruptedException {
        int deploymentDelay = 90 * 1000;
        log.info("waiting " + deploymentDelay + " millis for Carbon Application to list" + appName);
        ApplicationAdminClient appAdminClient = new ApplicationAdminClient(backendURL, sessionCookie);

        String[] appList;
        boolean doLoop = true;
        boolean isAppListed = false;
        long startTime = System.currentTimeMillis();
        long time;
        while (((time = (System.currentTimeMillis() - startTime)) < deploymentDelay) && doLoop) {
            appList = appAdminClient.listAllApplications();
            if (Arrays.asList(appList).contains(appName)) {
                isAppListed = true;
                log.info(appName + " Carbon Application is listed in" + time + " millis");
                doLoop = false;
            }

            Thread.sleep(500);
        }
        return isAppListed;
    }


    /**
     * Tenant admin user login functionality.
     *
     * @param domainKey of the tenant.
     * @throws Exception
     */
    protected void loginAsTenantAdmin(String domainKey) throws Exception {
        AutomationContext automationContext = new AutomationContext("AS", "appServerInstance0001", domainKey, "admin");
        hostURL = automationContext.getInstance().getHosts().get("default");
        LoginLogoutClient loginLogoutClient1 = new LoginLogoutClient(automationContext);
        sessionCookie = loginLogoutClient1.login();

    }

    /**
     * check the tenant is unloading functionality when tenant is idle more than configured tenant idle time.
     * This method will wait additional MAX_LOOP_TIME milliseconds to  system to unload the tenant. it will log total
     * idle time taken to unload.
     *
     * @param tenantDomain Tenant domain that need to check for unloading.
     * @return true if tenant is unload after tenant idle time + additional MAX_LOOP_TIME milliseconds if not return
     * false
     * @throws IOException
     * @throws InterruptedException
     */
    protected boolean checkTenantAutoUnloading(String tenantDomain) throws IOException, InterruptedException {
        boolean isTenantUnloaded = false;
        log.info("Sleeping  for " + TENANT_IDLE_TIME + " milliseconds (Tenant idle tome).");
        Thread.sleep(TENANT_IDLE_TIME);
        long totalSleepTime = 0;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < MAX_LOOP_TIME) {

            isTenantUnloaded = !isTenantLoaded(tenantDomain);
            totalSleepTime = System.currentTimeMillis() - startTime + TENANT_IDLE_TIME;
            if (isTenantUnloaded) {
                log.info("Tenant " + tenantDomain + " is unloaded in " + totalSleepTime + "milliseconds. Tenant idle " +
                        "time is :" + TENANT_IDLE_TIME + "milliseconds.");
                break;
            } else {
                Thread.sleep(1000);
            }
        }
        if (!isTenantUnloaded) {
            log.info("Tenant " + tenantDomain + " is not unloaded in " + totalSleepTime + "milliseconds. Tenant idle " +
                    "time is :" + TENANT_IDLE_TIME + "milliseconds.");
        }
        return isTenantUnloaded;
    }

    /**
     * check the web app is unloading reload in to ghost form  when web app is idle more than configured web app idle
     * time. This method will wait additional MAX_LOOP_TIME milliseconds to  system to unload the web app and reload in
     * ghost form. It will log total idle time taken to unload.
     *
     * @param tenantDomain Name of the tenant
     * @param webAppName   Nem of the web App
     * @return true if web app is unload after web app idle time + additional MAX_LOOP_TIME milliseconds if not return
     * false
     * @throws IOException
     * @throws InterruptedException
     */
    protected boolean checkWebAppAutoUnloadingToGhostState(String tenantDomain, String webAppName) throws IOException,
            InterruptedException {
        boolean isTenantInGhostState = false;

        log.info("Sleeping  for " + WEB_APP_IDLE_TIME + " milliseconds (WebApp idle tome).");
        Thread.sleep(WEB_APP_IDLE_TIME);
        long totalSleepTime = 0;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < MAX_LOOP_TIME) {

            isTenantInGhostState = !isWebAppLoaded(tenantDomain, webAppName);
            totalSleepTime = System.currentTimeMillis() - startTime + WEB_APP_IDLE_TIME;
            if (isTenantInGhostState) {

                log.info("Web App : " + webAppName + "in Tenant " + tenantDomain + " is unloaded in " + totalSleepTime +
                        "milliseconds. Web App idle time is :" + WEB_APP_IDLE_TIME + "milliseconds.");
                break;
            } else {
                Thread.sleep(1000);
            }

        }
        if (!isTenantInGhostState) {
            log.info("Web App : " + webAppName + "in Tenant " + tenantDomain + " is not unloaded in " + totalSleepTime +
                    "milliseconds. Web App idle time is :" + WEB_APP_IDLE_TIME + "milliseconds.");
        }
        return isTenantInGhostState;
    }


    /**
     * check the web app is unloading reload in to ghost form  when web app is idle more than configured web app idle
     * time in supper tenant. This method will wait additional MAX_LOOP_TIME milliseconds to  system to unload the web
     * app and reload in ghost form. It will log total idle time taken to unload.
     *
     * @param webAppName Name of the Web App
     * @return true if web app is unload after web app idle time + additional MAX_LOOP_TIME milliseconds  if not return
     * false
     * @throws IOException
     * @throws InterruptedException
     */
    protected boolean checkWebAppAutoUnloadingToGhostStateInSupperTenant(String webAppName) throws IOException,
            InterruptedException {
        boolean isTenantInGhostState = false;
        log.info("Sleeping  for " + WEB_APP_IDLE_TIME + " milliseconds (WebApp idle tome).");
        Thread.sleep(WEB_APP_IDLE_TIME);
        long totalSleepTime = 0;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < MAX_LOOP_TIME) {

            isTenantInGhostState = !isSupperTenantWebAppLoaded(webAppName);
            totalSleepTime = System.currentTimeMillis() - startTime + WEB_APP_IDLE_TIME;
            if (isTenantInGhostState) {
                log.info("Web App : " + webAppName + "in supper tenant is unloaded in " + totalSleepTime +
                        "milliseconds. Web App idle time is :" + WEB_APP_IDLE_TIME + "milliseconds.");
                break;
            } else {
                Thread.sleep(1000);
            }

        }
        if (!isTenantInGhostState) {
            log.info("Web App : " + webAppName + "in supper tenant is not unloaded in " + totalSleepTime +
                    "milliseconds. Web App idle time is :" + WEB_APP_IDLE_TIME + "milliseconds.");
        }
        return isTenantInGhostState;
    }


}
