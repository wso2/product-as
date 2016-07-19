<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
  ~
  ~ WSO2 Inc. licenses this file to you under the Apache License,
  ~ Version 2.0 (the "License"); you may not use this file except
  ~ in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied.See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  --%>

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