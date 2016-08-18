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

package org.wso2.appserver.sample.ee.jpa.servlet;

import org.apache.openjpa.persistence.PersistenceException;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OrderManagerServlet class.
 */
@WebServlet(name = "OrderManagerServlet", urlPatterns = "/")
public class OrderManagerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB
    private transient OrderManager orderManager;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getParameter("placeOrder") != null) {

            String item = request.getParameter("item");
            int quantity;
            try {
                quantity = Integer.parseInt(request.getParameter("quantity"));
                orderManager.addOrder(item, quantity);
                displayOrderList(request, response);
            } catch (NumberFormatException e) {
                request.setAttribute("info", "Please enter a valid quantity");
                displayHome(request, response);
            }

        } else if (request.getParameter("viewOrder") != null) {

            try {
                request.setAttribute("orders", orderManager.getOrders());
            } catch (PersistenceException exception) {
                request.setAttribute("info", "Please place a order first");
            }
            request.getRequestDispatcher("/WEB-INF/orderList.jsp").forward(request, response);

        } else if (request.getParameter("removeOrder") != null) {

            int orderId;
            try {
                orderId = Integer.parseInt(request.getParameter("orderId"));
                Order orderToBeRemoved = orderManager.findOrder(orderId);

                if (orderToBeRemoved != null) {
                    orderManager.deleteOrder(orderToBeRemoved);
                    request.setAttribute("info", "Order with the id " + orderId + " deleted");
                } else {
                    request.setAttribute("info", "Order id not found");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("info", "Please enter a valid order id");
            }
            displayOrderList(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    private void displayHome(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.getRequestDispatcher("/index.jsp").forward(request, response);

    }

    private void displayOrderList(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.setAttribute("orders", orderManager.getOrders());
        request.getRequestDispatcher("/WEB-INF/orderList.jsp").forward(request, response);
    }
}
