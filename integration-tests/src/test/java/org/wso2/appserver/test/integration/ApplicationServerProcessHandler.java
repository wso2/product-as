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

public class ApplicationServerProcessHandler {

    private final ProcessBuilder applicationServerProcessBuilder;
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

    public String getJacocoArgLine() {
        return jacocoArgLine;
    }


    public void startServer() throws IOException, InterruptedException {
        Process startupProcess;
        if (operatingSystem.toLowerCase().contains("windows")) {
            startupProcess = applicationServerProcessBuilder.command("cmd.exe", "/C", "bin\\catalina.bat", "start")
                    .start();
        } else {
            startupProcess = applicationServerProcessBuilder.command("./bin/catalina.sh", "start").start();
        }

        startupProcess.waitFor();
    }

    public void stopServer() throws IOException, InterruptedException {
        Process stopProcess;
        if (operatingSystem.toLowerCase().contains("windows")) {
            stopProcess = applicationServerProcessBuilder.command("cmd.exe", "/C", "bin\\catalina.bat", "stop")
                    .start();
        } else {
            stopProcess = applicationServerProcessBuilder.command("./bin/catalina.sh", "stop").start();
        }

        stopProcess.waitFor();
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }
}
