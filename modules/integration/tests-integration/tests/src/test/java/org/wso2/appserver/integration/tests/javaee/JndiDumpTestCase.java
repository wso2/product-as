/*
* Copyright 2004,2013 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.appserver.integration.tests.javaee;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * JavaEE heavily leverages JNDi to expose the resources. So,
 * by analyzing the JNDi dump, we can get a quite a lot of information
 * on how the environment behaves.
 */
public class JndiDumpTestCase extends WebappDeploymentTestCase {

    private static final Log log = LogFactory.getLog(JndiDumpTestCase.class);
    private static final String webAppFileName = "tomee-ejb-examples-1.1.0.war";
    private static final String webAppName = "tomee-ejb-examples-1.1.0";
    private static final String webAppLocalURL ="/tomee-ejb-examples-1.1.0";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        setWebAppFileName(webAppFileName);
        setWebAppName(webAppName);
        setWebAppURL(getWebAppURL() + webAppLocalURL);
    }

    @Test(groups = "wso2.as", description = "test jndi dump", dependsOnMethods = "webApplicationDeploymentTest")
    public void testJndiDump() throws Exception {
        BufferedReader in = null;

        try {
            String jndiUrl = getWebAppURL() + "/jndi";
            Map<String, String > resultMap = toResultMap(
                    runAndGetResultAsString(jndiUrl));

            String defaultJndiInfoPath = FrameworkPathUtil.getSystemResourceLocation()
                                         + "artifacts" + File.separator + "AS" + File.separator
                                         + "javaee" + File.separator + "default-jndi-dump.txt";
            in = new BufferedReader(new FileReader(defaultJndiInfoPath));

            String inputLine;
            String msg = "ejb-examples-1.1.0.war failed to validate the JNDi dump for: ";
            while ((inputLine = in.readLine()) != null) {

                String[] jndiKvArray = inputLine.split("=");
                String jndiKey = jndiKvArray[0];
                String expectedJndiValue = jndiKvArray[1];

                String actualJndiValue = resultMap.get(jndiKey);
                actualJndiValue = sanitize(actualJndiValue);

                assertEquals(actualJndiValue, expectedJndiValue, msg + jndiKey);
            }

        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //ignore
                }
            }


        }

    }

    /**
     * remove the object id from the value before asserting since the object id
     * is random.
     * ex. remove @69ba3c74 from the following.
     * org.apache.bval.jsr303.ClassValidator@69ba3c74
     */
    private String sanitize(String actualJndiValue) {
        actualJndiValue = actualJndiValue.contains("@") ?
                actualJndiValue.substring(0, actualJndiValue.indexOf('@')) :
                actualJndiValue;

        return actualJndiValue;
    }

}
