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

package org.wso2.appserver.sample.exchange.client;

import org.wso2.www.types.exchange.client.GetInfoResponse;
import org.wso2.www.types.exchange.client.MarketInfo;


public class ExchangeClientCallbackHandlerExt
        extends ExchangeClientCallbackHandler {
    boolean complete;

    public ExchangeClientCallbackHandlerExt(Object clientData) {
        super(null);
    }

    public boolean isComplete() {
        return complete;
    }

    private void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void receiveErrorgetInfo(Exception e) {
        // Fill here with the code to handle the exception
        System.out.println(e.getMessage());
        setComplete(true);
    }

    public void receiveResultgetInfo(GetInfoResponse param11) {
        // Fill here with the code to handle the response
        System.out.println("\nResults");
        System.out.println("-------");

        MarketInfo marketInfo = param11.getMarketInfo();

        System.out.println("Average price          :" +
                           marketInfo.getAvgPrice());
        System.out.println("Average price of trade :" +
                           marketInfo.getAvgPriceOfTrade());
        System.out.println("Market cap             :" +
                           marketInfo.getMarketCap());
        System.out.println("Total trader           :" +
                           marketInfo.getTotalTraded());
        System.out.println("\n\n\n");

        setComplete(true);
    }
}
