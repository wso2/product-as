/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.appserver.sample.commodityquote.client;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.UUIDGenerator;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.Stub;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.PolicyInclude;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.RampartMessageData;
import org.apache.rampart.policy.model.CryptoConfig;
import org.apache.rampart.policy.model.RampartConfig;
import org.apache.sandesha2.client.SandeshaClientConstants;
import org.apache.sandesha2.client.SandeshaListener;
import org.apache.sandesha2.client.SequenceReport;
import org.wso2.carbon.utils.NetworkUtils;
import org.wso2.www.types.client.ArrayOfString;
import org.wso2.www.types.client.GetQuoteRequest;
import org.wso2.www.types.client.GetQuoteResponse;
import org.wso2.www.types.client.GetSymbolsResponse;
import org.wso2.www.types.client.StockQuote;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.Properties;

public class Client {
    // command line parameters
    public static final String PARAM_QOS = "-qos";
    public static final String PARAM_ENDPOINT = "-e";
    public static final String PARAM_HELP = "-help";
    public static final String QOS_VALUE_RM = "rm";
    public static final String QOS_VALUE_SECURE = "secure";
    public static final String QOS_VALUE_SECURE_RM = "securerm";

    // ==========================================================
    private static final String INVOCATION_TYPE_ASYNC = "async";
    private static final String INVOCATION_TYPE_SYNC = "sync";
    private static final String MODULE_SECURITY = "rampart";
    private static final String MODULE_RM = "sandesha2";
    private static String invocationType = null;
    private static String qosValue = null;


    static final String SECURITY_TOKEN_ERROR_STR =
            "The security token could not be authenticated or authorized. " +
            "\nPlease make sure this user is authorized to access the CommodityQuote service, or " +
            "\nthat this user has a role which is authorized to access the CommodityQuote service.";
    private BufferedReader console = null;
    private CommodityQuoteStub stub = null;
    private ConfigurationContext configurationContext = null;
    String[] operations = {"getQuote", "getSymbols"};

    private static final String wso2appserverHome = System.getProperty("wso2appserver.home");

    // ===========================================================
    private static boolean isMailEPR = false;

    public Client(BufferedReader console, EndpointReference epr) throws Exception {
        String repo = "repository";
        configurationContext =
                ConfigurationContextFactory.createConfigurationContextFromFileSystem(repo);
        this.console = console;

        if (epr != null) {
            stub = new CommodityQuoteStub(configurationContext, epr.getAddress());
        }
    }

    public void showOperations() throws IOException {
        System.out.println("\n\nPlease select your operation...");
        System.out.println("---------------------------------");

        int length = operations.length;

        int i;

        for (i = 0; i < length; i++) {
            System.out.println("(" + (i + 1) + ") " + operations[i]);
        }

        System.out.println("(" + (i + 1) + ") Exit");
    }

    private void doGetQuote() {
        System.out.println("...Getting Quote...");

        if (stub == null) {
            String msg = "CommodityQuote stub is not available";
            System.out.println(msg);
            throw new RuntimeException(msg);
        }

        System.out.print("Please enter the symbol:");

        String symbol = readOption();

        if (symbol == null) {
            System.out.println("ERROR:Invalid symbol");
            return;
        }

        GetQuoteRequest getQuoteRequest = new GetQuoteRequest();
        getQuoteRequest.setSymbol(symbol);

        try {
            if (INVOCATION_TYPE_ASYNC.equalsIgnoreCase(invocationType)) {
                // run the stub in async two channel mode
                CommodityQuoteCallbackHandlerExt callback =
                        new CommodityQuoteCallbackHandlerExt(null) {
                        };

                stub.startgetQuote(getQuoteRequest, callback);

                while (!callback.isComplete()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        handleException(e);
                    }
                }

                return;
            }

            GetQuoteResponse response = stub.getQuote(getQuoteRequest);
            StockQuote stockQuote = response.getStockQuote();

            System.out.println("\nResults");
            System.out.println("-------");
            System.out.println("Name       :" + stockQuote.getName());
            System.out.println("Symbol     :" + stockQuote.getSymbol());
            System.out.println("Price      :" + stockQuote.getPrice());
        } catch (RemoteException e) {
            handleException(e);
        }

        System.out.println("\n\n");
    }

    private void doGetSymbols() {
        System.out.println("...Getting symbols...");

        if (stub == null) {
            String msg = "CommodityQuote stub is not available";
            System.out.println(msg);
            throw new RuntimeException(msg);
        }

        if (QOS_VALUE_RM.equals(qosValue)) {
            try {
                configureRM();
            } catch (Exception e) {
                e.printStackTrace();
                 String msg = "Error occurred while configuring RM";
                throw new RuntimeException(msg, e);
            }
        }

        try {
            if (INVOCATION_TYPE_ASYNC.equalsIgnoreCase(invocationType)) {
                // run the stub in async two channel mode
                CommodityQuoteCallbackHandlerExt callback =
                        new CommodityQuoteCallbackHandlerExt(null) {
                        };

                stub.startgetSymbols(callback);

                while (!callback.isComplete()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        handleException(e);
                    }
                }

                return;
            }

            GetSymbolsResponse response = stub.getSymbols();

            System.out.println("\nResults");
            System.out.println("-------");

            ArrayOfString arr = response.get_return();
            String[] symbols = arr.getValue();

            if (symbols != null) {
                System.out.println("\n");

                int count = 1;
                int length = symbols.length;

                for (int i = 0; i < length; i++) {
                    System.out.print(symbols[i] + "    ");
                    count++;

                    if (count == 10) {
                        count = 1;
                        System.out.println("\n");
                    }
                }
            } else {
                System.out.println("No symbols available");
            }
        } catch (RemoteException e) {
            handleException(e);
        }

        System.out.println("\n\n");
    }

    private String readOption() {
        try {
            String str;
            while ((str = console.readLine()).equals("")) {
            }
            return str;
        } catch (Exception e) {
            return null;
        }
    }

    private int readIntOption() {
        int option;

        while (true) {
            String s = readOption();

            try {
                option = Integer.parseInt(s);

                return option;
            } catch (NumberFormatException e) {
                System.out.println("Please enter an integer value.");
            }
        }
    }

    private void handleException(Exception e) {
        e.printStackTrace();
        if ((e == null) || (e.getMessage() == null)) {
            System.out.println(
                    "Unknown error occurred in communicating with the server.");
            return;
        }

        if (e.getMessage().
                indexOf("The security token could not be authenticated or authorized") != -1) {
            System.err.println(SECURITY_TOKEN_ERROR_STR);
        } else {
            System.err.println("Security failure. Please refer to the CommodityQuote documentation " +
                               "and configure the CommodityQuote service properly");
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        while (true) {
            showOperations();
            System.out.print(":");

            int option = readIntOption();

            try {
                if (option == 1) {
                    doGetQuote();
                } else if (option == 2) {
                    doGetSymbols();
                } else if (option == 3) {
                    System.exit(0);
                } else {
                    System.out.println(
                            "Invalid option selected. Please select a valid option {1, 2 or 3}\n");
                }
            } catch (Exception e) {
                System.out.println("Response is faulty.");
            }
        }
    }

    private void configureRM() throws IOException {
        stub._getServiceClient().engageModule(MODULE_RM);
        stub._getServiceClient().engageModule(org.apache.axis2.Constants.MODULE_ADDRESSING);

        Options clientOptions = stub._getServiceClient().getOptions();


        String sequenceKey = UUIDGenerator.getUUID();  //sequence key for thie sequence.
        clientOptions.setProperty(SandeshaClientConstants.SEQUENCE_KEY, sequenceKey);

        clientOptions.setProperty(SandeshaClientConstants.SANDESHA_LISTENER,
                                  new SandeshaListenerImpl());

        clientOptions.setTransportInProtocol(Constants.TRANSPORT_HTTP);
        clientOptions.setUseSeparateListener(true);
        clientOptions.setProperty(SandeshaClientConstants.SANDESHA_LISTENER,
                                  new SandeshaListenerImpl());

        String offeredSequenceId = UUIDGenerator.getUUID();
        clientOptions.setProperty(SandeshaClientConstants.OFFERED_SEQUENCE_ID, offeredSequenceId);
    }

    private void configureMail() throws AxisFault {
        AxisModule addressingModule = configurationContext.getAxisConfiguration()
                .getModule(org.apache.axis2.Constants.MODULE_ADDRESSING);
        if (addressingModule != null) {
            if (!stub._getServiceClient().getAxisService().isEngaged(addressingModule.getName())) {
                stub._getServiceClient().engageModule(addressingModule.getName());
            }
        }
        Options options = stub._getServiceClient().getOptions();
        options.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        options.setTransportInProtocol(Constants.TRANSPORT_MAIL);
        options.setUseSeparateListener(true);
        Properties p = new Properties();
        p.setProperty("mail.smtp.from", "blue@localhost");
        p.setProperty("mail.smtp.host", "localhost");

        Properties pp = new Properties();
        pp.setProperty("mail.pop3.host", "localhost");
        pp.setProperty("mail.pop3.user", "blue");
        pp.setProperty("mail.store.protocol", "pop3");
        pp.setProperty("transport.mail.pop3.password", "blue");
        pp.setProperty("transport.mail.replyToAddress", "blue@localhost");
        pp.setProperty("transport.listener.interval", "3000");
//        options.setProperty(org.apache.axis2.transport.mail.Constants.MAIL_POP3,pp);
//        options.setProperty(org.apache.axis2.transport.mail.Constants.MAIL_SMTP,p);
//        options.setProperty(org.apache.axis2.transport.mail.Constants.MAIL_SYNC,Boolean.TRUE);
//
    }

    private int configureSecurity(String[] args, EndpointReference epr)
            throws IOException, XMLStreamException {
        stub._getServiceClient().engageModule(MODULE_SECURITY);
        stub._getServiceClient()
                .engageModule(org.apache.axis2.Constants.MODULE_ADDRESSING);
        String clientSSLStore = wso2appserverHome + File.separator + "repository" + File.separator +
                "resources" + File.separator + "security" + File.separator + "wso2carbon.jks";

        System.getProperties().remove("javax.net.ssl.trustStore");
        System.getProperties().remove("javax.net.ssl.trustStoreType");
        System.getProperties().remove("javax.net.ssl.trustStorePassword");

        System.setProperty("javax.net.ssl.trustStore", clientSSLStore);
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

        String serviceEPR = getParam(PARAM_ENDPOINT, args);
        if (serviceEPR == null) {
            serviceEPR = computeDefaultHttpsEPR();
        }

        int scenarioNumber;

        while (true) {
            System.out.println("Enter security scenario number [1 - 15]...");
            System.out.println(" 1. UsernameToken\n" +
                               " 2. Non-repudiation\n" +
                               " 3. Integrity \t\n" +
                               " 4. Confidentiality \n" +
                               " 5. Sign and encrypt - X509 Authentication\n" +
                               " 6. Sign and Encrypt - Anonymous clients \n" +
                               " 7. Encrypt only - Username Token Authentication \n" +
                               " 8. Sign and Encrypt - Username Token Authentication\n" +
                               " 9. SecureConversation - Sign only - Service as STS - Bootstrap policy - Sign and Encrypt , X509 Authentication\n" +
                               "10. SecureConversation - Encrypt only - Service as STS - Bootstrap policy - Sign and Encrypt , X509 Authentication\n" +
                               "11. SecureConversation - Sign and Encrypt - Service as STS - Bootstrap policy - Sign and Encrypt , X509 Authentication \n" +
                               "12. SecureConversation - Sign Only - Service as STS - Bootstrap policy - Sign and Encrypt , Anonymous clients \t\n" +
                               "13. SecureConversation - Encrypt Only - Service as STS - Bootstrap policy - Sign and Encrypt , Anonymous clients \t\n" +
                               "14. SecureConversation - Encrypt Only - Service as STS - Bootstrap policy - Sign and Encrypt , Username Token Authentication \t\n" +
                               "15. SecureConversation - Sign and Encrypt - Service as STS - Bootstrap policy - Sign and Encrypt , Username Token Authentication \n");
            String temp = readOption();
            scenarioNumber = Integer.parseInt(temp);
            try {
                if (scenarioNumber > 0 && scenarioNumber < 16) {
                    break;
                } else {
                    System.out.println("Please enter a valid security scenario number");
                }
            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid value has been entered. Please select a value between [1 - 15]");
            }
        }

        System.out.println("Selected security scenario :  " + scenarioNumber);

        if (scenarioNumber == 1) {
            // Use HTTPS EPR
            epr.setAddress(serviceEPR);
            stub._getServiceClient().getOptions().setTo(epr);
            configureUtSec(stub, scenarioNumber);
        } else if (scenarioNumber == 7 || scenarioNumber == 8 ||
                   scenarioNumber == 14 || scenarioNumber == 15) {  // All UT scenarios which involve keys
            // Use HTTP EPR WITH USER + Keystore config
            configureUtKeystoreSec(stub, scenarioNumber);
        } else {   // Scenarios only involving keys
            // Use HTTP EPR with Keystore config
            configureKeystoreSec(stub, scenarioNumber);
        }
        return scenarioNumber;
    }

    private void configureUtSec(Stub stub, int scenario) throws AxisFault, FileNotFoundException,
                                                                XMLStreamException {
        if (scenario == 1 && isMailEPR) {
            System.out.println(
                    "Username token scenario should not work with mail transport. " +
                    "\n Please load the application again");
            System.exit(0);
            return;
        }
        // username token
        RampartConfig rc = new RampartConfig();
        System.out.println("Please enter your username :");

        String username = readOption();
        rc.setUser(username);
        System.out.println("Please enter your password :");
        String password = readOption();
        PWCallback.addUser(username, password);
        rc.setPwCbClass(PWCallback.class.getName());

        Policy policy = loadPolicy(scenario);
        policy.addAssertion(rc);

        if (QOS_VALUE_SECURE_RM.equals(qosValue)) {
            //TODO added per testing securerm scenario
            stub._getServiceClient().getServiceContext().getConfigurationContext()
                    .getAxisConfiguration().getPolicyInclude().addPolicyElement(PolicyInclude.AXIS_POLICY, policy);

        }

        stub._getServiceClient().getServiceContext()
                .setProperty(RampartMessageData.KEY_RAMPART_POLICY,
                             policy);

    }

    private void configureKeystoreSec(Stub stub, int scenario) throws FileNotFoundException,
                                                                      XMLStreamException {
        System.out.println(
                "In this demonstration, client will use client.jks and server should use\n" +
                "service.jks.");
        RampartConfig rc = new RampartConfig();

        Policy policy = loadPolicy(scenario);
        rc.setUser("client");
        rc.setEncryptionUser("service");
        rc.setPwCbClass(PWCallback.class.getName());

        CryptoConfig sigCryptoConfig = new CryptoConfig();

        sigCryptoConfig.setProvider("org.apache.ws.security.components.crypto.Merlin");

        String keystore = wso2appserverHome + File.separator + "samples" +
                          File.separator + "CommodityQuote" + File.separator +
                          "keys" + File.separator + "client.jks";

        Properties prop1 = new Properties();
        prop1.put("org.apache.ws.security.crypto.merlin.keystore.type", "JKS");
        prop1.put("org.apache.ws.security.crypto.merlin.file", keystore);
        prop1.put("org.apache.ws.security.crypto.merlin.keystore.password", "testing");
        sigCryptoConfig.setProp(prop1);

        CryptoConfig encrCryptoConfig = new CryptoConfig();
        encrCryptoConfig.setProvider("org.apache.ws.security.components.crypto.Merlin");

        Properties prop2 = new Properties();

        prop2.put("org.apache.ws.security.crypto.merlin.keystore.type", "JKS");


        prop2.put("org.apache.ws.security.crypto.merlin.file", keystore);
        prop2.put("org.apache.ws.security.crypto.merlin.keystore.password", "testing");
        encrCryptoConfig.setProp(prop2);

        rc.setSigCryptoConfig(sigCryptoConfig);
        rc.setEncrCryptoConfig(encrCryptoConfig);

        policy.addAssertion(rc);

        stub._getServiceClient().getServiceContext()
                .setProperty(RampartMessageData.KEY_RAMPART_POLICY, policy);

        if (QOS_VALUE_SECURE_RM.equals(qosValue)) {
            stub._getServiceClient().getServiceContext().getConfigurationContext()
                    .getAxisConfiguration().getPolicyInclude().addPolicyElement(PolicyInclude.AXIS_POLICY, policy);
        }

    }

    private void configureUtKeystoreSec(Stub stub, int scenario)
            throws AxisFault, FileNotFoundException,
                   XMLStreamException {
        System.out.println("In this demonstration, client will use client.jks and server should use\n" +
                           "service.jks.");
        RampartConfig rc = new RampartConfig();
        Policy policy = loadPolicy(scenario);

        System.out.println("Please enter your username :");

        String username = readOption();
        rc.setUser(username);
        System.out.println("Please enter your password :");
        String password = readOption();
        PWCallback.addUser(username, password);
        rc.setPwCbClass(PWCallback.class.getName());


        rc.setUserCertAlias("client");
        rc.setEncryptionUser("service");
        rc.setPwCbClass(PWCallback.class.getName());

        CryptoConfig sigCryptoConfig = new CryptoConfig();

        sigCryptoConfig.setProvider("org.apache.ws.security.components.crypto.Merlin");

        String keystore = wso2appserverHome + File.separator + "samples" +
                          File.separator + "CommodityQuote" + File.separator +
                          "keys" + File.separator + "client.jks";

        Properties prop1 = new Properties();
        prop1.put("org.apache.ws.security.crypto.merlin.keystore.type", "JKS");
        prop1.put("org.apache.ws.security.crypto.merlin.file", keystore);
        prop1.put("org.apache.ws.security.crypto.merlin.keystore.password", "testing");
        sigCryptoConfig.setProp(prop1);

        CryptoConfig encrCryptoConfig = new CryptoConfig();
        encrCryptoConfig.setProvider("org.apache.ws.security.components.crypto.Merlin");

        Properties prop2 = new Properties();

        prop2.put("org.apache.ws.security.crypto.merlin.keystore.type", "JKS");


        prop2.put("org.apache.ws.security.crypto.merlin.file", keystore);
        prop2.put("org.apache.ws.security.crypto.merlin.keystore.password", "testing");
        encrCryptoConfig.setProp(prop2);

        rc.setSigCryptoConfig(sigCryptoConfig);
        rc.setEncrCryptoConfig(encrCryptoConfig);

        policy.addAssertion(rc);

        stub._getServiceClient().getServiceContext()
                .setProperty(RampartMessageData.KEY_RAMPART_POLICY, policy);

        if (QOS_VALUE_SECURE_RM.equals(qosValue)) {
            stub._getServiceClient().getServiceContext().getConfigurationContext()
                    .getAxisConfiguration().getPolicyInclude().addPolicyElement(PolicyInclude.AXIS_POLICY, policy);
        }

    }

    private static Policy loadPolicy(int scenario) throws FileNotFoundException,
                                                          XMLStreamException {
        StAXOMBuilder builder =
                new StAXOMBuilder(wso2appserverHome + File.separator + "samples" + File.separator + "conf" + File.separator +
                                  "rampart" + File.separator + "scenario" + scenario +
                                  "-policy.xml");
        return PolicyEngine.getPolicy(builder.getDocumentElement());
    }

    private void configureSecureRM(String[] args,
                                   EndpointReference epr) throws IOException, XMLStreamException {
        int secScenarioNumber = this.configureSecurity(args, epr);
        if (secScenarioNumber == 1 || secScenarioNumber == 9) {
            System.err.println("Secure-RM not supported for scenarios 1 & 9 since HTTPS is required " +
                               "on the client side receiver. This is a limitation of the client.");
            System.exit(1);
        }
        this.configureRM();
    }

    public static void main(String[] args) {
        // check whether the user is asking for help
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (PARAM_HELP.equalsIgnoreCase(arg)) {
                printUsage();
                System.exit(0);
            }
        }

        String serviceEPR = getParam(PARAM_ENDPOINT, args);
        if (serviceEPR == null) {
            serviceEPR = computeDefaultHttpEPR();
        }

        if (serviceEPR != null) {
            if (serviceEPR.indexOf(org.apache.axis2.Constants.TRANSPORT_MAIL) > -1) {
                isMailEPR = true;
            }
        }

        String qos = getParam(PARAM_QOS, args);
        qosValue = qos;

        try {
            if ((qos != null) && !qos.equalsIgnoreCase(QOS_VALUE_RM) &&
                !qos.equalsIgnoreCase(QOS_VALUE_SECURE) &&
                !qos.equalsIgnoreCase(QOS_VALUE_SECURE_RM)) {
                System.out.println(
                        "ERROR : You have given an invalid value as the 'qos' parameter\n");
                printUsage();
                System.exit(0);
            }

            if (QOS_VALUE_RM.equalsIgnoreCase(qos) ||
                QOS_VALUE_SECURE_RM.equalsIgnoreCase(qos)) {
                invocationType = INVOCATION_TYPE_ASYNC;
            } else if (isMailEPR) {
                invocationType = INVOCATION_TYPE_ASYNC;
            } else {
                invocationType = INVOCATION_TYPE_SYNC;
            }

            EndpointReference epr = new EndpointReference(serviceEPR);
            BufferedReader console = new BufferedReader(new InputStreamReader(
                    System.in));

            System.out.println("COMMODITY QUOTE SAMPLE CLIENT");
            System.out.println("=============================\n");

            Client client = new Client(console, epr);

            if (QOS_VALUE_RM.equalsIgnoreCase(qos)) {
                client.configureRM();
            } else if (QOS_VALUE_SECURE.equalsIgnoreCase(qos)) {
                client.configureSecurity(args, epr);
            } else if (QOS_VALUE_SECURE_RM.equalsIgnoreCase(qos)) {
                client.configureSecureRM(args, epr);
            }

            if (isMailEPR) {
                client.configureMail();
            }

            System.out.println("Sample will be invoked using following parameters ..");
            System.out.println("CommodityQuoteService Endpoint reference   : " + epr.getAddress());

            if (qos != null) {
                System.out.println("Quality of Service                : " + qos);
            }

            client.start();
        } catch (Throwable e) {
            e.printStackTrace();
            if ((e == null) || (e.getMessage() == null)) {
                System.out.println(
                        "Unknown error occurred in communicating with the server.");
                return;
            }

            if (e.getMessage().indexOf(SECURITY_TOKEN_ERROR_STR) != -1) {
                System.out.println(SECURITY_TOKEN_ERROR_STR);
            } else {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * This will check the given parameter in the array and will return, if
     * available
     *
     * @param param
     * @param args
     * @return String
     */
    private static String getParam(String param, String[] args) {
        if ((param == null) || "".equals(param)) {
            return null;
        }

        for (int i = 0; i < args.length; i = i + 2) {
            String arg = args[i];
            if (param.equalsIgnoreCase(arg) && (args.length >= (i + 1))) {
                if (args.length == i + 1) {
                    System.err.println("Invalid value specified for option " + arg);
                    printUsage();
                    System.exit(1);
                }
                return args[i + 1];
            }
        }
        return null;
    }

    private static void printUsage() {
        System.out.println("\n============ HELP =============\n");
        System.out.println(
                "Following optional parameters can also be given when running the client\n\n");
        System.out.println(PARAM_QOS +
                           "       : One can give the qos parameters with this. Available qos");
        System.out.println("             parameters are ");
        System.out.println("                 " + QOS_VALUE_RM +
                           "       - enables Reliable Messaging with Apache Sandesha2 ");
        System.out.println("                 " + QOS_VALUE_SECURE +
                           "   - enables WS-Security with Apache Rampart ");
        System.out.println("                 " + QOS_VALUE_SECURE_RM +
                           " - enables both RM and WS-Security\n");
        System.out.println(PARAM_ENDPOINT +
                           "       : endpoint url of the CommodityQuote service");
    }

    private static class SandeshaListenerImpl implements SandeshaListener {
        public void onError(AxisFault fault) {
            System.out.println("ERROR:" + fault.getMessage());
        }

        public void onTimeOut(SequenceReport report) {
            System.out.println("ERROR: RM Sequence timed out");
        }
    }

    private static String computeDefaultHttpEPR() {
        FileInputStream in = null;
        try {
            Properties props = new Properties();
            String defEPRProperties = wso2appserverHome + File.separator + "samples" +
                                      File.separator + "CommodityQuote" + File.separator +
                                      "conf" + File.separator + "default_epr.properties";
            in = new FileInputStream(defEPRProperties);
            props.load(in);
            return props.getProperty("http.epr").
                    replaceAll("@hostname@", NetworkUtils.getLocalHostname());
        } catch (IOException e) {
            System.err.println(
                    "[ERROR] default_epr.properties not found in conf directory & " +
                    "the HTTP service EPR has not been specified.");
            System.exit(1);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String computeDefaultHttpsEPR() {
        FileInputStream in = null;
        try {
            Properties props = new Properties();
            String defEPRProperties = wso2appserverHome + File.separator + "samples" +
                                      File.separator + "CommodityQuote" + File.separator +
                                      "conf" + File.separator + "default_epr.properties";
            in = new FileInputStream(defEPRProperties);
            props.load(in);
            return props.getProperty("https.epr").
                    replaceAll("@hostname@", NetworkUtils.getLocalHostname());
        } catch (IOException e) {
            System.err.println(
                    "[ERROR] default_epr.properties not found in conf directory & " +
                    "the HTTP service EPR has not been specified.");
            System.exit(1);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
