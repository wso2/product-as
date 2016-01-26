package org.wso2.appserver.hibernate.jndi.sample.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.wso2.appserver.hibernate.jndi.sample.listener.model.Employee;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet("/EmployeeManager")
public class EmployeeManager extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static Log log = LogFactory.getLog(EmployeeManager.class);

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            log.error("Can't find h2 Driver", e);
        }
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdbforjndi", "sa", "");
             Statement statement = conn.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Employee(" +
                                    "id integer NOT NULL AUTO_INCREMENT, " +
                                    "name varchar(20) DEFAULT NULL, " +
                                    "PRIMARY KEY (id))");
        } catch (SQLException e) {
            log.error("SQLException", e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        SessionFactory sessionFactory = (SessionFactory) request.getServletContext().getAttribute("SessionFactory");
        Session session = sessionFactory.getCurrentSession();

        Transaction tx = session.beginTransaction();
        String empName = request.getParameter("empName");
        Employee employee = new Employee();
        employee.setName(empName);
        session.persist(employee);
        tx.commit();

        PrintWriter out = response.getWriter();
        out.print("Successfully persist the Employee");
    }

}
