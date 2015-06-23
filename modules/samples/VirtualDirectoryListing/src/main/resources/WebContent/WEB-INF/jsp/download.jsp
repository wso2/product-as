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
<%@ page import="java.io.BufferedInputStream" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="java.io.IOException" %>

<%
    String subPath = (String) request.getAttribute("subPath");
    String virtualDirectoryPath = (String) request.getAttribute("virtualDirectoryPath");
    String absolutePath = virtualDirectoryPath + File.separator + subPath;

    String fileName = absolutePath.substring(absolutePath.lastIndexOf(File.separator) + 1);

    BufferedInputStream bufferedInputStream = null;
    ServletOutputStream servletOutputStream = null;

    try {
        servletOutputStream = response.getOutputStream();
        File file = new File(absolutePath);

        //set response headers
        response.setContentType("text/plain");
        response.addHeader("Content-Disposition", "attachment; filename=" + fileName);

        response.setContentLength((int) file.length());

        FileInputStream fileInputStream = new FileInputStream(file);
        bufferedInputStream = new BufferedInputStream(fileInputStream);
        int readBytes = 0;

        //read from the file; write to the ServletOutputStream
        while ((readBytes = bufferedInputStream.read()) != -1) {
            servletOutputStream.write(readBytes);
        }

    } catch (IOException ioe) {
        throw new ServletException(ioe.getMessage());
    } finally {
        //close the input/output streams
        out.clear();
//        out = pageContext.pushBody();
        if (bufferedInputStream != null)
            bufferedInputStream.close();
        if (servletOutputStream != null) {
            servletOutputStream.flush();
            servletOutputStream.close();
        }
    }
%>