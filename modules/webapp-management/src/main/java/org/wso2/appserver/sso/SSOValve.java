package org.wso2.appserver.sso;

import org.apache.catalina.Context;
import org.apache.catalina.authenticator.SingleSignOn;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.wso2.appserver.utils.AppServerException;
import org.wso2.appserver.utils.configuration.loaders.ContextConfigurationLoader;
import org.wso2.appserver.utils.configuration.loaders.GlobalConfigurationLoader;
import org.wso2.appserver.utils.configuration.model.Configuration;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SSOValve extends SingleSignOn {
    private static final Logger logger = Logger.getLogger(SSOValve.class.getName());

    public SSOValve() {
        Configuration gc;
        try {
            gc = GlobalConfigurationLoader.getGlobalConfiguration();
        } catch (AppServerException e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO, "Initializing SAMLSSOValve...");
    }

    public void invoke(Request request, Response response) throws IOException, ServletException {
        logger.log(Level.INFO, "Invoking SAMLSSOValve...");

        Map<Context, Configuration> ec = ContextConfigurationLoader.getContextToConfigurationMap();

        logger.log(Level.INFO, "Got it...");
    }
}
