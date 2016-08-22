/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.appserver.sample.ee.jta.servlet;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

/**
 * Account class.
 */
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int transactionId;
    private float amount;
    private TransactionType transactionTypes;

    @Version
    private Date timestamp;

    public Account(float transactionAmount, TransactionType transactionTypes) {
        this.amount = transactionAmount;
        this.transactionTypes = transactionTypes;
    }

    public Account() {
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int id) {
        this.transactionId = id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float transactionAmount) {
        this.amount = transactionAmount;
    }

    public TransactionType getTransactionType() {
        return transactionTypes;
    }

    public void setTransactionTypes(TransactionType transactionTypes) {
        this.transactionTypes = transactionTypes;
    }

    public Date getTimestamp() {
        return (Date) timestamp.clone();
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = (Date) timestamp.clone();
    }
}

