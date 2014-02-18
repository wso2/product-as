package org.wso2.appserver.sample.mail.client;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.ByteArrayInputStream;

/**
 * Client which uses the mail transport..
 */
public class MailClient {

    public static void main(String[] args) {

        try {
            File configFile =new File("conf/axis2.xml");
            ConfigurationContext configurationContext =
                    ConfigurationContextFactory.createConfigurationContextFromFileSystem(
                            null, configFile.getAbsolutePath());
            ServiceClient serviceClient = new ServiceClient(configurationContext, null);

            serviceClient.setTargetEPR(new EndpointReference("mailto:wso2demomail@gmail.com"));
            serviceClient.engageModule("addressing");

            // ping invocation
            serviceClient.getOptions().setAction("urn:ping");
            serviceClient.fireAndForget(getPingPayload("Hello Pinging..."));

            // echo invocation

            // these are the mail transport parameters that should be set to receive the response
            serviceClient.getAxisService().addParameter("transport.mail.Address",
                    "wso2demomail@gmail.com");
            serviceClient.getAxisService().addParameter("transport.mail.Protocol", "pop3");
            serviceClient.getAxisService().addParameter("transport.PollInterval", "5");
            serviceClient.getAxisService().addParameter("mail.pop3.host", "pop.gmail.com");
            serviceClient.getAxisService().addParameter("mail.pop3.user", "wso2demomail");
            serviceClient.getAxisService().addParameter("mail.pop3.password", "mailpassword");
            serviceClient.getAxisService().addParameter("mail.pop3.port", "995");

            serviceClient.getAxisService().addParameter("mail.pop3.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            serviceClient.getAxisService().addParameter("mail.pop3.socketFactory.fallback",
                    "false");
            serviceClient.getAxisService().addParameter("mail.pop3.socketFactory.port", "995");
            serviceClient.getAxisService().addParameter("transport.mail.ContentType", "text/xml");

            serviceClient.getOptions().setAction("urn:echo");

            // TODO : There is an issue in mail transport In-Out synchronous case
            // TODO : fix it and uncomment this
//            OMElement response = serviceClient.sendReceive(getEchoPayload("Helloo..."));
//            System.out.println(response);

            System.out.println("Successfully Invoked the Mail Test service..");
            // terminate the config ctx to shut down listeners..
            configurationContext.terminate();

        } catch (Exception e) {
            // just print the stack trace
            e.printStackTrace();
        }
    }

    private static OMElement getEchoPayload(String input) throws XMLStreamException {
        String payload = "<ns:echo xmlns:ns=\"http://service.mail.sample.appserver.wso2.org\">" +
                "<ns:in>" + input + "</ns:in></ns:echo>";

        return new StAXOMBuilder(new ByteArrayInputStream(payload.getBytes())).getDocumentElement();
    }

    private static OMElement getPingPayload(String input) throws XMLStreamException {
        String payload = "<ns:ping xmlns:ns=\"http://service.mail.sample.appserver.wso2.org\">" +
                "<ns:ping>" + input + "</ns:ping></ns:ping>";

        return new StAXOMBuilder(new ByteArrayInputStream(payload.getBytes())).getDocumentElement();
    }

}
