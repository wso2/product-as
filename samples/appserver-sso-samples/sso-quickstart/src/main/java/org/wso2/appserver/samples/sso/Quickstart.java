/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.appserver.samples.sso;

import org.apache.commons.io.FileUtils;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Quick start sample for sso.
 */
public class Quickstart {
    private static final Log log = LogFactory.getLog(Quickstart.class);

    private final Path wso2asPath = Paths.get("../../");
    private final Path wso2isPath = Paths.get("packs/wso2is-5.1.0/");
    private Process wso2asProcess;
    private Process wso2isProcess;
    private String operatingSystem = System.getProperty("os.name");

    public static void main(String[] args) throws IOException, InterruptedException {
        new Quickstart().runSample();
    }

    public void runSample() throws IOException, InterruptedException {
        registerShutdownHook();

        // store original files
        Path serverxmlOriginalSrc = wso2asPath.resolve("conf/server.xml");
        Path wso2aswebxmlOriginalSrc = wso2asPath.resolve("conf").resolve("wso2/wso2as-web.xml");
        Path ssoidpconfigxmlOriginalSrc = wso2isPath.resolve("repository").resolve("conf").resolve("identity")
                .resolve("sso-idp-config.xml");

        Path serverxmlOriginalDest = Paths.get("configfiles/originals/wso2as/server.xml");
        Path wso2aswebxmlOriginalDest = Paths.get("configfiles/originals/wso2as/wso2as-web.xml");
        Path ssoidpconfigxmlOriginalDest = Paths.get("configfiles/originals/wso2is/sso-idp-config.xml");

        FileUtils.copyFile(serverxmlOriginalSrc.toFile(), serverxmlOriginalDest.toFile());
        FileUtils.copyFile(wso2aswebxmlOriginalSrc.toFile(), wso2aswebxmlOriginalDest.toFile());
        FileUtils.copyFile(ssoidpconfigxmlOriginalSrc.toFile(), ssoidpconfigxmlOriginalDest.toFile());

        // copy sample files
        Path serverxmlSampleSrc = Paths.get("configfiles/sampleconfigfiles/wso2as/server.xml");
        Path wso2aswebxmlSampleSrc = Paths.get("configfiles/sampleconfigfiles/wso2as/wso2as-web.xml");
        Path ssoidpconfigxmlSampleSrc = Paths.get("configfiles/sampleconfigfiles/wso2is/sso-idp-config.xml");

        Path serverxmlSampleDest = wso2asPath.resolve("conf/server.xml");
        Path wso2aswebxmlSampleDest = wso2asPath.resolve("conf").resolve("wso2/wso2as-web.xml");
        Path ssoidpconfigxmlSampleDest = wso2isPath.resolve("repository/conf").resolve("identity/sso-idp-config.xml");

        FileUtils.copyFile(serverxmlSampleSrc.toFile(), serverxmlSampleDest.toFile());
        FileUtils.copyFile(wso2aswebxmlSampleSrc.toFile(), wso2aswebxmlSampleDest.toFile());
        FileUtils.copyFile(ssoidpconfigxmlSampleSrc.toFile(), ssoidpconfigxmlSampleDest.toFile());

        // starting AS
        startasServer();

        // starting IS
        startisServer();

        log.info("Webapp1 URL: http://localhost:8080/foo-app/");
        log.info("Webapp2 URL: http://localhost:8080/bar-app/");

        log.info("Press ctrl+c to exit from the sample....");

        while (true) {
            Thread.sleep(1000);
        }
    }

    /**
     * Register shutdownhook for the sample to revert the changes.
     */
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                // killing the application server process
                if (wso2asProcess != null) {
                    wso2asProcess.destroy();
                }
                // killing the identity server process
                if (wso2isProcess != null) {
                    wso2isProcess.destroy();
                }

                try {
                    Path serverxmlOriginalSrc = Paths.get("configfiles/originals/wso2as/server.xml");
                    Path wso2aswebxmlOriginalSrc = Paths.get("configfiles/originals/wso2as/wso2as-web.xml");
                    Path ssoidpconfigxmlOriginalSrc = Paths.get("configfiles/originals/wso2is/sso-idp-config.xml");

                    Path serverxmlOriginalDest = wso2asPath.resolve("conf/server.xml");
                    Path wso2aswebxmlOriginalDest = wso2asPath.resolve("conf").resolve("wso2/wso2as-web.xml");
                    Path ssoidpconfigxmlOriginalDest = wso2isPath.resolve("repository").resolve("conf").resolve
                            ("identity").resolve("sso-idp-config.xml");

                    //revert the changes made during the sample
                    FileUtils.copyFile(serverxmlOriginalSrc.toFile(), serverxmlOriginalDest.toFile());
                    FileUtils.copyFile(wso2aswebxmlOriginalSrc.toFile(), wso2aswebxmlOriginalDest.toFile());
                    FileUtils.copyFile(ssoidpconfigxmlOriginalSrc.toFile(), ssoidpconfigxmlOriginalDest.toFile());
                } catch (IOException e) {
                    log.error("Error while copying config files.");
                }
            }
        });
    }

    /**
     * Start the Application server.
     *
     * @throws IOException
     */
    public void startasServer() throws IOException {
        if (operatingSystem.toLowerCase(Locale.getDefault()).contains("windows")) {
            wso2asProcess = Runtime.getRuntime()
                    .exec("cmd.exe /C " + wso2asPath.resolve("bin").resolve("catalina.bat") + " run");
        } else {
            wso2asProcess = Runtime.getRuntime().exec(wso2asPath.resolve("bin").resolve("catalina.sh") + " run");
        }

        waitForServerStartup(8080);
    }

    /**
     * Start the Identity server.
     *
     * @throws IOException
     */
    public void startisServer() throws IOException {
        log.info("Starting WSO2 Identity server...(This will take few seconds)");
        if (operatingSystem.toLowerCase(Locale.getDefault()).contains("windows")) {
            wso2isProcess = Runtime.getRuntime()
                    .exec("cmd.exe /C " + wso2isPath.resolve("bin").resolve("wso2server.bat"));
        } else {
            wso2isProcess = Runtime.getRuntime().exec(wso2isPath.resolve("bin").resolve("wso2server.sh")
                    .toAbsolutePath().toString());
        }

        String line;
        boolean isasStarted = false;

        try (BufferedReader input = new BufferedReader(
                new InputStreamReader(wso2isProcess.getInputStream(), StandardCharsets.UTF_8))) {
            while ((line = input.readLine()) != null) {
                if (line.contains("WSO2 Carbon started")) {
                    isasStarted = true;
                    break;
                }
            }
        }

        if (!isasStarted) {
            String message = "Error during WSO2 Identity server startup.";
            log.error(message);
            throw new IOException(message);
        }

        log.info("WSO2 Identity Server started.");
    }

    /**
     * Waiting for the corresponding port.
     *
     * @param port
     * @throws IOException
     */
    private void waitForServerStartup(int port) throws IOException {
        int serverStartCheckTimeout = 120;
        log.info("Starting WSO2 application server...");
        log.info("Checking server availability... (Timeout: " + serverStartCheckTimeout + " seconds)");
        int startupCounter = 0;
        boolean isTimeout = false;
        while (!isServerListening("localhost", port)) {
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
            log.info("WSO2 Application Server started.");
        } else {
            String message = "Server startup timeout.";
            log.error(message);
            throw new IOException(message);
        }
    }

    /**
     * Helper method for waitForServerStartup.
     *
     * @param host
     * @param port
     * @return boolean
     */
    private boolean isServerListening(String host, int port) {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception ignored) {
                    log.error("Error while closing the socket.");
                }
            }
        }
    }
}
