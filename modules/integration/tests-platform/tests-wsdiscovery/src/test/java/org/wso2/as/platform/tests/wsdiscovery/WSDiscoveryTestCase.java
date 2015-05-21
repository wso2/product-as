/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.as.platform.tests.wsdiscovery;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.ResourceAdminServiceClient;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.as.platform.tests.wsdiscovery.exceptions.WSDiscoveryTestException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.engine.frameworkutils.TestFrameworkUtils;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpsResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpsURLConnectionClient;
import org.wso2.carbon.governance.api.services.ServiceManager;
import org.wso2.carbon.governance.api.services.dataobjects.Service;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.registry.ws.client.registry.WSRegistryServiceClient;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static junit.framework.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This class will test the discovered services information
 */
public class WSDiscoveryTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(WSDiscoveryTestCase.class);
    private static final String CONTENT_TYPE = "text/html";
    private WebAppAdminClient webAppAdminClient;
    private final String jaxRSWebAppFileName = "discovery-jax-rs_1.0.0.war";
    private final String jaxRSWebAppName = "discovery-jax-rs_1.0.0";
    private final String jaxWSWebAppFileName = "discovery-jax-ws_1.0.0.war";
    private final String jaxWSWebAppName = "discovery-jax-ws_1.0.0";
    private final String hostName = "localhost";
    private static final String NAMESPACE = "http://docs.oasis-open.org/ws-dd/ns/discovery/2009/01";
    private static final String SCOPES = "http://wso2.com/carbon";
    private static final String VERSION = "1.0.0-SNAPSHOT";
    private static final String METADATA_VERSION_VALUE = "100";
    private static final String OVERVIEW_TYPES = "overview_types";
    private static final String OVERVIEW_NAME = "overview_name";
    private static final String OVERVIEW_VERSION = "overview_version";
    private static final String OVERVIEW_NAMESPACE = "overview_namespace";
    private static final String ENDPOINTS_ENTRY = "endpoints_entry";
    private static final String OVERVIEW_SCOPES = "overview_scopes";
    private static final String METADATA_VERSION = "metadataVersion";
    private static final String RESPONSE_VALUE = "Hello Is Called";
    private ServiceManager serviceManager;
    private static final int TIME_OUT_VALUE = 1000 * 60; //in milliseconds

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        AutomationContext gregAutomationContext = new AutomationContext("GREG", "greg001",
                TestUserMode.SUPER_TENANT_ADMIN);
        WSRegistryServiceClient wsRegistry = getWSRegistry(gregAutomationContext);
        Registry governance = getGovernanceRegistry(wsRegistry, gregAutomationContext);
        GovernanceUtils.loadGovernanceArtifacts((UserRegistry) governance, GovernanceUtils.findGovernanceArtifactConfigurations((UserRegistry) governance));
        serviceManager = new ServiceManager(governance);

        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        // Uploading the JAXRS web application
        webAppAdminClient.warFileUplaoder(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war" + File.separator + jaxRSWebAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, jaxRSWebAppName),
                "JAXRS Web Application Deployment failed");

        // Uploading the JAXWS web application
        webAppAdminClient.warFileUplaoder(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war" + File.separator + jaxWSWebAppFileName);
        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(backendURL, sessionCookie, jaxWSWebAppName),
                "JAXWS Web Application Deployment failed");

        // Waiting till the service available in the GREG
        Thread.sleep(20000);
    }

    @Test(groups = "wso2.as", description = "Testing the discovered JAXRS service")
    public void testServiceDiscoveryForJAXRS() throws Exception {
        // get the service for the given web app
        Service jAXRSService = serviceManager
                .getService(getServiceID("discovery-jax-rs_1.0.0_discovery_r_s_hello"));
        assertEquals("{http://hello.discovery/}DiscoveryRSHello", jAXRSService.getAttribute(OVERVIEW_TYPES));
        assertEquals("discovery-jax-rs_1.0.0_discovery_r_s_hello", jAXRSService.getAttribute(OVERVIEW_NAME));
        assertEquals(VERSION, jAXRSService.getAttribute(OVERVIEW_VERSION));
        assertEquals(NAMESPACE, jAXRSService.getAttribute(OVERVIEW_NAMESPACE));
        String serviceEndpoint = jAXRSService.getAttribute(ENDPOINTS_ENTRY);
        assertEquals(RESPONSE_VALUE, getJAXRSEndpointResponseData(serviceEndpoint.substring(1)));
        assertEquals(SCOPES, jAXRSService.getAttribute(OVERVIEW_SCOPES));
        assertEquals(METADATA_VERSION_VALUE, jAXRSService.getAttribute(METADATA_VERSION));
    }

    @Test(groups = "wso2.as", description = "Testing discovered JAXWS service")
    public void testServiceDiscoveryForJAXWS() throws Exception {
        // get the service for the given web app
        Service jAXWSService = serviceManager
                .getService(getServiceID("discovery-jax-ws_1.0.0_discovery_say_hello_service"));
        assertEquals("{http://hello.discovery/}DiscoverySayHello", jAXWSService.getAttribute(OVERVIEW_TYPES));
        assertEquals("discovery-jax-ws_1.0.0_discovery_say_hello_service",
                jAXWSService.getAttribute(OVERVIEW_NAME));
        assertEquals(VERSION, jAXWSService.getAttribute(OVERVIEW_VERSION));
        assertEquals(NAMESPACE, jAXWSService.getAttribute(OVERVIEW_NAMESPACE));
        String serviceEndpoint = jAXWSService.getAttribute(ENDPOINTS_ENTRY);
        assertTrue(getJAXWSEndpointResponseData(serviceEndpoint.substring(1)).toString()
                .contains("<return>Hello wsdiscovery !</return>"));
        assertEquals(SCOPES, jAXWSService.getAttribute(OVERVIEW_SCOPES));
        assertEquals(METADATA_VERSION_VALUE, jAXWSService.getAttribute(METADATA_VERSION));

    }


    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        webAppAdminClient.deleteWebAppFile(jaxRSWebAppFileName, hostName);
        webAppAdminClient.deleteWebAppFile(jaxWSWebAppFileName, hostName);
    }

    /**
     * This method will return WSRegistry service client for the given server
     */
    private WSRegistryServiceClient getWSRegistry(AutomationContext automationContext) throws WSDiscoveryTestException {

        System.setProperty("carbon.repo.write.mode", "true");
        WSRegistryServiceClient registry = null;
        ConfigurationContext configContext;
        String axis2Repo = FrameworkPathUtil.getSystemResourceLocation() + File.separator + "client";
        String axis2Conf = FrameworkPathUtil.getSystemResourceLocation() + "axis2config" +
                File.separator + "axis2_client.xml";
        try {
            TestFrameworkUtils.setKeyStoreProperties(automationContext);
            configContext = ConfigurationContextFactory.
                    createConfigurationContextFromFileSystem(axis2Repo, axis2Conf);
            configContext.setProperty(HTTPConstants.CONNECTION_TIMEOUT, TIME_OUT_VALUE);

            log.info("Group ConfigurationContext Timeout " + configContext.getServiceGroupContextTimeoutInterval());

            registry = new WSRegistryServiceClient(automationContext.getContextUrls().getBackEndUrl(),
                    automationContext.getContextTenant().getContextUser().getUserName(),
                    automationContext.getContextTenant().getContextUser().getPassword(), configContext);

            log.info("WS Registry Created - Login Successful");

        } catch (Exception e) {
            String msg = "Failed instantiate WSRegistry client instance ";
            log.error(msg, e);
            throw new WSDiscoveryTestException(msg, e);
        }
        return registry;
    }

    /**
     * This method will call to the service endpoint of the JAXRS service and get the response
     *
     * @param endpointURL service endpoint url
     */
    private String getJAXRSEndpointResponseData(String endpointURL) throws Exception {
        if (endpointURL != null) {
            if (endpointURL.startsWith("http://")) {
                HttpResponse httpResponse = getHttpResponse(endpointURL, CONTENT_TYPE);
                return httpResponse.getData();
            } else if(endpointURL.startsWith("https://")){
                HttpsResponse httpsResponse = getHtttpsResponse(endpointURL);
                return httpsResponse.getData();
            }
        }
        return null;
    }

    /**
     * This method will call to the service endpoint of the JAXWS
     * using axis client and get the response
     *
     * @param endpointURL service endpoint url
     * @return response as a OMElement
     */
    private OMElement getJAXWSEndpointResponseData(String endpointURL) throws AxisFault {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement response = axisServiceClient.sendReceive(createPayLoad(), endpointURL, "hello");
        return response;
    }

    /**
     * This method will create the payload which need to send the deployed jaxws service
     *
     * @return payload OMElement
     */
    private OMElement createPayLoad() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://hello.discovery/", "hel");
        OMElement getOme = fac.createOMElement("hello", omNs);
        OMElement getOmeTwo = fac.createOMElement(new QName("name"));
        getOmeTwo.setText("wsdiscovery");
        getOme.addChild(getOmeTwo);
        return getOme;
    }

    /**
     * This method will return Governance registry for the given server
     */
    private Registry getGovernanceRegistry(Registry registry, AutomationContext automationContext) throws Exception {

        Registry governance = null;
        TestFrameworkUtils.setKeyStoreProperties(automationContext);
        System.setProperty("carbon.repo.write.mode", "true");
        try {
            governance = GovernanceUtils.getGovernanceUserRegistry(registry,
                    automationContext.getContextTenant().getContextUser().getUserName());

        } catch (Exception e) {
            String msg = "Failed to instantiate governance registry instance ";
            log.error(msg, e);
            throw new WSDiscoveryTestException(msg, e);
        }
        return governance;
    }

    /**
     * This method will get the service id of the deployed webapp
     * from "/_system/config/" registry location
     *
     * @param serviceName deployed web application name
     * @return service id of the given web app
     */
    private String getServiceID(String serviceName) throws WSDiscoveryTestException {
        String serviceID = null;
        String errorMsg = "Service ID not available for the given service";
        try {
            ResourceAdminServiceClient resourceServiceClient = new ResourceAdminServiceClient(backendURL,
                    sessionCookie);
            serviceID = resourceServiceClient
                    .getProperty("/_system/config/repository/components/org.wso2.carbon.discovery.core/ServiceIDList",
                            serviceName);
            if (serviceID != null) {
                serviceID = serviceID.replaceAll("urn:uuid:", "");
            } else {
                log.error(errorMsg);
                throw new WSDiscoveryTestException("Service ID not available for the given service");
            }
        } catch (Exception e) {
            log.error(errorMsg, e);
            throw new WSDiscoveryTestException("Service ID not available for the given service");
        }
        return serviceID;
    }

    /**
     * This method will get the response data from the https endpoint
     *
     * @param endpoint https endpoint
     * @return HttpsResponse object
     */
    private HttpsResponse getHtttpsResponse(String endpoint) throws WSDiscoveryTestException {
        HttpsResponse httpsResponse = null;
        try {
            String userName = asServer.getSuperTenant().getTenantAdmin().getUserName();
            String password = asServer.getSuperTenant().getTenantAdmin().getPassword();
            httpsResponse = HttpsURLConnectionClient.getWithBasicAuth(endpoint, null, userName, password);
        } catch (XPathExpressionException e) {
            String msg = "Error while retrieving user details on endpoint server";
            log.error(msg, e);
            throw new WSDiscoveryTestException(msg, e);
        } catch (IOException e) {
            String msg = "Error while retrieving response data";
            log.error(msg, e);
            throw new WSDiscoveryTestException(msg, e);
        }
        return httpsResponse;
    }

    /**
     * This method will "Accept" header Types "application/json", etc..
     * @param endpoint service endpoint
     * @param contentType header type
     * @return HttpResponse
     * @throws Exception
     */
    private HttpResponse getHttpResponse(String endpoint, String contentType) throws Exception {

        String urlStr = endpoint;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept", contentType);
        conn.setRequestProperty("charset", "UTF-8");
        conn.setReadTimeout(10000);
        conn.connect();
        // Get the response
        StringBuilder sb = new StringBuilder();
        BufferedReader rd = null;
        try {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
        } finally {
            if (rd != null) {
                rd.close();
            }
        }
        return new HttpResponse(sb.toString(), conn.getResponseCode());
    }

}