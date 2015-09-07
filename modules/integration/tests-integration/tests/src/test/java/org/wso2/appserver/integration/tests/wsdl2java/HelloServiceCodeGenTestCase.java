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
package org.wso2.appserver.integration.tests.wsdl2java;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.AARServiceUploaderClient;
import org.wso2.appserver.integration.common.clients.WSDL2CodeClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.engine.frameworkutils.filters.CustomFileFilter;
import org.wso2.carbon.automation.engine.frameworkutils.filters.SuffixFilter;
import org.wso2.carbon.automation.engine.frameworkutils.filters.TypeFilter;
import org.wso2.carbon.automation.extensions.servers.utils.ArchiveExtractor;
import org.wso2.carbon.automation.extensions.servers.utils.ServerLogReader;
import org.wso2.carbon.utils.FileManipulator;
import org.wso2.carbon.utils.ServerConstants;

import javax.activation.DataHandler;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class HelloServiceCodeGenTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(HelloServiceCodeGenTestCase.class);
    private static String codeGenPath;
    private static String axis2Home;
    private static String baseDir;

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @BeforeClass(alwaysRun = true)
    public void testDeployService() throws Exception {
        super.init();
        AARServiceUploaderClient aarServiceUploaderClient
                = new AARServiceUploaderClient(backendURL, sessionCookie);

        aarServiceUploaderClient.uploadAARFile("HelloWorld.aar",
                FrameworkPathUtil.getSystemResourceLocation()  + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        "HelloWorld.aar", "");

        String axis2Service = "HelloService";
        isServiceDeployed(axis2Service);
        log.info("Axis2Service.aar service uploaded successfully");

        baseDir = (System.getProperty("basedir", ".")) + File.separator + "target";
    }

    @AfterClass(alwaysRun = true)
    public void cleanResources() throws RemoteException {
        deleteService("HelloService");
        super.cleanup();
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "generate client code HelloWorld service")
    public void testGeneratedClass() throws Exception {

        WSDL2CodeClient wsdl2CodeClient = new WSDL2CodeClient(backendURL, sessionCookie);
        String wsdlURL = asServer.getContextUrls().getServiceUrl() + "/HelloService?wsdl";
        log.info("Service URL -" + wsdlURL);
        String[] options = new String[]{"-gid", "WSO2", "-aid", "WSO2-Axis2-Client", "-vn",
                "0.0.1-SNAPSHOT", "-uri", wsdlURL, "-l", "java", "-d", "adb", "-wv", "1.1"};
        DataHandler dataHandler = wsdl2CodeClient.codeGen(options);
        InputStream in = null;
        OutputStream outputStream = null;

        try {
            in = dataHandler.getDataSource().getInputStream();
            //save generated zip
            outputStream = new FileOutputStream(new File(baseDir + File.separator + "generated.zip"));

            int read;
            byte[] bytes = new byte[1024];

            while ((read = in.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } finally {
            assert in != null;
            in.close();
            assert outputStream != null;
            outputStream.close();
        }
        log.info("Done!");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "extract generated zip", dependsOnMethods = "testGeneratedClass")
    public void testExtractGeneratedCode() throws IOException {
        codeGenPath = extractZip(baseDir + File.separator + "generated.zip");

        log.info("Generated code path " + codeGenPath);
        File codeGenFile = new File(codeGenPath);
        ServerLogReader inputStreamHandler;
        ServerLogReader errorStreamHandler;
        Process tempProcess = null;
        String[] cmdArray;
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                cmdArray = new String[]{"cmd.exe", "/c", "mvn clean install"};
                tempProcess = Runtime.getRuntime().exec(cmdArray, null, codeGenFile);

            } else {
                cmdArray = new String[]{"mvn", "clean", "install"};
                System.setProperty("user.dir", codeGenPath);
                tempProcess = Runtime.getRuntime().exec(cmdArray, null, codeGenFile);
            }


            errorStreamHandler =
                    new ServerLogReader("errorStream", tempProcess.getErrorStream());
            inputStreamHandler = new ServerLogReader("inputStream", tempProcess.getInputStream());

            // start the stream readers
            inputStreamHandler.start();
            errorStreamHandler.start();

            boolean buildStatus = waitForMessage(inputStreamHandler, "BUILD SUCCESS");
            assertTrue(buildStatus, "code generation successful");

            boolean status = false;
            if (new File(codeGenPath).exists()) {
                status = true;
            }
            assertTrue(status, "cannot find the generated zip file");
        } finally {
            if (tempProcess != null) {
                tempProcess.destroy();
            }
        }
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.STANDALONE})
    @Test(groups = "wso2.as", description = "test the generated source by running relevant main client",
            dependsOnMethods = "testExtractGeneratedCode")
    public void testGeneratedSource() throws Exception {
        String resourcePath = FrameworkPathUtil.getSystemResourceLocation()+ "artifacts" + File.separator +
                "AS" + File.separator + "codegen";

        String clientClassPath = resourcePath + File.separator + "HelloServiceClient.txt";
        String buildXMLPath = resourcePath + File.separator + "build.xml";
        List<File> filePath = getAllJavaFiles(new File(codeGenPath));
        String generatedJavaFileLocation;

        if (filePath.size() > 0) {
            generatedJavaFileLocation = filePath.get(0).getParent() + File.separator
                    + "HelloServiceClient.java";
        } else {
            throw new Exception("Code generation failed");
        }

        FileManipulator.copyFile(new File(clientClassPath), new File(generatedJavaFileLocation));
        axis2Home = System.getProperty(ServerConstants.CARBON_HOME);

        log.info("AXIS2_HOME - " + axis2Home);
        System.setProperty("AXIS2_HOME", axis2Home);


        editBuildXmlFile(buildXMLPath);
        Process tempProcessAnt = null;
        try {
            if (new File(generatedJavaFileLocation).exists()) {
                ServerLogReader inputStreamHandler;

                String[] cmdArray;
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {

                    cmdArray = new String[]{"cmd.exe", "/c", "ant"};
                    tempProcessAnt = Runtime.getRuntime().exec(cmdArray, null,
                            new File(codeGenPath + File.separator + "generated-sources"));

                } else {
                    cmdArray = new String[]{"ant"};
                    System.setProperty("user.dir", codeGenPath);
                    tempProcessAnt = Runtime.getRuntime().exec(cmdArray, null,
                            new File(codeGenPath + File.separator + "generated-sources"));
                }


                ServerLogReader errorStreamHandler =
                        new ServerLogReader("errorStream", tempProcessAnt.getErrorStream());
                inputStreamHandler = new ServerLogReader("inputStream", tempProcessAnt.getInputStream());

                // start the stream readers
                inputStreamHandler.start();
                errorStreamHandler.start();


                boolean status = waitForMessage(inputStreamHandler, "Hello World, Krishantha");
                assertTrue(status, "Invocation successful");

            }
        } finally {
            if (tempProcessAnt != null) {
                tempProcessAnt.destroy();
            }
        }
    }

    public static List<File> getAllJavaFiles(File directory) {
        if (directory.exists()) {
            return CustomFileFilter.getFilesRecursive(directory, new SuffixFilter(TypeFilter.FILE, ".java"));
        }
        return null;
    }

    public String extractZip(String zipFile)
            throws IOException {

        int indexOfZip = zipFile.lastIndexOf(".zip");
        if (indexOfZip == -1) {
            throw new IllegalArgumentException(zipFile + " is not a zip file");
        }
        String fileSeparator = (File.separator.equals("\\")) ? "\\" : "/";
        if (fileSeparator.equals("\\")) {
            zipFile = zipFile.replace("/", "\\");
        }
        String extractedDir =
                zipFile.substring(zipFile.lastIndexOf(fileSeparator) + 1,
                        indexOfZip);
        FileManipulator.deleteDir(extractedDir);
        String extractDir = "codegen" + System.currentTimeMillis();
        new ArchiveExtractor().extractFile(zipFile, baseDir + File.separator + extractDir);

        return new File(baseDir).getAbsolutePath() + File.separator + extractDir + File.separator;
    }

    public static void editBuildXmlFile(String buildXMLPath) throws Exception {
        InputStream in = new FileInputStream(new File(buildXMLPath));
        OMElement root = OMXMLBuilderFactory.createOMBuilder(in).getDocumentElement();
        FileOutputStream fileOutputStream = null;
        XMLStreamWriter writer = null;

        try {
            OMElement node;
            //iterate through Configuration properties
            Iterator configurationPropertiesIterator = root.getChildElements();
            boolean status = false;
            while (configurationPropertiesIterator.hasNext()) {
                node = (OMElement) configurationPropertiesIterator.next();

                Iterator attribute = node.getAllAttributes();
                while (attribute.hasNext()) {
                    OMAttribute attr = (OMAttribute) attribute.next();
                    if (attr.getAttributeValue().equals("${env.AXIS2_HOME}")) {
                        System.out.println("Found");
                        attr.setAttributeValue(axis2Home);
                        status = true;
                        break;
                    }
                }
                if (status) {
                    //break if the property is modified
                    break;
                }
            }

            fileOutputStream = new FileOutputStream(new File(codeGenPath + File.separator +
                    "generated-sources" + File.separator + "build.xml"));

            writer = XMLOutputFactory.newInstance().createXMLStreamWriter(fileOutputStream);

            root.serialize(writer);
            Thread.sleep(2000);
            root.build();
        } catch (Exception e) {
            log.error("Unable to edit build.xml" + e.getMessage());
            throw new Exception("Unable to edit build.xml" + e.getMessage());
        } finally {
            assert fileOutputStream != null;
            fileOutputStream.close();
            assert writer != null;
            writer.flush();
        }
    }


    public boolean waitForMessage(ServerLogReader inputStreamHandler, String message) {
        long time = System.currentTimeMillis() + 120 * 1000;
        while (System.currentTimeMillis() < time) {
            if (inputStreamHandler.getOutput().contains(message)) {
                return true;
            }
        }
        return false;
    }
}
