package org.wso2.appserver.sample.ee.jta.servlet;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Account2 extends Account {

    public Account2(float transactionAmount, TransactionType transactionTypes) {
        super(transactionAmount, transactionTypes);
    }

    public Account2() {
    }
}
