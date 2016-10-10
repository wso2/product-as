package org.wso2.appserver.apieverywhere;


import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.wso2.appserver.apieverywhere.exceptions.APIEverywhereException;
import org.wso2.appserver.apieverywhere.utils.APICreateRequest;
import org.wso2.appserver.configuration.context.WebAppApiEverywhere;
import org.wso2.appserver.configuration.listeners.ContextConfigurationLoader;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


/**
 * An implementation of {@code ServletContextListener} that listen to deployment of web app events.
 *
 * @since 6.0.0
 */
@WebListener
public class APIEverywhereInitiator implements ServletContextListener {

    private static final Log log = LogFactory.getLog(APIEverywhereInitiator.class);
    private ConfigScanner configScanner = new ConfigScanner();
    private APIBuilder apiBuilder = new APIBuilder();
    private APICreator apiCreator = new APICreator();
    private APICreateRequest apiCreateRequest = new APICreateRequest();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    //catch a web app deployment event
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) throws APIEverywhereException {
        ServletContext servletContext = servletContextEvent.getServletContext();
        ContextConfigurationLoader.getContextConfiguration(servletContext)
                .ifPresent(configuration -> {
                    WebAppApiEverywhere apiEverywhereConfiguration = configuration.getApiEverywhereConfiguration();

                    if (apiEverywhereConfiguration != null && apiEverywhereConfiguration.getCreateApi() != null) {
                        if (apiEverywhereConfiguration.getCreateApi()) {
                            HashMap<String, StringBuilder> beanParams = configScanner.scanConfigs(servletContext);
                            apiCreateRequest.setContext(servletContext.getContextPath());
                            apiCreateRequest.setName(servletContext.getContextPath().substring(1));
                            if (!beanParams.isEmpty()) {
                                String apiRequest = apiBuilder.build(beanParams, apiCreateRequest);
                                apiCreator.addAPIRequest(apiRequest);
                                executor.execute(apiCreator);
                                executor.shutdown();
                            }
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
