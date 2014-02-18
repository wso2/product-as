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
package org.wso2.carbon.integration.test.sampleservices.clientapiservice;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.aar.services.AARServiceUploaderClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * This class can be used for testing purposes of ClientAPI sample scenario.
 */
public class ClientAPITestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(ClientAPITestCase.class);
    private volatile boolean isComplete;
    private String epr;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        epr = asServer.getServiceUrl() + "/ClientAPIDemoService"; // endpoint url
    }

    @AfterClass(alwaysRun = true)
    public void serviceDelete() throws Exception {   // delete services
        deleteService("ClientAPIDemoService");
        log.info("ClientAPIDemoService deleted");
    }

    @Test(groups = "wso2.as", description = "Upload service and verify deployment")
    public void servicesUpload() throws Exception {
        AARServiceUploaderClient aarServiceUploaderClient
                = new AARServiceUploaderClient(asServer.getBackEndUrl(),
                asServer.getSessionCookie());

        aarServiceUploaderClient.uploadAARFile("ClientAPIDemoService.aar",
                ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        "ClientAPIDemoService.aar", "");

        isServiceDeployed("ClientAPIDemoService");
        log.info("ClientAPIDemoService.aar service uploaded and deployed successfully");
    }

    @Test(groups = "wso2.as", description = "Serving each client type selection",
            dependsOnMethods = "servicesUpload")
    public void blockingClientRun() {
        try {
            ServiceClient serviceClient = new ServiceClient();

            Options options = new Options();
            options.setTo(new EndpointReference(epr));
            options.setAction("urn:echo");
            serviceClient.setOptions(options);

            // Blocking call
            OMElement response = serviceClient.sendReceive(getEchoPayload("Hello, I'm BlockingClient"));
            log.info(response);
            assertEquals(response.toString(), "<ns:echoResponse xmlns:ns=" +
                    "\"http://service.clientapi.sample.appserver.wso2.org\">" +
                    "<ns:return>Hello, I'm BlockingClient</ns:return></ns:echoResponse>");
        } catch (Exception e) {
            log.info(e);
        }
    }

    @Test(groups = "wso2.as", description = "Serving each client type selection",
            dependsOnMethods = "blockingClientRun")
    public void singleChannelNonBlockingClientRun() {

        isComplete = false;
        try {
            ServiceClient serviceClient = new ServiceClient();

            Options options = new Options();
            options.setTo(new EndpointReference(epr));
            options.setAction("urn:echo");
            serviceClient.setOptions(options);

            AxisCallback callback = new AxisCallback() {

                //This is called when we receive a message.
                public void onMessage(MessageContext messageContext) {
                    log.info("Response: " + messageContext.getEnvelope().getBody().getFirstElement());
                    isComplete = true;
                }


                // This gets called when a fault message is received.
                public void onFault(MessageContext messageContext) {
                    log.info("Fault: " +
                            messageContext.getEnvelope().getBody().getFirstElement());
                }


                // This gets called ONLY when an internal processing exception occurs.
                public void onError(Exception e) {
                    log.info("onError " + e);
                }


                //This is called at the end of the MEP no matter what happens, quite like a finally block.
                public void onComplete() {
                    log.info("Completed request");
                    isComplete = true;
                }
            };

            // Non-blocking call
            serviceClient.sendReceiveNonBlocking(getEchoPayload("Hello, I'm SingleChannelNonBlockingClient"), callback); // Notice the callback!
            log.info("Sent request. Waiting for response...");

            int i = 0;
            while (!isComplete) {
                Thread.sleep(500);
                i++;
                if (i > 20) {
                    fail("Response not received within 10s");
                }
            }

            assertTrue(isComplete);

        } catch (Exception e) {
            log.info(e);
        }
    }

    @Test(groups = "wso2.as", description = "Serving each client type selection",
            dependsOnMethods = "singleChannelNonBlockingClientRun")
    public void DualChannelNonBlockingClientRun() {

        isComplete = false;
        try {
            ConfigurationContext configCtx = ConfigurationContextFactory
                    .createConfigurationContextFromFileSystem(null, null);
            ServiceClient serviceClient = new ServiceClient(configCtx, null);

            serviceClient.engageModule("addressing"); // IMPORTANT

            Options options = new Options();
            options.setTo(new EndpointReference(epr));
            options.setAction("urn:echo");

            options.setUseSeparateListener(true);
            options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

            serviceClient.setOptions(options);

            AxisCallback callback = new AxisCallback() {

                //This is called when we receive a message.
                public void onMessage(MessageContext messageContext) {
                    log.info("Response: " + messageContext.getEnvelope()
                            .getBody().getFirstElement());
                    isComplete = true;
                }

                // This gets called when a fault message is received.
                public void onFault(MessageContext messageContext) {
                    log.info("Fault: " +
                            messageContext.getEnvelope().getBody().getFirstElement());
                }

                // This gets called ONLY when an internal processing exception occurs.
                public void onError(Exception e) {
                    log.info("onError" + e);
                }

                //This is called at the end of the MEP quits like a finally block.
                public void onComplete() {
                    log.info("Completed request");
                    isComplete = true;
                }
            };

            // Non-blocking call
            serviceClient.sendReceiveNonBlocking(getEchoPayload
                    ("Hello, I'm DualChannelNonBlockingClient"), callback); // Notice the callback!
            log.info("Sent request. Waiting for response...");

            int i = 0;
            while (!isComplete) {
                Thread.sleep(500);
                i++;
                if (i > 20) {
                    fail("Response not received within 10s");

                }
            }

            assertTrue(isComplete);

        } catch (Exception e) {
            log.info(e);
        }
    }

    @Test(groups = "wso2.as", description = "Serving each client type selection",
            dependsOnMethods = "DualChannelNonBlockingClientRun")
    public void dynamicBlockingClientRun() {
        try {
            ServiceClient serviceClient = new ServiceClient(null, new URL(eprToWSDLUrl()), null, null);
            // ConfigurationContext ,WSDL URL ,Service name ,service port name

            // Blocking call
            QName operation = new QName("http://service.clientapi.sample." +
                    "appserver.wso2.org", "echo");
            OMElement response = serviceClient.sendReceive(operation, getEchoPayload("Hello, I'm DynamicBlockingClient"));
            log.info("Response: " + response);
            assertEquals(response.toString(), "<ns:echoResponse xmlns:ns=\"" +
                    "http://service.clientapi.sample.appserver.wso2.org\">" +
                    "<ns:return>Hello, I'm DynamicBlockingClient</ns:return></ns:echoResponse>");
        } catch (Exception e) {
            log.info(e);
        }
    }

    @Test(groups = "wso2.as", description = "Serving each client type selection",
            dependsOnMethods = "dynamicBlockingClientRun")
    public void rpcClientRun() {
        try {
            RPCServiceClient rpcClient = new RPCServiceClient(null, // ConfigurationContext
                    new URL(eprToWSDLUrl()), // WSDL URL
                    null, // Service name
                    null // service port name
            );

            // Blocking call
            QName operation = new QName("http://service.clientapi.sample.appserver.wso2.org", "echo");
            Object[] objects = rpcClient.invokeBlocking(operation,
                    new Object[]{"Hello, I'm RPCClient"},
                    new Class[]{String.class});
            log.info("Response: " + objects[0]);
            assertEquals(objects[0].toString(), "Hello, I'm RPCClient");
        } catch (Exception e) {
            log.info(e);
        }
    }

    @Test(groups = "wso2.as", description = "Serving each client type selection",
            dependsOnMethods = "rpcClientRun")
    public void fireAndForgetClientRun() {
        try {
            ServiceClient serviceClient = new ServiceClient();

            Options options = new Options();
            options.setTo(new EndpointReference(epr));
            options.setAction("urn:echo");
            serviceClient.setOptions(options);

            log.info("Invoking Fire and Forget without Exceptions..");
            serviceClient.fireAndForget(getEchoPayload("Hello, I'm FireAndForgetClient"));
            log.info("Invoking Fire and Forget with Exceptions. But the " +
                    "client will neglect the return exception..");
            serviceClient.fireAndForget(getEchoPayload("exception"));
        } catch (Exception e) {
            log.info(e);
        }
    }

    @Test(groups = "wso2.as", description = "Serving each client type selection",
            dependsOnMethods = "fireAndForgetClientRun" , expectedExceptions = AxisFault.class)
    public void sendRobustClientRun() throws Exception{

            ServiceClient serviceClient = new ServiceClient();

            Options options = new Options();
            options.setTo(new EndpointReference(epr));
            options.setAction("urn:update");
            serviceClient.setOptions(options);

            log.info("Invoking Send-Robust without Exceptions..");
            serviceClient.sendRobust(getEchoPayload("Hello, I'm SendRobustClient"));
            log.info("Invoking Send-Robust with Exceptions. Send Robust client will " +
                    "print the exception..");
            serviceClient.sendRobust(getEchoPayload("exception"));

    }

    @Test(groups = "wso2.as", description = "Serving each client type selection",
            dependsOnMethods = "sendRobustClientRun")
    public void sampleOperationClientRun() {
        try {
            ServiceClient serviceClient = new ServiceClient();
            OperationClient operationClient = serviceClient
                    .createClient(ServiceClient.ANON_OUT_IN_OP);

            MessageContext outMsgCtx = new MessageContext();

            Options options = outMsgCtx.getOptions();
            options.setTo(new EndpointReference(epr));
            options.setAction("urn:echo");
            serviceClient.setOptions(options);

            outMsgCtx.setEnvelope(getEnvelope());
            log.info("Request: " + outMsgCtx.getEnvelope());

            operationClient.addMessageContext(outMsgCtx);

            operationClient.execute(true); // true - blocking, false - nonblocking (use with opClient.setCallback)

            MessageContext inMsgCtx = operationClient
                    .getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            log.info("Response: " + inMsgCtx.getEnvelope());
            assertEquals(inMsgCtx.getEnvelope().toString(),"<?xml version='1.0' encoding='utf-8'?>" +
                    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "<soapenv:Body><ns:echoResponse xmlns:ns=" +
                    "\"http://service.clientapi.sample.appserver.wso2.org\">" +
                    "<ns:return>Hello, I'm Operation JaxrsContentNegotiationTestCase</ns:return></ns:echoResponse>" +
                    "</soapenv:Body></soapenv:Envelope>");
        } catch (Exception e) {
            log.info(e);
        }
    }

    private OMElement getEchoPayload(String in) throws XMLStreamException {
        String payload = "<ns:echo xmlns:ns=\"http://service.clientapi.sample.appserver" +
                ".wso2.org\"><ns:value>" + in + "</ns:value></ns:echo>";
        return new StAXOMBuilder(new ByteArrayInputStream(payload.getBytes())).getDocumentElement();
    }

    private String eprToWSDLUrl() {
        if (epr.endsWith("/")) {
            epr = epr.substring(0, epr.length() - 1);
        }
        return epr + "?wsdl";
    }

    private SOAPEnvelope getEnvelope() throws XMLStreamException {

        SOAPFactory soap11Fac = OMAbstractFactory.getSOAP11Factory();
        SOAPEnvelope soapEnvelope = soap11Fac.getDefaultEnvelope();

        // Add SOAP body content
        soapEnvelope.getBody().addChild(getEchoPayload("Hello, I'm Operation JaxrsContentNegotiationTestCase"));

        // Add a sample header
        String header = "<ns:CustomerKey xmlns:ns=\"http://wso2.com/wsas/customer\">" +
                "Key#1234</ns:CustomerKey>";
        OMElement headerEle =
                new StAXOMBuilder(new ByteArrayInputStream(header.getBytes())).getDocumentElement();
        soapEnvelope.getHeader().addChild(headerEle);
        return soapEnvelope;
    }
}
