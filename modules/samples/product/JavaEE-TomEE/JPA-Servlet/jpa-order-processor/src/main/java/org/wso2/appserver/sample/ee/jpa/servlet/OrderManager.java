package org.wso2.appserver.sample.ee.jpa.servlet;

import java.util.List;

public interface OrderManager {
    public void addOrder(String item, int quantity);

    public List<Order> getOrders();

    public void deleteOrder(Order order);

    public Order findOrder(int id);

}
