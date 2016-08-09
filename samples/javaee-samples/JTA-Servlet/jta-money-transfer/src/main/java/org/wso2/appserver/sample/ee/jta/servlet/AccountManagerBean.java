/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.appserver.sample.ee.jta.servlet;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * AccountManagerBean class.
 */
public class AccountManagerBean {

    private static final org.apache.juli.logging.Log logger = org.apache.juli.logging.LogFactory
            .getLog(AccountManagerBean.class);

    private EntityManagerFactory createEntityManager;

    private EntityManager entityManager;

    public AccountManagerBean() {
        createEntityManager = Persistence.createEntityManagerFactory("unit-bank");
        entityManager = createEntityManager.createEntityManager();
    }

    public void transfer(float amount) {

        Account1 account1 = new Account1(amount, TransactionType.CREDIT);
        Account2 account2 = new Account2(amount, TransactionType.DEBIT);
        Log log = new Log("acc1", "acc2", amount);

        logger.info("");
        logger.info("Sample transaction with commit");
        logger.info("==============================");
        logger.info("Operation: Transferring " + amount + " from Account2 to Account 1");

        entityManager.getTransaction().begin();
        entityManager.persist(account1);
        logger.info("Account 1 entry successful");
        entityManager.persist(account2);
        logger.info("Account 2 entry successful");
        entityManager.persist(log);
        logger.info("Log entry successful");
        entityManager.getTransaction().commit();
        logger.info("");
        printData();

        logger.info("Sample transaction with rollback");
        logger.info("================================");
        logger.info("Operation: Transferring " + amount + " from Account2 to Account 1");

        entityManager.getTransaction().begin();
        entityManager.persist(account1);
        logger.info("Account 1 entry successful");
        entityManager.persist(account2);
        logger.info("Account 2 entry successful");
        entityManager.getTransaction().rollback();
        logger.info("rollback method was called");
        logger.info("");
        printData();
    }

    private void printData() {
        Query query = entityManager.createQuery("select acc from Account1 acc");
        List<Account> acc = query.getResultList();
        logger.info("Account 1:");
        printAccount(acc);

        query = entityManager.createQuery("select acc from Account2 acc");
        acc = query.getResultList();
        logger.info("Account 2:");
        printAccount(acc);

        query = entityManager.createQuery("select log from Log log");
        List<Log> log = query.getResultList();
        logger.info("Log:");
        printLog(log);

    }

    private void printLog(List<Log> log) {
        logger.info("Log ID | Credit acc | Debit acc | Amount | Timestamp");
        for (Log l : log) {
            logger.info(l.getLogId() + " | " + l.getCredit() + " | " + l.getDebit() + " | " + l.getAmount() + " | " + l
                    .getTimestamp());
        }
        logger.info("");
    }

    private void printAccount(List<Account> account) {
        logger.info("Transaction ID | Amount | Transaction Type | Timestamp");
        for (Account acc : account) {
            logger.info(
                    acc.getTransactionId() + " | " + acc.getAmount() + " | " + acc.getTransactionType() + " | " + acc
                            .getTimestamp());
        }
        logger.info("");
    }
}
