package org.wso2.appserver.sample.ee.jta.servlet;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MoneyTransferServlet", urlPatterns = "/")
public class Servlet extends HttpServlet {

    @Inject
    AccountManagerBean accountManagerBean;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("Please have a look at the terminal to see the output");
        accountManagerBean.transfer(100);
    }
}
