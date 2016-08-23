package org.wso2.appserver.apieverywhere;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


/**
 *
 */
@WebListener
public class WebAppDeployerListener implements ServletContextListener{

    private static final Log log = LogFactory.getLog(WebAppDeployerListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //APIs of web application have to be extracted
        log.info("A web application is deployed");
        ServletContext servletContext = servletContextEvent.getServletContext();
        servletContext.getServerInfo();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //no channge when an web app destroyed.
    }
}
