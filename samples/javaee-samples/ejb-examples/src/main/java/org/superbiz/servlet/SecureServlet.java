/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.superbiz.servlet;

import java.io.IOException;
import java.security.Principal;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SecureServlet class.
 */
public class SecureServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB
    private transient SecureEJBLocal secureEJBLocal;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        ServletOutputStream out = response.getOutputStream();

        out.println("Servlet");
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            out.println("Servlet.getUserPrincipal()=" + principal + " [" + principal.getName() + "]");
        } else {
            out.println("Servlet.getUserPrincipal()=<null>");
        }
        out.println("Servlet.isCallerInRole(\"admin\")=" + request.isUserInRole("admin"));
        out.println("Servlet.isCallerInRole(\"manager\")=" + request.isUserInRole("manager"));
        out.println("Servlet.isCallerInRole(\"fake\")=" + request.isUserInRole("fake"));
        out.println();

        out.println("@EJB=" + secureEJBLocal);
        if (secureEJBLocal != null) {
            principal = secureEJBLocal.getCallerPrincipal();
            if (principal != null) {
                out.println("@EJB.getCallerPrincipal()=" + principal + " [" + principal.getName() + "]");
            } else {
                out.println("@EJB.getCallerPrincipal()=<null>");
            }
            out.println("@EJB.isCallerInRole(\"admin\")=" + secureEJBLocal.isCallerInRole("admin"));
            out.println("@EJB.isCallerInRole(\"manager\")=" + secureEJBLocal.isCallerInRole("manager"));
            out.println("@EJB.isCallerInRole(\"fake\")=" + secureEJBLocal.isCallerInRole("fake"));

            try {
                secureEJBLocal.allowUserMethod();
                out.println("@EJB.allowUserMethod() ALLOWED");
            } catch (EJBAccessException e) {
                out.println("@EJB.allowUserMethod() DENIED");
            }

            try {
                secureEJBLocal.allowManagerMethod();
                out.println("@EJB.allowManagerMethod() ALLOWED");
            } catch (EJBAccessException e) {
                out.println("@EJB.allowManagerMethod() DENIED");
            }

            try {
                secureEJBLocal.allowFakeMethod();
                out.println("@EJB.allowFakeMethod() ALLOWED");
            } catch (EJBAccessException e) {
                out.println("@EJB.allowFakeMethod() DENIED");
            }

            try {
                secureEJBLocal.denyAllMethod();
                out.println("@EJB.denyAllMethod() ALLOWED");
            } catch (EJBAccessException e) {
                out.println("@EJB.denyAllMethod() DENIED");
            }
        }
        out.println();
    }
}
