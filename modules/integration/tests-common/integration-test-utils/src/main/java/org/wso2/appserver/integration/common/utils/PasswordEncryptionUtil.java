/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wso2.appserver.integration.common.exception.PasswordEncryptionIntegrationTestException;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;


/**
 * This Class is implemented to encrypt password and check password has encrypted.
 */

public class PasswordEncryptionUtil {

    private static final Log log = LogFactory.getLog(PasswordEncryptionUtil.class);
    private static final String SERVER_START_LINE = "Starting WSO2 Carbon";
    private static final String MANAGEMENT_CONSOLE_URL = "Mgt Console URL";

    /**
     * By checking the master-datasources.xml password node has encrypted attribute
     *
     * @return boolean - true : if password has encrypted ,false : if not
     * @throws Exception - Error when getting and reading master-datasources.xml
     */
    public static boolean isPasswordEncrypted(String carbonHome) throws Exception {
        boolean foundEncryption = false;
        try {
            FileInputStream file =
                    new FileInputStream(new File(carbonHome + File.separator + "repository" +
                                                 File.separator + "conf" + File.separator + "datasources" +
                                                 File.separator + "master-datasources.xml"));

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(file);
            XPath xPath = XPathFactory.newInstance().newXPath();


            NodeList nodeList = (NodeList) xPath.compile
                    (ASIntegrationConstants.ENCRYPTED_PASSWD_URL).evaluate(xmlDocument,
                                                                           XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element eElement = (Element) nodeList.item(i);
                if (eElement.getAttribute(ASIntegrationConstants.PASSWORD_PROPERTY_SECRET_ALIAS_KEY).
                        equals(ASIntegrationConstants.SVN_SECRET_ALIAS_WSO2_DATASOURCE)
                    && nodeList.item(i).getFirstChild().getNodeValue().equals("password")) {
                    foundEncryption = true;
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Error when passing the master-datasources.xml file to create xmlDocument", e);
            throw new PasswordEncryptionIntegrationTestException("Error when passing the master-datasources.xml file to create xmlDocument", e);
        }
        return foundEncryption;
    }

    /**
     * By using run.sh running the ciphertool.sh and give the password as input
     *
     * @param carbonHome - carbon server installation location
     * @param cmdArray   - commands to be executed.
     * @return - boolean shell script ran successfully or not
     * @throws IOException - Error when reading the InputStream when running shell script
     */
    public static boolean runCipherToolScriptAndCheckStatus(String carbonHome, String[] cmdArray)
            throws PasswordEncryptionIntegrationTestException {
        boolean foundTheMessage = false;
        BufferedReader br = null;
        Process process = null;
        try {
            log.info("Running the ciphertool.sh ..");

            File commandDir = new File(carbonHome + "/bin");
            ProcessBuilder processBuilder = new ProcessBuilder(cmdArray);
            processBuilder.directory(commandDir);
            process = processBuilder.start();
            br = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                log.info(line);
                if (line.contains("Encryption is done Successfully")) {
                    foundTheMessage = true;
                }
            }
            return foundTheMessage;
        } catch (IOException ex) {
            log.error("Error when reading the InputStream when running shell script ", ex);
            throw new PasswordEncryptionIntegrationTestException("Error when reading the InputStream when running shell script ", ex);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.warn("error while closing the buffered reader");
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * Verifying the server startup and shutdown by reading the log
     *
     * @param logViewerClient - logs that has to be read
     * @return boolean - if found the server startup and shutdown correctly, else false
     * @throws RemoteException             -
     * @throws LogViewerLogViewerException
     */
    public static boolean verifyInLogs(LogViewerClient logViewerClient)
            throws PasswordEncryptionIntegrationTestException {
        boolean status = false;
        int startLine = 0;
        int stopLine = 0;
        try {
            LogEvent[] logEvents = logViewerClient.getAllRemoteSystemLogs();
            if (logEvents.length > 0) {
                for (int i = 0; i < logEvents.length; i++) {
                    if (logEvents[i] != null) {
                        if (logEvents[i].getMessage().contains(SERVER_START_LINE)) {
                            stopLine = i;
                            log.info("Server started message found - " + logEvents[i].getMessage());
                        }
                        if (logEvents[i].getMessage().contains(MANAGEMENT_CONSOLE_URL)) {
                            startLine = i;
                            log.info("Server stopped message found - " + logEvents[i].getMessage());
                        }
                    }
                    if (startLine != 0 && stopLine != 0) {
                        status = true;
                        break;
                    }
                }
            }
            return status;
        } catch (RemoteException ex) {
            log.error("Error when getting the log ", ex);
            throw new PasswordEncryptionIntegrationTestException("Error when getting the log ", ex);
        } catch (LogViewerLogViewerException ex) {
            log.error("Error when reading the log ", ex);
            throw new PasswordEncryptionIntegrationTestException("Error when reading the log ", ex);
        }
    }

}
