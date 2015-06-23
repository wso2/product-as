<%@ page import="org.wso2.appserver.sample.ee.jpa.servlet.Order" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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