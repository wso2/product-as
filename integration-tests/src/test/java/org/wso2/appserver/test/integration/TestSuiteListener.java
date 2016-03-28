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
import org.w3c.dom.NamedNodeMap;
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
import java.util.HashMap;
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

            // These min and max values are for the HTTP connector port
            int portCheckMin = Integer.valueOf(System.getProperty(TestConstants.PORT_CHECK_MIN));
            int portCheckMax = Integer.valueOf(System.getProperty(TestConstants.PORT_CHECK_MAX));

            int portDeduction = TestConstants.TOMCAT_DEFAULT_PORT - portCheckMin;
            int portCheckRange = portCheckMax - portCheckMin;

            applicationServerPort = getAvailablePort(TestConstants.TOMCAT_DEFAULT_PORT_NAME,
                    TestConstants.TOMCAT_DEFAULT_PORT, portCheckMin, portCheckRange);

            int ajpPort = getAvailablePort(TestConstants.TOMCAT_AJP_PORT_NAME, TestConstants.TOMCAT_DEFAULT_AJP_PORT,
                    TestConstants.TOMCAT_DEFAULT_AJP_PORT - portDeduction, portCheckRange);
            int serverShutdownPort = getAvailablePort(TestConstants.TOMCAT_SERVER_SHUTDOWN_PORT_NAME,
                    TestConstants.TOMCAT_DEFAULT_SERVER_SHUTDOWN_PORT,
                    TestConstants.TOMCAT_DEFAULT_SERVER_SHUTDOWN_PORT - portDeduction, portCheckRange);

            System.setProperty(TestConstants.APPSERVER_PORT, String.valueOf(applicationServerPort));

            if (applicationServerPort != TestConstants.TOMCAT_DEFAULT_PORT ||
                    ajpPort != TestConstants.TOMCAT_DEFAULT_AJP_PORT ||
                    serverShutdownPort != TestConstants.TOMCAT_DEFAULT_SERVER_SHUTDOWN_PORT) {
                log.info("Changing the ports of server.xml [{}:{}, {}:{}, {}:{}]",
                        TestConstants.TOMCAT_DEFAULT_PORT_NAME, applicationServerPort,
                        TestConstants.TOMCAT_AJP_PORT_NAME, ajpPort, TestConstants.TOMCAT_SERVER_SHUTDOWN_PORT_NAME,
                        serverShutdownPort);

                updateServerPorts(applicationServerPort, ajpPort, serverShutdownPort);
            }

            addValveToServerXML(TestConstants.CONFIGURATION_LOADER_SAMPLE_VALVE);

            log.info("Starting the server...");
            applicationServerProcess = startPlatformDependApplicationServer();

            waitForServerStartup();

            if (isSuccessServerStartup) {
                log.info("Application server started successfully. Running test suite...");
            }

        } catch (IOException | TransformerException | SAXException | ParserConfigurationException |
                XPathExpressionException ex) {
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
        log.info("Starting server as a " + os + " process");

        if (os.toLowerCase().contains("windows")) {
            return Runtime.getRuntime().exec("\\bin\\catalina.bat run", null, appserverHome);
        } else {
            return Runtime.getRuntime().exec("./bin/catalina.sh run", null, appserverHome);
        }
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
     * Updates http and ajp connector ports and server shutdown port in server.xml
     *
     * @param httpConnectorPort  http connector port
     * @param ajpPort            ajp port
     * @param serverShutdownPort server shutdown port
     */
    private static void updateServerPorts(int httpConnectorPort, int ajpPort, int serverShutdownPort)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Path serverXML = Paths.get(System.getProperty(TestConstants.APPSERVER_HOME), "conf", "server.xml");

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().
                parse(new InputSource(serverXML.toString()));

        //  Change http connector and ajp connector ports
        Map<String, String> connectorProtocolPortMap = new HashMap<>();
        connectorProtocolPortMap.put("HTTP/1.1", String.valueOf(httpConnectorPort));
        connectorProtocolPortMap.put("AJP/1.3", String.valueOf(ajpPort));

        NodeList connectors = document.getElementsByTagName("Connector");
        for (int i = 0; i < connectors.getLength(); i++) {
            Node connector = connectors.item(i);
            NamedNodeMap connectorAttributes = connector.getAttributes();
            String protocol = connectorAttributes.getNamedItem("protocol").getTextContent();
            if (connectorProtocolPortMap.containsKey(protocol)) {
                connectorAttributes.getNamedItem("port").setTextContent(connectorProtocolPortMap.get(protocol));
            }
        }

        // change server shutdown port
        Node server = document.getElementsByTagName("Server").item(0);
        server.getAttributes().getNamedItem("port").setTextContent(String.valueOf(serverShutdownPort));

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(serverXML.toFile().getPath()));

    }

    /**
     * Checks whether the given default port is available, if not try to find an available port within the range
     * staring from the minimum port value.
     *
     * @param portName  name of the port
     * @param port      default port
     * @param portMin   minimum port value in the range
     * @param portRange port range
     * @return available port
     */
    private int getAvailablePort(String portName, int port, int portMin, int portRange) {
        if (isPortAvailable(port)) {
            log.info("{} default port {} is available.", portName, port);
            return port;
        } else {
            log.info("{} default port {} is not available. Trying to use a port between {} and {}", portName, port,
                    portMin, (portMin + portRange));

            port = portMin;
            while (port <= (portMin + portRange)) {
                log.info("Trying to use port {} for {}", port, portName);
                if (isPortAvailable(port)) {
                    log.info("Port {} is available for {}", port, portName);
                    break;
                }
                port++;
            }
        }

        if (port > (portMin + portRange)) {
            throw new RuntimeException("Couldn't find a port for " + portName + " between ports " + portMin + " and " +
                    (portMin + portRange));
        } else {
            return port;
        }
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
}
