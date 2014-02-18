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

package org.wso2.carbon.integration.test.springservice;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.SpringServiceMaker;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import java.io.File;

import static org.testng.Assert.assertTrue;

public class SpringServiceDeploymentTestCase extends ASIntegrationTest {
    private static final Log log = LogFactory.getLog(SpringServiceDeploymentTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.as", description = "upload spring service and verify deployment")
    public void testSpringServiceUpload() throws Exception {
        String springContextFilePath = ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                                       File.separator + "AS" + File.separator + "spring" + File.separator +
                                       "artifact1" + File.separator + "context.xml";

        String springBeanFilePath = ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                                    File.separator + "AS" + File.separator + "spring" + File.separator +
                                    "artifact1" + File.separator + "SpringService.jar";
        SpringServiceMaker newMarker = new SpringServiceMaker();
        newMarker.createAndUploadSpringBean(springContextFilePath, springBeanFilePath,
                                            asServer.getSessionCookie(), asServer.getBackEndUrl());
        isServiceDeployed("SpringBean");
    }

    @Test(groups = "wso2.as", description = "Invoke spring service", dependsOnMethods = "testSpringServiceUpload")
    public void testInvokeSpringService() throws Exception {
        String operationName = "echoInt";
        String expectedIntValue = "451";
        String namespaceOfService = "http://service.carbon.wso2.org";
        String epr = asServer.getServiceUrl() + "/" + "SpringBean";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement result =
                axisServiceClient.sendReceive(createPayLoad(operationName, expectedIntValue,
                                                            namespaceOfService), epr, operationName);
        assertTrue(result.toString().contains("<return>" + expectedIntValue + "</return>"));

    }

    @AfterClass(alwaysRun = true)
    public void testCleanup() throws Exception {
        deleteService("SpringBean");
        log.info("SpringBean service deleted");
    }

    private static OMElement createPayLoad(String operation, String expectedValue,
                                           String namespace) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace(namespace, "p");
        OMElement method = fac.createOMElement(operation, omNs);
        OMElement value = fac.createOMElement("arg0", omNs);
        value.addChild(fac.createOMText(value, expectedValue));
        method.addChild(value);
        return method;
    }

}
