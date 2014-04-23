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

package org.wso2.appserver.integration.tests.security;

import org.wso2.appserver.integration.common.clients.AARServiceUploaderClient;
import org.wso2.appserver.integration.common.clients.ModuleAdminServiceClient;
import org.wso2.appserver.integration.common.clients.ServiceAdminClient;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.common.HomePageGenerator;
import org.wso2.carbon.automation.test.utils.http.client.HttpsResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpsURLConnectionClient;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.utils.ClientConnectionUtil;
import org.wso2.carbon.module.mgt.stub.ModuleAdminServiceModuleMgtExceptionException;
import org.wso2.carbon.module.mgt.stub.types.ModuleMetaData;
import org.wso2.carbon.service.mgt.stub.ServiceAdminException;
import org.wso2.carbon.service.mgt.stub.types.carbon.ServiceMetaData;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

import static org.testng.Assert.*;

public class LocalTransportTestCase extends ASIntegrationTest {
    private static final Log log = LogFactory.getLog(LocalTransportTestCase.class);
    private String serviceName = "SimpleStockQuoteService";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        AARServiceUploaderClient aarServiceUploaderClient
                = new AARServiceUploaderClient(backendURL,
                sessionCookie);

        aarServiceUploaderClient.uploadAARFile("SimpleStockQuoteService.aar",
                FrameworkPathUtil.getSystemResourceLocation() + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        "SimpleStockQuoteService.aar", "");

        isServiceDeployed(serviceName);
        log.info("Service uploaded successfully");
    }

    @Test(groups = {"wso2.as"}, description = "Unauthorize module engagement")
    public void testToEngageModuleToService()
            throws IOException,
            LoginAuthenticationExceptionException,
            XMLStreamException, ModuleAdminServiceModuleMgtExceptionException, XPathExpressionException {

        ModuleAdminServiceClient moduleAdminServiceClient =
                new ModuleAdminServiceClient(backendURL, sessionCookie);
        ModuleMetaData[] modulesBefore = moduleAdminServiceClient.listModulesForService(serviceName);

        String moduleId = null;
        for (ModuleMetaData moduleBefore : modulesBefore) {
            if (!moduleBefore.getEngagedServiceLevel()) {
                moduleId = moduleBefore.getModuleId();
                break;
            }
        }

        String url = HomePageGenerator.getProductHomeURL(asServer) +
                "/modulemgt/service_eng_ajaxprocessor.jsp?action=engage&moduleId=" + moduleId +
                "&serviceName=" + serviceName;
        HttpsResponse response = HttpsURLConnectionClient.getRequest(url, null);
        log.info("HTTPS client response code " + response.getResponseCode());
        ModuleMetaData[] moduleListAfterEngage =
                moduleAdminServiceClient.listModulesForService(serviceName);

        boolean status = false;
        for (ModuleMetaData modulesAfter : moduleListAfterEngage) {
            if (modulesAfter.getModuleId().equals(moduleId)) {
                if ((modulesAfter.getEngagedServiceLevel())) {
                    status = true;
                }
                break;
            }
        }
        assertFalse(status, "Authorization skipped when engaging Modules");
    }

    @Test(groups = {"wso2.as"}, description = "Inactivate service")
    public void testEnableMTOM() throws IOException, ServiceAdminException, XPathExpressionException {
        ServiceAdminClient serviceAdminClient =
                new ServiceAdminClient(backendURL, sessionCookie);
        ServiceMetaData serviceMetaData = serviceAdminClient.getServicesData(serviceName);
        boolean statusBefore = serviceMetaData.getActive();
        assertTrue(statusBefore, "service is not active");
        String url = webAppURL +
                "/service-mgt/change_service_state_ajaxprocessor.jsp?serviceName=" + serviceName + "&isActive=false";

        HttpsResponse response = HttpsURLConnectionClient.getRequest(url, null);
        log.info("HTTPS client response code " + response.getResponseCode());

        ServiceMetaData serviceMetaDataAfter = serviceAdminClient.getServicesData(serviceName);
        assertTrue(serviceMetaDataAfter.getActive(), "Authorization skipped when activate/deactivate " +
                "service though change_service_state_ajaxprocessor");
    }

    @Test(groups = {"wso2.as"}, description = "Unauthorize restart of carbon server")
    public void testRestartGracefully()
            throws InterruptedException, IOException, XPathExpressionException, LoginAuthenticationExceptionException {

        String url = webAppURL +
                "/server-admin/proxy_ajaxprocessor.jsp?action=restartGracefully";

        HttpsResponse response = HttpsURLConnectionClient.getRequest(url, null);
        log.info("HTTPS client response code " + response.getResponseCode());

        Thread.sleep(5000); //force wait until server start gracefully shut down.
        boolean status = false;
        try {
            //try to login while server is restaring
            AuthenticatorClient authenticatorClient = new AuthenticatorClient(backendURL);
            authenticatorClient.login(asServer.getSuperTenant().getTenantAdmin().getUserName(),
                    asServer.getSuperTenant().getTenantAdmin().getPassword(),
                    asServer.getDefaultInstance().getHosts().get("default"));
        } catch (AxisFault axisFault) {
            status = true;
        } catch (LoginAuthenticationExceptionException e) {
            status = true;
        }
        assertFalse(status, "Authorization skipped when gracefully restarting the server");

        //wait for server startup
        ClientConnectionUtil.waitForLogin(asServer);
    }

}
