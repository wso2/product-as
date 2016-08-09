/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.superbiz.servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.HandlerChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceRef;

/**
 * Server entry point of pojo and ejb service
 */
public class WebserviceServlet extends HttpServlet {

    private static ServletOutputStream servletOutputStream;
    private static Logger logger = Logger.getLogger(WebserviceServlet.class.toString());
    @WebServiceRef
    @HandlerChain(file = "client-handlers.xml")
    private transient HelloPojo helloPojo;
    @WebServiceRef
    @HandlerChain(file = "client-handlers.xml")
    private transient HelloEjb helloEjb;

    public static void write(String message) {
        try {
            ServletOutputStream out = servletOutputStream;
            out.println(message);
        } catch (Exception e) {
            logger.log(Level.WARNING, e.toString());
        }
    }

    private static void setServletOutputStream(ServletOutputStream out) {
        servletOutputStream = out;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        response.setContentType("text/plain");
        ServletOutputStream out = response.getOutputStream();

        setServletOutputStream(out);
        try {
            out.println("Pojo Webservice");
            out.println("    helloPojo.hello(\"Bob\")=" + helloPojo.hello("Bob"));
            out.println();
            out.println("    helloPojo.hello(null)=" + helloPojo.hello(null));
            out.println();
            out.println("EJB Webservice");
            out.println("    helloEjb.hello(\"Bob\")=" + helloEjb.hello("Bob"));
            out.println();
            out.println("    helloEjb.hello(null)=" + helloEjb.hello(null));
            out.println();
        } finally {
            setServletOutputStream(out);
        }
    }
}
