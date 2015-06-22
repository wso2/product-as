// Scope @PostConstructor @PreDestroy

package org.wso2.appserver.sample.ee.cdi.event;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "HelloServlet", urlPatterns = "/")
public class HelloServlet extends javax.servlet.http.HttpServlet {

    @Inject
    @Named("Receptionist")
    private Greeter receptionist;

    @Inject
    @Named("LiftOperator")
    private Greeter lifeOperator;

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        PrintWriter writer = response.getWriter();
        writer.println(receptionist.greet());
        writer.println(lifeOperator.greet());
        writer.close();
    }
}
