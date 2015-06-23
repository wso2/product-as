package org.wso2.appserver.sample.ee.jta.servlet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

public class AccountManagerBean {

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

        System.out.println();
        System.out.println("Sample transaction with commit");
        System.out.println("==============================");
        System.out.println("Operation: Transferring " + amount + " from Account2 to Account 1");

        entityManager.getTransaction().begin();
        entityManager.persist(account1);
        System.out.println("Account 1 entry successful");
        entityManager.persist(account2);
        System.out.println("Account 2 entry successful");
        entityManager.persist(log);
        System.out.println("Log entry successful");
        entityManager.getTransaction().commit();
        System.out.println();
        printData();

        System.out.println("Sample transaction with rollback");
        System.out.println("================================");
        System.out.println("Operation: Transferring " + amount + " from Account2 to Account 1");

        entityManager.getTransaction().begin();
        entityManager.persist(account1);
        System.out.println("Account 1 entry successful");
        entityManager.persist(account2);
        System.out.println("Account 2 entry successful");
        entityManager.getTransaction().rollback();
        System.out.println("rollback method was called");
        System.out.println();
        printData();
    }


    private void printData() {
        Query query = entityManager.createQuery("select acc from Account1 acc");
        List<Account> acc = query.getResultList();
        System.out.println("Account 1:");
        printAccount(acc);

        query = entityManager.createQuery("select acc from Account2 acc");
        acc = query.getResultList();
        System.out.println("Account 2:");
        printAccount(acc);

        query = entityManager.createQuery("select log from Log log");
        List<Log> log = query.getResultList();
        System.out.println("Log:");
        printLog(log);

    }

    private void printLog(List<Log> log) {
        System.out.println("Log ID | Credit acc | Debit acc | Amount | Timestamp");
        for (Log l : log) {
            System.out.println(l.getLogId() + " | " + l.getCredit() + " | " + l.getDebit() + " | " + l.getAmount() + " | " + l.getTimestamp());
        }
        System.out.println();
    }

    private void printAccount(List<Account> account) {
        System.out.println("Transaction ID | Amount | Transaction Type | Timestamp");
        for (Account acc : account) {
            System.out.println(acc.getTransactionId() + " | " + acc.getAmount() + " | " + acc.getTransactionType() + " | " + acc.getTimestamp());
        }
        System.out.println();
    }
}
