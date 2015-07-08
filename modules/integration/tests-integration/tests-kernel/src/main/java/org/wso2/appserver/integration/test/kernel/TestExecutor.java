/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.appserver.integration.test.kernel;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class will execute the test classes according to the testng.xml files
 * defined as the System Property ARG_SUITE_XML_FILES from the tests jar file
 */

public class TestExecutor {

    private static final String REPORT_DIR = "surefire-reports";
    private static final String ARG_SUITE_XML_FILES = "suiteXmlFile";
    private static final String ARG_VERBOSE = "verbose.mode";
    private static final String MAVEN_TEST_SKIP = "maven.test.skip";
    private static final String SKIP_TEST = "skipTests";
    private static final String MAVEN_TEST_FAILURE_IGNORE = "maven.test.failure.ignore";

    public static void main(String[] args) throws Exception {

        //skip running test cases when the maven.test.skip property is true
        if (!(Boolean.parseBoolean(System.getProperty(MAVEN_TEST_SKIP))
              || Boolean.parseBoolean(System.getProperty(SKIP_TEST)))) {
            TestListenerAdapter tla = new TestListenerAdapter();
            TestNG tng = new TestNG();
            tng.addListener(tla);

            tng.setOutputDirectory(REPORT_DIR);
            tng.setParallel(Boolean.FALSE.toString());
            tng.setUseDefaultListeners(true);
            String verbose = System.getProperty(ARG_VERBOSE);
            if (verbose == null || verbose.isEmpty()) {
                tng.setVerbose(2);
            } else {
                tng.setVerbose(Integer.parseInt(verbose.trim()));
            }

            List<String> files = new ArrayList<String>();
            String testNgFiles = System.getProperty(ARG_SUITE_XML_FILES);

            if (testNgFiles == null || testNgFiles.isEmpty()) {
                throw new AutomationFrameworkException("No testNg test suite to execute.");
            }
            files.addAll(Arrays.asList(testNgFiles.split(",")));

            tng.setTestSuites(files);
            tng.run();

            //skipp build failure when the maven.test.failure.ignore property is true
            if (!Boolean.parseBoolean(System.getProperty(MAVEN_TEST_FAILURE_IGNORE))) {
                if (!(tla.getFailedTests() == null || tla.getFailedTests().isEmpty())) {
                    throw new AutomationFrameworkException("TestFailed " + tla.getFailedTests());
                }
                if (!(tla.getConfigurationFailures() == null || tla.getConfigurationFailures().isEmpty())) {
                    throw new AutomationFrameworkException("TestFailed " + tla.getConfigurationFailures());
                }
            }

        }
    }
}

