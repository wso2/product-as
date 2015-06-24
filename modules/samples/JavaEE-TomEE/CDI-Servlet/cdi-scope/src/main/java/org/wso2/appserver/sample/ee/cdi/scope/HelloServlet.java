// Scope @PostConstructor @PreDestroy

package org.wso2.appserver.sample.ee.cdi.scope;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "HelloServlet", urlPatterns = "/")
public class HelloServlet extends javax.servlet.http.HttpServlet {
    private static final Log log = LogFactory.getLog(HelloServlet.class);

    @Inject
    @Named("Receptionist")
    private Greeter receptionist;

    @Inject
    @Named("LiftOperator")
    private Greeter lifeOperator;

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        PrintWriter writer = response.getWriter();
        String receptionistGreeting = receptionist.greet();
        String lifeOperatorGreeting = lifeOperator.greet();

        writer.println(receptionistGreeting);
        log.info(receptionistGreeting);

        writer.println(lifeOperatorGreeting);
        log.info(lifeOperatorGreeting);
        writer.close();
    }
}
