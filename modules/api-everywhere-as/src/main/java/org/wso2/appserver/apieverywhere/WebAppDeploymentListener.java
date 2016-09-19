package org.wso2.appserver.apieverywhere;


import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.wso2.appserver.apieverywhere.exceptions.APIEverywhereException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * An implementation of {@code ServletContextListener} that scan deployed web apps
 *
 * @since 6.0.0
 */
public class WebAppDeploymentListener implements ServletContextListener {

    private static final Log log = LogFactory.getLog(WebAppDeploymentListener.class);

    //catch a web app deployment event
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        log.info("new web app is deployed : " + servletContext.getContextPath());

        APIScanner apiScanner = new APIScanner();
        try {
            apiScanner.scan(servletContext);
        } catch (APIEverywhereException e) {
            //what to do here??
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //no change when an web app destroyed.
    }
}
