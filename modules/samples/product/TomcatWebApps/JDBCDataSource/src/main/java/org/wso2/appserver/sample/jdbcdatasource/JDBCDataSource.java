/*
 * Copyright 2011-2012 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.wso2.appserver.sample.jdbcdatasource;

import org.apache.naming.SelectorContext;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

public class JDBCDataSource extends HttpServlet {
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) {

        try {
            Boolean param = Boolean.valueOf(request.getParameter("getValues"));

            Context initCtx = new InitialContext();
            SelectorContext selectorContext =
                    new SelectorContext((Hashtable<String, Object>) initCtx.getEnvironment(), false);
            Context envCtx = (Context) selectorContext.lookup("java:comp/env");

            DataSource ds = (DataSource)
                    envCtx.lookup("jdbc/TestDB");

            Connection conn = ds.getConnection();
            Statement statement = conn.createStatement();

            if (param) {
                int i = 1;
                ResultSet rs = statement.executeQuery("select * from employee");
                while (rs.next()) {
                    PrintWriter writer = null;

                    writer = response.getWriter();

                    writer.println(i+". Employee Name = " + rs.getString("name") +
                    "   Age = "+ rs.getString("age"));
                    i++;
                }

                // Close the result set, statement and the connection
                rs.close();
            } else {
                String employee_name = request.getParameter("emp_name");
                int employee_age = Integer.parseInt(request.getParameter("emp_age"));
                int rs = statement.executeUpdate("insert into employee values('" + employee_name +
                                                 "', '" + employee_age + "')");

                PrintWriter writer = null;

                writer = response.getWriter();

                writer.println(employee_name + " (Age - " + employee_age + ") " +
                               "was added to employee table");

            }
            statement.close();
            conn.close();
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
