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

package org.wso2.appserver.sample.ee.jpa.servlet;

import java.util.List;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

/**
 * OrderManagerImpl class.
 */
@Stateful
public class OrderManagerImpl implements OrderManager {

    @PersistenceContext(unitName = "jpaSample", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    @Override
    public void addOrder(String item, int quantity) {
        Order order = new Order(item, quantity);
        entityManager.persist(order);
    }

    @Override
    public List<Order> getOrders() {
        Query query = entityManager.createQuery("SELECT o FROM ORDERS o");
        List<Order> results = query.getResultList();
        return results;
    }

    @Override
    public void deleteOrder(Order order) {
        entityManager.remove(order);
    }

    @Override
    public Order findOrder(int id) {
        return entityManager.find(Order.class, id);
    }

}
