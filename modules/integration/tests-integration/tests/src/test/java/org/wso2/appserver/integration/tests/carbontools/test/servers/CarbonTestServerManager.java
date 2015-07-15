package org.wso2.appserver.integration.tests.carbontools.test.servers;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.extensions.servers.carbonserver.TestServerManager;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.util.HashMap;

/**
 * Prepares the Carbon server for test runs, starts the server, and stops the server after
 * test runs
 */
public class CarbonTestServerManager {

    private static final Log log = LogFactory.getLog(CarbonTestServerManager.class);
    private static TestServerManager testServerInstance = null;
    private static CarbonTestServerManager instance = new CarbonTestServerManager();
    private static boolean isServerRunning = false;

    private CarbonTestServerManager() {
        try {
            testServerInstance = new TestServerManager(new AutomationContext("AS", TestUserMode.SUPER_TENANT_ADMIN));
            testServerInstance.getCommands().put("-Dsetup", "");
        } catch (XPathExpressionException e) {
            log.error(e);
        }
    }

    public static void start(int portOffset) throws Exception {
        if (isServerRunning) {
            throw new Exception("Server already Running..");
        }
        deleteDatabases();
        testServerInstance.getCommands().put("-DportOffset", String.valueOf(portOffset));
        testServerInstance.startServer();
        isServerRunning = true;
        Thread.sleep(5000);
    }

    public static void start(HashMap<String, String> serverPropertyMap) throws Exception {
        if (isServerRunning) {
            throw new Exception("Server already Running..");
        }
        deleteDatabases();
        testServerInstance.getCommands().clear();
        testServerInstance.getCommands().put("-Dsetup", "");
        testServerInstance.getCommands().putAll(serverPropertyMap);
        testServerInstance.startServer();
        isServerRunning = true;
        Thread.sleep(5000);
    }

    public static void stop() throws AutomationFrameworkException {
        testServerInstance.stopServer();
        isServerRunning = false;
    }

    public static void reStart() throws AutomationFrameworkException {
        testServerInstance.restartGracefully();
        isServerRunning = true;
    }

    public static String getCarbonHome() {
        return testServerInstance.getCarbonHome();
    }

    public static boolean isServerRunning() {
        return isServerRunning;
    }

    private static void deleteDatabases() throws Exception {
        if (CarbonTestServerManager.getCarbonHome() == null || CarbonTestServerManager.getCarbonHome().isEmpty()) {
            return;
        }
        File dbDir = new File(CarbonTestServerManager.getCarbonHome() + File.separator + "repository"
                              + File.separator + "database");
        final File[] files = dbDir.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
        Thread.sleep(1000);
    }

}

