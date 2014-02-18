/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.integration.test.sampleservices.axis2module;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.logging.LogViewerClient;
import org.wso2.carbon.automation.api.clients.module.mgt.ModuleAdminServiceClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.serverutils.ServerConfigurationManager;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.integration.test.ASIntegrationTest;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.carbon.module.mgt.stub.types.ModuleMetaData;
import org.wso2.carbon.utils.CarbonUtils;

import javax.activation.DataHandler;
import java.io.*;
import java.net.URL;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This class can be used for testing purposes of Axis2Module sample scenario.
 * test class disabled due to https://wso2.org/jira/browse/WSAS-1300
 */
public abstract class Axis2ModuleTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(Axis2ModuleTestCase.class);
    private ModuleAdminServiceClient moduleAdminServiceClient;
    private ServerConfigurationManager serverConfigurationManager;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        defineLogLevel();
        serverConfigurationManager = new ServerConfigurationManager(asServer.getBackEndUrl());
        serverConfigurationManager.restartGracefully(); // restarting the server
    }

    @AfterClass(alwaysRun = true)
    public void disengageAndDeleteModule() throws Exception {
        moduleAdminServiceClient =
                new ModuleAdminServiceClient(asServer.getBackEndUrl(), asServer.getSessionCookie());
        String moduleId = null;
        ModuleMetaData[] moduleMetaData = moduleAdminServiceClient.getModuleList();

        for (ModuleMetaData module : moduleMetaData) {
            if (module.getModulename().contains("logmodule")) {
                moduleId = module.getModuleId();
            }
        }
        assertTrue(moduleAdminServiceClient.disengageModule(moduleId, "HelloService"));
        moduleAdminServiceClient.deleteModule(moduleId);
        removeLogLevel();
        // restarting the server
        serverConfigurationManager = new ServerConfigurationManager(asServer.getBackEndUrl());
        serverConfigurationManager.restartGracefully();
    }

    @Test(groups = "wso2.as", description = "HelloService - engage logmodule")
    public void engageModule() throws Exception {
        super.init();
        moduleAdminServiceClient =
                new ModuleAdminServiceClient(asServer.getBackEndUrl(), asServer.getSessionCookie());

        URL url = new URL("file://" + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                          "artifacts" + File.separator + "AS" + File.separator + "mar" + File.separator +
                          "LogModule-1.0.0.mar");
        DataHandler dh = new DataHandler(url);
        moduleAdminServiceClient.uploadModule(dh);  // upload logmodule
        serverConfigurationManager.restartGracefully();

        // server restart point after engaging the module

        super.init();
        moduleAdminServiceClient =
                new ModuleAdminServiceClient(asServer.getBackEndUrl(), asServer.getSessionCookie());

        ModuleMetaData[] moduleMetaData = moduleAdminServiceClient.listModulesForService("HelloService");

        boolean moduleExists = false;  // checking the availability of logmodule module for the service

        for (ModuleMetaData aModuleMetaData : moduleMetaData) {
            if (aModuleMetaData.getModulename().equals("logmodule")) {
                moduleExists = true;
                //engaging the logmodule to the service
                assertTrue(moduleAdminServiceClient.engageModule("logmodule", "HelloService"));
                break;
            }
        }

        assertTrue(moduleExists, "module engagement failure due to the unavailability of logmodule" +
                                 " module at service level context");
    }

    @Test(groups = "wso2.as", description = "Invoke service - HelloService",
          dependsOnMethods = "engageModule")
    public void invokeService() throws Exception {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        String endpoint = asServer.getServiceUrl() + "/HelloService";

        OMElement response = axisServiceClient.sendReceive(createPayLoad(), endpoint, "greet");
        log.info("Response for Invoke Service : " + response);

        assertTrue(response.toString().contains("<ns:greetResponse xmlns:ns=" +
                                                "\"http://www.wso2.org/types\"><return>Hello World, Hello Wso2 !!!" +
                                                "</return></ns:greetResponse>"));
    }

    @Test(groups = "wso2.as", description = "Retrieve DEBUG type log records from the log file and" +
                                            " perform the verification of request and response messages ",
          dependsOnMethods = "invokeService")
    public void readLog() throws Exception {
        boolean incomingMessageStatus = false;  //  check expected incoming message
        boolean outgoingMessageStatus = false;  //  check expected outgoing message

        LogViewerClient logViewerClient = new LogViewerClient(asServer.getBackEndUrl(),
                                                              userInfo.getUserName(), userInfo.getPassword());
        LogEvent[] logEvent = logViewerClient.getAllSystemLogs();

        for (LogEvent aLogEvent : logEvent) {
            if (aLogEvent.getLogger().contains("DEBUG")) {
                if (aLogEvent.getMessage().contains("Incoming Message")) {
                    assertTrue(aLogEvent.getMessage().contains("<ns:name>Hello Wso2</ns:name></ns:greet>"));
                    incomingMessageStatus = true;

                } else if (aLogEvent.getMessage().contains("Outgoing Message")) {
                    assertTrue(aLogEvent.getMessage().equals("<return>Hello World, Hello Wso2 !!!</return>"));
                    outgoingMessageStatus = true;
                }
            }
        }

        assertTrue(incomingMessageStatus, " No incoming message log found.");
        assertTrue(outgoingMessageStatus, " No outgoing message log found.");
    }

    private void defineLogLevel() throws IOException {
        String log4jFilePath = CarbonUtils.getCarbonHome() + File.separator + "repository" + File.separator + "conf" +
                               File.separator + "log4j.properties";

        FileWriter fileWriter = new FileWriter(new File(log4jFilePath), true);
        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
        // Prerequisite  - appending 'log4j.logger.org.wso2.carbon.log.module=DEBUG' entry to
        bufferWriter.write("log4j.logger.org.wso2.carbon.log.module=DEBUG");
        bufferWriter.close();
    }

    private void removeLogLevel() throws IOException {
        String log4jFilePath = CarbonUtils.getCarbonHome() + File.separator + "repository" + File.separator + "conf" +
                               File.separator + "log4j.properties";

        File inFile = new File(log4jFilePath);

        //Creating a new file this will become the original file later
        File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

        BufferedReader bufferedReader = new BufferedReader(new FileReader(log4jFilePath));
        PrintWriter printWriter = new PrintWriter(new FileWriter(tempFile));

        String line;

        while ((line = bufferedReader.readLine()) != null) {
            //write if not only contain
            if (!line.trim().equals("log4j.logger.org.wso2.carbon.log.module=DEBUG")) {
                printWriter.println(line);
                printWriter.flush();
            }
        }
        printWriter.close();
        bufferedReader.close();

        //Delete the original file
        if (!inFile.delete()) {
            assertFalse(true, "Could not delete file");
        }

        //Rename the new file to the filename the original file had.
        if (!tempFile.renameTo(inFile)) {
            assertFalse(true, "Could not rename file");
        }
    }

    private static OMElement createPayLoad() {   // creating the payload
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://www.wso2.org/types", "ns");
        OMElement getOmeOne = fac.createOMElement("greet", omNs);
        OMElement getOmeTwo = fac.createOMElement("name", omNs);
        getOmeTwo.setText("Hello Wso2");
        getOmeOne.addChild(getOmeTwo);
        return getOmeOne;
    }
}
