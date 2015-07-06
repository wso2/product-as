/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.appserver.samples.jndi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/tomcat-resource-lookup")
public class TomcatDataSourceLookupServlet extends HttpServlet {
    private static final Log log = LogFactory.getLog(TomcatDataSourceLookupServlet.class);


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Context initCtx;
        PrintWriter out = response.getWriter();

        try {
            initCtx = new InitialContext();
            Context envContext = (Context) initCtx.lookup("java:comp/env");
            String dsName = request.getParameter("dsName");
            if (dsName != null && !dsName.equals("")) {
                DataSource dataSource = (DataSource) envContext.lookup(dsName);
                out.write("DataSourceAvailable");
            } else {
                out.write("DataSourceNotFound");
            }
        } catch (NamingException e) {
            log.error("JNDI resource not available", e);
            out.write("DataSourceNotFound");
        }
    }
}
