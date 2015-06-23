package org.wso2.appserver.sample.ee.jpa.servlet;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "ORDERS")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(length = 45)
    private String item;

    private int quantity;

    @Version
    @Column(length = 45)
    private Date timestamp;

    public Order() {
    }

    public Order(String item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
