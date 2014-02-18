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

/**
 * CommodityQuoteMessageReceiverInOut.java This file was auto-generated from
 * WSDL by the Apache Axis2 version: #axisVersion# #today#
 */
package org.wso2.appserver.sample.commodityquote.services;

import org.wso2.www.types.services.ArrayOfString;
import org.wso2.www.types.services.GetQuoteResponse;
import org.wso2.www.types.services.GetSymbolsResponse;


/**
 * CommodityQuoteService
 */
public class CommodityQuoteService implements CommodityQuoteSkeletonInterface {
    /**
     * Auto generated method signature
     *
     * @param param0
     */
    public org.wso2.www.types.services.GetQuoteResponse getQuote(
            org.wso2.www.types.services.GetQuoteRequest param0) {
        GetQuoteResponse getQuoteResponse = new GetQuoteResponse();
        getQuoteResponse.setStockQuote(CommodityQuoteUtil.getInstance()
                .getQuote(param0.getSymbol()));

        return getQuoteResponse;
    }

    /**
     * Auto generated method signature
     *
     */
    public org.wso2.www.types.services.GetSymbolsResponse getSymbols() {
        GetSymbolsResponse getSymbolsResponse = new GetSymbolsResponse();
        String[] symbols = CommodityQuoteUtil.getInstance().getSymbols();
        ArrayOfString arrayOfString = new ArrayOfString();
        arrayOfString.setValue(symbols);
        getSymbolsResponse.set_return(arrayOfString);

        return getSymbolsResponse;
    }
}
