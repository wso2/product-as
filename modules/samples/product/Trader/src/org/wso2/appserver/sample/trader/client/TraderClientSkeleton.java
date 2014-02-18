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

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ServiceContext;
import org.wso2.appserver.sample.trader.Trader;
import org.wso2.www.types.trader.client.service.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * TraderClientSkeleton java skeleton for the axisService
 */
public class TraderClientSkeleton implements TraderClientSkeletonInterface {
    private Trader trader;

    public static boolean testing = false;
    public static String testingExchangeServiceName;

    public void init(ServiceContext serviceContext) throws AxisFault {
        try {
            if (!testing) {
                trader = Trader.getInstance().createTraderWithExchangeEPR(
                        serviceContext.getConfigurationContext().getServiceContextPath());
            } else {
                //If this will be used in test case
                trader = Trader.getInstance().createTraderWithExchangeEPR(
                        serviceContext.getConfigurationContext().getServiceContextPath(),
                        testingExchangeServiceName);
            }

        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    public org.wso2.www.types.trader.client.service.SellResponse sell(
            org.wso2.www.types.trader.client.service.SellRequest request) {
        SellResponse sellResponse = new SellResponse();

        try {
            trader.sell(request.getUserid(),
                        request.getPassword(), request.getSymbol(),
                        request.getQty().intValue());

            TradeStatus tradeStatus = getTradeStatus(true, "Success");
            sellResponse.setTrade_status(tradeStatus);
        } catch (Exception e) {
            sellResponse.setTrade_status(getTradeStatus(false, e.getMessage()));
        }

        return sellResponse;
    }

    private TradeStatus getTradeStatus(boolean status, String reason) {
        TradeStatus tradeStatus = new TradeStatus();
        tradeStatus.setReason(reason);
        tradeStatus.setStatus(status);

        return tradeStatus;
    }

    public org.wso2.www.types.trader.client.service.BuyResponse buy(
            org.wso2.www.types.trader.client.service.BuyRequest buyReq) {
        BuyResponse buyResponse = new BuyResponse();

        try {
            trader.buy(buyReq.getUserid(), buyReq.getPassword(),
                       buyReq.getSymbol(), buyReq.getQty());
            buyResponse.setTrade_status(getTradeStatus(true, "Success"));
        } catch (Exception e) {
            buyResponse.setTrade_status(getTradeStatus(false, e.getMessage()));
        }

        return buyResponse;
    }

    /**
     * Auto generated method signature
     */
    public org.wso2.www.types.trader.client.service.GetPortfolioResponse getPortfolio(
            org.wso2.www.types.trader.client.service.GetPortfolioRequest portFolioReq) {
        GetPortfolioResponse portfolioResponse = new GetPortfolioResponse();
        PortFolio portFolio = new PortFolio();

        try {
            List portFoliItems = new ArrayList();
            Map portfolioMap = trader.getPortfolio(portFolioReq.getUserid(),
                                                   portFolioReq.getPassword());

            if (portfolioMap == null) {
                PortFolioItem portFolioItem = getPortfolioItem("Invalid", 0);
                portFolio.setPortFolioItem(new PortFolioItem[]{portFolioItem});
            } else {
                for (Iterator keyIter = portfolioMap.keySet().iterator();
                     keyIter.hasNext();) {
                    String symbol = (String) keyIter.next();
                    portFoliItems.add(getPortfolioItem(symbol,
                                                       ((Integer) portfolioMap
                                                               .get(symbol)).intValue()));
                }

                if (portFoliItems.size() == 0) {
                    portFolio.setPortFolioItem(new PortFolioItem[]{
                            getPortfolioItem("Empty", 0)
                    });
                } else {
                    portFolio.setPortFolioItem((PortFolioItem[]) portFoliItems.toArray(
                            new PortFolioItem[portFoliItems.size()]));
                }
            }
        } catch (Exception e) {
            portFolio.setPortFolioItem(new PortFolioItem[]{
                    getPortfolioItem("Invalid", 0)
            });
        }

        portfolioResponse.setPortFolio(portFolio);

        return portfolioResponse;
    }

    private PortFolioItem getPortfolioItem(String symbol, int amount) {
        PortFolioItem portFolioItem = new PortFolioItem();
        portFolioItem.setAmount(amount);
        portFolioItem.setSymbol(symbol);

        return portFolioItem;
    }

    public org.wso2.www.types.trader.client.service.DepositResponse deposit(
            org.wso2.www.types.trader.client.service.DepositRequest depositReq) {
        DepositResponse depositResponse = new DepositResponse();

        try {
            trader.depositMoney(depositReq.getUseridr(),
                                depositReq.getPassword(), depositReq.getAmount());
            depositResponse.setDepositStatus("Deposit Successful !!");
        } catch (Exception e) {
            depositResponse.setDepositStatus("Deposit failed. Reason : " +
                                             e.getMessage());
        }

        return depositResponse;
    }

    public org.wso2.www.types.trader.client.service.GetQuoteResponse getQuote(
            org.wso2.www.types.trader.client.service.GetQuoteRequest param8) {
        GetQuoteResponse getQuoteResponse = new GetQuoteResponse();


        StockQuote quote = trader.getQuote(param8.getSymbol());
        if (quote == null) {
            getQuoteResponse.setStock_quote(new StockQuote());
        }
        getQuoteResponse.setStock_quote(quote);
        return getQuoteResponse;
    }

    /**
     * Auto generated method signature   //TODO
     */
    public org.wso2.www.types.trader.client.service.CreateAccountResponse createAccount(
            org.wso2.www.types.trader.client.service.CreateAccountRequest createAccReq) {
        CreateAccountResponse createAccResp = new CreateAccountResponse();


        String userAccountId =
                trader.createUserAccount(createAccReq.getClientinfo()
                        .getSsn(),
                                         createAccReq.getClientinfo().getName(),
                                         createAccReq.getPassword());

        createAccResp.setUserid(userAccountId);


        return createAccResp;
    }

    /**
     * Auto generated method signature
     *
     * @param param12
     */
    public org.wso2.www.types.trader.client.service.GetSymbolsResponse getSymbols(
            org.wso2.www.types.trader.client.service.GetSymbolsRequest param12) {
        GetSymbolsResponse getSymbolsResponse = new GetSymbolsResponse();
        ArrayOfString arrayOfString = new ArrayOfString();

        arrayOfString.setValue(trader.getSymbols());
        getSymbolsResponse.set_return(arrayOfString);

        return getSymbolsResponse;
    }
}
