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

package org.wso2.appserver.sample.commodityquote.services.util;

import org.wso2.www.types.services.StockQuote;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;


public class Utils {
    /**
     * @return map returns a symbol table with symbol as the key and StockQuote
     *         as the value
     */
    public static Map getPrePopulatedStockMarket() {
        Map symbolTable = SymbolTable.getInstance().populateSymbolTable();
        System.out.print("Populating the stock market ................");

        Random random = new Random();

        for (Iterator iterator = symbolTable.keySet().iterator(); iterator.hasNext();) {
            String symbol = (String) iterator.next();

            StockQuote stockQuote = new StockQuote();
            stockQuote.setName((String) symbolTable.get(symbol));
            stockQuote.setSymbol(symbol);
            stockQuote.setPrice(Math.round((random.nextFloat() * 100)));
            symbolTable.put(symbol, stockQuote);
        }

        StockQuote stockQuote = new StockQuote();
        stockQuote.setSymbol("w");
        stockQuote.setName("Tungsten");
        stockQuote.setPrice((float) 101.22);
        symbolTable.put("w", stockQuote);
        System.out.println("Done.");

        return symbolTable;
    }
}
