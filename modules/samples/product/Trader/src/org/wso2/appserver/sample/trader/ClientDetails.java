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

package org.wso2.appserver.sample.trader;

import java.util.HashMap;
import java.util.Map;


public class ClientDetails {
    private String ssn;
    private String name;
    private double credit;
    private String password;

    /**
     * Key - Stock symbol Value - Holding as an Integer.
     */
    private Map stockHoldings;

    public ClientDetails(String ssn, String name, double credit) {
        this.ssn = ssn;
        this.name = name;
        this.password = name; // for the time being password is the name
        this.credit = credit;
        stockHoldings = new HashMap();
    }

    public ClientDetails(String ssn, String name, String password) {
        this.ssn = ssn;
        this.name = name;
        this.password = password; // for the time being password is the name
        this.credit = 1000000.00;
        stockHoldings = new HashMap();
    }

    public ClientDetails(String ssn, String name) {
        this.ssn = ssn;
        this.name = name;
        this.password = name; // for the time being password is the name
        this.credit = 1000000.00;
        stockHoldings = new HashMap();
        stockHoldings.put("w", new Integer(2000));
        stockHoldings.put("he", new Integer(4000));
        stockHoldings.put("h", new Integer(44000));
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    /**
     * Key - Stock symbol Value - Holding as an Integer.
     *
     * @return stock holdings map
     */
    public Map getStockHoldings() {
        if(stockHoldings == null){
            stockHoldings = new HashMap();
        }
        return stockHoldings;
    }

    public void setStockHoldings(Map stockHoldings) {
        this.stockHoldings = stockHoldings;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
