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

package org.wso2.appserver.sample.utils;

import org.wso2.appserver.sample.trader.ClientDetails;
import org.wso2.appserver.sample.trader.SymbolTable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;


public class Utils {
    public static final String TRADER_EXCHANGE = "TraderExchange";
    public static final String TRADER_CLIENT = "TraderClient";

    /**
     * @param interactionType - This should be either TraderExchange or
     *                        TraderClient
     * @return map returns a symbol table with symbol as the key and StockQuote
     *         as the value
     */
    public static Map getPrePopulatedStockMarket(String interactionType) {
        Map symbolTable = SymbolTable.getInstance().populateSymbolTable();
        System.out.print("Populating the stock market ................");

        Iterator iterator = symbolTable.keySet().iterator();
        Random random = new Random();

        while (iterator.hasNext()) {
            String symbolName = (String) iterator.next();

            if (TRADER_EXCHANGE.equals(interactionType)) {
                org.wso2.www.types.trader.exchange.service.StockQuote stockQuote =
                        new org.wso2.www.types.trader.exchange.service.StockQuote();
                stockQuote.setName(symbolName);
                stockQuote.setSymbol((String) symbolTable.get(symbolName));
                stockQuote.setPrice(Math.round((random.nextFloat() * 100)));
                symbolTable.put(symbolName, stockQuote);
            } else if (TRADER_CLIENT.equals(interactionType)) {
                org.wso2.www.types.trader.client.service.StockQuote stockQuote =
                        new org.wso2.www.types.trader.client.service.StockQuote();
                stockQuote.setName(symbolName);
                stockQuote.setSymbol((String) symbolTable.get(symbolName));
                stockQuote.setPrice(Math.round((random.nextFloat() * 100)));
                symbolTable.put(symbolName, stockQuote);
            }
        }

        if (TRADER_EXCHANGE.equals(interactionType)) {
            org.wso2.www.types.trader.exchange.service.StockQuote stockQuote =
                    new org.wso2.www.types.trader.exchange.service.StockQuote();
            stockQuote.setName("w");
            stockQuote.setSymbol("Tungsten");
            stockQuote.setPrice((float) 101.22);
            symbolTable.put("w", stockQuote);
        } else if (TRADER_CLIENT.equals(interactionType)) {
            org.wso2.www.types.trader.client.service.StockQuote stockQuote =
                    new org.wso2.www.types.trader.client.service.StockQuote();
            stockQuote.setName("w");
            stockQuote.setSymbol("Tungsten");
            stockQuote.setPrice((float) 101.22);
            symbolTable.put("w", stockQuote);
        }

        System.out.println("Done.");

        return symbolTable;
    }

    public static Map getPrePopulatedClientList() {
        Map clients = new HashMap();
        clients.put("Sanjiva", new ClientDetails("123451", "Sanjiva"));
        clients.put("Dims", new ClientDetails("123452", "Dims"));
        clients.put("Paul", new ClientDetails("123453", "Paul"));
        clients.put("Jivaka", new ClientDetails("123454", "Jivaka"));
        clients.put("Ajith", new ClientDetails("123455", "Ajith"));
        clients.put("Chinthaka", new ClientDetails("123456", "Chinthaka"));
        clients.put("Ruchith", new ClientDetails("123457", "Ruchith"));
        clients.put("Deepal", new ClientDetails("123458", "Deepal"));
        clients.put("Saminda", new ClientDetails("123459", "Saminda"));
        clients.put("Azeez", new ClientDetails("123460", "Azeez"));
        clients.put("Chamil", new ClientDetails("123461", "Chamil"));
        clients.put("Chamikara", new ClientDetails("123462", "Chamikara"));
        clients.put("Sanka", new ClientDetails("123463", "Sanka"));
        clients.put("Chatra", new ClientDetails("123464", "Chatra"));
        clients.put("Samisa", new ClientDetails("123465", "Samisa"));
        clients.put("Sahan", new ClientDetails("123466", "Sahan"));
        clients.put("Amila", new ClientDetails("123467", "Amila"));

        return clients;
    }
}
