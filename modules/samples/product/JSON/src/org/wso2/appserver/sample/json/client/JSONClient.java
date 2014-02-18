package org.wso2.appserver.sample.json.client;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.util.HashMap;

/**
 * JSONClient which calls to the JSONService
 */
public class JSONClient {

    public static final String PARAM_HELP = "-help";
    public static final String PARAM_CT = "-ct";
    public static final String CT_AJ = "aj";
    public static final String CT_AJB = "ajb";
    private static final String PARAM_ENDPOINT = "-e";

    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_JSON_BADGERFISH = "application/json/badgerfish";

    public static final String ECHO_STRING = "Hello JSON Service";

    public static void main(String[] args) {

        // check whether the user is asking for help
        for (String arg : args) {
            if (PARAM_HELP.equalsIgnoreCase(arg)) {
                printUsage();
                System.exit(0);
            }
        }

        String contentType = APPLICATION_JSON;
        String epr = "http://localhost:9763/services/JSONService";
        // ideally, number of args should be 2. if it is 0, we consider
        // "application/json" as the default

        String param = null;
        HashMap<String, String> paramMap = new HashMap<String, String>();
        for (String arg : args) {
            if (param == null) {
                param = arg;
            } else {
                paramMap.put(param, arg);
                param = null;
            }
        }

        if (paramMap.get(PARAM_ENDPOINT) != null) {
            epr = paramMap.get(PARAM_ENDPOINT);
        }

        if (paramMap.get(PARAM_CT) != null) {
            String option = paramMap.get(PARAM_CT);
            if (CT_AJB.equals(option)) {
                contentType = APPLICATION_JSON_BADGERFISH;
            } else if (!CT_AJ.equals(option)) {
                exitDueToInvalidArgs();
            }
        }

        if (paramMap.size() > 2) {
            exitDueToInvalidArgs();
        }

        try {
            // We've set port in the EPR to 9763, which is the default port for AppServer.
            // But if you want to see the JSON messages on the wire, chenge this and use TCPMON
            EndpointReference targetEPR = new EndpointReference(epr);

            Options options = new Options();
            options.setTo(targetEPR);

            // IMPORTANT : It is a must to properly set the message Type when using JSON
            options.setProperty(Constants.Configuration.MESSAGE_TYPE, contentType);

            File configFile =new File("conf/axis2.xml");
            ConfigurationContext clientConfigurationContext = ConfigurationContextFactory
                            .createConfigurationContextFromFileSystem(null,
                                    configFile.getAbsolutePath());
            ServiceClient sender = new ServiceClient(clientConfigurationContext, null);
            sender.setOptions(options);
            options.setTo(targetEPR);

            OMElement echoPayload = getEchoPayload(contentType);
            OMElement result = sender.sendReceive(echoPayload);
            if (result != null && echoPayload.toString().equals(result.toString().trim())) {
                System.out.println("\nJSON Service invocation successfull..\n");
            }
        } catch (Exception e) {
            // print stack trace as this is a sample..
            e.printStackTrace();
        }

    }

    private static OMElement getEchoPayload(String contentType) throws XMLStreamException {
        String payload = "<echo><value>" + ECHO_STRING + "</value></echo>";

        // If the content type is "application/json/badgerfish", we
        // can have namespaces within our payload
        if (APPLICATION_JSON_BADGERFISH.equals(contentType)) {
            payload = "<echo><ns:value xmlns:ns=\"http://services.wsas.training.wso2.org\">" +
                    ECHO_STRING + "</ns:value></echo>";
        }

        // If you want to send JSON Arrays, use the following payload
        // payload = "<echo><value>Hello1</value><value>Hello2</value><value>Hello3</value></echo>";

        // return an OMElement from the payload..
        return new StAXOMBuilder(new ByteArrayInputStream(payload.getBytes())).getDocumentElement();
    }

    private static void printUsage() {
        System.out.println("\n=============== JSON Sample HELP ===============\n");
        System.out.println("Following optional parameters can be used" +
                " when running the client\n");
        System.out.println(PARAM_ENDPOINT + "\t: Endpoint URL of the service.");
        System.out.println(PARAM_CT + "\t: Content type can be set using this parameter. " +
                "Valid content types are..");
        System.out.println("        " + CT_AJ +
                "       - Content type is set as \"application/json\" ");
        System.out.println("        " + CT_AJB +
                "      - Content type is set as \"application/json/badgerfish\" \n");
    }

    private static void exitDueToInvalidArgs() {
        System.out.println("\n\nInvalid parameters. Use " + PARAM_HELP +
                " to get valid list of parameters..\n");
        System.exit(0);
    }

}
