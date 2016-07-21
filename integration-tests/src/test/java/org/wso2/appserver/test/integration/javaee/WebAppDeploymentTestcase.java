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

package org.wso2.appserver.test.integration.javaee;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.wso2.appserver.test.integration.TestBase;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WebAppDeploymentTestcase extends TestBase {

    private static final Logger log = LoggerFactory.getLogger(WebAppDeploymentTestcase.class);

    @Test(description = "check web app deployment.")
    public void testWebAppDeployment() throws IOException {
        File webApp = new File("samples/JavaEE-TomEE/javaee-examples/target/javaee-examples.war");
        File webAppDir = new File(getAppserverHome()+"/webapps/javaee-examples.war");
        Files.copy(webApp.toPath(), webAppDir.toPath());

        Set<String> list = (Set<String>) collectAllDeployedApps();

        for(String webApp1: list) {
            log.info("----------------------------------------------------------"+webApp1);
        }


        //waitUntilwebAppdeployed
        //assert web app deployed successfully or not
    }
    private Iterable<String> collectAllDeployedApps() {
        try {
            final Set<String> result = new HashSet<>();
            final Set<ObjectName> instances = findServer()
                    .queryNames(new ObjectName("Catalina:j2eeType=WebModule,*"), null);
            for (ObjectName each : instances) {
                result.add(StringUtils.substringAfterLast(each.getKeyProperty("name"), "/")); //it will be in format like //localhost/appname
            }
            return result;
        } catch (MalformedObjectNameException e) {
            //handle
        }
        return null;
    }

    private MBeanServer findServer() {
        ArrayList<MBeanServer> servers = MBeanServerFactory.findMBeanServer(null);
        for (MBeanServer eachServer : servers) {
            for (String domain : eachServer.getDomains()) {
                if (domain.equals("Catalina")) {
                    return eachServer;
                }
            }
        }

        return null;

    }

}

