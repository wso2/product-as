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

package org.wso2.appserver.integration.lazy.loading;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.lazy.loading.util.LazyLoadingTestException;
import org.wso2.appserver.integration.lazy.loading.util.TenantStatus;
import org.wso2.appserver.integration.lazy.loading.util.WebAppStatus;
import org.wso2.carbon.application.mgt.stub.ApplicationAdminExceptionException;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpURLConnectionClient;
import org.wso2.carbon.integration.common.admin.client.ApplicationAdminClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

/**
 * Base class to hold the common variables and utility methods of Lazy Loading (Ghost deployment) Test cases.
 * All Lazy Loading (Ghost deployment)  Test  Classes must inherit from this.
 * Replacing the carbon.xml with Ghost deployment configuration and deployment of the
 * tenant-info-service web application is implemented in side init() method.
 */
public abstract class LazyLoadingBaseTest extends ASIntegrationTest {


    protected static final String TENANT_DOMAIN_1_kEY = "tenant1";
    protected static final String TENANT_DOMAIN_2_KEY = "tenant2";
    protected static final String SUPER_TENANT_DOMAIN_KEY = "superTenant";

    protected String SUPER_TENANT_DOMAIN;
    protected String TENANT_DOMAIN_1;
    protected String TENANT_DOMAIN_2;

    protected WebAppAdminClient webAppAdminClient;
    protected ServerConfigurationManager serverManager;
    protected String hostURL;
    protected static final String CARBON_HOME = System.getProperty(ServerConstants.CARBON_HOME);
    protected static String ARTIFACTS_LOCATION;

    private static final Log log = LogFactory.getLog(LazyLoadingBaseTest.class);
    private long TENANT_IDLE_TIME;
    private long WEB_APP_IDLE_TIME;
    private static final long MAX_THRESHOLD_TIME = 2 * 60 * 1000;


    private static final String PRODUCT_GROUP_NAME = "AS";
    private static final String INSTANCE_NAME = "appServerInstance0001";
    private static final String USER_KEY = "admin";

    private static final String TENANT_INFO_SERVICE = "lazy-loading-info";
    private static final String TENANT_INFO_SERVICE_FILE_NAME = TENANT_INFO_SERVICE + ".war";
    private static final String IS_TENANT_LOADED_METHOD_URL = TENANT_INFO_SERVICE + "/tenant-status";
    private static final String IS_WEB_APP_LOADED_METHOD_URL = TENANT_INFO_SERVICE + "/webapp-status";


    private static final String TENANT_IDLE_XPATH = "//listenerExtensions/platformExecutionManager/extentionClasses/" +
            "*[name()='class']/*[name()='parameter'][@name='-Dtenant.idle.time']/@value";
    private static final String WEB_APP_IDLE_XPATH = "//listenerExtensions/platformExecutionManager/extentionClasses/" +
            "*[name()='class']/*[name()='parameter'][@name='-Dwebapp.idle.time']/@value";

    private static final String CARBON_XML = "carbon.xml";


    private static final String CARBON_REPOSITORY_LOCATION =
            CARBON_HOME + File.separator + "repository" + File.separator + "conf" + File.separator + CARBON_XML;
    protected static final int CONCURRENT_THREAD_COUNT = 40;

    @Override
    /**
     * Login as super admin and do the replacement of carbon.xml with Ghost deployment
     * configuration and deployment of the tenant-info-service web application.
     * At the end it will restart the server gracefully.
     * @throws Exception
     */
    public void init() throws Exception {
        super.init();

        serverManager = new ServerConfigurationManager(asServer);
        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        TENANT_IDLE_TIME =
                Long.parseLong(asServer.getConfigurationNode(TENANT_IDLE_XPATH).getNodeValue()) * 60 * 1000;
        WEB_APP_IDLE_TIME =
                Long.parseLong(asServer.getConfigurationNode(WEB_APP_IDLE_XPATH).getNodeValue()) * 60 * 1000;

        ARTIFACTS_LOCATION =
                TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" + File.separator +
                        "AS" + File.separator + "ghost" + File.separator;


        String tenantInfoServiceArtifactLocation =
                ARTIFACTS_LOCATION + TENANT_INFO_SERVICE_FILE_NAME;


        String carbonArtifactLocation = ARTIFACTS_LOCATION + CARBON_XML;

        File sourceFile = new File(carbonArtifactLocation);
        File targetFile = new File(CARBON_REPOSITORY_LOCATION);

        SUPER_TENANT_DOMAIN =
                new AutomationContext(PRODUCT_GROUP_NAME, INSTANCE_NAME, SUPER_TENANT_DOMAIN_KEY, USER_KEY).getSuperTenant().getDomain();

        TENANT_DOMAIN_1 =
                new AutomationContext(PRODUCT_GROUP_NAME, INSTANCE_NAME, TENANT_DOMAIN_1_kEY, USER_KEY).getContextTenant().getDomain();

        TENANT_DOMAIN_2 =
                new AutomationContext(PRODUCT_GROUP_NAME, INSTANCE_NAME, TENANT_DOMAIN_2_KEY, USER_KEY).getContextTenant().getDomain();


        serverManager.applyConfigurationWithoutRestart(sourceFile, targetFile, true);
        log.info("carbon.xml replaced with :" + carbonArtifactLocation);

        webAppAdminClient.warFileUplaoder(tenantInfoServiceArtifactLocation);
        serverManager.restartGracefully();
        log.info("Server Restarted after applying carbon.xml and tenant information utility web application");

    }


    /**
     * This method will do a REST  GET call to the tenant-info-service (deployed by init() method) to get the tenant
     * status information
     *
     * @param tenantDomain -  Domain Name of the tenant.
     * @return TenantStatus - a JSON object that contains whether the configuration context is loaded or not.
     * @throws LazyLoadingTestException - Exception throws when send the Get request and retrieve the JSON data from response.
     */
    protected TenantStatus getTenantStatus(String tenantDomain) throws LazyLoadingTestException {

        TenantStatus tenantStatus;
        String requestUrl = webAppURL + "/" + IS_TENANT_LOADED_METHOD_URL + "/" + tenantDomain;
        try {
            HttpResponse response = HttpURLConnectionClient.sendGetRequest(requestUrl, null);
            JSONObject tenantStatusJSON = new JSONObject(response.getData());
            tenantStatus =
                    new TenantStatus(tenantStatusJSON.getJSONObject("TenantStatus").getBoolean("tenantContextLoaded"));
        } catch (IOException ioException) {
            String customErrorMessage = "IOException when sending the Get request to:" + requestUrl;
            log.error(customErrorMessage, ioException);
            throw new LazyLoadingTestException(customErrorMessage, ioException);
        } catch (JSONException jsonException) {
            String customErrorMessage = "JSONException when retrieving the values from json object TenantStatus" + requestUrl;
            log.error(customErrorMessage, jsonException);
            throw new LazyLoadingTestException(customErrorMessage, jsonException);
        }

        return tenantStatus;
    }

    /**
     * This method will do a REST  GET call to the tenant-info-service (deployed by init() method) to get the web-app
     * status information.
     * IT will return the  the deployment status of given web application of given tenant is loaded in Ghost form.
     *
     * @param tenantDomain -  Domain Name of the tenant.
     * @param webAppName   -  Name of the Web Application
     * @return WebAppStatus -  which include Tenant status, whether the web-app is started or not, whether the web-app is in
     * ghost mode or not.
     * @throws LazyLoadingTestException - Exception throws when send the Get request and retrieve the JSON data from response.
     */
    protected WebAppStatus getWebAppStatus(String tenantDomain, String webAppName) throws LazyLoadingTestException {
        String requestUrl = webAppURL + "/" + IS_WEB_APP_LOADED_METHOD_URL + "/" + tenantDomain + "/" + webAppName;
        WebAppStatus webAppStatus;
        try {

            HttpResponse response = HttpURLConnectionClient.sendGetRequest(requestUrl, null);
            JSONObject webAppStatusJSON = new JSONObject(response.getData()).getJSONObject("WebAppStatus");
            boolean isTenantLoaded = webAppStatusJSON.getJSONObject("tenantStatus").getBoolean("tenantContextLoaded");
            boolean isWebAppStarted = webAppStatusJSON.getBoolean("webAppStarted");
            boolean isWebAppGhost = webAppStatusJSON.getBoolean("webAppGhost");
            webAppStatus = new WebAppStatus(new TenantStatus(isTenantLoaded), isWebAppStarted, isWebAppGhost);
        } catch (IOException ioException) {
            String customErrorMessage =
                    "IOException when sending the Get request to:" + requestUrl;
            log.error(customErrorMessage, ioException);
            throw new LazyLoadingTestException(customErrorMessage, ioException);
        } catch (JSONException jsonException) {
            String customErrorMessage = "JSONException when retrieving the values from json object WebAppStatus" + requestUrl;
            log.error(customErrorMessage, jsonException);
            throw new LazyLoadingTestException(customErrorMessage, jsonException);
        }

        return webAppStatus;

    }


    /**
     * Check the given Jaggery application is deployed correctly. This method is wait for 90 seconds
     * for deployment of jaggery application and each 500 milliseconds  of wait it will check the
     * deployment status.
     *
     * @param appName - Name of the application.
     * @return boolean - true if the application is get deployed before the maximum wait time of 90 seconds.
     * @throws LazyLoadingTestException - Exception throws when creating WebAppAdminClient.
     */
    protected boolean isJaggeryAppDeployed(String appName) throws LazyLoadingTestException {
        int deploymentDelayInMilliseconds = 90 * 1000;
        log.info("waiting " + deploymentDelayInMilliseconds + " millis for Service deployment " + appName);
        WebAppAdminClient webAppAdminClient;
        List<String> webAppList;
        List<String> faultyWebAppList;
        long startTime;
        long time;
        boolean isWebAppDeployed = false;
        boolean doLoop = true;

        try {
            webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        } catch (AxisFault axisFault) {
            String customErrorMessage =
                    "AxisFault Exception  when creating WebAppAdminClient object. backend URL:" + backendURL +
                            " Session Cookie: " + sessionCookie;
            log.error(customErrorMessage, axisFault);
            throw new LazyLoadingTestException(customErrorMessage, axisFault);
        }

        startTime = System.currentTimeMillis();

        while (((time = (System.currentTimeMillis() - startTime)) < deploymentDelayInMilliseconds) && doLoop) {
            //Get the web app list
            try {
                webAppList = webAppAdminClient.getWebApplist(appName);
                faultyWebAppList = webAppAdminClient.getFaultyWebAppList(appName);
            } catch (RemoteException remoteException) {
                String customErrorMessage = "remoteException Exception when calling methods in WebAppAdminClient";
                log.error(customErrorMessage, remoteException);
                throw new LazyLoadingTestException(customErrorMessage, remoteException);
            }
            // Find given app in faulty app list. If found return the loop with isWebAppDeployed=false
            for (String faultWebAppName : faultyWebAppList) {
                if (faultWebAppName.equalsIgnoreCase(appName)) {
                    isWebAppDeployed = false;
                    log.info(appName + "- Jaggery Application is faulty");
                    doLoop = false;
                }
            }
            // Find the given app in web app list. If found return the loop with isWebAppDeployed=true
            for (String webAppName : webAppList) {
                if (webAppName.equalsIgnoreCase(appName)) {
                    isWebAppDeployed = true;
                    log.info(appName + " Jaggery Application deployed in " + time + " millis");
                    doLoop = false;
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException interruptedException) {
                String customErrorMessage = "InterruptedException occurs when sleeping 1000 milliseconds and while" +
                        " waiting for Jaggery Application to get deployed ";
                log.warn(customErrorMessage, interruptedException);
            }
        }
        return isWebAppDeployed;
    }

    /**
     * Check the given Carbon application is deployed correctly. This method is wait for 90 seconds
     * for deployment of Carbon application and each 500 milliseconds  of wait it will check the
     * deployment status.
     *
     * @param appName - Name of the application.
     * @return boolean - true if the application is listed.
     * @throws LazyLoadingTestException - Exception throws when creating WebAppAdminClient.
     */
    protected boolean isCarbonAppListed(String appName) throws LazyLoadingTestException {
        int deploymentDelayInMilliseconds = 90 * 1000;
        log.info("waiting " + deploymentDelayInMilliseconds + " millis for Carbon Application to list" + appName);
        ApplicationAdminClient appAdminClient;
        String[] appList;
        boolean doLoop = true;
        boolean isAppListed = false;
        long time;
        long startTime;

        try {
            appAdminClient = new ApplicationAdminClient(backendURL, sessionCookie);
        } catch (AxisFault axisFault) {
            String customErrorMessage = "AxisFault Exception  when creating WebAppAdminClient object. backend URL:" +
                    backendURL + " Session Cookie: " + sessionCookie;
            log.error(customErrorMessage, axisFault);
            throw new LazyLoadingTestException(customErrorMessage, axisFault);
        }

        startTime = System.currentTimeMillis();
        while (((time = (System.currentTimeMillis() - startTime)) < deploymentDelayInMilliseconds) && doLoop) {
            //List all applications
            try {
                appList = appAdminClient.listAllApplications();
            } catch (ApplicationAdminExceptionException applicationAdminExceptionException) {
                String customErrorMessage = "ApplicationAdminExceptionException Exception when when calling " +
                        "listAllApplications() methods in WebAppAdminClient";
                log.error(customErrorMessage, applicationAdminExceptionException);
                throw new LazyLoadingTestException(customErrorMessage, applicationAdminExceptionException);
            } catch (RemoteException remoteException) {
                String customErrorMessage =
                        "RemoteException Exception when calling listAllApplications() methods in WebAppAdminClient";
                log.error(customErrorMessage, remoteException);
                throw new LazyLoadingTestException(customErrorMessage, remoteException);
            }
            //Find given application is available in app list. if found  return isAppListed = true or else with false
            if (Arrays.asList(appList).contains(appName)) {
                isAppListed = true;
                log.info(appName + " Carbon Application is listed in" + time + " millis");
                doLoop = false;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException interruptedException) {
                String customErrorMessage = "InterruptedException occurs when sleeping 500 milliseconds and while" +
                        " waiting for Carbon Application to  listed in ApplicationAdminClient.";
                log.warn(customErrorMessage, interruptedException);
            }
        }
        return isAppListed;
    }


    /**
     * Tenant admin user login functionality.
     *
     * @param domainKey -  Domain key of the tenant.
     * @throws LazyLoadingTestException - Exception throws when  creating the AutomationContext and login() method of LoginLogoutClient.java
     */
    protected void loginAsTenantAdmin(String domainKey) throws LazyLoadingTestException {
        try {
            AutomationContext automationContext = new AutomationContext(PRODUCT_GROUP_NAME, INSTANCE_NAME, domainKey,
                    "admin");
            hostURL = automationContext.getInstance().getHosts().get("default");
            LoginLogoutClient loginLogoutClient1 = new LoginLogoutClient(automationContext);
            sessionCookie = loginLogoutClient1.login();
        } catch (XPathExpressionException xPathExpressionException) {
            String customErrorMessage = "XPathExpressionException exception  when login as tenant admin.";
            log.error(customErrorMessage, xPathExpressionException);
            throw new LazyLoadingTestException(customErrorMessage, xPathExpressionException);

        } catch (IOException ioException) {
            String customErrorMessage = "IOException exception  when login as tenant admin.";
            log.error(customErrorMessage, ioException);
            throw new LazyLoadingTestException(customErrorMessage, ioException);

        } catch (SAXException saxException) {
            String customErrorMessage = "SAXException exception  when login as tenant admin.";
            log.error(customErrorMessage, saxException);
            throw new LazyLoadingTestException(customErrorMessage, saxException);

        } catch (XMLStreamException xmlStreamException) {
            String customErrorMessage = "XMLStreamException exception  when login as tenant admin.";
            log.error(customErrorMessage, xmlStreamException);
            throw new LazyLoadingTestException(customErrorMessage, xmlStreamException);

        } catch (LoginAuthenticationExceptionException loginAuthenticationExceptionException) {
            String customErrorMessage = "LoginAuthenticationExceptionException exception  when login as tenant admin.";
            log.error(customErrorMessage, loginAuthenticationExceptionException);
            throw new LazyLoadingTestException(customErrorMessage, loginAuthenticationExceptionException);

        } catch (URISyntaxException uriSyntaxException) {
            String customErrorMessage = "URISyntaxException exception  when login as tenant admin.";
            log.error(customErrorMessage, uriSyntaxException);
            throw new LazyLoadingTestException(customErrorMessage, uriSyntaxException);

        }


    }

    /**
     * check the tenant is unloading functionality when tenant is idle more than configured tenant idle time.
     * This method will wait additional MAX_THRESHOLD_TIME milliseconds to  system to unload the tenant. it will log
     * total idle time taken to unload.
     *
     * @param tenantDomain - Tenant domain that need to check for unloading.
     * @return boolean - true if tenant is unload after tenant idle time + additional MAX_THRESHOLD_TIME milliseconds if not return
     * false
     * @throws LazyLoadingTestException - Exception throws when  calling th REST call to get tenant in formation.
     */
    protected boolean checkTenantAutoUnloading(String tenantDomain) throws LazyLoadingTestException {
        boolean isTenantUnloaded = false;
        long totalSleepTime = 0;
        log.info("Sleeping  for " + TENANT_IDLE_TIME + " milliseconds (Tenant idle tome).");

        try {
            Thread.sleep(TENANT_IDLE_TIME);
        } catch (InterruptedException interruptedException) {
            String customErrorMessage = "InterruptedException occurs when sleeping for TENANT_IDLE_TIME" +
                    interruptedException.getMessage();
            log.warn(customErrorMessage, interruptedException);
        }

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < MAX_THRESHOLD_TIME) {
            // check for tenant status
            isTenantUnloaded = !getTenantStatus(tenantDomain).isTenantContextLoaded();
            totalSleepTime = System.currentTimeMillis() - startTime + TENANT_IDLE_TIME;
            // If Tenant is unloaded exit the loop, else sleep and continue the loop
            if (isTenantUnloaded) {
                log.info("Tenant " + tenantDomain + " is unloaded in " + totalSleepTime + "milliseconds. Tenant idle " +
                        "time is :" + TENANT_IDLE_TIME + "milliseconds.");
                break;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    String customErrorMessage = "InterruptedException occurs when sleeping 1000 milliseconds and while" +
                            " waiting for tenant to auto unload" + interruptedException.getMessage();
                    log.warn(customErrorMessage, interruptedException);

                }
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
     * time. This method will wait additional MAX_THRESHOLD_TIME milliseconds to  system to unload the web app and
     * reload in ghost form. It will log total idle time taken to unload.
     *
     * @param tenantDomain - Name of the tenant
     * @param webAppName   -  Nem of the web App
     * @return true if web app is unload after web app idle time + additional MAX_THRESHOLD_TIME milliseconds if not return
     * false
     * @throws LazyLoadingTestException - Exception throws when  calling th REST call to get web app in formation.
     */
    protected boolean checkWebAppAutoUnloadingToGhostState(String tenantDomain, String webAppName) throws
            LazyLoadingTestException {
        boolean isTenantInGhostState = false;
        long totalSleepTime = 0;

        log.info("Sleeping  for " + WEB_APP_IDLE_TIME + " milliseconds (WebApp idle tome).");
        try {
            Thread.sleep(WEB_APP_IDLE_TIME);
        } catch (InterruptedException interruptedException) {
            String customErrorMessage = "InterruptedException occurs when sleeping for WEB_APP_IDLE_TIME";
            log.warn(customErrorMessage);
        }

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < MAX_THRESHOLD_TIME) {
            // get the web app ghost status
            isTenantInGhostState = getWebAppStatus(tenantDomain, webAppName).isWebAppGhost();
            totalSleepTime = System.currentTimeMillis() - startTime + WEB_APP_IDLE_TIME;
            // if web app is in ghost status  exit the loop or else sleep and continue the loop.
            if (isTenantInGhostState) {

                log.info("Web App : " + webAppName + "in Tenant " + tenantDomain + " is unloaded in " + totalSleepTime +
                        "milliseconds. Web App idle time is :" + WEB_APP_IDLE_TIME + "milliseconds.");
                break;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    String customErrorMessage =
                            "InterruptedException occurs when sleeping 1000 milliseconds and while waiting for Web-app to auto unload";
                    log.warn(customErrorMessage, interruptedException);
                }
            }

        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException interruptedException) {
            String customErrorMessage =
                    "InterruptedException occurs when sleeping 1000 milliseconds.";
            log.warn(customErrorMessage, interruptedException);
        }
        if (!isTenantInGhostState) {
            log.info("Web App : " + webAppName + "in Tenant " + tenantDomain + " is not unloaded in " + totalSleepTime +
                    "milliseconds. Web App idle time is :" + WEB_APP_IDLE_TIME + "milliseconds.");
        }
        return isTenantInGhostState;
    }


}
