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

import org.wso2.appserver.sample.exchange.Exchange;
import org.wso2.www.types.exchange.client.service.GetInfoResponse;
import org.wso2.www.types.exchange.client.service.MarketInfo;

import java.util.Random;


/**
 * ExchangeClientSkeleton java skeleton for the axisService
 */
public class ExchangeClientSkeleton implements ExchangeClientSkeletonInterface {
    Exchange exchange = Exchange.getInstance();

    public org.wso2.www.types.exchange.client.service.GetInfoResponse getInfo(
            org.wso2.www.types.exchange.client.service.GetInfoRequest getInfoRequest) {
        MarketInfo info = new MarketInfo();
        Random rnd = new Random();

        info.setAvgPrice(rnd.nextInt(100));
        info.setAvgPriceOfTrade(rnd.nextInt(100));
        info.setMarketCap(rnd.nextInt(100000));
        info.setTotalTraded(exchange.getTotalTraded());

        GetInfoResponse getInfoResponse = new GetInfoResponse();
        getInfoResponse.setMarketInfo(info);

        return getInfoResponse;
    }
}
