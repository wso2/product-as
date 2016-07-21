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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.naming.SelectorContext;
import org.owasp.esapi.ESAPI;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Demonstrate fetching data from database
 */
public class JDBCDataSource extends HttpServlet {

    private static final Log log = LogFactory.getLog(JDBCDataSource.class);

    private static final long serialVersionUID = 481656508634865277L;

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) {

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            Boolean param = Boolean.valueOf(request.getParameter("getValues"));

            Context initCtx = new InitialContext();
            SelectorContext selectorContext =
                    new SelectorContext((Hashtable<String, Object>) initCtx.getEnvironment(), false);
            Context envCtx = (Context) selectorContext.lookup("java:comp/env");

            DataSource ds = (DataSource)
                    envCtx.lookup("jdbc/TestDB");

            conn = ds.getConnection();

            if (param) {
                int i = 1;
                statement = conn.prepareStatement("select * from employee");
                rs = statement.executeQuery();
                while (rs.next()) {
                    PrintWriter writer = response.getWriter();
                    writer.println(i + ". Employee Name = " + rs.getString("name") + " Age = " + rs.getString("age"));
                    i++;
                }
            } else {
                statement = conn.prepareStatement("insert into employee values(?,?)");
                String employeeName = ESAPI.encoder().encodeForHTML(request.getParameter("emp_name"));
                int employeeAge = Integer.parseInt(request.getParameter("emp_age"));
                statement.setString(1, employeeName);
                statement.setInt(2, employeeAge);
                statement.executeUpdate();

                PrintWriter writer = response.getWriter();

                writer.println(employeeName + " (Age - " + employeeAge + ") " + "was added to employee table");

            }
        } catch (NamingException e) {
           log.error("JNDI lookup failed with ", e);
        } catch (SQLException e) {
            log.error("Error occurred while querying database ", e);
        } catch (IOException e) {
            log.error("Error occurred while writing to output", e);
        } finally {
            // Close the result set, statement and the connection
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    log.error("Error occurred while closing result set ", e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.error("Error occurred while closing statement.");
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("Error occurred while closing connection.");
                }
            }
        }
    }
}
