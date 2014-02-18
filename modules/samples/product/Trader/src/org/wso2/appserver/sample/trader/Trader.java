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

import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.sandesha2.client.SandeshaClientConstants;
import org.wso2.appserver.sample.exchange.trader.ExchangeTraderStub;
import org.wso2.appserver.sample.utils.Utils;
import org.wso2.www.types.exchange.trader.*;
import org.wso2.www.types.trader.client.service.StockQuote;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Trader {
    public final static String EXCHANGE_SERVICE_ENDPORT_ADDRESS = "exchangeEndpointAddress";
    private static Trader ourInstance;

    private String myEPR;
    private String exchangeEPR;
    private String httpPort;

    private Map stockMarket;
    private Map clientDetails;

    private Trader() {

        httpPort = System.getProperty("carbon.http.port");

        httpPort = (httpPort == null) ? "9763" : httpPort;

        myEPR = "http://127.0.0.1:" + httpPort + "/services/TraderClient";
        exchangeEPR = "http://127.0.0.1:" + httpPort + "/services/ExchangeTrader";

        stockMarket = Utils.getPrePopulatedStockMarket(Utils.TRADER_CLIENT);
        clientDetails = Utils.getPrePopulatedClientList();

    }

    public static Trader getInstance() {
        if (ourInstance == null) {
            ourInstance = new Trader();
        }

        return ourInstance;
    }

    public void setExchangeEPR(String serviceContextPath) throws SocketException {
        exchangeEPR = "http://localhost:" + httpPort + serviceContextPath + "/ExchangeTrader";
    }

    public void setExchangeEPR(String serviceContextPath,
                               String serviceName) throws SocketException {
        exchangeEPR = "http://localhost:" + httpPort + serviceContextPath + "/" + serviceName;
    }

    public Trader createTraderWithExchangeEPR(String serviceContextPath) throws SocketException {
        ourInstance.setExchangeEPR(serviceContextPath);
        return ourInstance;
    }

    public Trader createTraderWithExchangeEPR(String serviceContextPath,
                                              String serviceName) throws SocketException {
        ourInstance.setExchangeEPR(serviceContextPath, serviceName);
        return ourInstance;
    }

    public void updateInformation(String symbol, Float price) {
        if (symbol != null &&
                SymbolTable.getInstance().isSymbolAvailable(symbol)) {
            stockMarket.put(symbol, price);
        }
    }

    public StockQuote getQuote(String symbol) {
        StockQuote stockQuote = (StockQuote) stockMarket.get(symbol);

        if (stockQuote == null) {
            stockQuote = new StockQuote();
            stockQuote.setName("No Symbol");
            stockQuote.setSymbol(symbol);
            stockQuote.setHigh(0);
            stockQuote.setLow(0);
            stockQuote.setPrice(0);
        }

        return stockQuote;
    }

    public String[] getSymbols() {
        Iterator iterator = stockMarket.keySet().iterator();
        String[] symbols = new String[stockMarket.keySet().size()];
        int i = 0;

        while (iterator.hasNext()) {
            symbols[i++] = (String) iterator.next();
        }

        return symbols;
    }

    public void buy(String userName, String password, String symbol, int qty)
            throws Exception {
        ClientDetails clientDetails = getClientDetails(userName, password);

        if (!SymbolTable.getInstance().isSymbolAvailable(symbol)) {
            throw new Exception("Symbol Not found");
        }

        StockQuote stocksDetails = (StockQuote) stockMarket.get(symbol);
        double credit = clientDetails.getCredit();
        float amountRequired = (stocksDetails.getPrice() * qty);

        if (credit < amountRequired) {
            throw new Exception(
                    "Not enough credit to complete transaction. Credit left = $" +
                            credit + ".  Required amount = $" + amountRequired);
        }

        clientDetails.setCredit(credit - amountRequired);

        Map stockHoldings = clientDetails.getStockHoldings();
        Integer stockHoldingsInt = (Integer) stockHoldings.get(symbol);

        if ((stockHoldingsInt == null) || (stockHoldingsInt.intValue() <= 0)) {
            stockHoldings.put(symbol, new Integer(qty));
        } else {
            stockHoldings.put(symbol,
                    new Integer(stockHoldingsInt.intValue() + qty));
        }

        ConfigurationContext configurationContext =
                ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null);

        // call the exchange now
        ExchangeTraderStub stubForExchange = new ExchangeTraderStub(configurationContext, exchangeEPR);

        //this request does not have to be reliable
        stubForExchange._getServiceClient().getOptions().setProperty
                (SandeshaClientConstants.UNRELIABLE_MESSAGE, Constants.VALUE_TRUE);

        BuyRequest buyReq = new BuyRequest();
        buyReq.setUserid(userName);
        buyReq.setSymbol(symbol);
        buyReq.setQty(qty);
        stubForExchange.buy(buyReq);
    }

    public void sell(String userName, String password, String symbol, int qty)
            throws Exception {
        ClientDetails clientDetails = getClientDetails(userName, password);

        Map stockHoldings = clientDetails.getStockHoldings();

        if (stockHoldings == null) {
            throw new Exception("No stock holdings for this user");
        }

        if (!SymbolTable.getInstance().isSymbolAvailable(symbol)) {
            throw new Exception("Symbol Not found");
        }

        Integer holdingsInt = (Integer) stockHoldings.get(symbol);

        if ((holdingsInt == null) || (holdingsInt.intValue() < qty)) {
            throw new Exception(
                    "cannot sell more than you have. You have only " +
                            ((holdingsInt == null) ? "0" : (holdingsInt.intValue() + " ")) +
                            ", but trying to sell " + qty);
        }

        stockHoldings.put(symbol, new Integer(holdingsInt.intValue() - qty));

        ConfigurationContext configurationContext =
                ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null);

        // call the exchange now
        ExchangeTraderStub stubForExchange = new ExchangeTraderStub(configurationContext, exchangeEPR);

        //this request does not have to be reliable
        stubForExchange._getServiceClient().getOptions().setProperty
                (SandeshaClientConstants.UNRELIABLE_MESSAGE, Constants.VALUE_TRUE);

        SellRequest sellReq = new SellRequest();
        sellReq.setUserid(userName);
        sellReq.setSymbol(password);
        sellReq.setQty(qty); // this is ridiculous
        stubForExchange.sell(sellReq);
    }

    public void depositMoney(String userName, String password, double amount)
            throws Exception {
        ClientDetails clientDetails = getClientDetails(userName, password);
        clientDetails.setCredit(clientDetails.getCredit() + amount);
    }

    public ClientDetails getClientDetails(String userName, String password)
            throws Exception {
        if ((userName == null) || (password == null)) {
            throw new Exception("Username and/or password is empty");
        }

        ClientDetails clientDetails = (ClientDetails) this.clientDetails.get(userName);

        if ((clientDetails == null) ||
                !password.equals(clientDetails.getPassword())) {
            throw new Exception("Username cannot be found or password is wrong");
        }

        return clientDetails;
    }

    public String createUserAccount(String ssn, String name, String password) {
        try {
            if (clientDetails.get(name) != null) {
                throw new Exception("User already exists!");
            }
            clientDetails.put(name, new ClientDetails(ssn, name, password));

            ConfigurationContext configurationContext =
                    ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null);

            ExchangeTraderStub stubForExchange = new ExchangeTraderStub(configurationContext, exchangeEPR);

            RegisterClientRequest regClientRequest = new RegisterClientRequest();
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.setName(name);
            clientInfo.setSsn(ssn);
            regClientRequest.setClientInfo(clientInfo);

            //this request does not have to be reliable
            stubForExchange._getServiceClient().getOptions().setProperty
                    (SandeshaClientConstants.UNRELIABLE_MESSAGE, Constants.VALUE_TRUE);

            RegisterClientResponse registerClientResponse =
                    stubForExchange.registerClient(regClientRequest);

            return registerClientResponse.getUserid();
        } catch (Exception e) {
            e.printStackTrace();

            return e.getMessage();
        }
    }

    public Map getPortfolio(String userName, String password)
            throws Exception {
        ClientDetails clientDetails = getClientDetails(userName, password);

        if ((clientDetails == null) ||
                (clientDetails.getStockHoldings() == null)) {
            return null;
        }

        return clientDetails.getStockHoldings();
    }

    public String getMyEPR() {
        return myEPR;
    }

    public void setMyEPR(String myEPR) {
        this.myEPR = myEPR;
    }

    public String getExchangeDefaultEPR() {
        return exchangeEPR;
    }

}
