package org.wso2.carbon.integration.test.sampleservices.mexservice;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.dataretrieval.DRConstants;
import org.apache.axis2.dataretrieval.client.MexClient;
import org.apache.axis2.mex.MexConstants;
import org.apache.axis2.mex.om.Metadata;
import org.apache.axis2.mex.om.MetadataSection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.aar.services.AARServiceUploaderClient;
import org.wso2.carbon.automation.api.clients.module.mgt.ModuleAdminServiceClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.integration.test.ASIntegrationTest;
import org.wso2.carbon.module.mgt.stub.types.ModuleMetaData;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MEXTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(MEXTestCase.class);

    @DataProvider
    public Object[][] serviceNameDataProvider() {    // service names
        return new Object[][]{
                {"HelloWorldService1"},
                {"HelloWorldService2"},
                {"HelloWorldService3"},
        };
    }

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @AfterClass(alwaysRun = true)
    public void servicesDelete() throws Exception {
        deleteService("HelloWorldService1");      // removing uploaded HelloWorldService1.aar
        log.info("HelloWorldService1 deleted");

        deleteService("HelloWorldService2");      // removing uploaded HelloWorldService2.aar
        log.info("HelloWorldService2 deleted");

        deleteService("HelloWorldService3");      // removing uploaded HelloWorldService3.aar
        log.info("HelloWorldService3 deleted");
    }

    @Test(groups = "wso2.as", description = "Upload HelloWorldServices and verify deployment",
            dataProvider = "serviceNameDataProvider")
    public void servicesUpload(String serviceName) throws Exception {

        AARServiceUploaderClient aarServiceUploaderClient
                = new AARServiceUploaderClient(asServer.getBackEndUrl(),
                asServer.getSessionCookie());

        // uploading HelloWorldServices
        aarServiceUploaderClient.uploadAARFile(serviceName + ".aar",
                ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        serviceName + ".aar", "");

        isServiceDeployed(serviceName);  // verifying the deployment
        log.info(serviceName + ".aar service uploaded and deployed successfully");
    }

    @Test(groups = "wso2.as", description = "invoke MEX services",
            dependsOnMethods = "servicesUpload", dataProvider = "serviceNameDataProvider")
    public void invokeServices(String serviceName) throws Exception {

        boolean moduleExists = false;  // checking the availability of wso2mex-4.0 module for the service

        ModuleAdminServiceClient moduleAdminServiceClient =
                new ModuleAdminServiceClient(asServer.getBackEndUrl(), asServer.getSessionCookie());

        ModuleMetaData[] moduleMetaData = moduleAdminServiceClient.listModulesForService(serviceName);
        for (int x = 0; x <= moduleMetaData.length; x++) {
            if (moduleMetaData[x].getModulename().equals("wso2mex")) {
                moduleExists = true;
                //engaging the module to the service
                moduleAdminServiceClient.engageModule(moduleMetaData[x].getModuleId(), serviceName);
                break;
            }
        }

        assertTrue(moduleExists, "module engagement failure due to the unavailability of wso2mex module " +
                "at service level context");

        // for each service URL types : XML Schema , WSDL , WS-Policy
        for (int x = 1; x <= 3; x++) {
            mexClient(x, serviceName);
        }
    }

    private void mexClient(int type, String serviceName) throws Exception {
        String targetEPR = "http://localhost:9763/services/" + serviceName;
        MexClient serviceClient = getServiceClient(targetEPR);

        OMElement request;
        OMElement response = null;
        String responseString = null;
        String dialect = null;
        String identifier = "";   // as this is optional
        identifier = (identifier.length() == 0) ? null : identifier;

        try {
            switch (type) {

                case 1:
                    dialect = MexConstants.SPEC.DIALECT_TYPE_SCHEMA;  // dialect type
                    request = serviceClient.setupGetMetadataRequest(dialect,
                            identifier);
                    response = serviceClient.sendReceive(request);  // sending the request
                    log.info(response);
                    responseString = response.toString();

                    if (serviceName.equals("HelloWorldService1")) {
                        assertTrue(responseString.startsWith("<mex:Metadata")&&
                                responseString.contains("services/HelloWorldService1?xsd</mex:Location>"));


                    } else if (serviceName.equals("HelloWorldService2")) {
                        assertTrue(responseString.startsWith("<mex:Metadata")&&
                                responseString.contains("/services/HelloWorldService2?xsd</mex:Location>"));


                    } else {
                        assertTrue(responseString.startsWith("<mex:Metadata")&&
                                responseString.contains("/services/HelloWorldService3?xsd</mex:Location>"));
                    }

                    break;

                case 2:
                    dialect = MexConstants.SPEC.DIALECT_TYPE_WSDL;  // dialect type
                    request = serviceClient.setupGetMetadataRequest(dialect,
                            identifier);
                    response = serviceClient.sendReceive(request);    // sending the request
                    log.info(response);
                    responseString = response.toString();

                    if (serviceName.equals("HelloWorldService1")) {

                        assertTrue(responseString.startsWith("<mex:Metadata")&&
                                responseString.contains("services/HelloWorldService1?wsdl"));

                    } else if (serviceName.equals("HelloWorldService2")) {
                        assertTrue(responseString.startsWith("<mex:Metadata")&&
                                responseString.contains("/services/HelloWorldService2?wsdl"));

                    } else {
                        assertTrue(responseString.startsWith("<mex:Metadata")&&
                                responseString.contains("/services/HelloWorldService3?wsdl"));
                    }

                    break;

                case 3:
                    dialect = MexConstants.SPEC.DIALECT_TYPE_POLICY;    // dialect type
                    request = serviceClient.setupGetMetadataRequest(dialect,
                            identifier);
                    response = serviceClient.sendReceive(request);    // sending the request
                    responseString = response.toString();

                    if (serviceName.equals("HelloWorldService1")) {
                        assertTrue(responseString.startsWith("<mex:Metadata")&&
                                responseString.contains("http://example1.service.mex.sample.appserver.wso2.org"));

                    } else if (serviceName.equals("HelloWorldService2")) {
                        assertTrue(responseString.startsWith("<mex:Metadata")&&
                                responseString.contains("/services/HelloWorldService2?policy"));


                    } else {
                        assertTrue(responseString.startsWith("<mex:Metadata")&&
                                responseString.contains("http://example3.service.mex.sample.appserver.wso2.org"));


                    }

                    break;

                default:
                    break;
            }
        } catch (NumberFormatException ex) {
            log.info(ex);
        }

        Metadata metadata = new Metadata();
        metadata.fromOM(response);

        MetadataSection[] metaDatSections = metadata.getMetadatSections();
        // checking the metadata availability
        if (metaDatSections == null || metaDatSections.length == 0) {
            log.info("No MetadataSection is available for service " + serviceName + " ,  Dialect "
                    + dialect);
        }
    }

    private MexClient getServiceClient(String targetEPR) throws AxisFault {
        MexClient serviceClient = new MexClient();

        Options options = serviceClient.getOptions();
        options.setTo(new EndpointReference(targetEPR));
        options.setAction(DRConstants.SPEC.Actions.GET_METADATA_REQUEST);

        options.setExceptionToBeThrownOnSOAPFault(true);

        return serviceClient;
    }
}
