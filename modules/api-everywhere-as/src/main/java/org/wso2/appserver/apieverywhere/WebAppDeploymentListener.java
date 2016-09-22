package org.wso2.appserver.apieverywhere;


import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.wso2.appserver.apieverywhere.exceptions.APIEverywhereException;
import org.wso2.appserver.configuration.context.WebAppApiEverywhere;
import org.wso2.appserver.configuration.listeners.ContextConfigurationLoader;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * An implementation of {@code ServletContextListener} that listen to deployment of web app events.
 *
 * @since 6.0.0
 */
public class WebAppDeploymentListener implements ServletContextListener {

    private static final Log log = LogFactory.getLog(WebAppDeploymentListener.class);

    //catch a web app deployment event
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) throws APIEverywhereException {
        ServletContext servletContext = servletContextEvent.getServletContext();
        log.info("New web app is deployed : " + servletContext.getContextPath());

        ContextConfigurationLoader.getContextConfiguration(servletContext)
                .ifPresent(configuration -> {
                    WebAppApiEverywhere apiEverywhereConfiguration = configuration.getApiEverywhereConfiguration();

                    if (apiEverywhereConfiguration != null && apiEverywhereConfiguration.getCreateApi() != null) {
                        if (apiEverywhereConfiguration.getCreateApi()) {
                            APIScanner apiScanner = new APIScanner();
                                apiScanner.scan(servletContext);
                        } else {
                            log.info("Creation of API is blocked by the user.");
                        }
                    } else {
                        log.info("No configuration found to set up API creation");
                    }
                });

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //no change when an web app destroyed.
    }
}
