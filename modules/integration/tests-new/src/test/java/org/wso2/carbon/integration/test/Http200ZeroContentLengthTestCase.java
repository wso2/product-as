/*
*Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.integration.test;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.webapp.mgt.WebAppAdminClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentBuilder;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentVariables;
import org.wso2.carbon.automation.utils.webapp.WebAppUtil;

import javax.xml.namespace.QName;
import java.io.File;
import java.rmi.RemoteException;

import static org.testng.Assert.fail;


public class Http200ZeroContentLengthTestCase {
    private static final Log log = LogFactory.getLog(Http200ZeroContentLengthTestCase.class);
    private EnvironmentVariables environmentAS;
    private WebAppAdminClient webAppAdminClient;
    private final String webAppFileName = "servlet-zero-content-length.war";
    private final String webAppCtx = "/servlet-zero-content-length";

    @BeforeClass(alwaysRun = true)
    public void testInitialize() throws Exception, RemoteException {
        int userId = 0;
        EnvironmentBuilder builder = new EnvironmentBuilder().as(userId);
        environmentAS = builder.build().getAs();
        log.info("Executing HTTP 200 OK Zero Content Length Test Case");

    }

    @Test(groups = {"wso2.as"}, description = "Upload webapp file")
    public void testUploadWebAPP() throws Exception {
        webAppAdminClient = new WebAppAdminClient(environmentAS.getBackEndUrl(),
                                                  environmentAS.getSessionCookie());
        webAppAdminClient.warFileUplaoder(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                                          "artifacts" + File.separator + "AS" + File.separator + "war"
                                          + File.separator + webAppFileName);

        WebAppUtil.waitForWebAppDeployment(environmentAS.getWebAppURL() + webAppCtx, "Hello World");

    }


    @Test(groups = "wso2.as", description = "Invoke EP using axis2 service client",
          dependsOnMethods = "testUploadWebAPP")
    public void testInvokeServiceClient() throws Exception {
        EndpointReference targetEPR = new EndpointReference(environmentAS.getWebAppURL() +
                                                            "/servlet-zero-content-length/Mock");
        OMElement payload = getEchoOMElement();
        Options options = new Options();
        options.setTo(targetEPR);
        options.setAction("urn:echo");

        ConfigurationContext defaultContext = ConfigurationContextFactory.
                createConfigurationContextFromFileSystem(null, null);

        //Blocking invocation
        ServiceClient sender = new ServiceClient(defaultContext, null);
        sender.setOptions(options);
        try {
            sender.sendRobust(payload);
        } catch (Exception e) {
            fail("Error while sending/receieving message from : " + targetEPR.toString());
        }
    }

    private OMElement getEchoOMElement() {
        String ns = "http://echo.services.core.carbon.wso2.org";
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement rootEle = factory.createOMElement(new QName(ns, "echoString"));

        OMElement childEle = factory.createOMElement(new QName("in"));
        childEle.setText("9875453");
        rootEle.addChild(childEle);

        return rootEle;
    }

}
