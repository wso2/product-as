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
// TODO: 9/23/16 change the class name
public class WebAppDeploymentListener implements ServletContextListener {

    private static final Log log = LogFactory.getLog(WebAppDeploymentListener.class);
    private APIScanner apiScanner = new APIScanner();
    private APICreator apiCreator = new APICreator();

    //catch a web app deployment event
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) throws APIEverywhereException {
        ServletContext servletContext = servletContextEvent.getServletContext();

        ContextConfigurationLoader.getContextConfiguration(servletContext)
                .ifPresent(configuration -> {
                    WebAppApiEverywhere apiEverywhereConfiguration = configuration.getApiEverywhereConfiguration();

                    if (apiEverywhereConfiguration != null && apiEverywhereConfiguration.getCreateApi() != null) {
                        if (apiEverywhereConfiguration.getCreateApi()) {
                            apiScanner.scan(servletContext).ifPresent(apiCreateRequest -> {
                                if (apiCreator.isAlive()) {
                                    log.info("Thread is already running!!!");
//                                    while (apiCreator.isAlive()) {
//                                        try {
//                                            Thread.sleep(10);
//                                        } catch (InterruptedException e) {
//                                            log.info("Thread not found: ", e);
//                                            throw new APIEverywhereException("Thread not found", e);
//                                        }
//                                    }

                                }
                                apiCreator.setAPIRequest(apiCreateRequest);
                                apiCreator.start();
                            });
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("Creation of API is blocked by the user.");
                            }
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("No configuration found to set up API creation");
                        }
                    }
                });

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //no change when an web app destroyed.
    }
}
