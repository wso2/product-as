<!--
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
~ KIND, either express or implied. See the License for the
~ specific language governing permissions and limitations
~ under the License.
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>SaaS Test Demo Login Page</title>	
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/css/styles.css" />	
</head>
<body>
<center>
	<h1>SaaS Multi-Tenancy Powered by</h1>	
	<img src="<%=request.getContextPath() %>/images/carbon_logo_h42.gif"   alt="image"/><br>
	<h1>Framework</h1>
</center>

<div class="instructions">
	This application demonstrates Carbon Multi-Tenancy (MT) for cache, registry, user realm, and queue objects. 
	 The application also demonstrates keying a database table by tenant id.
</div>
 
<br>

<form method="POST" action="j_security_check">
	<table>
		<tr>
			<td colspan="2"><h2>Login to the SaaS Test Multi-Tenant Demo application</h2></td>
		</tr>
		<tr>
			<td>Name:</td>
			<td><input type="text" name="j_username" id="j_username" /></td>
		</tr>
		<tr>
			<td>Password:</td>
			<td><input type="password" name="j_password" /></td>
		</tr>
		<tr>
			<td colspan="2" align="center"><input type="submit" value="Go" /></td>
		</tr>
	</table>
</form>
<P>
<div class="instructions">if you RTFM, <a  href="./docs/docs.jsp" target="_blank" >Read The SaaS Test App Manual</a></div>

</body>
</html>