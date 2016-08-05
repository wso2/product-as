/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.appserver.test.integration;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.appserver.test.integration.statisticspublishing.Constants;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


/**
 * The test suite listeners class provides the environment setup for the integration test.
 *
 * @since 6.0.0
 */
public class TestListener implements ITestListener {
    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    private int serverStartCheckTimeout;
    private File appserverHome;
    private int applicationServerPort;
    private int thriftPort;
    private int thriftSSLPort;
    private boolean isSuccessServerStartup;
    private ApplicationServerProcessHandler processHandler;
    private ServerStatusHook serverStatusHook;
    private boolean isSuccessTermination = false;

    @Override
    public void onStart(ITestContext iTestContext) {

        try {
            log.info("Starting test: " + iTestContext.getName());
            String hookClassName = iTestContext.getCurrentXmlTest().getParameter("server-status-hook");
            if (hookClassName != null) {
                Class<?> clazz = Class.forName(hookClassName);
                Object hookObject = clazz.newInstance();

                if (hookObject instanceof ServerStatusHook) {
                    serverStatusHook = (ServerStatusHook) hookObject;
                } else {
                    log.info("Class: " + clazz.getName() + " must implement the ServerStatusHook interface.");
                }
            }

            log.info("Starting pre-integration test setup...");

            appserverHome = new File(System.getProperty(TestConstants.APPSERVER_HOME));
            log.info("Application server home : " + appserverHome.toString());

            // copying jaggery sample web app to webapps directory
            copyJaggeryWebApp(appserverHome);

            processHandler = new ApplicationServerProcessHandler(appserverHome);

            serverStartCheckTimeout = Integer.valueOf(System.getProperty(TestConstants.SERVER_TIMEOUT));

            applicationServerPort = TestConstants.TOMCAT_DEFAULT_PORT;
            int ajpPort = TestConstants.TOMCAT_DEFAULT_AJP_PORT;
            int serverShutdownPort = TestConstants.TOMCAT_DEFAULT_SERVER_SHUTDOWN_PORT;
            int httpsPort = TestConstants.TOMCAT_DEFAULT_HTTPS_PORT;

            if (!TestUtils.isPortAvailable(applicationServerPort) || !TestUtils.isPortAvailable(ajpPort) ||
                    !TestUtils.isPortAvailable(serverShutdownPort)) {
                int portCheckMin = Integer.valueOf(System.getProperty(TestConstants.PORT_CHECK_MIN));
                int portCheckMax = Integer.valueOf(System.getProperty(TestConstants.PORT_CHECK_MAX));

                List<Integer> availablePorts = TestUtils.getAvailablePortsFromRange(portCheckMin, portCheckMax, 3);

                if ((availablePorts != null) && availablePorts.size() > 2) {
                    applicationServerPort = availablePorts.get(0);
                    ajpPort = availablePorts.get(1);
                    serverShutdownPort = availablePorts.get(2);
                }
            }

            if (!TestUtils.isPortAvailable(httpsPort)) {
                int portCheckMin = Integer.valueOf(System.getProperty(TestConstants.HTTPS_PORT_CHECK_MIN));
                int portCheckMax = Integer.valueOf(System.getProperty(TestConstants.HTTPS_PORT_CHECK_MAX));

                List<Integer> availablePorts = TestUtils.getAvailablePortsFromRange(portCheckMin, portCheckMax, 1);

                if ((availablePorts != null) && availablePorts.size() > 0) {
                    httpsPort = availablePorts.get(0);
                }
            }

            updateServerPorts(applicationServerPort, ajpPort, serverShutdownPort, httpsPort);

            System.setProperty(TestConstants.APPSERVER_PORT, String.valueOf(applicationServerPort));

            if (iTestContext.getName().equals("configuration-loader-test")) {
                addValveToServerXML(TestConstants.CONFIGURATION_LOADER_SAMPLE_VALVE);
            }

            //setting thrift ports and valve before starting statistics publishing tests
            if (iTestContext.getName().equals("statistics-publishing-test")) {
                thriftPort = Constants.DEFAULT_THRIFT_PORT;
                thriftSSLPort = Constants.DEFAULT_THRIFT_SSL_PORT;

                if (!TestUtils.isPortAvailable(thriftPort) || !TestUtils.isPortAvailable(thriftSSLPort)) {
                    List<Integer> availablePort = TestUtils.getAvailablePortsFromRange(
                            Constants.PORT_SCAN_MIN, Constants.PORT_SCAN_MAX, 2);
                    if ((availablePort != null) && availablePort.size() > 1) {
                        thriftPort = availablePort.get(0);
                        thriftSSLPort = availablePort.get(1);
                    }
                }
                updateThriftPorts(thriftSSLPort, thriftPort);
                System.setProperty(Constants.THRIFT_PORT, String.valueOf(thriftPort));
                System.setProperty(Constants.THRIFT_SSL_PORT, String.valueOf(thriftSSLPort));

                addValveToServerXML(TestConstants.HTTP_STATISTICS_PUBLISHING_VALVE);
            }

            log.info(processHandler.getOperatingSystem() + " operating system was detected");
            log.info("Jacoco argLine: " + processHandler.getJacocoArgLine());

            if (serverStatusHook != null) {
                serverStatusHook.beforeServerStart();
            }

            log.info("Starting the server [{}:{}, {}:{}, {}:{}, {}:{}] ...",
                    TestConstants.TOMCAT_DEFAULT_PORT_NAME, applicationServerPort,
                    TestConstants.TOMCAT_AJP_PORT_NAME, ajpPort,
                    TestConstants.TOMCAT_SERVER_SHUTDOWN_PORT_NAME, serverShutdownPort,
                    TestConstants.TOMCAT_SERVER_HTTPS_PORT_NAME, httpsPort);

            processHandler.startServer();
            registerShutdownHook();
            waitForServerStartup();

            if (isSuccessServerStartup) {
                log.info("Application server started successfully. Running test suite...");
            }

            if (serverStatusHook != null) {
                serverStatusHook.afterServerStart();
            }

        } catch (Exception ex) {
            String message = "Could not start the server process";
            log.error(message, ex);
            throw new RuntimeException(message, ex);
        }
    }

    private void copyJaggeryWebApp(File appserverHome) throws IOException {
        FileUtils.copyDirectory(
                Paths.get(appserverHome.toString(), "samples", "jaggery-sample-apps", "coffeeshop").toFile(),
                Paths.get(appserverHome.toString(), "webapps", "coffeeshop").toFile());
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        log.info("Starting post-integration tasks...");
        if (serverStatusHook != null) {
            try {
                serverStatusHook.beforeServerShutdown();
            } catch (Exception ignore) {
            }
        }
        log.info("Terminating the Application server");
        processHandler.stopServer();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignore) {
        }
        if (serverStatusHook != null) {
            try {
                serverStatusHook.afterServerShutdown();
            } catch (Exception ignore) {
            }
        }
        log.info("Finished the post-integration tasks...");
        log.info("Finished test: " + iTestContext.getName());
        isSuccessTermination = true;

        //revert thrift port changes made during the test
        if (iTestContext.getName().equals("statistics-publishing-test")) {
            try {
                updateThriftPorts(Constants.ORIGINAL_THRIFT_SSL_PORT, Constants.ORIGINAL_THRIFT_PORT);
            } catch (Exception ex) {
                String message = "Error while reverting thrift ports.";
                log.error(message, ex);
                throw new RuntimeException(message, ex);
            }
        }
    }


    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (!isSuccessTermination) {
                    if (serverStatusHook != null) {
                        try {
                            serverStatusHook.beforeServerShutdown();
                        } catch (Exception ignore) {
                        }
                    }
                    processHandler.stopServer();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignore) {
                    }
                    if (serverStatusHook != null) {
                        try {
                            serverStatusHook.afterServerShutdown();
                        } catch (Exception ignore) {
                        }
                    }
                }
            }
        });
    }


    private void waitForServerStartup() throws IOException {
        log.info("Checking server availability... (Timeout: " + serverStartCheckTimeout + " seconds)");
        int startupCounter = 0;
        boolean isTimeout = false;
        while (!TestUtils.isServerListening("localhost", applicationServerPort)) {
            if (startupCounter >= serverStartCheckTimeout) {
                isTimeout = true;
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            startupCounter++;
        }

        if (!isTimeout) {
            isSuccessServerStartup = true;
            log.info("Server started.");
        } else {
            isSuccessServerStartup = false;
            String message = "Server startup timeout.";
            log.error(message);
            throw new IOException(message);
        }
    }

    /**
     * Updates http and ajp connector ports and server shutdown port in server.xml.
     *
     * @param httpConnectorPort  http connector port
     * @param ajpPort            ajp port
     * @param serverShutdownPort server shutdown port
     * @param httpsPort https connector port
     */
    private static void updateServerPorts(int httpConnectorPort, int ajpPort, int serverShutdownPort, int httpsPort)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Path serverXML = Paths.get(System.getProperty(TestConstants.APPSERVER_HOME), "conf", "server.xml");

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().
                parse(new InputSource(serverXML.toString()));

        //  change http connector and ajp connector ports
        Map<String, String> connectorProtocolPortMap = new HashMap<>();
        connectorProtocolPortMap.put("HTTP/1.1", String.valueOf(httpConnectorPort));
        connectorProtocolPortMap.put("AJP/1.3", String.valueOf(ajpPort));
        connectorProtocolPortMap.put("org.apache.coyote.http11.Http11NioProtocol", String.valueOf(httpsPort));

        NodeList connectors = document.getElementsByTagName("Connector");
        for (int i = 0; i < connectors.getLength(); i++) {
            Node connector = connectors.item(i);
            NamedNodeMap connectorAttributes = connector.getAttributes();
            String protocol = connectorAttributes.getNamedItem("protocol").getTextContent();
            if (connectorProtocolPortMap.containsKey(protocol)) {
                connectorAttributes.getNamedItem("port").setTextContent(connectorProtocolPortMap.get(protocol));
                if (connectorAttributes.getNamedItem("redirectPort") != null) {
                    connectorAttributes.getNamedItem("redirectPort").setTextContent(String.valueOf(httpsPort));
                }
            }
        }

        //  change server shutdown port
        Node server = document.getElementsByTagName("Server").item(0);
        server.getAttributes().getNamedItem("port").setTextContent(String.valueOf(serverShutdownPort));

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(serverXML.toFile().getPath()));

    }

    /**
     * Updates thrift ports in wso2as.xml.
     *
     * @param thriftPort
     * @param thriftSSLPort
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws TransformerException
     */
    private static void updateThriftPorts(int thriftSSLPort, int thriftPort)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Path serverXML = Paths.get(System.getProperty(TestConstants.APPSERVER_HOME), "conf", "wso2", "wso2as.xml");

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().
                parse(new InputSource(serverXML.toString()));

        Node authenticationURL = document.getElementsByTagName("AuthenticationURL").item(0);
        authenticationURL.setTextContent("ssl://" + Constants.HOST + ":" + thriftSSLPort);

        Node publisherURL = document.getElementsByTagName("PublisherURL").item(0);
        publisherURL.setTextContent("tcp://" + Constants.HOST + ":" + thriftPort);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(serverXML.toFile().getPath()));

    }

    /**
     * Registers an Apache Tomcat Valve in the server.xml of the Application Server Catalina config base.
     *
     * @param className the fully qualified class name of the Valve implementation
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created
     * @throws SAXException                 if any parse errors occur
     * @throws IOException                  if an I/O error occurs
     * @throws XPathExpressionException     if the XPath expression cannot be evaluated
     * @throws TransformerException         if an error occurs during the transformation
     */
    private static void addValveToServerXML(String className)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException,
            TransformerException {
        Path serverXML = Paths.get(System.getProperty(TestConstants.APPSERVER_HOME), "conf", "server.xml");
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().
                parse(new InputSource(serverXML.toString()));
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList valves = (NodeList) xpath.
                evaluate("/Server/Service/Engine/Host/Valve", document, XPathConstants.NODESET);

        Element valve = document.createElement("Valve");
        Attr attrClassName = document.createAttribute("className");
        attrClassName.setValue(className);
        valve.setAttributeNode(attrClassName);
        valves.item(0).getParentNode().appendChild(valve);

        Transformer xFormer = TransformerFactory.newInstance().newTransformer();
        xFormer.transform(new DOMSource(document), new StreamResult(serverXML.toFile().getAbsolutePath()));
    }


    @Override
    public void onTestStart(ITestResult iTestResult) {

    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {

    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {

    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }
}
