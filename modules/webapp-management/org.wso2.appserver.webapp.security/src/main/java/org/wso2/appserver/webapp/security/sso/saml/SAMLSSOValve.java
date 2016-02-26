/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.appserver.webapp.security.sso.saml;

import org.apache.catalina.authenticator.SingleSignOn;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.wso2.appserver.configuration.context.ContextConfiguration;
import org.wso2.appserver.configuration.listeners.ContextConfigurationLoader;
import org.wso2.appserver.configuration.listeners.ServerConfigurationLoader;
import org.wso2.appserver.configuration.server.ServerConfiguration;
import org.wso2.appserver.exceptions.AppServerException;
import org.wso2.appserver.webapp.security.sso.utils.SSOException;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;

/**
 * This class implements an Apache Tomcat valve, which performs SAML 2.0 based single-sign-on (SSO) function.
 * <p>
 * This is a sub-class of the {@code org.apache.catalina.authenticator.SingleSignOn} class.
 *
 * @since 6.0.0
 */
public class SAMLSSOValve extends SingleSignOn {
    private static final Logger logger = Logger.getLogger(SAMLSSOValve.class.getName());

    public SAMLSSOValve() throws SSOException {
        logger.log(Level.INFO, "Initializing SAML 2.0 based Single-Sign-On Valve...");
    }

    /**
     * Performs single-sign-on (SSO) processing for this request using SAML 2.0 protocol.
     * <p>
     * This method overrides the parent {@link SingleSignOn} class' invoke() method.
     *
     * @param request  the servlet request processed
     * @param response the servlet response generated
     * @throws IOException      if an input/output error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        logger.log(Level.FINE, "Invoking SAMLSSOValve. Request URI : " + request.getRequestURI());

        ServerConfiguration serverConfiguration;
        try {
            serverConfiguration = ServerConfigurationLoader.getGlobalConfiguration();
        } catch (AppServerException e) {
            throw new SSOException("failed to load the server level configuration", e);
        }

        Optional<ContextConfiguration> configuration = ContextConfigurationLoader.
                retrieveContextConfiguration(request.getContext());
        ContextConfiguration contextConfiguration;
        if (configuration.isPresent()) {
            //  Retrieves the configuration instance if exists
            contextConfiguration = configuration.get();
        } else {
            //  Invokes next valve and move on to it, if no configuration instance exists
            getNext().invoke(request, response);
            return;
        }



        logger.log(Level.FINE, "End of SAMLSSOValve invoke.");

        //  Moves onto the next valve
        getNext().invoke(request, response);
    }
}
