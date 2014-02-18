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

package org.wso2.appserver.sample.trader.client;

import org.wso2.www.types.trader.client.*;


public class TraderClientCallbackHandlerExt extends TraderClientCallbackHandler {
    boolean complete;

    public TraderClientCallbackHandlerExt(Object clientData) {
        super(clientData);
    }

    public boolean isComplete() {
        return complete;
    }

    private void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void receiveErrorbuy(Exception e) {
        //Fill here with the code to handle the exception
        System.out.println(e.getMessage());

        setComplete(true);
    }

    public void receiveErrorcreateAccount(Exception e) {
        //Fill here with the code to handle the exception
        System.out.println(e.getMessage());
        setComplete(true);
    }

    public void receiveErrordeposit(Exception e) {
        //Fill here with the code to handle the exception
        System.out.println(e.getStackTrace());
        setComplete(true);
    }

    public void receiveErrorgetPortfolio(Exception e) {
        //Fill here with the code to handle the exception
        System.out.println(e.getMessage());

        setComplete(true);
    }

    public void receiveErrorgetQuote(Exception e) {
        //Fill here with the code to handle the exception
        System.out.println(e.getMessage());
        setComplete(true);
    }

    public void receiveErrorgetSymbols(Exception e) {
        //Fill here with the code to handle the exception
        System.out.println(e.getMessage());
        setComplete(true);
    }

    public void receiveErrorsell(Exception e) {
        //Fill here with the code to handle the exception
        System.out.println(e.getStackTrace());

        setComplete(true);
    }

    public void receiveResultbuy(BuyResponse param73) {
        //Fill here with the code to handle the response
        TradeStatus tradeStatus = param73.getTrade_status();

        System.out.println("Status :" + tradeStatus.getStatus());
        System.out.println("Reason :" + tradeStatus.getReason());

        setComplete(true);
    }

    public void receiveResultcreateAccount(CreateAccountResponse param81) {
        //Fill here with the code to handle the response
        System.out.println("\nResults");
        System.out.println("-------");
        System.out.println("User ID       :" + param81.getUserid());

        setComplete(true);
    }

    public void receiveResultdeposit(DepositResponse param77) {
        //Fill here with the code to handle the response
        System.out.println("Done");
        setComplete(true);
    }

    public void receiveResultgetPortfolio(GetPortfolioResponse param75) {
        //Fill here with the code to handle the response
        System.out.println("\nResults");
        System.out.println("-------");

        PortFolio portFolio = param75.getPortFolio();
        PortFolioItem[] portFolioItems = portFolio.getPortFolioItem();

        if (portFolioItems != null) {
            int length = portFolioItems.length;
            if (length == 1 && portFolioItems[0].getSymbol().equalsIgnoreCase("Invalid")) {
            	System.out.println("Username and/or password invalid!");
			} else if(length == 1 && portFolioItems[0].getSymbol().equalsIgnoreCase("Empty")) { 
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

        setComplete(true);
    }

    public void receiveResultgetQuote(GetQuoteResponse param79) {
        //Fill here with the code to handle the response
        StockQuote stockQuote = param79.getStock_quote();
        System.out.println("\nResults");
        System.out.println("-------");
        System.out.println("Name       :" + stockQuote.getName());
        System.out.println("Symbol     :" + stockQuote.getSymbol());
        System.out.println("High value :" + stockQuote.getHigh());
        System.out.println("Low value  :" + stockQuote.getLow());
        System.out.println("Price      :" + stockQuote.getPrice());

        setComplete(true);
    }

    public void receiveResultgetSymbols(GetSymbolsResponse param83) {
        //Fill here with the code to handle the response
        System.out.println("\nResults");
        System.out.println("-------");

        ArrayOfString arr = param83.get_return();
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

        setComplete(true);
    }

    public void receiveResultsell(SellResponse param71) {
        //Fill here with the code to handle the response
        System.out.println("\nResults");
        System.out.println("-------");

        TradeStatus tradeStatus = param71.getTrade_status();

        System.out.println("Status :" + tradeStatus.getStatus());
        System.out.println("Reason :" + tradeStatus.getReason());

        setComplete(true);
    }
}
