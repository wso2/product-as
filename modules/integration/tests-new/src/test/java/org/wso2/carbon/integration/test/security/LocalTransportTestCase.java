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

package org.wso2.carbon.integration.test.security;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.api.clients.aar.services.AARServiceUploaderClient;
import org.wso2.carbon.automation.api.clients.authenticators.AuthenticatorClient;
import org.wso2.carbon.automation.api.clients.module.mgt.ModuleAdminServiceClient;
import org.wso2.carbon.automation.api.clients.service.mgt.ServiceAdminClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.ClientConnectionUtil;
import org.wso2.carbon.automation.core.utils.environmentutils.ProductUrlGeneratorUtil;
import org.wso2.carbon.automation.utils.httpclient.HttpsResponse;
import org.wso2.carbon.automation.utils.httpclient.HttpsURLConnectionClient;
import org.wso2.carbon.endpoint.stub.types.EndpointAdminEndpointAdminException;
import org.wso2.carbon.integration.test.ASIntegrationTest;
import org.wso2.carbon.module.mgt.stub.ModuleAdminServiceModuleMgtExceptionException;
import org.wso2.carbon.module.mgt.stub.types.ModuleMetaData;
import org.wso2.carbon.service.mgt.stub.ServiceAdminException;
import org.wso2.carbon.service.mgt.stub.types.carbon.ServiceMetaData;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

import static org.testng.Assert.*;

public class LocalTransportTestCase extends ASIntegrationTest {
    private static final Log log = LogFactory.getLog(LocalTransportTestCase.class);
    private String serviceName = "SimpleStockQuoteService";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(ProductConstant.ADMIN_USER_ID);
        AARServiceUploaderClient aarServiceUploaderClient
                = new AARServiceUploaderClient(asServer.getBackEndUrl(),
                asServer.getSessionCookie());

        aarServiceUploaderClient.uploadAARFile("SimpleStockQuoteService.aar",
                ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        "SimpleStockQuoteService.aar", "");

        isServiceDeployed(serviceName);
        log.info("Service uploaded successfully");
    }

    @Test(groups = {"wso2.as"}, description = "Unauthorize module engagement")
    public void testToEngageModuleToService()
            throws IOException, EndpointAdminEndpointAdminException,
            LoginAuthenticationExceptionException,
            XMLStreamException, ModuleAdminServiceModuleMgtExceptionException {

        ModuleAdminServiceClient moduleAdminServiceClient =
                new ModuleAdminServiceClient(asServer.getBackEndUrl(), asServer.getSessionCookie());
        ModuleMetaData[] modulesBefore = moduleAdminServiceClient.listModulesForService(serviceName);

        String moduleId = null;
        for (ModuleMetaData moduleBefore : modulesBefore) {
            if (!moduleBefore.getEngagedServiceLevel()) {
                moduleId = moduleBefore.getModuleId();
                break;
            }
        }

        String url = ProductUrlGeneratorUtil.getProductHomeURL(ProductConstant.APP_SERVER_NAME) +
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
    public void testEnableMTOM() throws IOException, ServiceAdminException {
        ServiceAdminClient serviceAdminClient =
                new ServiceAdminClient(asServer.getBackEndUrl(), asServer.getSessionCookie());
        ServiceMetaData serviceMetaData = serviceAdminClient.getServicesData(serviceName);
        boolean statusBefore = serviceMetaData.getActive();
        assertTrue(statusBefore, "service is not active");
        String url = ProductUrlGeneratorUtil.getProductHomeURL(ProductConstant.APP_SERVER_NAME) +
                "/service-mgt/change_service_state_ajaxprocessor.jsp?serviceName=" + serviceName + "&isActive=false";

        HttpsResponse response = HttpsURLConnectionClient.getRequest(url, null);
        log.info("HTTPS client response code " + response.getResponseCode());

        ServiceMetaData serviceMetaDataAfter = serviceAdminClient.getServicesData(serviceName);
        assertTrue(serviceMetaDataAfter.getActive(), "Authorization skipped when activate/deactivate " +
                "service though change_service_state_ajaxprocessor");
    }

    @Test(groups = {"wso2.as"}, description = "Unauthorize restart of carbon server")
    public void testRestartGracefully()
            throws InterruptedException, IOException {

        String url = ProductUrlGeneratorUtil.getProductHomeURL(ProductConstant.APP_SERVER_NAME) +
                "/server-admin/proxy_ajaxprocessor.jsp?action=restartGracefully";

        HttpsResponse response = HttpsURLConnectionClient.getRequest(url, null);
        log.info("HTTPS client response code " + response.getResponseCode());

        Thread.sleep(5000); //force wait until server start gracefully shut down.
        boolean status = false;
        try {
            //try to login while server is restaring
            AuthenticatorClient authenticatorClient = new AuthenticatorClient(asServer.getBackEndUrl());
            authenticatorClient.login(userInfo.getUserName(), userInfo.getPassword(),
                    asServer.getProductVariables().getHostName());
        } catch (AxisFault axisFault) {
            status = true;
        } catch (LoginAuthenticationExceptionException e) {
            status = true;
        }
        assertFalse(status, "Authorization skipped when gracefully restarting the server");

        //wait for server startup
        ClientConnectionUtil.waitForLogin(Integer.parseInt(asServer.getProductVariables().getHttpsPort()),
                asServer.getProductVariables().getHostName(),
                asServer.getBackEndUrl());
    }

}
