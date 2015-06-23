<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>JPA-Servlet example</title>
</head>
<body>
<div>
    <b style="color: red;">${info}</b>
</div>
<h1>
    Order Placement System
</h1>

<form action="order" method="post">
    Item: <input type="text" name="item"/>
    Quantity: <input type="text" name="quantity"/>
    <input type="submit" value="Place Order" name="placeOrder"/>
    <input type="submit" value="View Orders" name="viewOrder"/>
</form>

</body>
</html>