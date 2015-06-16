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
package org.wso2.appserver.integration.tests.jaggery.utils;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.tests.jaggery.exceptions.JaggeryTestException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.List;

public class JaggeryTestUtil {

    private static  final Log log = LogFactory.getLog(JaggeryTestUtil.class);
    private static final int WAIT_TIME = 500;

    public static URLConnection openConnection(URL url) {
        long timeoutExpiredMs = System.currentTimeMillis() + WAIT_TIME;
        URLConnection jaggeryServerConnection = null;
        try {
            jaggeryServerConnection = url.openConnection();
        } catch (IOException ignored) {
        }
        while ((jaggeryServerConnection == null) && (System.currentTimeMillis() <= timeoutExpiredMs)) {
            try {
                jaggeryServerConnection = url.openConnection();
            } catch (IOException ignored) {
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
        return jaggeryServerConnection;
    }

    public static BufferedReader inputReader(URLConnection jaggeryServerConnection) {
        long timeoutExpiredMs = System.currentTimeMillis() + WAIT_TIME;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    jaggeryServerConnection.getInputStream()));
        } catch (IOException ignored) {
        }
        while ((in == null) && (System.currentTimeMillis() <= timeoutExpiredMs)) {
            try {
                in = new BufferedReader(
                        new InputStreamReader(jaggeryServerConnection.getInputStream()));
            } catch (IOException ignored) {
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
        return in;
    }

    /**
     * Check the given Jaggery application is deployed correctly. This method is wait for 90 seconds
     * for deployment of Jaggery application and each 500 milliseconds of wait, it will check the
     * deployment status.
     *
     * @param appName Jaggery application name
     * @param backendURL server url
     * @param sessionCookie session cookie
     * @return deployment status of the Jaggery app
     */
    public static boolean isJaggeryAppDeployed(String appName, String backendURL, String sessionCookie)
            throws Exception {
        int deploymentDelayInMilliseconds = 90 * 1000;
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
            throw new JaggeryTestException(customErrorMessage, axisFault);

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
                throw new JaggeryTestException(customErrorMessage, remoteException);
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
                String customErrorMessage = "InterruptedException occurs when sleeping 1000 milliseconds and while"
                        + " waiting for Jaggery Application to get deployed ";
                log.warn(customErrorMessage, interruptedException);
                // In InterreuptedException it doesnt need to throw a exception
            }
        }
        return isWebAppDeployed;
    }
}
