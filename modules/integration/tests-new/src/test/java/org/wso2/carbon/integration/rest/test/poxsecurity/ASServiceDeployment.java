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

package org.wso2.carbon.integration.rest.test.poxsecurity;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import java.io.File;
import java.rmi.RemoteException;

public class ASServiceDeployment extends ASIntegrationTest {
    private String[] fileNames =
            {"StudentService.aar", "Axis2Service.aar", "SimpleStockQuoteService.aar"};

    @BeforeTest(alwaysRun = true)
    public void testDeployService() throws Exception {
        super.init(ProductConstant.ADMIN_USER_ID);
        for (String fileName : fileNames) {
            String studentServiceFilePath = ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                                            File.separator + "AS" + File.separator + "aar" +
                                            File.separator + fileName;
            deployAarService(fileName.replace(".aar", ""), fileName, studentServiceFilePath, "");
        }
    }

    @AfterTest(alwaysRun = true)
    public void testUnDeployService() throws RemoteException {
        for (String fileName : fileNames) {
            deleteService(fileName.replace(".aar", ""));
        }
    }

}
