/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.appserver.sample;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is a sample Java Servlet class used for SAML 2.0 SSO Valve demonstration.
 *
 * @since 6.0.0
 */
public class SingleSignOnServlet extends HttpServlet {
    private static final long serialVersionUID = -2819638897469276709L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletName = getServletName();
        printMessage(resp, servletName);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        writer.append("<!DOCTYPE html>\r\n")
                .append("<html>\r\n")
                .append("<head>\r\n")
                .append("<title>MusicStore-App</title>\r\n")
                .append("</head>\r\n");

        writer.append("<h2>");
        writer.append("You have successfully logged out from the Music Store...");
        writer.append("</h2>\r\n");

        writer.append("<div id='footer-area'>");
        writer.append("<p>\u00a9 2016 WSO2</p>");
        writer.append("</div>");

        writer.append("<body>\r\n")
                .append("</body>\r\n")
                .append("</html>\r\n");
    }

    /**
     * Prints a message based on the servlet name.
     *
     * @param response    the {@code HttpServletResponse} instance
     * @param servletName the name of the servlet
     * @throws IOException if an I/O error occurs
     */
    private static void printMessage(HttpServletResponse response, String servletName) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.append("<!DOCTYPE html>\r\n")
                .append("<html>\r\n")
                .append("<head>\r\n")
                .append("<title>MusicStore-App</title>\r\n")
                .append("</head>\r\n")
                .append("<img src='images/cart-logo.gif' alt='Logo' />");

        writer.append("<h2>");
        switch (servletName) {
            case "english":
                writer.append("Welcome to WSO2 Application Server!");
                break;
            case "french":
                writer.append("Bienvenue à WSO2 Application Server!");
                break;
            default:
                break;
        }
        writer.append("</h2>\r\n");

        writer.append("<p><form action='logout'>");
        writer.append("<input type='submit' value='Logout'>");
        writer.append("</form>\r\n</p>\r\n");

        writer.append("<div id='footer-area'>");
        writer.append("<p>©2016 WSO2</p>");
        writer.append("</div>");

        writer.append("<body>\r\n")
                .append("</body>\r\n")
                .append("</html>\r\n");
    }
}
