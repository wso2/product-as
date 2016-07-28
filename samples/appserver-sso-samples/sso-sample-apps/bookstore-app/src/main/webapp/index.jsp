<!--
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
   -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.Date" %>
<html lang="en">
   <head>
      <style>
         h1 {text-align:center;}
         h2 {text-align:center;}
         table {
         border-collapse: collapse;
         width: 100%;
         }
         th, td {
         text-align: left;
         padding: 8px;
         }
         tr:nth-child(even){background-color: #f2f2f2}
         th {
         background-color: #3333ff;
         color: white;
         }
      </style>
   </head>
   <body>
      <img src="images/book-title.png" alt="starting" style="width: 100%;" />
      <p></p>
      <h2 style="color: #2e6c80;">Welcome to the Book Store</h2>
      <p></p>
      <table style="height: 373px;" width="605">
         <thead>
            <tr>
               <th>Book</th>
               <th>Author</th>
            </tr>
         </thead>
         <tbody>
            <tr>
               <td>Dont Let Me Down</td>
               <td>Stephen Hawking</td>
            </tr>
            <tr>
               <td>A Brief History of Time</td>
               <td>Bryan Stevenson</td>
            </tr>
            <tr>
               <td>Meditations (Dover Thrift Editions) </td>
               <td> Marcus Aurelius</td>
            </tr>
         </tbody>
      </table>
      <p></p>
      <a href="http://localhost:8080/musicstore-app">Please visit our Music Store</a>
      <p></p>
      <div>
         <form action="logout">
            <input type="submit" value="Logout">
         </form>
      </div>
      </div>
      </div>
   </body>
</html>