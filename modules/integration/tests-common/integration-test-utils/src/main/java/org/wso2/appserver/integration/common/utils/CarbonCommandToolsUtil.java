package org.wso2.appserver.integration.common.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
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


public class CarbonCommandToolsUtil {

    /**
     * This class has the method which using by carbon tools test cases
     */

    private static final Log log = LogFactory.getLog(CarbonCommandToolsUtil.class);
    private static int TIMEOUT = 180 * 1000; // Max time to wait
    private static final long DEFAULT_START_STOP_WAIT_MS = 1000 * 60 * 2;
    private static final String SERVER_STARTUP_MESSAGE = "Mgt Console URL";
    private static ServerLogReader inputStreamHandler;

    /**
     * This method is for start a as server
     *
     * @param carbonHome        - carbon home
     * @param portOffset        - port offset
     * @param automationContext - AutomationContext
     * @param parameters        - server startup arguments as an string array
     * @return Process of the startup execution
     * @throws Exception
     */
    public static Process startServerUsingCarbonHome(String carbonHome, int portOffset,
                                                     AutomationContext automationContext,
                                                     String[] parameters)
            throws Exception {

        Process tempProcess;
        String scriptName = "wso2server";
        File commandDir = new File(carbonHome);
        String[] cmdArray;
        log.info("Starting server............. ");
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
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
        inputStreamHandler = new ServerLogReader("inputStream", tempProcess.getInputStream());
        // start the stream readers
        inputStreamHandler.start();
        errorStreamHandler.start();
        ClientConnectionUtil.waitForPort(Integer.parseInt(FrameworkConstants.SERVER_DEFAULT_HTTPS_PORT) +
                                         portOffset, DEFAULT_START_STOP_WAIT_MS, false,
                                         automationContext.getInstance().getHosts().get("default"));
        //wait until Mgt console url printed.
        long time = System.currentTimeMillis() + 60 * 1000;
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
     * @throws XPathExpressionException - Error when getting data from automation.xml
     * @throws RemoteException          - Error when shutdown the server
     */
    public static void serverShutdown(int portOffset,
                                      AutomationContext automationContext)
            throws XPathExpressionException, RemoteException {
        long time = System.currentTimeMillis() + DEFAULT_START_STOP_WAIT_MS;
        log.info("Shutting down server..");
        boolean logOutSuccess = false;
        if (ClientConnectionUtil.isPortOpen(Integer.parseInt(ExtensionConstants.
                                                                     SERVER_DEFAULT_HTTPS_PORT))) {

            int httpsPort = Integer.parseInt(FrameworkConstants.SERVER_DEFAULT_HTTPS_PORT) + portOffset;
            String url = automationContext.getContextUrls().getBackEndUrl();
            String backendURL = url.replaceAll("(:\\d+)", ":" + httpsPort);

            ServerAdminClient serverAdminServiceClient = new ServerAdminClient(backendURL,
                              automationContext.getContextTenant().getTenantAdmin().getUserName(),
                              automationContext.getContextTenant().getTenantAdmin().getPassword());

            serverAdminServiceClient.shutdown();

            while (System.currentTimeMillis() < time && !logOutSuccess) {
                logOutSuccess = isServerDown(automationContext, portOffset);
                // wait until server shutdown is completed
            }
            log.info("Server stopped successfully...");
        }

    }

    /**
     * This method is to merge two arrays together
     *
     * @param parameters - Server startup arguments
     * @param cmdArray   - Server startup command
     * @return - merged array
     */
    private static String[] mergePropertiesToCommandArray(String[] parameters, String[] cmdArray) {
        if (parameters != null) {
            cmdArray = ArrayUtils.addAll(cmdArray, parameters);
        }
        return cmdArray;
    }

    /**
     * This method is to check whether server is down or not
     *
     * @param automationContext - AutomationContext
     * @param portOffset        - port offset
     * @return boolean - if server is down true : else false
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public static boolean isServerDown(AutomationContext automationContext,
                                       int portOffset)
            throws XPathExpressionException {
        boolean isPortOpen = true;
            long startTime = System.currentTimeMillis();
            // Looping the waitForPort method for a time to check the server is down or not
            while (isPortOpen && (System.currentTimeMillis() - startTime) < TIMEOUT) {
                isPortOpen = ClientConnectionUtil.isPortOpen(
                        Integer.parseInt(FrameworkConstants.SERVER_DEFAULT_HTTPS_PORT) + portOffset);
            }

        return !isPortOpen;
    }

    /**
     * This method is to check running os is windows or not
     *
     * @return if current os is windows return true : else false
     */
    public static boolean isCurrentOSWindows() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return true;
        }
        return false;

    }

    /**
     * This method is to execute commands and reading the logs to find the expected string.
     *
     * @param directory      - Directory which has the file to be executed .
     * @param cmdArray       - Command array to be executed.
     * @param expectedString - Expected string in  the log.
     * @return boolean - true : Found the expected string , false : not found the expected string.
     * @throws IOException - Error while getting the command directory
     */
    public static boolean isScriptRunSuccessfully(String directory, String[] cmdArray,
                                                  String expectedString) throws IOException {
        boolean isFoundTheMessage = false;
        BufferedReader br = null;
        Process process = null;
        try {
            File commandDir = new File(directory);
            process = Runtime.getRuntime().exec(cmdArray, null, commandDir);
            String line;
            long startTime = System.currentTimeMillis();
            while ((System.currentTimeMillis() - startTime) < TIMEOUT) {
                br = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
                while ((line = br.readLine()) != null) {
                    log.info(line);
                    if (line.contains(expectedString)) {
                        log.info("found the string " + expectedString + " in line " + line);
                        isFoundTheMessage = true;
                        break;
                    }
                }
                if(isFoundTheMessage){
                    break;
                }
            }
            return isFoundTheMessage;
        } catch (IOException ex) {
            log.error("Error when reading the InputStream when running shell script  " +
                      ex.getMessage(), ex);
            throw new IOException("Error when reading the InputStream when running shell script "
                                  + ex.getMessage(), ex);
        } finally {
            if (br != null) {
                br.close();
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
     * @param stringArrayToFind - String array to be find in the log
     * @param cookie            - cookie
     * @return -  if found all the  string in one line: true else false
     * @throws RemoteException             - Error when initializing the log
     * @throws LogViewerLogViewerException - Error while reading the log
     * @throws InterruptedException        - Error occurred when thread sleep
     */
    public static boolean findMultipleStringsInLog(String backEndUrl, String[] stringArrayToFind,
                                                   String cookie)
            throws RemoteException, InterruptedException, LogViewerLogViewerException {
        boolean expectedStringFound = false;
        LogViewerClient logViewerClient = new LogViewerClient(backEndUrl, cookie);

        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < TIMEOUT) {
            LogEvent[] logs = logViewerClient.getAllRemoteSystemLogs();
            for (LogEvent item : logs) {
                String message = item.getMessage();
                for (String stringToFind : stringArrayToFind) {
                    if (message.contains(stringToFind)) {
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
            if (expectedStringFound) {
                break;
            }
            Thread.sleep(500); // wait for 0.5 second to check the log again.
        }
        return expectedStringFound;
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
        ClientConnectionUtil.waitForPort(
                Integer.parseInt(FrameworkConstants.SERVER_DEFAULT_HTTPS_PORT) + portOffset,
                DEFAULT_START_STOP_WAIT_MS, false, automationContext.getInstance().getHosts().get("default"));

        //Waiting util login to the the server this will throw LoginAuthenticationExceptionException if fails
        ClientConnectionUtil.waitForLogin(automationContext);
        log.info("Server started successfully.");
        return true;
    }
}
