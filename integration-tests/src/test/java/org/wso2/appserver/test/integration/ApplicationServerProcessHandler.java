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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Class for handling the WSO2 Application Server start and stop process.
 *
 * @since 6.0.0
 */
public class ApplicationServerProcessHandler {

    private final ProcessBuilder applicationServerProcessBuilder;
    private Process applicationServerProcess;
    private String jacocoArgLine;
    private String operatingSystem;

    public ApplicationServerProcessHandler(File appserverHome) {

        applicationServerProcessBuilder = new ProcessBuilder()
                .directory(appserverHome)
                .redirectErrorStream(true);

        setEnvironmentsVariables();
        operatingSystem = System.getProperty("os.name");

    }

    private void setEnvironmentsVariables() {

        String jacocoAgentDir = System.getProperty("jacoco-agent.dir");
        String jacocoRuntimeJar = System.getProperty("jacoco-agent.runtime");
        String jacocoDataFile = System.getProperty("jacoco-agent.data.file");
        jacocoArgLine = "-javaagent:" + Paths.get(jacocoAgentDir, jacocoRuntimeJar)
                + "=destfile=" + Paths.get(jacocoAgentDir, jacocoDataFile);
        applicationServerProcessBuilder.environment().put("JAVA_OPTS", jacocoArgLine);
    }

    /**
     * Get the jacoco test coverage agent argument.
     *
     * @return jacoco argument.
     */
    public String getJacocoArgLine() {
        return jacocoArgLine;
    }

    /**
     * Starts the WSO2 Application Server as a separate process.
     *
     * @throws IOException          when process fails to start.
     */
    public void startServer() throws IOException {
        File redirectFile = Paths.get(System.getProperty("build.directory"), "server.log").toFile();
        applicationServerProcessBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(redirectFile));
        if (operatingSystem.toLowerCase().contains("windows")) {
            applicationServerProcess = applicationServerProcessBuilder
                    .command("cmd.exe", "/C", "bin\\catalina.bat", "run").start();
        } else {
            applicationServerProcess = applicationServerProcessBuilder.command("./bin/catalina.sh", "run").start();
        }
    }

    /**
     * Stop the previously started WSO2 Application Server.
     *
     */
    public void stopServer() {
        if (applicationServerProcess != null) {
            applicationServerProcess.destroy();
        }
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }
}
