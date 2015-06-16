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

package org.wso2.appserver.integration.common.utils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appserver.integration.common.bean.DataSourceBean;
import org.wso2.appserver.integration.common.exception.CarbonToolsIntegrationTestException;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.ContextXpathConstants;
import org.wso2.carbon.automation.engine.frameworkutils.enums.OperatingSystems;
import org.wso2.carbon.automation.extensions.ExtensionConstants;
import org.wso2.carbon.automation.extensions.servers.utils.ClientConnectionUtil;
import org.wso2.carbon.automation.extensions.servers.utils.InputStreamHandler;
import org.wso2.carbon.automation.extensions.servers.utils.ServerLogReader;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.integration.common.admin.client.ServerAdminClient;
import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;

import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.Arrays;

/**
 * This class has the method which using by carbon tools test cases
 */

public class CarbonCommandToolsUtil {

    private static final Log log = LogFactory.getLog(CarbonCommandToolsUtil.class);
    private static int TIMEOUT_MS = 180 * 1000; // Max time to wait
    private static final long DEFAULT_START_STOP_WAIT_MS = 1000 * 60 * 2;
    private static final String SERVER_STARTUP_MESSAGE = "Mgt Console URL";

    /**
     * This method is for start a as server
     *
     * @param carbonHome        - carbon home
     * @param portOffset        - port offset
     * @param automationContext - AutomationContext
     * @param parameters        - server startup arguments as an string array
     * @return Process of the startup execution
     * @throws Exception - Error occurred when starting the the server
     */
    public static Process startServerUsingCarbonHome(String carbonHome, int portOffset,
                                                     AutomationContext automationContext,
                                                     String[] parameters) throws Exception {

        Process tempProcess;
        String scriptName = "wso2server";
        File commandDir = new File(carbonHome);
        String[] cmdArray;
        log.info("Starting server............. ");
        if (CarbonCommandToolsUtil.getCurrentOperatingSystem().
                contains(OperatingSystems.WINDOWS.name().toLowerCase())) {
            commandDir = new File(carbonHome + File.separator + "bin");
            cmdArray = new String[]{"cmd.exe", "/c", scriptName + ".bat", "-DportOffset=" + portOffset};
            cmdArray = mergePropertiesToCommandArray(parameters, cmdArray);
            tempProcess = Runtime.getRuntime().exec(cmdArray, null, commandDir);
        } else {
            cmdArray = new String[]{"sh", "bin/" + scriptName + ".sh", "-DportOffset=" + portOffset};
            cmdArray = mergePropertiesToCommandArray(parameters, cmdArray);
            tempProcess = Runtime.getRuntime().exec(cmdArray, null, commandDir);
        }
        InputStreamHandler errorStreamHandler =
                new InputStreamHandler("errorStream", tempProcess.getErrorStream());
        ServerLogReader inputStreamHandler = new ServerLogReader("inputStream", tempProcess.getInputStream());
        // start the stream readers
        inputStreamHandler.start();
        errorStreamHandler.start();
        ClientConnectionUtil.waitForPort(Integer.parseInt(FrameworkConstants.SERVER_DEFAULT_HTTPS_PORT) +
                                         portOffset, DEFAULT_START_STOP_WAIT_MS, false,
                                         automationContext.getInstance().getHosts().get("default"));
        //wait until Mgt console url printed.
        long time = System.currentTimeMillis() + DEFAULT_START_STOP_WAIT_MS;
        while (!inputStreamHandler.getOutput().contains(SERVER_STARTUP_MESSAGE) &&
               System.currentTimeMillis() < time) {
            // wait until server startup is completed
        }
        ClientConnectionUtil.waitForLogin(automationContext);
        log.info("Server started successfully.");
        return tempProcess;
    }

    /**
     * This method is to shutdown a server
     *
     * @param portOffset        - port offset
     * @param automationContext - AutomationContext
     * @throws CarbonToolsIntegrationTestException - Error when trying to shutdown the server
     */
    public static void serverShutdown(int portOffset, AutomationContext automationContext)
            throws CarbonToolsIntegrationTestException {
        long time = System.currentTimeMillis() + DEFAULT_START_STOP_WAIT_MS;
        log.info("Shutting down server..");
        try {
            boolean logOutSuccess = false;
            if (ClientConnectionUtil.isPortOpen(
                    Integer.parseInt(ExtensionConstants.SERVER_DEFAULT_HTTPS_PORT))) {

                int httpsPort = Integer.parseInt(FrameworkConstants.SERVER_DEFAULT_HTTPS_PORT) + portOffset;
                String url = automationContext.getContextUrls().getBackEndUrl();
                String backendURL = url.replaceAll("(:\\d+)", ":" + httpsPort);

                ServerAdminClient serverAdminServiceClient;
                serverAdminServiceClient =
                        new ServerAdminClient(backendURL,
                                              automationContext.getContextTenant().getTenantAdmin().getUserName(),
                                              automationContext.getContextTenant().getTenantAdmin().getPassword());

                serverAdminServiceClient.shutdown();

                while (System.currentTimeMillis() < time && !logOutSuccess) {
                    // wait until server shutdown is completed
                    logOutSuccess = isServerDown(portOffset);
                }
                log.info("Server stopped successfully...");
            }
        } catch (XPathExpressionException ex) {
            log.error("Error when reading automation.xml ", ex);
            throw new CarbonToolsIntegrationTestException("Error when reading automation.xml ", ex);
        } catch (RemoteException ex) {
            log.error("Error while shutdown the server ", ex);
            throw new CarbonToolsIntegrationTestException("Error while shutdown the server ", ex);
        }

    }

    /**
     * This method is to check whether server is down or not
     *
     * @param portOffset - port offset
     * @return boolean - if server is down true : else false
     */
    public static boolean isServerDown(int portOffset) {
        boolean isPortOpen = true;
        long startTime = System.currentTimeMillis();
        // Looping the isPortOpen method, waiting for a while  to check the server is down or not
        while (isPortOpen && (System.currentTimeMillis() - startTime) < TIMEOUT_MS) {
            isPortOpen = ClientConnectionUtil.isPortOpen(
                    Integer.parseInt(FrameworkConstants.SERVER_DEFAULT_HTTPS_PORT) + portOffset);
            if (isPortOpen) {
                try {
                    Thread.sleep(500); // waiting 0.5 sec to check isPortOpen again
                } catch (InterruptedException e) {
                    log.warn("Thread interruption occurred");
                }
            }
        }
        return !isPortOpen;
    }

    /**
     * This method is to get operating system
     *
     * @return if current os is windows return true : else false
     */
    public static String getCurrentOperatingSystem() {
        return System.getProperty(FrameworkConstants.SYSTEM_PROPERTY_OS_NAME).toLowerCase();
    }

    /**
     * This method is to execute commands and reading the logs to find the expected string.
     *
     * @param directory      - Directory which has the file to be executed .
     * @param cmdArray       - Command array to be executed.
     * @param expectedString - Expected string in  the log.
     * @return boolean - true : Found the expected string , false : not found the expected string.
     * @throws CarbonToolsIntegrationTestException - Error while running the command.
     */
    public static boolean isScriptRunSuccessfully(String directory, String[] cmdArray,
                                                  String expectedString)
            throws CarbonToolsIntegrationTestException {
        boolean isFoundTheMessage = false;
        BufferedReader br = null;
        Process process = null;
        try {
            File commandDir = new File(directory);
            process = Runtime.getRuntime().exec(cmdArray, null, commandDir);
            String line;
            long startTime = System.currentTimeMillis();
            while (!isFoundTheMessage && (System.currentTimeMillis() - startTime) < TIMEOUT_MS) {
                br = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
                while ((line = br.readLine()) != null) {
                    log.info(line);
                    if (line.contains(expectedString)) {
                        log.info("found the string " + expectedString + " in line " + line);
                        isFoundTheMessage = true;
                        break;
                    }
                }
            }
            return isFoundTheMessage;
        } catch (IOException ex) {
            log.error("Error when reading the InputStream when running shell script  ", ex);
            throw new CarbonToolsIntegrationTestException("Error when reading the InputStream when " +
                                                          "running shell script ", ex);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.warn("Error when closing the buffer reader ", e);
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * This method to find multiple strings in same line in log
     *
     * @param backEndUrl        - server back end url
     * @param searchStringArray - String array to be find in the log
     * @param cookie            - cookie
     * @return -  if found all the  string in one line: true else false
     * @throws CarbonToolsIntegrationTestException - Error when getting the logs and search the string
     */
    public static boolean searchOnLogs(String backEndUrl, String[] searchStringArray, String cookie)
            throws CarbonToolsIntegrationTestException {
        boolean expectedStringFound = false;
        LogViewerClient logViewerClient = null;
        try {
            logViewerClient = new LogViewerClient(backEndUrl, cookie);
            long startTime = System.currentTimeMillis();
            while (!expectedStringFound && (System.currentTimeMillis() - startTime) < TIMEOUT_MS) {
                LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
                for (LogEvent item : logs) {
                    String message = item.getMessage();
                    for (String searchString : searchStringArray) {
                        if (message.contains(searchString)) {
                            expectedStringFound = true;
                        } else {
                            expectedStringFound = false;
                            break;
                        }
                    }
                    if (expectedStringFound) {
                        break;
                    }
                }
                try {
                    Thread.sleep(500); // wait for 0.5 second to check the log again.
                } catch (InterruptedException e) {
                    log.warn("Thread interruption occurred");
                }
            }
            return expectedStringFound;
        } catch (RemoteException ex) {
            log.error("Error when getting the log ", ex);
            throw new CarbonToolsIntegrationTestException("Error when getting the log ", ex);
        } catch (LogViewerLogViewerException ex) {
            log.error("Error when reading the log  ", ex);
            throw new CarbonToolsIntegrationTestException("Error when reading the log ", ex);
        }
    }

    /**
     * This method to check whether server is up or not
     * This method wait for some time to check login status by checking the port and login
     * This will throw an exception if port is not open or couldn't login
     *
     * @param automationContext - AutomationContext
     * @return true: If server is up else false
     * @throws Exception - Error while waiting for login
     */
    public static boolean isServerStartedUp(AutomationContext automationContext, int portOffset)
            throws Exception {

        //Waiting util a port is open, If couldn't open within given time this will throw an Exception
        ClientConnectionUtil.waitForPort(Integer.parseInt(FrameworkConstants.SERVER_DEFAULT_HTTPS_PORT) +
                                         portOffset, DEFAULT_START_STOP_WAIT_MS, false,
                                         automationContext.getInstance().getHosts().get("default"));

        //Waiting util login to the the server this will throw LoginAuthenticationExceptionException if fails
        ClientConnectionUtil.waitForLogin(automationContext);
        log.info("Server started successfully.");
        return true;
    }

    /**
     * This method is to merge two arrays together
     *
     * @param parameters - Server startup arguments
     * @param cmdArray   - Server startup command
     * @return - merged array
     */
    private static String[] mergePropertiesToCommandArray(String[] parameters, String[] cmdArray) {
        if (parameters != null && cmdArray != null) {
            Object[] cmdObjectArray = ArrayUtils.addAll(cmdArray, parameters);
            cmdArray = Arrays.asList(cmdObjectArray).toArray(new String[cmdObjectArray.length]);
        }
        return cmdArray;
    }

    /**
     * Get the data source information from the automation configuration file
     *
     * @param dataSourceName - Data source name given in the configuration file
     * @return DataSourceInformation - Information about the data source.
     * @throws XPathExpressionException - Throws if an exception occurred when getting data for configuration file
     */
    public static DataSourceBean getDataSourceInformation(String dataSourceName)
            throws XPathExpressionException {

        AutomationContext automationContext =
                new AutomationContext(ASIntegrationConstants.AS_PRODUCT_GROUP,
                                      ASIntegrationConstants.AS_INSTANCE_0002,
                                      ContextXpathConstants.SUPER_TENANT,
                                      ContextXpathConstants.SUPER_ADMIN);

        String URL = automationContext.getConfigurationValue(String.format(
                ASIntegrationConstants.CONTEXT_XPATH_DATA_SOURCE + "/url", dataSourceName));

        String userName = automationContext.getConfigurationValue(String.format(
                ASIntegrationConstants.CONTEXT_XPATH_DATA_SOURCE + "/username", dataSourceName));

        char[] passWord = automationContext.getConfigurationValue(String.format(
                ASIntegrationConstants.CONTEXT_XPATH_DATA_SOURCE + "/password", dataSourceName)).toCharArray();

        String driverClassName = automationContext.getConfigurationValue(String.format(
                ASIntegrationConstants.CONTEXT_XPATH_DATA_SOURCE + "/driverClassName", dataSourceName));

        return new DataSourceBean(URL, userName, passWord, driverClassName);
    }
}
