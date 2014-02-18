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

import org.wso2.www.types.client.ArrayOfString;
import org.wso2.www.types.client.GetQuoteResponse;
import org.wso2.www.types.client.GetSymbolsResponse;
import org.wso2.www.types.client.StockQuote;


public class CommodityQuoteCallbackHandlerExt
        extends CommodityQuoteCallbackHandler {
    private boolean complete;

    public CommodityQuoteCallbackHandlerExt(Object clientData) {
        super(clientData);
    }

    public boolean isComplete() {
        return complete;
    }

    private void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void receiveErrorgetQuote(Exception e) {
        //Fill here with the code to handle the exception
        System.out.println("GetQuote reported an error:" + e.getMessage());

        setComplete(true);
    }

    public void receiveErrorgetSymbols(Exception e) {
        //Fill here with the code to handle the exception
        System.out.println("GetSymbols reported an error:" + e.getMessage());

        setComplete(true);
    }

    public void receiveResultgetQuote(GetQuoteResponse param21) {
        //Fill here with the code to handle the response
        StockQuote stockQuote = param21.getStockQuote();

        System.out.println("Printing stock quote...");
        System.out.println(stockQuote.getName());
        System.out.println(stockQuote.getSymbol());
        System.out.println(stockQuote.getPrice());
        System.out.println(stockQuote.getHigh());
        System.out.println(stockQuote.getLow());

        setComplete(true);
    }

    public void receiveResultgetSymbols(GetSymbolsResponse param23) {
        //Fill here with the code to handle the response
        System.out.println("Printing symbols...");

        ArrayOfString symbols = param23.get_return();
        String[] symbolArr = symbols.getValue();

        if (symbolArr == null) {
            System.out.println("No symbols found");

            return;
        }

        int size = symbolArr.length;

        int rowCount = 1;
        System.out.println("\n\n");

        for (int i = 0; i < size; i++) {
            String nextSymbol = symbolArr[i];

            if (rowCount <= 7) {
                System.out.print("\t" + nextSymbol);
                rowCount++;
            } else {
                rowCount = 1;

                System.out.print("\n");
                System.out.print("\t" + nextSymbol);
                rowCount++;
            }
        }

        setComplete(true);
    }
}
