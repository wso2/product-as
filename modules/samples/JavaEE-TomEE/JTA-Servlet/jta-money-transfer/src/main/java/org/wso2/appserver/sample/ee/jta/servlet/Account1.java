package org.wso2.appserver.sample.ee.jta.servlet;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Account1 extends Account {
    public Account1(float transactionAmount, TransactionType transactionTypes) {
        super(transactionAmount, transactionTypes);
    }

    public Account1() {
    }
}
