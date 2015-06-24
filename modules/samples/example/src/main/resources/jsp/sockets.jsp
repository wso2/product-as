<!--
 ~ Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 ~
 ~ WSO2 Inc. licenses this file to you under the Apache License,
 ~ Version 2.0 (the "License"); you may not use this file except
 ~ in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~    http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied.  See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 -->
<%@ page import="java.net.Socket" %>
<%@ page import="java.net.InetAddress" %>
<%
    // Create a socket without a timeout
    try {
        InetAddress addr = InetAddress.getByName("java.sun.com");
        int port = 80;

        // This constructor will block until the connection succeeds
        Socket socket = new Socket(addr, port);
    } catch (Exception e) {
        e.printStackTrace();
    }
%>