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

package org.wso2.appserver.sample.exchange;

//import org.wso2.appserver.ServerConstants;
import org.wso2.appserver.sample.trader.ClientDetails;
import org.wso2.appserver.sample.utils.Utils;
import org.wso2.www.types.exchange.trader.service.ClientInfo;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;


public class Exchange {
    private static Exchange ourInstance = new Exchange();
    private String myEPR = "http://127.0.0.1:3501/services/ExchangeTrader";

    private ArrayList tradersToBeNotified = new ArrayList();
    private Map registeredClients;
    private int totalTraded = 0;

    private Exchange() {
        tradersToBeNotified = new ArrayList();
        Utils.getPrePopulatedStockMarket(Utils.TRADER_EXCHANGE);
        registeredClients = Utils.getPrePopulatedClientList();
    }

    public static Exchange getInstance() {
        return ourInstance;
    }

    public void registerTrader(String epr) {
        tradersToBeNotified.add(epr);
    }

    public String registerClient(ClientInfo info) {
        ClientDetails clntDetails = new ClientDetails(info.getSsn(),
                                                      info.getName());
        this.registeredClients.put(info.getName(), clntDetails);

        return info.getName();
    }

    public int getTotalTraded() {
        return this.totalTraded;
    }

    public void buy(String userId, String symbol, int quantity) {
        this.totalTraded += quantity;
    }

    public void sell(String userId, String symbol, int quantity) {
        this.totalTraded += quantity;
    }

    public String getMyEPR() {
        return myEPR;
    }

    public void setMyEPR(String myEPR) {
        this.myEPR = myEPR;
    }
}
