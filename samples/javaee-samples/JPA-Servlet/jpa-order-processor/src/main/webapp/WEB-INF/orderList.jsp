<%@ page import="java.util.List" %>
<%@ page import="org.wso2.appserver.sample.ee.jpa.servlet.Order" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
  --%>

<html>
<head>
    <title>JPA-Servlet example</title>
</head>
<body>

<div>
    <b style="color: red;"> ${info} </b>
</div>

<h1>List of orders</h1>

<table>
    <thead>
    <tr>
        <td>Order ID</td>
        <td>Item</td>
        <td>Quantity</td>
        <td>Timestamp</td>
    </tr>
    </thead>

    <%
        List<Order> orderList = (List<Order>) request.getAttribute("orders");
        if (orderList.size() > 0) {
            for (Order order : orderList) {
    %>
    <tr>
        <td><%= order.getId()%>
        </td>
        <td><%= order.getItem()%>
        </td>
        <td><%= order.getQuantity()%>
        </td>
        <td><%= order.getTimestamp()%>
        </td>
    </tr>
    <%
            }
        }
    %>
</table>

<form action="order" method="get">
    <input type="submit" value="Place another order" name="anotherOrder"/>
</form>

<h1>Order removal</h1>

<form action="order" method="post">
    Order id: <input type="text" name="orderId"/>
    <input type="submit" value="Remove order" name="removeOrder"/>
</form>

</body>
</html>