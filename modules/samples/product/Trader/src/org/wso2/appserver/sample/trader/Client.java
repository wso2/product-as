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

package org.wso2.appserver.sample.trader;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.UUIDGenerator;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.RampartMessageData;
import org.apache.rampart.policy.model.RampartConfig;
import org.apache.sandesha2.client.SandeshaClientConstants;
import org.apache.sandesha2.client.SandeshaListener;
import org.apache.sandesha2.client.SequenceReport;
import org.wso2.appserver.sample.exchange.client.ExchangeClientCallbackHandlerExt;
import org.wso2.appserver.sample.exchange.client.ExchangeClientStub;
import org.wso2.appserver.sample.trader.client.TraderClientCallbackHandlerExt;
import org.wso2.appserver.sample.trader.client.TraderClientStub;
import org.wso2.carbon.utils.NetworkUtils;
import org.wso2.www.types.exchange.client.GetInfoRequest;
import org.wso2.www.types.exchange.client.GetInfoResponse;
import org.wso2.www.types.exchange.client.MarketInfo;
import org.wso2.www.types.trader.client.ArrayOfString;
import org.wso2.www.types.trader.client.BuyRequest;
import org.wso2.www.types.trader.client.BuyResponse;
import org.wso2.www.types.trader.client.ClientInfo;
import org.wso2.www.types.trader.client.CreateAccountRequest;
import org.wso2.www.types.trader.client.CreateAccountResponse;
import org.wso2.www.types.trader.client.DepositRequest;
import org.wso2.www.types.trader.client.DepositResponse;
import org.wso2.www.types.trader.client.GetPortfolioRequest;
import org.wso2.www.types.trader.client.GetPortfolioResponse;
import org.wso2.www.types.trader.client.GetQuoteRequest;
import org.wso2.www.types.trader.client.GetQuoteResponse;
import org.wso2.www.types.trader.client.GetSymbolsRequest;
import org.wso2.www.types.trader.client.GetSymbolsResponse;
import org.wso2.www.types.trader.client.PortFolio;
import org.wso2.www.types.trader.client.PortFolioItem;
import org.wso2.www.types.trader.client.SellRequest;
import org.wso2.www.types.trader.client.SellResponse;
import org.wso2.www.types.trader.client.StockQuote;
import org.wso2.www.types.trader.client.TradeStatus;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


public class Client {
    // command line parameters
    public static final String PARAM_QOS = "-qos";
    public static final String PARAM_EXCHANGE_ENDPOINT = "-ee";
    public static final String PARAM_TRADER_ENDPOINT = "-te";
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
    private static final String SECURITY_TOKEN_ERROR_STR =
            "The security token could not be authenticated or authorized. " +
            "\nPlease make sure this user is authorized to access the TraderClient service " +
            "and ExchageClient service, or " +
            "\nthat this user has a role which is authorized to access the Trader service " +
            "and ExchageClient service.";
    private static final String[] operations = {"createAccount",
                                                "getQuote",
                                                "getSymbols",
                                                "deposit",
                                                "buy",
                                                "sell",
                                                "getPortFolio",
                                                "getMarketInfo"};

    private BufferedReader console = null;
    private TraderClientStub traderStub = null;
    private ExchangeClientStub exchangeStub = null;
    private ConfigurationContext configurationContext = null;
    private SandeshaListnerImpl sandeshaListener;

    private static String traderServiceEPR;
    private static String exchangeServiceEPR;
    private static final String wso2appserverHome = System.getProperty("wso2appserver.home");

    public Client(BufferedReader console,
                  EndpointReference trader,
                  EndpointReference exchange) throws Exception {
        String repo = "repository";
        configurationContext =
                ConfigurationContextFactory.createConfigurationContextFromFileSystem(repo);

        AxisConfiguration axisConfiguration = configurationContext.getAxisConfiguration();
        Map services = axisConfiguration.getServices();
        ArrayList serviceNames = new ArrayList();
        Iterator ssitr = services.keySet().iterator();

        while (ssitr.hasNext()) {
            String s = (String) ssitr.next();
            serviceNames.add(s);
        }

        for (int i = 0; i < serviceNames.size(); i++) {
            String s = (String) serviceNames.get(i);
            axisConfiguration.removeService(s);
        }

        this.console = console;
        if (trader != null) {
            traderStub = new TraderClientStub(configurationContext,
                                              trader.getAddress());
        }

        if (exchange != null) {
            exchangeStub = new ExchangeClientStub(configurationContext,
                                                  exchange.getAddress());
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

    public void start() throws IOException {
        while (true) {
            showOperations();
            System.out.print(":");

            int option = readIntOption();

            if (option == 1) {
                doCreateAccount();
            } else if (option == 2) {
                doGetQuote();
            } else if (option == 3) {
                doGetSymbols();
            } else if (option == 4) {
                doDeposit();
            } else if (option == 5) {
                doBuy();
            } else if (option == 6) {
                doSell();
            } else if (option == 7) {
                doGetPortFolio();
            } else if (option == 8) {
                doGetMarketInfo();
            } else if (option == 9) {
                System.exit(0);
            } else {
                System.out.println(
                        "Invalid option selected. Please select a valid option in the range [1 to 9]\n");
            }
        }
    }

    private void doCreateAccount() {
        System.out.println("...Creating account...");

        if (traderStub == null) {
            System.out.println("Trade stub is not available");
        }

        System.out.print("Please enter the Name:");

        String name = readOption();

        if (name == null) {
            System.out.println("ERROR:Invalid Username");

            return;
        }

        System.out.print("Please enter the password:");

        String password = readOption();

        if (password == null) {
            System.out.println("ERROR:Invalid password");

            return;
        }

        CreateAccountRequest createAccountRequest = new CreateAccountRequest();
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setName(name);
        clientInfo.setSsn("NotNeeded");
        createAccountRequest.setClientinfo(clientInfo);
        createAccountRequest.setPassword(password);

        try {
            if (INVOCATION_TYPE_ASYNC.equalsIgnoreCase(invocationType)) {
                // run the stub in async two channel mode
                TraderClientCallbackHandlerExt callback = new TraderClientCallbackHandlerExt(null);
                traderStub.startcreateAccount(createAccountRequest, callback);
                waitForCompletion(callback);

                return;
            }

            CreateAccountResponse response = traderStub.createAccount(createAccountRequest);

            System.out.println("\nResults");
            System.out.println("-------");
            System.out.println("User ID       :" + response.getUserid());
        } catch (RemoteException e) {
            handleException(e);
        }

        System.out.println("\n\n");
    }

    private void doGetQuote() {
        System.out.println("...Getting Quote...");

        if (traderStub == null) {
            System.out.println("Trade stub is not available");
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
                TraderClientCallbackHandlerExt callback = new TraderClientCallbackHandlerExt(null) {
                };

                // run the stub in async two channel mode
                traderStub.startgetQuote(getQuoteRequest, callback);
                waitForCompletion(callback);

                return;
            }

            GetQuoteResponse response = traderStub.getQuote(getQuoteRequest);
            StockQuote stockQuote = response.getStock_quote();

            System.out.println("\nResults");
            System.out.println("-------");
            System.out.println("Name       :" + stockQuote.getName());
            System.out.println("Symbol     :" + stockQuote.getSymbol());
            System.out.println("High value :" + stockQuote.getHigh());
            System.out.println("Low value  :" + stockQuote.getLow());
            System.out.println("Price      :" + stockQuote.getPrice());
        } catch (RemoteException e) {
            handleException(e);
        }

        System.out.println("\n\n");
    }

    private void handleException(Exception e) {
        if ((e == null) || (e.getMessage() == null)) {
            System.out.println(
                    "Unknown error occurred in communicating with the server.");

            return;
        }

        if (e.getMessage().indexOf("The security token could not be authenticated or authorized") !=
            -1) {
            System.err.println(SECURITY_TOKEN_ERROR_STR);
        } else
        if (e.getMessage().indexOf("Request does not contain required Security header") != -1) {
            System.err.println(
                    "Required security header not found in request.\n" +
                    " Username Token Authentication has been enabled, " +
                    "\nand security module has been engaged with the Trader service. " +
                    "\nPlease use the 'security QoS parameter, and run the client again.'");
        } else {
            System.err.println(e.getMessage());
        }
    }

    private void doGetSymbols() {
        System.out.println("...Getting symbols...");

        if (traderStub == null) {
            System.out.println("Trade stub is not available");
        }

        GetSymbolsRequest request = new GetSymbolsRequest();

        try {
            if (INVOCATION_TYPE_ASYNC.equalsIgnoreCase(invocationType)) {
                // run the stub in async two channel mode
                TraderClientCallbackHandlerExt callback = new TraderClientCallbackHandlerExt(null) {
                };

                traderStub.startgetSymbols(request, callback);
                waitForCompletion(callback);

                return;
            }

            GetSymbolsResponse response = traderStub.getSymbols(request);

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

    private void doDeposit() {
        System.out.println("...Doing deposit...");

        if (traderStub == null) {
            System.out.println("Trade stub is not available");
        }

        System.out.print("Please enter the username:");

        String username = readOption();

        if (username == null) {
            System.out.println("ERROR:Invalid username");

            return;
        }

        System.out.print("Please enter the password:");

        String password = readOption();

        if (password == null) {
            System.out.println("ERROR:Invalid password");

            return;
        }

        System.out.print("Please enter the amount:");

        float amount = readFloatOption();

        if (amount <= 0) {
            System.out.println("ERROR:Invalid amount");

            return;
        }

        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setUseridr(username);
        depositRequest.setPassword(password);
        depositRequest.setAmount(amount);

        try {
            if (INVOCATION_TYPE_ASYNC.equalsIgnoreCase(invocationType)) {
                // run the stub in async two channel mode
                TraderClientCallbackHandlerExt callback = new TraderClientCallbackHandlerExt(null) {
                };

                traderStub.startdeposit(depositRequest, callback);
                waitForCompletion(callback);

                return;
            }

            DepositResponse depositResponse = traderStub.deposit(depositRequest);
            System.out.println(depositResponse.getDepositStatus());
        } catch (RemoteException e) {
            handleException(e);
        }

        System.out.println("\n\n");
    }

    private void doBuy() {
        System.out.println("...Buying stocks...");

        if (traderStub == null) {
            System.out.println("Trade stub is not available");
        }

        System.out.print("Please enter the username:");

        String username = readOption();

        if (username == null) {
            System.out.println("ERROR:Invalid value");

            return;
        }

        System.out.print("Please enter the password:");

        String password = readOption();

        if (password == null) {
            System.out.println("ERROR:Invalid value");

            return;
        }

        System.out.print("Please enter the symbol:");

        String symbol = readOption();

        if (symbol == null) {
            System.out.println("ERROR:Invalid value");

            return;
        }

        System.out.print("Please enter the quantity:");

        int quantity = readIntOption();

        if (quantity <= 0) {
            System.out.println("ERROR:Invalid value");

            return;
        }

        BuyRequest buyRequest = new BuyRequest();
        buyRequest.setUserid(username);
        buyRequest.setPassword(password);
        buyRequest.setSymbol(symbol);
        buyRequest.setQty(quantity);

        try {
            if (INVOCATION_TYPE_ASYNC.equalsIgnoreCase(invocationType)) {
                // run the stub in async two channel mode
                TraderClientCallbackHandlerExt callback = new TraderClientCallbackHandlerExt(null);
                traderStub.startbuy(buyRequest, callback);
                waitForCompletion(callback);

                return;
            }

            System.out.println("\nResults");
            System.out.println("-------");

            BuyResponse buyResponse = traderStub.buy(buyRequest);
            TradeStatus tradeStatus = buyResponse.getTrade_status();

            System.out.println("Status :" + tradeStatus.getStatus());
            System.out.println("Reason :" + tradeStatus.getReason());
        } catch (RemoteException e) {
            handleException(e);
        }

        System.out.println("\n\n");
    }

    private void doSell() {
        System.out.println("...Selling stocks...");

        if (traderStub == null) {
            System.out.println("Trade stub is not available");
        }

        System.out.print("Please enter the username:");

        String username = readOption();

        if (username == null) {
            System.out.println("ERROR:Invalid value");

            return;
        }

        System.out.print("Please enter the password:");

        String password = readOption();

        if (password == null) {
            System.out.println("ERROR:Invalid value");

            return;
        }

        System.out.print("Please enter the symbol:");

        String symbol = readOption();

        if (symbol == null) {
            System.out.println("ERROR:Invalid value");

            return;
        }

        System.out.print("Please enter the quantity:");

        BigInteger quantity = readBigIntegerOption();

        SellRequest sellRequest = new SellRequest();
        sellRequest.setUserid(username);
        sellRequest.setPassword(password);
        sellRequest.setSymbol(symbol);
        sellRequest.setQty(quantity);

        try {
            if (INVOCATION_TYPE_ASYNC.equalsIgnoreCase(invocationType)) {
                // run the stub in async two channel mode
                TraderClientCallbackHandlerExt callback = new TraderClientCallbackHandlerExt(null) {
                };

                traderStub.startsell(sellRequest, callback);
                waitForCompletion(callback);

                return;
            }

            System.out.println("\nResults");
            System.out.println("---------");

            SellResponse sellResponse = traderStub.sell(sellRequest);
            TradeStatus tradeStatus = sellResponse.getTrade_status();

            System.out.println("Status :" + tradeStatus.getStatus());
            System.out.println("Reason :" + tradeStatus.getReason());
        } catch (RemoteException e) {
            handleException(e);
        }

        System.out.println("\n\n");
    }

    private void doGetPortFolio() {
        System.out.println("...Getting Portfolio...");

        if (traderStub == null) {
            System.out.println("Trade stub is not available");
        }

        System.out.print("Please enter the username:");

        String username = readOption();

        if (username == null) {
            System.out.println("ERROR:Invalid value");

            return;
        }

        System.out.print("Please enter the password:");

        String password = readOption();

        if (password == null) {
            System.out.println("ERROR:Invalid value");

            return;
        }

        GetPortfolioRequest getPortfolioRequest = new GetPortfolioRequest();
        getPortfolioRequest.setUserid(username);
        getPortfolioRequest.setPassword(password);

        try {
            if (INVOCATION_TYPE_ASYNC.equalsIgnoreCase(invocationType)) {
                // run the stub in async two channel mode
                TraderClientCallbackHandlerExt callback = new TraderClientCallbackHandlerExt(null) {
                };

                traderStub.startgetPortfolio(getPortfolioRequest, callback);
                waitForCompletion(callback);

                return;
            }

            System.out.println("\nResults");
            System.out.println("-------");

            GetPortfolioResponse getPortfolioResponse =
                    traderStub.getPortfolio(getPortfolioRequest);
            PortFolio portFolio = getPortfolioResponse.getPortFolio();
            PortFolioItem[] portFolioItems = portFolio.getPortFolioItem();

            if (portFolioItems != null) {
                int length = portFolioItems.length;

                if (length == 1 && portFolioItems[0].getSymbol().equalsIgnoreCase("Invalid")) {
                    System.out.println("Username and/or password invalid!");
                } else if (length == 1 && portFolioItems[0].getSymbol().equalsIgnoreCase("Empty")) {
                    System.out.println("No portfolio items are available");
                } else {

                    for (int i = 0; i < length; i++) {
                        PortFolioItem portFolioItem = portFolioItems[i];
                        System.out.println("Symbol:" + portFolioItem.getSymbol() +
                                           "     Amount:" + portFolioItem.getAmount());
                    }
                }
            } else {
                System.out.println("No portfolio items are available");
            }
        } catch (RemoteException e) {
            handleException(e);
        }

        System.out.println("\n\n");
    }

    private void doGetMarketInfo() {
        System.out.println("...Getting market information...");

        if (exchangeStub == null) {
            System.err.println("Exchange stub is not available");
            return;
        }

        GetInfoRequest getInfoRequest = new GetInfoRequest();

        try {
            if (INVOCATION_TYPE_ASYNC.equalsIgnoreCase(invocationType)) {
                // run the stub in async two channel mode
                ExchangeClientCallbackHandlerExt callback =
                        new ExchangeClientCallbackHandlerExt(null);
                exchangeStub.startgetInfo(getInfoRequest, callback);
                waitForCompletion(callback);

                exchangeStub._getServiceClient().getOptions()
                        .setProperty("Sandesha2ClientAPIPropertySequenceKey",
                                     "sequence1");

                return;
            }

            System.out.println("\nResults");
            System.out.println("-------");

            GetInfoResponse getInfoResponse = exchangeStub.getInfo(getInfoRequest);
            MarketInfo marketInfo = getInfoResponse.getMarketInfo();

            System.out.println("Average price          :" +
                               marketInfo.getAvgPrice());
            System.out.println("Average price of trade :" +
                               marketInfo.getAvgPriceOfTrade());
            System.out.println("Market cap             :" +
                               marketInfo.getMarketCap());
            System.out.println("Total trader           :" +
                               marketInfo.getTotalTraded());
            System.out.println("\n\n\n");
        } catch (RemoteException e) {
            handleException(e);
        }

        System.out.println("\n\n");
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

    private float readFloatOption() {
        float option;

        while (true) {
            String s = readOption();

            try {
                option = Float.parseFloat(s);

                return option;
            } catch (NumberFormatException e) {
                System.out.println("Please enter an float value.");
            }
        }
    }

    private BigInteger readBigIntegerOption() {

        while (true) {
            String s = readOption();

            try {
                return new BigInteger(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter an valid number (BigInteger)");
            }
        }
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

    private void configureRM() throws AxisFault, IOException {
        traderStub._getServiceClient().engageModule(new QName(MODULE_RM));
        exchangeStub._getServiceClient().engageModule(new QName(MODULE_RM));
        traderStub._getServiceClient().engageModule(new QName(
                org.apache.axis2.Constants.MODULE_ADDRESSING));
        exchangeStub._getServiceClient().engageModule(new QName(
                org.apache.axis2.Constants.MODULE_ADDRESSING));

        Options clientOptions = traderStub._getServiceClient().getOptions();

        String sequenceKey = UUIDGenerator.getUUID();  //sequence key for thie sequence.
        clientOptions.setProperty(SandeshaClientConstants.SEQUENCE_KEY, sequenceKey);

        clientOptions.setTransportInProtocol(Constants.TRANSPORT_HTTP);
        clientOptions.setUseSeparateListener(true);
        sandeshaListener = new SandeshaListnerImpl();
        clientOptions.setProperty(SandeshaClientConstants.SANDESHA_LISTENER,
                                  sandeshaListener);

        //configuring the exchange stub
        clientOptions = exchangeStub._getServiceClient().getOptions();

        clientOptions.setProperty(SandeshaClientConstants.AcksTo,
                                  exchangeStub._getServiceClient()
                                          .getMyEPR(Constants.TRANSPORT_HTTP)
                                          .getAddress());
        sequenceKey = UUIDGenerator.getUUID();
        clientOptions.setProperty(SandeshaClientConstants.SEQUENCE_KEY,
                                  sequenceKey);

        clientOptions.setTransportInProtocol(Constants.TRANSPORT_HTTP);
        clientOptions.setUseSeparateListener(true);

        sandeshaListener = new SandeshaListnerImpl();
        clientOptions.setProperty(SandeshaClientConstants.SANDESHA_LISTENER,
                                  sandeshaListener);
    }

    private void configureSecurity() throws AxisFault, IOException, XMLStreamException {

        String clientSSLStore = wso2appserverHome + File.separator + "repository" + File.separator +
                "resources" + File.separator + "security" + File.separator + "wso2carbon.jks";

        System.getProperties().remove("javax.net.ssl.trustStore");
        System.getProperties().remove("javax.net.ssl.trustStoreType");
        System.getProperties().remove("javax.net.ssl.trustStorePassword");

        System.setProperty("javax.net.ssl.trustStore", clientSSLStore);
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

        traderStub._getServiceClient().engageModule(new QName(MODULE_SECURITY));
        traderStub._getServiceClient()
                .engageModule(new QName(org.apache.axis2.Constants.MODULE_ADDRESSING));
        System.out.println("Enabling UsernameToken authentication scenario\n");

        // username token
        RampartConfig rc = new RampartConfig();
        System.out.println("Please enter your username : ");

        String username = readOption();
        rc.setUser(username);
        System.out.println("Please enter your password : ");
        String password = readOption();
        traderStub._getServiceClient().getOptions().setUserName(username);
        traderStub._getServiceClient().getOptions().setPassword(password);
        Policy policy = loadPolicy(1);
        policy.addAssertion(rc);


        traderStub._getServiceClient().getServiceContext()
                .setProperty(RampartMessageData.KEY_RAMPART_POLICY,
                             policy);


        exchangeStub._getServiceClient().engageModule(new QName(MODULE_SECURITY));
        exchangeStub._getServiceClient()
                .engageModule(new QName(org.apache.axis2.Constants.MODULE_ADDRESSING));
        exchangeStub._getServiceClient().getOptions().setUserName(username);
        exchangeStub._getServiceClient().getOptions().setPassword(password);
        exchangeStub._getServiceClient().getServiceContext()
                .setProperty(RampartMessageData.KEY_RAMPART_POLICY,
                             policy);
    }


    private static Policy loadPolicy(int scenario) throws FileNotFoundException,
                                                          XMLStreamException {
        StAXOMBuilder builder =
                new StAXOMBuilder(wso2appserverHome + File.separator + "samples" + File.separator + "conf" + File.separator +
                                  "rampart" + File.separator + "scenario" + scenario +
                                  "-policy.xml");
        return PolicyEngine.getPolicy(builder.getDocumentElement());
    }

    private void configureSecureRM() throws IOException, XMLStreamException {
        this.configureSecurity();
        this.configureRM();
    }

    public static void main(String[] args) {
        // check whether the user is asking for help
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (PARAM_HELP.equalsIgnoreCase(arg)) {
                printUsage();
                System.exit(0);
            } else if (arg.startsWith("-") && !isParameter(arg)) {
                System.out.println("Invalid parameter " + arg);
                printUsage();
                System.exit(0);
            }
        }
        // init the default values
        Properties props = new Properties();

        try {
            String defEPRProperties = wso2appserverHome + File.separator + "samples" +
                                      File.separator + "Trader" + File.separator +
                                      "conf" + File.separator + "default_epr.properties";
            props.load(new FileInputStream(defEPRProperties));
            if (traderServiceEPR == null) {
                traderServiceEPR =
                        props.getProperty("trader.http.epr").
                                replaceAll("@hostname@",
                                           NetworkUtils.getLocalHostname());
            }
            if (exchangeServiceEPR == null) {
                exchangeServiceEPR =
                        props.getProperty("exchange.http.epr").
                                replaceAll("@hostname@",
                                           NetworkUtils.getLocalHostname());
            }
        } catch (IOException e) {
            System.err.println(
                    "[WARN] default_epr.properties not found in conf directory");
        }

        // first the get the value to a temp string. If its not null then override that with the
        // default values
        String tempValue = getParam(PARAM_TRADER_ENDPOINT, args);

        if (tempValue != null) {
            traderServiceEPR = tempValue;
        }

        tempValue = getParam(PARAM_EXCHANGE_ENDPOINT, args);

        if (tempValue != null) {
            exchangeServiceEPR = tempValue;

            // set the exchange service url as a system property so that trader can retrieve it later
            System.setProperty(Trader.EXCHANGE_SERVICE_ENDPORT_ADDRESS,
                               exchangeServiceEPR);
        }

        String qos = getParam(PARAM_QOS, args);

        try {
            if ((qos != null) && !qos.equalsIgnoreCase(QOS_VALUE_RM) &&
                !qos.equalsIgnoreCase(QOS_VALUE_SECURE) &&
                !qos.equalsIgnoreCase(QOS_VALUE_SECURE_RM)) {
                System.out.println(
                        "ERROR : You have given an invalid value as the 'qos' parameter\n");
                printUsage();
                System.exit(0);
            }

            if (QOS_VALUE_SECURE.equals(qos) || QOS_VALUE_SECURE_RM.equals(qos)) {
                tempValue = getParam(PARAM_TRADER_ENDPOINT, args);
                if (tempValue == null) {
                    traderServiceEPR =
                            props.getProperty("trader.https.epr").
                                    replaceAll("@hostname@",
                                               NetworkUtils.getLocalHostname());
                }
                tempValue = getParam(PARAM_EXCHANGE_ENDPOINT, args);
                if (tempValue == null) {
                    exchangeServiceEPR =
                            props.getProperty("exchange.https.epr").
                                    replaceAll("@hostname@",
                                               NetworkUtils.getLocalHostname());
                }
            }

            System.out.println("Using following parameters");
            System.out.println("TraderService Endpoint reference   : " + traderServiceEPR);
            System.out.println("ExchangeService Endpoint reference : " + exchangeServiceEPR);

            if (qos != null) {
                System.out.println("QOS                 : " + qos);
            }

            if (QOS_VALUE_RM.equalsIgnoreCase(qos) ||
                QOS_VALUE_SECURE_RM.equalsIgnoreCase(qos)) {
                invocationType = INVOCATION_TYPE_ASYNC;
            } else {
                invocationType = INVOCATION_TYPE_SYNC;
            }

            EndpointReference trader = new EndpointReference(traderServiceEPR);
            EndpointReference exchange = new EndpointReference(exchangeServiceEPR);
            BufferedReader console = new BufferedReader(new InputStreamReader(
                    System.in));

            System.out.println("STARTING TRADE SAMPLE CLIENT");
            System.out.println("=============================\n");

            Client client = new Client(console, trader, exchange);

            if (QOS_VALUE_RM.equalsIgnoreCase(qos)) {
                client.configureRM();
            } else if (QOS_VALUE_SECURE.equalsIgnoreCase(qos)) {
                client.configureSecurity();
            } else if (QOS_VALUE_SECURE_RM.equalsIgnoreCase(qos)) {
                client.configureSecureRM();
            }

            client.start();
        } catch (Throwable e) {
            if ((e == null) || (e.getMessage() == null)) {
                System.out.println(
                        "Error occurred when communicating with the server.");

                return;
            }

            if (e.getMessage().indexOf(SECURITY_TOKEN_ERROR_STR) != -1) {
                System.out.println(SECURITY_TOKEN_ERROR_STR);
            } else {
                System.out.println(e.getMessage());
            }

        }
    }

    private static boolean isParameter(String arg) {
        return PARAM_EXCHANGE_ENDPOINT.equalsIgnoreCase(arg) ||
               PARAM_HELP.equalsIgnoreCase(arg) || PARAM_QOS.equalsIgnoreCase(arg) ||
               PARAM_TRADER_ENDPOINT.equalsIgnoreCase(arg);
    }

    private void waitForCompletion(TraderClientCallbackHandlerExt callback) {
        boolean sandeshaCheck = false;

        while (!sandeshaCheck && !callback.isComplete()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                handleException(e);
            }

            sandeshaCheck = ((sandeshaListener != null) &&
                             (sandeshaListener.isError || sandeshaListener.isTimeOut));
        }
    }

    private void waitForCompletion(ExchangeClientCallbackHandlerExt callback) {
        boolean sandeshaCheck = false;

        while (!sandeshaCheck && !callback.isComplete()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                handleException(e);
            }

            sandeshaCheck = ((sandeshaListener != null) &&
                             (sandeshaListener.isError || sandeshaListener.isTimeOut));
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
        System.out.println(PARAM_TRADER_ENDPOINT +
                           "       : endpoint url of the trader service");
        System.out.println(PARAM_EXCHANGE_ENDPOINT +
                           "       : endpoint url of the exchange. This will only be used in client-exchange interaction.");
    }

    private static class SandeshaListnerImpl implements SandeshaListener {
        public boolean isTimeOut;
        public boolean isError;

        public SandeshaListnerImpl() {
        }

        public void onError(AxisFault fault) {
            System.out.println("ERROR:" + fault.getMessage());
            isError = true;
        }

        public void onTimeOut(SequenceReport report) {
            System.out.println("ERROR: RM Sequence timed out");
            isTimeOut = true;
        }
    }
}
