// @Produces

package org.wso2.appserver.sample.ee.cdi.produces;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "HelloServlet", urlPatterns = "/")
public class HelloServlet extends javax.servlet.http.HttpServlet {

    @Inject
    @Greetings(GreetingType.HI)
    private Greeter greeter1;

    @Inject
    @Greetings(GreetingType.BYE)
    private Greeter greeter2;

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
            throws javax.servlet.ServletException, IOException {
        PrintWriter writer = response.getWriter();
        writer.println(greeter1.greet());
        writer.println(greeter2.greet());
        writer.close();
    }
}
