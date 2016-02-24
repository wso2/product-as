/*
* Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.appserver.integration.tests.config;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.util.SystemOutLogger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationConstants;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.appserver.integration.common.utils.WebAppTypes;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.frameworkutils.CodeCoverageUtils;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.engine.frameworkutils.ReportGenerator;
import org.wso2.carbon.automation.engine.frameworkutils.TestFrameworkUtils;
import org.wso2.carbon.automation.extensions.servers.carbonserver.CarbonServerManager;
import org.wso2.carbon.automation.extensions.servers.utils.ArchiveExtractor;
import org.wso2.carbon.automation.extensions.servers.utils.ClientConnectionUtil;
import org.wso2.carbon.automation.extensions.servers.utils.FileManipulator;
import org.wso2.carbon.automation.extensions.servers.utils.ServerLogReader;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This test class is use to test the JNDI Hibernate Integration
 * If the app can't be deploy successfully and throws a NamingException it implies that the integration fails
 */
public class EnvironmentVariableReadTestCase extends ASIntegrationTest {
    private static final Log log = LogFactory.getLog(EnvironmentVariableReadTestCase.class);
    private TestUserMode userMode;
    private WebAppAdminClient webAppAdminClient;
    private ServerConfigurationManager serverManager;
    private HttpClient httpClient = new HttpClient();
    private int portOffset;
    private int SERVER_PORT = 15000;
    private int REGISTRY_PORT = 16000;
    private Process process;
    private static int defaultHttpPort = Integer.parseInt("9763");
    private static int defaultHttpsPort = Integer.parseInt("9443");
    private ServerLogReader inputStreamHandler;
    private ServerLogReader errorStreamHandler;
    private boolean isCoverageEnable = false;
    private String coverageDumpFilePath;
    private String carbonHome;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();

        portOffset = 200;
        String location = System.getProperty("carbon.zip");
        String carbonHome = setUpCarbonHome(location);
        Map<String, String> systemParams = new HashMap<>();
        systemParams.put("-DportOffset", Integer.toString(portOffset));

        Map<String, String> envpMap = System.getenv();
        String[] envp = new String[envpMap.size() + 2];
        Iterator it = envpMap.entrySet().iterator();
        int index = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            envp[index++] = pair.getKey() + "=" + pair.getValue();
        }
        envp[index++] = "SERVER_PORT=" + SERVER_PORT;
        envp[index++] = "REGISTRY_PORT=" + REGISTRY_PORT;


        Path sourcePath =
                Paths.get(TestConfigurationProvider.getResourceLocation(), "artifacts", "AS", "config",
                        "carbon.xml");
        Path targetPath =
                Paths.get(carbonHome, "repository", "conf", "carbon.xml");
        applyConfiguration(sourcePath.toFile(), targetPath.toFile());
        startServerUsingCarbonHome(carbonHome, systemParams, envp);


//        Map<String, String> env = new HashMap<>();
//        env.put("SERVER_PORT", Integer.toString(SERVER_PORT));
//        env.put("REGISTRY_PORT", Integer.toString(REGISTRY_PORT));
//        setEnv(env);
//
//        serverManager = new ServerConfigurationManager(asServer);
//        serverManager.applyConfigurationWithoutRestart(sourcePath.toFile(), targetPath.toFile(), true);
//        serverManager.restartForcefully();
//
//        super.init();
    }

    @Test(groups = "wso2.as", description = "Try to persist a Employee obj through the Sessionfactory")
    public void testConnectingToJMXServer() throws Exception {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://localhost:" + (SERVER_PORT + portOffset) +
                "/jndi/rmi://localhost:" + (REGISTRY_PORT + portOffset) + "/jmxrmi");
        Map<String, Object> environment = new HashMap<>();
        String[] credentials = {"admin", "admin"};
        environment.put(JMXConnector.CREDENTIALS, credentials);
        JMXConnector jmxc = JMXConnectorFactory.connect(url, environment);
        log.info("Connection Id =" + jmxc.getConnectionId());
    }

    public synchronized void startServerUsingCarbonHome(String carbonHome, Map<String, String> commandMap, String[] envp) throws AutomationFrameworkException {
        if (this.process == null) {
            this.portOffset = this.checkPortAvailability(commandMap);
            Process tempProcess = null;

            try {
                if (!commandMap.isEmpty() && this.getPortOffsetFromCommandMap(commandMap) == 0) {
                    System.setProperty("carbon.home", carbonHome);
                }

                File e = new File(carbonHome);
                log.info("Starting carbon server............. ");
                String scriptName = TestFrameworkUtils.getStartupScriptFileName(carbonHome);
                String[] parameters = this.expandServerStartupCommandList(commandMap);
                String[] cmdArray;
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    e = new File(carbonHome + File.separator + "bin");
                    cmdArray = new String[]{"cmd.exe", "/c", scriptName + ".bat"};
                    cmdArray = this.mergePropertiesToCommandArray(parameters, cmdArray);
                    tempProcess = Runtime.getRuntime().exec(cmdArray, envp, e);
                } else {
                    cmdArray = new String[]{"sh", "bin/" + scriptName + ".sh"};
                    cmdArray = this.mergePropertiesToCommandArray(parameters, cmdArray);
                    tempProcess = Runtime.getRuntime().exec(cmdArray, envp, e);
                }

                this.errorStreamHandler = new ServerLogReader("errorStream", tempProcess.getErrorStream());
                this.inputStreamHandler = new ServerLogReader("inputStream", tempProcess.getInputStream());
                this.inputStreamHandler.start();
                this.errorStreamHandler.start();
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            serverShutdown(portOffset);
                        } catch (Exception var2) {
                            log.error("Error while server shutdown ..", var2);
                        }

                    }
                });
                ClientConnectionUtil.waitForPort(defaultHttpPort + this.portOffset, 300000L, false, (String) this.asServer.getInstance().getHosts().get("default"));
                long time = System.currentTimeMillis() + 60000L;

                while (true) {
                    if (this.inputStreamHandler.getOutput().contains("Mgt Console URL") || System.currentTimeMillis() >= time) {
                        int httpsPort = defaultHttpsPort + this.portOffset;
                        String backendURL = this.asServer.getContextUrls().getSecureServiceUrl().replaceAll("(:\\d+)", ":" + httpsPort);
                        User superUser = this.asServer.getSuperTenant().getTenantAdmin();
                        ClientConnectionUtil.waitForLogin(backendURL, superUser);
                        log.info("Server started successfully.");
                        break;
                    }
                }
            } catch (XPathExpressionException | IOException var13) {
                throw new IllegalStateException("Unable to start server", var13);
            }

            this.process = tempProcess;
        }
    }

    private int checkPortAvailability(Map<String, String> commandMap) throws AutomationFrameworkException {
        int portOffset = this.getPortOffsetFromCommandMap(commandMap);
        if (ClientConnectionUtil.isPortOpen(defaultHttpPort + portOffset)) {
            throw new AutomationFrameworkException("Unable to start carbon server on port " + (defaultHttpPort + portOffset) + " : Port already in use");
        } else if (ClientConnectionUtil.isPortOpen(defaultHttpsPort + portOffset)) {
            throw new AutomationFrameworkException("Unable to start carbon server on port " + (defaultHttpsPort + portOffset) + " : Port already in use");
        } else {
            return portOffset;
        }
    }

    private int getPortOffsetFromCommandMap(Map<String, String> commandMap) {
        return commandMap.containsKey("-DportOffset") ? Integer.parseInt((String) commandMap.get("-DportOffset")) : 0;
    }

    private String[] expandServerStartupCommandList(Map<String, String> commandMap) {
        if (commandMap != null && commandMap.size() != 0) {
            String[] cmdParaArray = null;
            String cmdArg = null;
            if (commandMap.containsKey("cmdArg")) {
                cmdArg = (String) commandMap.get("cmdArg");
                cmdParaArray = cmdArg.trim().split("\\s+");
                commandMap.remove("cmdArg");
            }

            String[] parameterArray = new String[commandMap.size()];
            int arrayIndex = 0;
            Set entries = commandMap.entrySet();

            String parameter;
            for (Iterator i$ = entries.iterator(); i$.hasNext(); parameterArray[arrayIndex++] = parameter) {
                Map.Entry entry = (Map.Entry) i$.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (value != null && !value.isEmpty()) {
                    parameter = key + "=" + value;
                } else {
                    parameter = key;
                }
            }

            if (cmdArg != null) {
                commandMap.put("cmdArg", cmdArg);
            }

            if (cmdParaArray != null && cmdParaArray.length != 0) {
                return (String[]) ArrayUtils.addAll(parameterArray, cmdParaArray);
            } else {
                return parameterArray;
            }
        } else {
            return null;
        }
    }

    private String[] mergePropertiesToCommandArray(String[] parameters, String[] cmdArray) {
        if (parameters != null) {
            cmdArray = this.mergerArrays(cmdArray, parameters);
        }

        return cmdArray;
    }

    private String[] mergerArrays(String[] array1, String[] array2) {
        return (String[]) ArrayUtils.addAll(array1, array2);
    }

    public synchronized void serverShutdown(int portOffset) throws AutomationFrameworkException {
        if (this.process != null) {
            log.info("Shutting down server..");
            if (ClientConnectionUtil.isPortOpen(Integer.parseInt("9443") + portOffset)) {
                int e = defaultHttpsPort + portOffset;
                String url = null;

                try {
                    url = this.asServer.getContextUrls().getBackEndUrl();
                } catch (XPathExpressionException var10) {
                    throw new AutomationFrameworkException("Get context failed", var10);
                }

                String backendURL = url.replaceAll("(:\\d+)", ":" + e);

                try {
                    ClientConnectionUtil.sendForcefulShutDownRequest(backendURL, this.asServer.getSuperTenant().getContextUser().getUserName(), this.asServer.getSuperTenant().getContextUser().getPassword());
                } catch (AutomationFrameworkException var8) {
                    throw new AutomationFrameworkException("Get context failed", var8);
                } catch (XPathExpressionException var9) {
                    throw new AutomationFrameworkException("Get context failed", var9);
                }

                long time = System.currentTimeMillis() + 300000L;

                while (!this.inputStreamHandler.getOutput().contains("Halting JVM") && System.currentTimeMillis() < time) {
                    ;
                }

                log.info("Server stopped successfully...");
            }

            this.inputStreamHandler.stop();
            this.errorStreamHandler.stop();
            this.process.destroy();
            this.process = null;
            if (this.isCoverageEnable) {
                try {
                    log.info("Generating Jacoco code coverage...");
                    this.generateCoverageReport(new File(this.carbonHome + File.separator + "repository" + File.separator + "components" + File.separator + "plugins" + File.separator));
                } catch (IOException var7) {
                    log.error("Failed to generate code coverage ", var7);
                    throw new AutomationFrameworkException("Failed to generate code coverage ", var7);
                }
            }

            if (portOffset == 0) {
                System.clearProperty("carbon.home");
            }
        }

    }

    public synchronized String setUpCarbonHome(String carbonServerZipFile) throws IOException, AutomationFrameworkException {
        if (this.process != null) {
            return this.carbonHome;
        } else {
            int indexOfZip = carbonServerZipFile.lastIndexOf(".zip");
            if (indexOfZip == -1) {
                throw new IllegalArgumentException(carbonServerZipFile + " is not a zip file");
            } else {
                String fileSeparator = File.separator.equals("\\") ? "\\" : "/";
                if (fileSeparator.equals("\\")) {
                    carbonServerZipFile = carbonServerZipFile.replace("/", "\\");
                }

                String extractedCarbonDir = carbonServerZipFile.substring(carbonServerZipFile.lastIndexOf(fileSeparator) + 1, indexOfZip);
                FileManipulator.deleteDir(extractedCarbonDir);
                String extractDir = "carbontmp" + System.currentTimeMillis();
                String baseDir = System.getProperty("basedir", ".") + File.separator + "target";
                log.info("Extracting carbon zip file.. ");
                (new ArchiveExtractor()).extractFile(carbonServerZipFile, baseDir + File.separator + extractDir);
                this.carbonHome = (new File(baseDir)).getAbsolutePath() + File.separator + extractDir + File.separator + extractedCarbonDir;

                try {
                    this.isCoverageEnable = Boolean.parseBoolean(this.asServer.getConfigurationValue("//coverage"));
                } catch (XPathExpressionException var8) {
                    throw new AutomationFrameworkException("Coverage configuration not found in automation.xml", var8);
                }

                if (this.isCoverageEnable) {
                    this.instrumentForCoverage();
                }

                return this.carbonHome;
            }
        }
    }

    private void instrumentForCoverage() throws IOException, AutomationFrameworkException {
        String scriptName = TestFrameworkUtils.getStartupScriptFileName(this.carbonHome);
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            this.insertJacocoAgentToBatScript(scriptName);
            if (log.isDebugEnabled()) {
                log.debug("Included files " + CodeCoverageUtils.getInclusionJarsPattern(":"));
                log.debug("Excluded files " + CodeCoverageUtils.getExclusionJarsPattern(":"));
            }
        } else {
            this.insertJacocoAgentToShellScript(scriptName);
        }

    }

    private void insertJacocoAgentToBatScript(String scriptName) throws IOException {
        String jacocoAgentFile = CodeCoverageUtils.getJacocoAgentJarLocation();
        this.coverageDumpFilePath = FrameworkPathUtil.getCoverageDumpFilePath();
        CodeCoverageUtils.insertJacocoAgentToStartupBat(new File(this.carbonHome + File.separator + "bin" + File.separator + scriptName + ".bat"), new File(this.carbonHome + File.separator + "tmp" + File.separator + scriptName + ".bat"), "-Dcatalina.base", "-javaagent:" + jacocoAgentFile + "=destfile=" + this.coverageDumpFilePath + "" + ",append=true,includes=" + CodeCoverageUtils.getInclusionJarsPattern(":"));
    }

    private void insertJacocoAgentToShellScript(String scriptName) throws IOException {
        String jacocoAgentFile = CodeCoverageUtils.getJacocoAgentJarLocation();
        this.coverageDumpFilePath = FrameworkPathUtil.getCoverageDumpFilePath();
        CodeCoverageUtils.insertStringToFile(new File(this.carbonHome + File.separator + "bin" + File.separator + scriptName + ".sh"), new File(this.carbonHome + File.separator + "tmp" + File.separator + scriptName + ".sh"), "-Dwso2.server.standalone=true", "-javaagent:" + jacocoAgentFile + "=destfile=" + this.coverageDumpFilePath + "" + ",append=true,includes=" + CodeCoverageUtils.getInclusionJarsPattern(":") + " \\");
    }

    private void generateCoverageReport(File classesDir) throws IOException, AutomationFrameworkException {
        CodeCoverageUtils.executeMerge(FrameworkPathUtil.getJacocoCoverageHome(), FrameworkPathUtil.getCoverageMergeFilePath());
        ReportGenerator reportGenerator = new ReportGenerator(new File(FrameworkPathUtil.getCoverageMergeFilePath()), classesDir, new File(CodeCoverageUtils.getJacocoReportDirectory()), (File) null);
        reportGenerator.create();
        log.info("Jacoco coverage dump file path : " + FrameworkPathUtil.getCoverageDumpFilePath());
        log.info("Jacoco class file path : " + classesDir);
        log.info("Jacoco coverage HTML report path : " + CodeCoverageUtils.getJacocoReportDirectory() + File.separator + "index.html");
    }

    public void applyConfiguration(File sourceFile, File targetFile) throws IOException {
        FileChannel source = null;
        FileChannel destination = null;

        if (!targetFile.exists() && !targetFile.createNewFile()) {
            throw new IOException("File " + targetFile + "creation fails");
        }

        source = (new FileInputStream(sourceFile)).getChannel();
        destination = (new FileOutputStream(targetFile)).getChannel();


        destination.transferFrom(source, 0L, source.size());
        if (source != null) {
            source.close();
        }

        if (destination != null) {
            destination.close();
        }
    }
}

