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

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Quick start sample for sso.
 */
public class Quickstart {
    private static final Log log;

    static {
        System.setProperty("org.apache.juli.formatter", "org.apache.juli.VerbatimFormatter");
        log = LogFactory.getLog(Quickstart.class);
    }

    private Path wso2asPath = Paths.get("..", "..");
    private Path wso2isPath = Paths.get("packs", "wso2is-5.1.0");
    private Path wso2isZipPath = Paths.get("packs", "wso2is-5.1.0.zip");
    private Process wso2asProcess;
    private Process wso2isProcess;
    private String operatingSystem = System.getProperty("os.name");

    public static void main(String[] args) throws IOException, InterruptedException {
        new Quickstart().runSample();
    }

    private void runSample() throws IOException, InterruptedException {
        log.info("Starting sample...\n");

        String wso2isZipPathProperty = System.getProperty("wso2is.zip.path");
        if (wso2isZipPathProperty != null) {
            wso2isZipPath = Paths.get(wso2isZipPathProperty);
            if (Files.notExists(wso2isZipPath)) {
                log.error("WSO2 Identity Server path: " + wso2isZipPath + " could not be found. ");
                return;
            }
            Path parentisPath = wso2isZipPath.getParent();
            if (parentisPath != null) {
                wso2isPath = parentisPath.resolve("wso2is-5.1.0");
            } else {
                log.error("WSO2 Identity Server path: " + wso2isZipPath + " could not be found. ");
                return;
            }
        }

        if (Files.notExists(wso2isZipPath)) {
            log.error("WSO2 Identity server has not been found in the packs directory, "
                    + "Please copy that in to the packs directory and restart the sample.");
            return;
        }

        unzip(wso2isZipPath.toString(), wso2isZipPath.getParent().toString());
        makeFilesInDirExecutable(wso2isPath.resolve("bin"));

        registerShutdownHook();

        Path webappsDir = wso2asPath.resolve("webapps");

//        deployWebapp("http://maven.wso2.org/nexus/content/repositories/snapshots/org/wso2/appserver/org.wso2"
//                + ".appserver.samples.bar-app/5.3.1-SNAPSHOT/org.wso2.appserver.samples.bar-app-5.3.1-20160323"
//                + ".054246-163.war", webappsDir);

        // store original files
        Path serverxmlOriginalSrc = wso2asPath.resolve("conf").resolve("server.xml");
        Path wso2aswebxmlOriginalSrc = wso2asPath.resolve("conf").resolve("wso2").resolve("wso2as-web.xml");
        Path ssoidpconfigxmlOriginalSrc = wso2isPath.resolve("repository").resolve("conf").resolve("identity")
                .resolve("sso-idp-config.xml");

        Path originalsLocation = Paths.get("configfiles", "originals");
        if (Files.notExists(originalsLocation)) {
            Files.createDirectory(originalsLocation);
        }
        if (Files.notExists(originalsLocation.resolve("wso2as"))) {
            Files.createDirectory(originalsLocation.resolve("wso2as"));
        }
        if (Files.notExists(originalsLocation.resolve("wso2is"))) {
            Files.createDirectory(originalsLocation.resolve("wso2is"));
        }

        Path serverxmlOriginalDest = originalsLocation.resolve("wso2as").resolve("server.xml");
        Path wso2aswebxmlOriginalDest = originalsLocation.resolve("wso2as").resolve("wso2as-web.xml");
        Path ssoidpconfigxmlOriginalDest = originalsLocation.resolve("wso2is").resolve("sso-idp-config.xml");

        Files.copy(serverxmlOriginalSrc, serverxmlOriginalDest, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(wso2aswebxmlOriginalSrc, wso2aswebxmlOriginalDest, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(ssoidpconfigxmlOriginalSrc, ssoidpconfigxmlOriginalDest, StandardCopyOption.REPLACE_EXISTING);

        // copy sample files
        Path serverxmlSampleSrc = Paths.get("configfiles", "sampleconfigfiles", "wso2as", "server.xml");
        Path wso2aswebxmlSampleSrc = Paths.get("configfiles", "sampleconfigfiles", "wso2as", "wso2as-web.xml");
        Path ssoidpconfigxmlSampleSrc = Paths.get("configfiles", "sampleconfigfiles", "wso2is", "sso-idp-config.xml");

        Path serverxmlSampleDest = wso2asPath.resolve("conf").resolve("server.xml");
        Path wso2aswebxmlSampleDest = wso2asPath.resolve("conf").resolve("wso2").resolve("wso2as-web.xml");
        Path ssoidpconfigxmlSampleDest = wso2isPath.resolve("repository").resolve("conf").resolve("identity")
                .resolve("sso-idp-config.xml");

        Files.copy(serverxmlSampleSrc, serverxmlSampleDest, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(wso2aswebxmlSampleSrc, wso2aswebxmlSampleDest, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(ssoidpconfigxmlSampleSrc, ssoidpconfigxmlSampleDest, StandardCopyOption.REPLACE_EXISTING);

        // starting AS
        startasServer();

        // starting IS
        startisServer();

        log.info("Go to following web app URLs to check the sso functionality.");
        log.info("Webapp1 URL: http://localhost:8080/musicstore-app/");
        log.info("Webapp2 URL: http://localhost:8080/bookstore-app/");

        log.info("\nPress ctrl+c to exit from the sample....");

        while (true) {
            Thread.sleep(1000);
        }
    }

    /**
     * Register shutdownhook for the sample to revert the changes.
     */
    private void registerShutdownHook() {
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
                    Path serverxmlOriginalSrc = Paths.get("configfiles", "originals", "wso2as", "server.xml");
                    Path wso2aswebxmlOriginalSrc = Paths.get("configfiles", "originals", "wso2as", "wso2as-web.xml");
                    Path ssoidpconfigxmlOriginalSrc = Paths
                            .get("configfiles", "originals", "wso2is", "sso-idp-config.xml");

                    Path serverxmlOriginalDest = wso2asPath.resolve("conf").resolve("server.xml");
                    Path wso2aswebxmlOriginalDest = wso2asPath.resolve("conf").resolve("wso2")
                            .resolve("wso2as-web.xml");
                    Path ssoidpconfigxmlOriginalDest = wso2isPath.resolve("repository").resolve("conf")
                            .resolve("identity").resolve("sso-idp-config.xml");

                    //revert the changes made during the sample
                    if (Files.exists(serverxmlOriginalSrc)) {
                        Files.move(serverxmlOriginalSrc, serverxmlOriginalDest, StandardCopyOption.ATOMIC_MOVE);
                    }
                    if (Files.exists(wso2aswebxmlOriginalSrc)) {
                        Files.move(wso2aswebxmlOriginalSrc, wso2aswebxmlOriginalDest, StandardCopyOption.ATOMIC_MOVE);
                    }
                    if (Files.exists(ssoidpconfigxmlOriginalSrc)) {
                        Files.move(ssoidpconfigxmlOriginalSrc, ssoidpconfigxmlOriginalDest,
                                StandardCopyOption.ATOMIC_MOVE);
                    }
                } catch (IOException e) {
                    log.warn("Error while reverting changes." + e.getMessage(), e);
                }
            }
        });
    }

    private void deployWebapp(String sourceUrl, Path target) throws IOException {
        URL url = new URL(sourceUrl);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(target.toString());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }

    private void unzip(String zipFilePath, String destDirectory) throws IOException {
        log.info("Extracting WSO2 Identity Server zip file...");
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            if (!destDir.exists()) {
                Files.createDirectory(destDir.toPath());
            }
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                if (!dir.exists()) {
                    Files.createDirectory(dir.toPath());
                }
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        log.info("Extracting completed.\n");
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }

    private void makeFilesInDirExecutable(Path path) {
        File[] files = path.toFile().listFiles();
        if (files != null) {
            Arrays.asList(files).forEach(file -> file.setExecutable(true));
        }
    }

    /**
     * Start the Application server.
     *
     * @throws IOException
     */
    private void startasServer() throws IOException {
        if (operatingSystem.toLowerCase(Locale.ENGLISH).contains("windows")) {
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
    private void startisServer() throws IOException {
        log.info("Starting WSO2 Identity Server...(This will take few seconds)");
        if (operatingSystem.toLowerCase(Locale.ENGLISH).contains("windows")) {
            wso2isProcess = Runtime.getRuntime()
                    .exec("cmd.exe /C " + wso2isPath.resolve("bin").resolve("wso2server.bat"));
        } else {
            wso2isProcess = Runtime.getRuntime().exec(wso2isPath.resolve("bin").resolve("wso2server.sh")
                    .toAbsolutePath().toString());
        }

        // waiting for IS server startup
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

        log.info("WSO2 Identity Server started.\n");
    }

    /**
     * Waiting for the corresponding port.
     *
     * @param port listening port
     * @throws IOException
     */
    private void waitForServerStartup(int port) throws IOException {
        int serverStartCheckTimeout = 120;
        log.info("Starting WSO2 Application Server...(This will take few seconds)");
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
            log.info("WSO2 Application Server started.\n");
        } else {
            String message = "Server startup timeout.";
            log.error(message);
            throw new IOException(message);
        }
    }

    /**
     * Helper method for waitForServerStartup.
     *
     * @param host listening host
     * @param port listening port
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
                } catch (Exception e) {
                    log.error("Error while closing the socket.", e);
                }
            }
        }
    }
}
