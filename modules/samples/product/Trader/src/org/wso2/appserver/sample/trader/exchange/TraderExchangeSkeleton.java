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

package org.wso2.appserver.sample.trader.exchange;

import org.wso2.appserver.sample.trader.Trader;
import org.wso2.www.types.trader.exchange.service.StockQuote;


/**
 * TraderExchangeSkeleton java skeleton for the axisService
 */
public class TraderExchangeSkeleton implements TraderExchangeSkeletonInterface {
    public void update(
            org.wso2.www.types.trader.exchange.service.UpdateRequest updateRequest) {
        StockQuote stock_quote = updateRequest.getStock_quote();

        Trader.getInstance().updateInformation(stock_quote.getSymbol(),
                                               new Float(stock_quote.getPrice()));

    }
}
