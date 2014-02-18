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

package org.wso2.appserver.sample.commodityquote.services;

import org.wso2.appserver.sample.commodityquote.services.util.SymbolTable;
import org.wso2.appserver.sample.commodityquote.services.util.Utils;
import org.wso2.www.types.services.StockQuote;

import java.util.Iterator;
import java.util.Map;


public class CommodityQuoteUtil {
    public final static String EXCHANGE_SERVICE_ENDPORT_ADDRESS = "exchangeEndpointAddress";
    private static CommodityQuoteUtil ourInstance = new CommodityQuoteUtil();
    private String myEPR;
    private Map stockMarket;

    private CommodityQuoteUtil() {
        String httpPort = System.getProperty("carbon.http.port");
        httpPort = (httpPort == null) ? "9763" : httpPort;
        myEPR = "http://127.0.0.1:" + httpPort + "/services/Trader";
        stockMarket = Utils.getPrePopulatedStockMarket();
    }

    public static CommodityQuoteUtil getInstance() {
        return ourInstance;
    }

    public StockQuote getQuote(String symbol) {
        StockQuote stockQuote = (StockQuote) stockMarket.get(symbol);

        if (stockQuote == null) {
            stockQuote = new StockQuote();
            stockQuote.setSymbol(symbol);
            stockQuote.setName("No Symbol");
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

    public String getMyEPR() {
        return myEPR;
    }

    public void setMyEPR(String myEPR) {
        this.myEPR = myEPR;
    }
}
