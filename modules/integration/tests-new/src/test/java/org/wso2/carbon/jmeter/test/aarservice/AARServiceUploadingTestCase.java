/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.carbon.jmeter.test.aarservice;

import org.testng.annotations.Test;
import org.wso2.automation.tools.jmeter.JMeterTest;
import org.wso2.automation.tools.jmeter.JMeterTestManager;
import org.wso2.carbon.automation.core.ProductConstant;

import java.io.File;

/**
 * This test class execute a jmeter script which verify the aar deployment and undeployment
 */
public class AARServiceUploadingTestCase {

    @Test(groups = "wso2.as", description = "Upload aar service and verify deployment from jmeter script"
            , enabled = false)
    public void uploadAndInvokeAARService() throws Exception {
        JMeterTestManager testRunner = new JMeterTestManager();
        File testScript = new File(ProductConstant.getResourceLocations(ProductConstant.APP_SERVER_NAME)
                                   + File.separator + "jmeter" + File.separator + "aar-upload-script.jmx");


        JMeterTest test = new JMeterTest(testScript);
        testRunner.runTest(test);
    }
}
