/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.appserver.integration.test.server.security.manager.extension;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.extensions.ExecutionListenerExtension;
import org.wso2.carbon.automation.engine.frameworkutils.enums.OperatingSystems;
import org.wso2.carbon.automation.extensions.ExtensionConstants;
import org.wso2.carbon.automation.extensions.servers.carbonserver.TestServerManager;
import org.wso2.carbon.automation.extensions.servers.utils.ServerLogReader;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.integration.common.utils.FileManager;

import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * This Extension class will start the carbon server with java security manager enabled
 */

public class CarbonServerWithSecurityManagerExtension extends ExecutionListenerExtension {
    private static final Log log = LogFactory.getLog(CarbonServerWithSecurityManagerExtension.class);
    private static TestServerManager testServerWithSecurityManager;

    @Override
    public void initiate() throws AutomationFrameworkException {
        if (!System.getProperty(FrameworkConstants.SYSTEM_PROPERTY_OS_NAME).toLowerCase()
                .contains(OperatingSystems.WINDOWS.toString().toLowerCase())) {
            AutomationContext context;
            try {
                context = new AutomationContext("AS", TestUserMode.SUPER_TENANT_ADMIN);
            } catch (XPathExpressionException e) {
                throw new AutomationFrameworkException("Error Initiating Server Information", e);
            }

            //if port offset is not set, setting it to 0
            if (getParameters().get(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND) == null) {
                getParameters().put(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND, "0");
            }

            testServerWithSecurityManager = new TestServerManager(context, null, getParameters()) {
                public void configureServer() throws AutomationFrameworkException {

                    String resourcePtah = TestConfigurationProvider.getResourceLocation("AS")
                                          + File.separator + "security" + File.separator + "manager";

                    //copying java options to wso2server.sh
                    /**
                     -Djava.security.manager=org.wso2.carbon.bootstrap.CarbonSecurityManager \
                     -Djava.security.policy=$CARBON_HOME/repository/conf/sec.policy \
                     -Drestricted.packages=sun.,com.sun.xml.internal.ws.,com.sun.xml.internal.bind.
                     ,com.sun.imageio.,org.wso2.carbon. \
                     -Ddenied.system.properties=javax.net.ssl.trustStore,javax.net.ssl.trustStorePassword
                     ,denied.system.properties \
                     */
                    try {
                        addSecOptions(new File(testServerWithSecurityManager.getCarbonHome() + File.separator + "bin"
                                               + File.separator + "wso2server.sh"));
                        //copying script file to sign the jar files
                        FileManager.copyFile(new File(resourcePtah + File.separator + "sign-packs.sh")
                                , testServerWithSecurityManager.getCarbonHome() + File.separator + "sign-packs.sh");

                        File commandDir = new File(testServerWithSecurityManager.getCarbonHome());
                        //signing the jar files
                        Process signingProcess = Runtime.getRuntime().exec(new String[]{"sh", "sign-packs.sh"}, null, commandDir);
                        ServerLogReader signingProcessInputStreamHandler = new ServerLogReader("inputStream"
                                , signingProcess.getInputStream());
                        signingProcessInputStreamHandler.start();
                        //wait signing process to complete
                        signingProcess.waitFor();
                    } catch (IOException e) {
                        throw new AutomationFrameworkException(e.getMessage(), e);
                    } catch (InterruptedException e) {
                        throw new AutomationFrameworkException(e.getMessage(), e);
                    }

                }
            };
        }
    }

    @Override
    public void onExecutionStart()
            throws AutomationFrameworkException {
        if (!System.getProperty(FrameworkConstants.SYSTEM_PROPERTY_OS_NAME).toLowerCase()
                .contains(OperatingSystems.WINDOWS.toString().toLowerCase())) {
            try {
                String carbonHome = testServerWithSecurityManager.startServer();
                System.setProperty(ExtensionConstants.CARBON_HOME, carbonHome);
            } catch (IOException e) {
                throw new AutomationFrameworkException("Error while starting server " + e.getMessage(), e);
            } catch (XPathExpressionException e) {
                throw new AutomationFrameworkException("Error while starting server " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void onExecutionFinish() throws AutomationFrameworkException {
        if (System.getProperty(FrameworkConstants.SYSTEM_PROPERTY_OS_NAME).toLowerCase()
                .contains(OperatingSystems.WINDOWS.toString().toLowerCase())) {
            testServerWithSecurityManager.stopServer();
        }

    }

    public static TestServerManager getTestServer() {
        return testServerWithSecurityManager;
    }

    /**
     * Editing the file with java options
     *
     * @param inFile input file
     * @throws IOException when there is no such file
     */
    private void addSecOptions(File inFile) throws IOException {
        String lineToBeInserted = "-Djava.security.manager=org.wso2.carbon.bootstrap.CarbonSecurityManager \\\n" +
                                  "    -Djava.security.policy=$CARBON_HOME/repository/conf/sec.policy \\\n" +
                                  "    -Drestricted.packages=sun.,com.sun.xml.internal.ws.,com.sun.xml.internal.bind.,com.sun.imageio.,org.wso2.carbon. \\\n" +
                                  "    -Ddenied.system.properties=javax.net.ssl.trustStore,javax.net.ssl.trustStorePassword,denied.system.properties \\";

        String lineToBeChecked = "org.wso2.carbon.bootstrap.Bootstrap";

        File tmpFile = new File(inFile.getAbsolutePath() + ".tmp");
        FileInputStream fis = new FileInputStream(inFile);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        FileOutputStream fos;
        PrintWriter out = null;

        try {
            //create temporary out file to hold file content
            fos = new FileOutputStream(tmpFile);
            out = new PrintWriter(new OutputStreamWriter(fos, Charset.forName("UTF-8")), true);

            String thisLine;
            while ((thisLine = in.readLine()) != null) {
                if (thisLine.contains(lineToBeChecked)) {
                    out.println(lineToBeInserted);
                }
                out.println(thisLine);
            }

            if (!tmpFile.renameTo(inFile)) {
                throw new IOException("Failed to rename file " + tmpFile.getName() + "as " + inFile.getName());
            }

            if (tmpFile.exists()) {
                if (!tmpFile.delete()) {
                    log.warn("Failed to delete temporary file - " + tmpFile.getAbsolutePath());
                }
            }

            log.info("File " + inFile.getName() + " has been modified and inserted new line before " + lineToBeChecked);
            log.info("New line inserted in to : " + inFile.getName());
            log.info("New line inserted : " + lineToBeInserted);

        } finally {
            if (out != null) {
                out.flush();
            }
            if (out != null) {
                out.close();
            }
            in.close();
        }
    }
}
