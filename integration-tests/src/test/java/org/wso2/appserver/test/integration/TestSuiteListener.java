/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.wso2.appserver.test.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

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
public class TestSuiteListener implements ISuiteListener {
    private static final Logger log = LoggerFactory.getLogger(TestSuiteListener.class);
    private Process applicationServerProcess;
    private int serverStartCheckTimeout;
    private File appserverHome;
    private int applicationServerPort;
    private boolean isSuccessServerStartup;

    @Override
    public void onStart(ISuite iSuite) {
        try {
            log.info("Starting pre-integration test setup...");

            appserverHome = new File(System.getProperty(TestConstants.APPSERVER_HOME));
            log.info("Application server home : " + appserverHome.toString());

            serverStartCheckTimeout = Integer.valueOf(System.getProperty(TestConstants.SERVER_TIMEOUT));

            int availablePort = TestConstants.TOMCAT_DEFAULT_PORT;

            if (isPortAvailable(availablePort)) {
                log.info("Default port " + availablePort + " is available.");
            } else {
                int portCheckMin = Integer.valueOf(System.getProperty(TestConstants.PORT_CHECK_MIN));
                int portCheckMax = Integer.valueOf(System.getProperty(TestConstants.PORT_CHECK_MAX));

                log.info("Default port " + TestConstants.TOMCAT_DEFAULT_PORT + " is not available. Trying to use a " +
                        "port between " + portCheckMin + " and " + portCheckMax);

                availablePort = portCheckMin;

                while (availablePort <= portCheckMax) {
                    log.info("Trying to use port " + availablePort);
                    if (isPortAvailable(availablePort)) {
                        log.info("Port " + availablePort + " is available and is using as the port for the server.");
                        break;
                    }
                    availablePort++;
                }
            }

            addValveToServerXML(TestConstants.CONFIGURATION_LOADER_SAMPLE_VALVE);

            applicationServerPort = availablePort;
            System.setProperty(TestConstants.APPSERVER_PORT, String.valueOf(applicationServerPort));

            log.info("Changing the HTTP connector port of the server to " + applicationServerPort);
            setHTTPConnectorPort(applicationServerPort);

            log.info("Starting the server...");
            applicationServerProcess = startPlatformDependApplicationServer();

            waitForServerStartup();

            if (isSuccessServerStartup) {
                log.info("Application server started successfully. Running test suite...");
            }

        } catch (IOException | TransformerException | SAXException | ParserConfigurationException
                | XPathExpressionException ex) {
            terminateApplicationServer();
            String message = "Could not start the server";
            log.error(message, ex);
            throw new RuntimeException(message, ex);
        }
    }

    @Override
    public void onFinish(ISuite iSuite) {

        log.info("Starting post-integration tasks...");
        log.info("Terminating the Application server");
        terminateApplicationServer();
        log.info("Finished the post-integration tasks...");
    }

    private Process startPlatformDependApplicationServer() throws IOException {
        String os = System.getProperty("os.name");
        log.info(os + " operating system was detected");
        if (os.toLowerCase().contains("unix") || os.toLowerCase().contains("linux")) {
            log.info("Starting server as a " + os + " process");
            return applicationServerProcess = new ProcessBuilder().directory(appserverHome)
                    .command("./bin/catalina.sh", "run").start();
        } else if (os.toLowerCase().contains("windows")) {
            log.info("Starting server as a " + os + " process");
            return applicationServerProcess = new ProcessBuilder().directory(appserverHome)
                    .command("\\bin\\catalina.bat", "run").start();
        }
        return null;
    }

    public void terminateApplicationServer() {
        if (applicationServerProcess != null) {
            applicationServerProcess.destroy();
        }
    }

    private void waitForServerStartup() throws IOException {
        log.info("Checking server availability... (Timeout: " + serverStartCheckTimeout + " seconds)");
        int startupCounter = 0;
        boolean isTimeout = false;
        while (!isServerListening("localhost", applicationServerPort)) {
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
     * Checks if the server is listening using the {@code host} name and the {@code port} number specified.
     *
     * @param host the host name
     * @param port the port number
     * @return true if the server is listening else false
     */
    private static boolean isServerListening(String host, int port) {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Checks if the specified {@code port} number is available or closed.
     *
     * @param port the port number
     * @return true if the specified {@code port} is available or closed else false
     */
    private static boolean isPortAvailable(final int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            return true;
        } catch (final IOException ignored) {
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ignored) {
                }
            }
        }
        return false;
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
        valves.item(0).getParentNode().insertBefore(valve, valves.item(0));

        Transformer xFormer = TransformerFactory.newInstance().newTransformer();
        xFormer.transform(new DOMSource(document), new StreamResult(serverXML.toFile().getAbsolutePath()));
    }

    /**
     * Replaces the HTTP connector port in server.xml with the given value.
     *
     * @param httpConnectorPort port to be set in the HTTP connector
     */
    private static void setHTTPConnectorPort(int httpConnectorPort)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Path serverXML = Paths.get(System.getProperty(TestConstants.APPSERVER_HOME), "conf", "server.xml");
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().
                parse(new InputSource(serverXML.toString()));
        NodeList connectors = document.getElementsByTagName("Connector");
        Node httpConnector = null;
        for (int i = 0; i < connectors.getLength(); i++) {
            Node connector = connectors.item(i);
            if (connector.getAttributes().getNamedItem("protocol").getTextContent().equals("HTTP/1.1")) {
                httpConnector = connector;
                break;
            }
        }

        if (httpConnector != null) {
            Node port = httpConnector.getAttributes().getNamedItem("port");
            port.setTextContent(String.valueOf(httpConnectorPort));
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(serverXML.toFile().getPath()));
    }
}
