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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;


/**
 * This Class is implemented to encrypt password and check password has encrypted.
 */

public class PasswordEncryptionUtil {

    private static final Log log = LogFactory.getLog(PasswordEncryptionUtil.class);

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
            String expression = "datasources-configuration/datasources/datasource/definition" +
                    "[@type='RDBMS']/configuration/password";

            NodeList nodeList = (
                    NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element eElement = (Element) nodeList.item(i);
                if (eElement.getAttribute("svns:secretAlias").equals("Datasources.WSO2_CARBON_DB.Configuration.Password")
                        && nodeList.item(i).getFirstChild().getNodeValue().equals("password")) {
                    foundEncryption = true;
                    break;
                }
            }
        } catch (Exception e) {
            handleException("Error when passing the master-datasources.xml file to create xmlDocument", e);
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
            throws IOException {
        boolean foundTheMessage = false;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        Process process = null;
        try {
            log.info("Running the ciphertool.sh ..");

            File commandDir = new File(carbonHome + "/bin");
            ProcessBuilder processBuilder = new ProcessBuilder(cmdArray);
            processBuilder.directory(commandDir);
            process = processBuilder.start();
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
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
            throw new IOException("Error when reading the InputStream when running shell script ", ex);
        } finally {
            if (is != null) {
                is.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (br != null) {
                br.close();
            }
            if (process != null) {
                process.destroy();
            }
        }
    }


    private static void handleException(String msg, Exception e) throws Exception {
        log.error(msg, e);
        throw new Exception(msg, e);
    }

}
