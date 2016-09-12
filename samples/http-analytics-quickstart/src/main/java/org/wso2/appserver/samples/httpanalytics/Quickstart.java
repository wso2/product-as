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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.appserver.samples.httpanalytics;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.wso2.carbon.databridge.agent.AgentHolder;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.stream.LongStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class generates sample events, with a random interval, for the past 30 days and publishes them to
 * http-analytics.
 */
public class Quickstart {

    private static final Log log;

    private static final String STREAM_NAME = "org.wso2.http.analytics.stream";
    private static final String STREAM_VERSION = "1.0.0";
    private static final int THRIFT_PORT = 7611;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";
    private static final String HTTP_ANALYTICS_DISTRIBUTION_SYSTEM_PARAMETER = "wso2http-analytics.zip.path";
    private static final Path PACKS_DIRECTORY = Paths.get("packs");

    // maximum possible interval between events in seconds
    private static final int MAX_INTERVAL_BETWEEN_EVENTS = 900;

    static String hostname;

    static {
        System.setProperty("org.apache.juli.formatter", "org.apache.juli.VerbatimFormatter");
        log = LogFactory.getLog(Quickstart.class);
    }

    private String operatingSystem = System.getProperty("os.name");
    private Process wso2HttpAnalyticsProcess;

    public static void main(String[] args) throws IOException, URISyntaxException {
        hostname = InetAddress.getLocalHost().getHostName();
        new Quickstart().runSample();
    }

    private void runSample() throws IOException, URISyntaxException {

        // Check for WSO2 HTTP Analytics distribution
        Path httpAnalyticsZipPath = getHttpAnalyticsDistributionFromSystemParameter();
        if (httpAnalyticsZipPath == null) {
            httpAnalyticsZipPath = getHttpAnalyticsDistributionFromPacksDirectory();
        }
        if (httpAnalyticsZipPath == null) {
            logErrorAndExit("Couldn't find WSO2 HTTP Analytics distribution either with " +
                    "-Dwso2http-analytics.zip.path or in packs directory.");
        }

        // Unzip WSO2 HTTP Analytics distribution
        if (!Files.exists(PACKS_DIRECTORY)) {
            Files.createDirectory(PACKS_DIRECTORY);
        }
        unzip(httpAnalyticsZipPath, PACKS_DIRECTORY);

        // Register shutdown hook
        registerShutdownHook();

        // Start HTTP Analytics server
        String httpAnalyticsFileName = httpAnalyticsZipPath.toFile().getName();
        String extractedDirectoryName = httpAnalyticsFileName.substring(0, httpAnalyticsFileName.lastIndexOf("."));
        Path extractedDir = PACKS_DIRECTORY.resolve(extractedDirectoryName);
        makeStartupScriptsExecutable(extractedDir.resolve("bin"));
        startHttpAnalyticsServer(extractedDir);

        publishSampleEvents();

        log.info("You can access the HTTP analytics dashboard via https://" + hostname
                + ":9443/portal/dashboards/http-analytics/");
        log.info("Use the following credentials to access the dashboard.\n" +
                "Username: " + USERNAME + "\nPassword: " + PASSWORD);

        log.info("\nPress ctrl+c to exit from the sample....");

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Checks and returns the WSO2 HTTP Analytics distribution path set via -Dwso2http-analytics.zip.path
     *
     * @return WSO2 HTTP Analytics distribution zip file path
     */
    private Path getHttpAnalyticsDistributionFromSystemParameter() {
        String httpAnalyticsZip = System.getProperty(HTTP_ANALYTICS_DISTRIBUTION_SYSTEM_PARAMETER);
        if (httpAnalyticsZip != null) {
            Path httpAnalyticsZipPath = Paths.get(httpAnalyticsZip);
            if (httpAnalyticsZipPath != null && Files.exists(httpAnalyticsZipPath)) {
                return httpAnalyticsZipPath;
            } else {
                logErrorAndExit("WSO2 HTTP Analytics distribution zip with the path provided via -D" +
                        HTTP_ANALYTICS_DISTRIBUTION_SYSTEM_PARAMETER + " doesn't exist.");
            }
        }
        return null;
    }

    /**
     * Checks and returns the WSO2 HTTP Analytics distribution from "packs" directory
     *
     * @return WSO2 HTTP Analytics distribution zip file path
     */
    private Path getHttpAnalyticsDistributionFromPacksDirectory() {
        if (!Files.exists(PACKS_DIRECTORY)) {
            return null;
        }

        Optional<Path> httpAnalyticsZipPath;
        try {
            httpAnalyticsZipPath = Files.list(PACKS_DIRECTORY)
                    .map(Path::getFileName)
                    .filter(this::isValidDistribution)
                    .findFirst();
        } catch (IOException e) {
            log.error("Error while listing the files in packs directory", e);
            return null;
        }

        if (httpAnalyticsZipPath.isPresent()) {
            return PACKS_DIRECTORY.resolve(httpAnalyticsZipPath.get());
        }

        return null;
    }

    /**
     * Unzip the given file to the given destination.
     *
     * @param src  source zip file
     * @param dest destination directory
     * @throws IOException
     */
    private void unzip(Path src, Path dest) throws IOException {
        log.info("Extracting WSO2 HTTP Analytics distribution...");
        if (!Files.exists(dest)) {
            Files.createDirectory(dest);
        }

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(src.toString()));
        ZipEntry zipEntry = zipInputStream.getNextEntry();

        while (zipEntry != null) {
            String filePath = dest.toString() + File.separator + zipEntry.getName();
            if (!zipEntry.isDirectory()) {
                extractFile(zipInputStream, filePath);
            } else {
                if (!Files.exists(Paths.get(filePath))) {
                    Files.createDirectory(Paths.get(filePath));
                }
            }

            zipInputStream.closeEntry();
            zipEntry = zipInputStream.getNextEntry();
        }

        zipInputStream.closeEntry();
        log.info("Extraction of WSO2 HTTP Analytics distribution is completed.");
    }

    /**
     * Makes the WSO2 server startup script files executable.
     *
     * @param path path of the root directory of extracted WSO2 HTTP Analytics distribution
     * @throws IOException
     */
    private void makeStartupScriptsExecutable(Path path) throws IOException {
        Files.list(path)
                .map(Path::toFile)
                .filter(file -> file.getName().startsWith("wso2server"))
                .forEach(file1 -> file1.setExecutable(true));
    }

    /**
     * Start WSO2 HTTP Analytics Server.
     *
     * @param httpAnalyticsPath path of the root directory of extracted WSO2 HTTP Analytics distribution
     * @throws IOException
     */
    private void startHttpAnalyticsServer(Path httpAnalyticsPath) throws IOException {
        log.info("Starting WSO2 HTTP Analytics server...(This will take few seconds)");
        if (operatingSystem.toLowerCase(Locale.ENGLISH).contains("windows")) {
            wso2HttpAnalyticsProcess = Runtime.getRuntime()
                    .exec("cmd.exe /C " + httpAnalyticsPath.resolve("bin").resolve("wso2server.bat"));
        } else {
            wso2HttpAnalyticsProcess = Runtime.getRuntime().exec(httpAnalyticsPath.resolve("bin")
                    .resolve("wso2server.sh")
                    .toAbsolutePath().toString());
        }

        // waiting for HTTP Analytics server to startup
        String line;
        boolean isHttpAnalyticsStarted = false;
        try (BufferedReader input = new BufferedReader(
                new InputStreamReader(wso2HttpAnalyticsProcess.getInputStream(), StandardCharsets.UTF_8))) {
            while ((line = input.readLine()) != null) {
                if (line.contains("WSO2 Carbon started")) {
                    isHttpAnalyticsStarted = true;
                    break;
                }
            }
        }

        if (!isHttpAnalyticsStarted) {
            logErrorAndExit("Error during WSO2 HTTP Analytics server startup.");
        }

        log.info("WSO2 HTTP Analytics server started.\n");
    }

    /**
     * Publish sample events to WSO2 HTTP Analytics.
     *
     * @throws URISyntaxException
     */
    private void publishSampleEvents() throws URISyntaxException {
        log.info("Initializing data publishing");

        System.setProperty("javax.net.ssl.trustStore", getTrustStorePath());
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        AgentHolder.setConfigPath(getDataAgentConfigPath());

        String thriftUrl = "tcp://" + hostname + ":" + THRIFT_PORT;

        DataPublisher dataPublisher = null;
        try {
            dataPublisher = new DataPublisher(thriftUrl, USERNAME, PASSWORD);
        } catch (DataEndpointAgentConfigurationException | DataEndpointException | DataEndpointConfigurationException
                | DataEndpointAuthenticationException | TransportException e) {
            logErrorAndExit("Error in initializing data publisher", e);
        }

        String streamId = DataBridgeCommonsUtils.generateStreamId(STREAM_NAME, STREAM_VERSION);

        log.info("Starting data publishing");
        publishEvents(dataPublisher, streamId);

        log.info("Stopping data publishing");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }

        try {
            dataPublisher.shutdown();
        } catch (DataEndpointException e) {
            logErrorAndExit("Error in stopping data publisher", e);
        }
        log.info("Data publishing stopped");
    }

    /**
     * Registry shutdown hook to kill WSO2 HTTP Analytics process.
     */
    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                killHttpAnalyticsProcess();
            }
        });
    }

    /**
     * Iterates from 30 days back from now to current time with random intervals (subjected to a maximum interval
     * defined by MAX_INTERVAL_BETWEEN_EVENTS in seconds) and for each iteration, publishes an event with sample HTTP
     * data to WSO2 HTTP Analytics.
     *
     * @param dataPublisher data publisher to be used
     * @param streamId      stream id
     */
    private void publishEvents(DataPublisher dataPublisher, String streamId) {
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        long timeFrom = calendar.getTimeInMillis();

        Random random = new Random();

        LongStream.iterate(timeFrom, time -> time + random.nextInt(MAX_INTERVAL_BETWEEN_EVENTS) * 1000)
                .distinct()
                .limit(100000)
                .filter(e -> e < currentTime)
                .forEach(e -> dataPublisher.publish(EventGenerator.generateEvent(streamId, e)));
    }


    /**
     * Checks whether the given file name is a valid WSO2 distribution.
     *
     * @param distributionFileName distribution filename to be checked
     * @return true, if the given filename is a valid WSO2 distribution file name, false otherwise
     */
    private boolean isValidDistribution(Path distributionFileName) {
        String filename = distributionFileName.toString().toLowerCase(Locale.ENGLISH);
        return filename.startsWith("wso2http-analytics-") && filename.endsWith(".zip");
    }

    /**
     * Extracts files from zip input stream
     *
     * @param zipInputStream zip input stream
     * @param filePath       input file path
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipInputStream, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipInputStream.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }

    /**
     * Logs the error message and exit.
     *
     * @param msg error message to be logged
     */
    private void logErrorAndExit(String msg) {
        logErrorAndExit(msg, null);
    }

    /**
     * Logs the error message and exit.
     *
     * @param msg       error message to be logged
     * @param exception exception to be logged
     */
    private void logErrorAndExit(String msg, Exception exception) {
        log.error(msg, exception);
        killHttpAnalyticsProcess();
        System.exit(1);
    }

    /**
     * Destroy HTTP Analytics server process.
     */
    private void killHttpAnalyticsProcess() {
        if (wso2HttpAnalyticsProcess != null) {
            wso2HttpAnalyticsProcess.destroy();
        }
    }

    /**
     * Returns the truststore file path
     *
     * @return truststore file path
     */
    private String getTrustStorePath() {
        return Paths.get("..", "..", "conf", "wso2", "client-truststore.jks").toString();
    }

    /**
     * Returns data agent config file path
     *
     * @return data agent config file path
     * @throws URISyntaxException
     */
    private String getDataAgentConfigPath() throws URISyntaxException {
        return Paths.get("..", "..", "conf", "wso2", "data-agent-conf.xml").toString();
    }

}
