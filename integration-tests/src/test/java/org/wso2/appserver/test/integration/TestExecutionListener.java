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
 *
 */

package org.wso2.appserver.test.integration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IExecutionListener;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;



public class TestExecutionListener implements IExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(TestExecutionListener.class);
    private Process applicationServerProcess;
    private int serverStartCheckTimeout;
    private File appserverHome;
    private int applicationServerPort;
    private boolean isSuccessServerStartup = false;

    @Override
    public void onExecutionStart() {

        log.info("Starting pre-integration test setup...");

        appserverHome = new File(System.getProperty(TestConstants.APPSERVER_HOME));
        log.info("Application server home : " + appserverHome.toString());

        serverStartCheckTimeout = Integer.valueOf(System.getProperty(TestConstants.SERVER_TIMEOUT));

        log.info("Searching for free port...");
        System.setProperty(TestConstants.APPSERVER_PORT, String.valueOf(getAvailablePort()));
        log.info("Found free port : " + System.getProperty(TestConstants.APPSERVER_PORT));
        applicationServerPort = Integer.valueOf(System.getProperty(TestConstants.APPSERVER_PORT));

        replaceServerXML();

        try {

            log.info("Starting the server...");
            applicationServerProcess = startPlatformDependApplicationServer();
        } catch (IOException ex) {
            terminateApplicationServer();
            log.error("Could not start process", ex);
        }

        if (applicationServerProcess != null) {
            waitForServerStartup();
        }

        if (isSuccessServerStartup) {
            log.info("Application server started successfully. Running test suite...");
        }
    }

    @Override
    public void onExecutionFinish() {

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
            return applicationServerProcess = new ProcessBuilder()
                    .directory(appserverHome)
                    .command("./bin/catalina.sh", "run")
                    .start();
        } else if (os.toLowerCase().contains("windows")) {
            log.info("Starting server as a " + os + " process");
            return applicationServerProcess = new ProcessBuilder()
                    .directory(appserverHome)
                    .command("\\bin\\catalina.bat", "run")
                    .start();
        }
        return null;
    }

    public void terminateApplicationServer() {
        if (applicationServerProcess != null) {
            applicationServerProcess.destroy();
        }
    }

    private void waitForServerStartup() {

        log.info("Checking server availability... (Timeout: " + serverStartCheckTimeout + " seconds)");
        int currentSeconds = 0;
        boolean isTimeout = false;
        while (!isServerListening("localhost", applicationServerPort)) {
            if (currentSeconds >= 20) {
                isTimeout = true;
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            currentSeconds++;
        }

        if (!isTimeout) {
            isSuccessServerStartup = true;
            log.info("Server started in " + currentSeconds + " seconds");
        } else {
            isSuccessServerStartup = false;
            log.error("Server startup timeout.");
        }
    }


    private boolean isServerListening(String host, int port) {
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


    private int getAvailablePort() {
        int port;
        int min = Integer.valueOf(System.getProperty(TestConstants.PORT_CHECK_MIN));
        int max = Integer.valueOf(System.getProperty(TestConstants.PORT_CHECK_MAX));
        do {
            port = new Random().nextInt(max - min + 1) + min;
        } while (!isPortAvailable(port));
        return port;
    }

    private boolean isPortAvailable(final int port) {
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

    private void replaceServerXML() {
        try {
            Path serverXML = Paths.get(appserverHome.getAbsolutePath(), "conf", "server.xml");
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new InputSource(serverXML.toString()));

            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList nodes = (NodeList) xpath.evaluate("/Server/Service/Connector[1]/@port", doc,
                    XPathConstants.NODESET);

            nodes.item(0).setNodeValue(System.getProperty(TestConstants.APPSERVER_PORT));
            Transformer xFormer = TransformerFactory.newInstance().newTransformer();
            xFormer.transform(new DOMSource(doc), new StreamResult(serverXML.toFile()));
        } catch (Exception ex) {
            log.error("Error while changing the server.xml", ex);
        }
    }
}
