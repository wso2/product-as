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

import org.apache.catalina.LifecycleException;
import org.apache.catalina.authenticator.SingleSignOn;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.wso2.appserver.configuration.context.ContextConfiguration;
import org.wso2.appserver.configuration.context.SSOConfiguration;
import org.wso2.appserver.configuration.listeners.ContextConfigurationLoader;
import org.wso2.appserver.configuration.listeners.ServerConfigurationLoader;
import org.wso2.appserver.configuration.server.ServerConfiguration;
import org.wso2.appserver.exceptions.AppServerException;
import org.wso2.appserver.webapp.security.sso.agent.SSOAgentConfiguration;
import org.wso2.appserver.webapp.security.sso.agent.SSOAgentRequestResolver;
import org.wso2.appserver.webapp.security.sso.bean.RelayState;
import org.wso2.appserver.webapp.security.sso.saml.signature.SSOX509Credential;
import org.wso2.appserver.webapp.security.sso.utils.SSOConstants;
import org.wso2.appserver.webapp.security.sso.utils.SSOException;
import org.wso2.appserver.webapp.security.sso.utils.SSOUtils;

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

    private ServerConfiguration serverConfiguration;
    private ContextConfiguration contextConfiguration;
    private SSOAgentConfiguration ssoAgentConfiguration;

    public SAMLSSOValve() throws SSOException {
        logger.log(Level.INFO, "Initializing SAML 2.0 based Single-Sign-On Valve...");
    }

    /**
     * Initializes the WSO2 Application Server level configurations.
     *
     * @throws LifecycleException if an error occurs when loading the server level configurations
     */
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        //  Load the global server level configurations
        try {
            serverConfiguration = ServerConfigurationLoader.getGlobalConfiguration();
        } catch (AppServerException e) {
            throw new LifecycleException("Failed to load the server level configurations", e);
        }
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

        //  Load the effective context level configurations
        Optional<ContextConfiguration> configuration = ContextConfigurationLoader.
                retrieveContextConfiguration(request.getContext());
        if (configuration.isPresent()) {
            //  Retrieve the configuration instance if exists
            contextConfiguration = configuration.get();
        } else {
            //  Invokes next valve and move on to it, if no configuration instance exists
            getNext().invoke(request, response);
            return;
        }

        //  Checks if single-sign-on feature is enabled
        if (!contextConfiguration.getSingleSignOnConfiguration().isSSOEnabled()) {
            logger.log(Level.FINE, "SAML 2.0 Single-Sign-On not enabled in webapp " + request.getContext().getName());
            //  Moves onto the next valve, if single-sign-on is not enabled
            getNext().invoke(request, response);
            return;
        }

        SAMLSSOUtils.setDefaultConfigurations(contextConfiguration);

        ssoAgentConfiguration = (SSOAgentConfiguration) (request.getSessionInternal().
                getNote(SSOConstants.SAMLSSOValveConstants.SSO_AGENT_CONFIG));
        if (ssoAgentConfiguration == null) {
            try {
                //  Constructs a new SSOAgentConfiguration instance
                ssoAgentConfiguration = createSSOAgentConfiguration(request.getContextPath());
                request.getSessionInternal().
                        setNote(SSOConstants.SAMLSSOValveConstants.SSO_AGENT_CONFIG, ssoAgentConfiguration);
            } catch (SSOException e) {
                logger.log(Level.SEVERE, "Error on initializing SAML2SSOManager", e);
                return;
            }
        }

        try {
            SSOAgentRequestResolver requestResolver = new SSOAgentRequestResolver(request, ssoAgentConfiguration);

            //  If the request URL matches one of the URL(s) to skip, moves on to the next valve
            if (requestResolver.isURLToSkip()) {
                logger.log(Level.FINE, "Request matched a skip URL. Skipping...");
                getNext().invoke(request, response);
                return;
            }

            SAMLSSOManager samlssoManager;
            if (requestResolver.isSAML2SLORequest()) {

            } else if (requestResolver.isSAML2SSOResponse()) {
                //  Handles single-sign-on responses during the process
                logger.log(Level.FINE, "Processing SSO Response...");
                samlssoManager = new SAMLSSOManager(ssoAgentConfiguration);

                String redirectPath = readAndForgetRedirectPathAfterSLO(request);
                samlssoManager.processResponse(request);
                redirectAfterProcessingResponse(request, response, redirectPath);
                return;
            } else if (requestResolver.isSLOURL()) {

            } else if ((requestResolver.isSAML2SSOURL()) || ((request.getSession(false) == null) || (
                    request.getSession(false).getAttribute(SSOConstants.SAMLSSOValveConstants.SESSION_BEAN) == null))) {
                //  Handles the unauthenticated requests for all contexts
                logger.log(Level.FINE, "Processing SSO URL...");
                samlssoManager = new SAMLSSOManager(ssoAgentConfiguration);

                String relayStateId = SSOUtils.createID();
                RelayState relayState = generateRelayState(request);
                ssoAgentConfiguration.getSAML2().setRelayState(relayStateId);
                Optional.ofNullable(request.getSession(false)).
                        ifPresent(httpSession -> httpSession.setAttribute(relayStateId, relayState));

                ssoAgentConfiguration.getSAML2().setPassiveAuthn(false);
                if (requestResolver.isHttpPostBinding()) {
                    String htmlPayload = samlssoManager.handleAuthnRequestForPOSTBinding(request);
                    samlssoManager.sendCharacterData(response, htmlPayload);
                } else {
                    response.sendRedirect(samlssoManager.handleAuthnRequestForRedirectBinding(request));
                }
                return;
            }

        } catch (SSOException e) {
            logger.log(Level.SEVERE, "An error has occurred", e);
            throw e;
        }

        logger.log(Level.FINE, "End of SAMLSSOValve invoke");

        //  Moves onto the next valve
        getNext().invoke(request, response);
    }

    /**
     * Creates an {@code SSOAgentConfiguration} instance based on the configurations specified.
     *
     * @param contextPath          the context path of the processing {@link Request}
     * @return the created {@link SSOAgentConfiguration}
     * @throws SSOException if an error occurs when creating and validating the {@link SSOAgentConfiguration} instance
     */
    private SSOAgentConfiguration createSSOAgentConfiguration(String contextPath) throws SSOException {
        SSOAgentConfiguration ssoAgentConfiguration = new SSOAgentConfiguration();
        ssoAgentConfiguration.initConfig(serverConfiguration, contextConfiguration);

        ssoAgentConfiguration.getSAML2().
                setSSOAgentX509Credential(new SSOX509Credential(serverConfiguration.getSingleSignOnConfiguration()));

        ssoAgentConfiguration.getSAML2().
                setSPEntityId(Optional.ofNullable(ssoAgentConfiguration.getSAML2().getSPEntityId()).
                        orElse((String) SAMLSSOUtils.generateIssuerID(contextPath).get()));
        ssoAgentConfiguration.getSAML2().
                setACSURL(Optional.ofNullable(ssoAgentConfiguration.getSAML2().getACSURL()).orElse(SAMLSSOUtils.
                        generateConsumerURL(contextPath, contextConfiguration.getSingleSignOnConfiguration()).get()));

        ssoAgentConfiguration.verifyConfig();
        return ssoAgentConfiguration;
    }

    /**
     * Generates a {@code RelayState} based on the {@code Request}.
     *
     * @param request the {@link Request} instance
     * @return the created {@link RelayState} instance
     */
    private RelayState generateRelayState(Request request) {
        RelayState relayState = new RelayState();
        relayState.setRequestedURL(request.getRequestURI());
        relayState.setRequestQueryString(request.getQueryString());
        relayState.setRequestParameters(request.getParameterMap());

        return relayState;
    }

    /**
     * Returns the redirect path after single-logout (SLO), read from the {@code request}.
     * <p>
     * If the redirect path is read from session then it is removed. Priority order of reading the redirect path is from
     * the Session, Context and Config, respectively.
     *
     * @param request       the servlet request processed
     * @return redirect path relative to the current application path
     */
    private String readAndForgetRedirectPathAfterSLO(Request request) {
        //  Reads the redirect path. This has to read before the session get invalidated as it first
        //  tries to read the redirect path from the session attribute
        String redirectPath = null;

        if (request.getSession(false) != null) {
            redirectPath = (String) request.getSession(false).
                    getAttribute(SSOConstants.SAMLSSOValveConstants.REDIRECT_PATH_AFTER_SLO);
            request.getSession(false).removeAttribute(SSOConstants.SAMLSSOValveConstants.REDIRECT_PATH_AFTER_SLO);
        }
        redirectPath = Optional.ofNullable(redirectPath).orElse(request.getContext().
                findParameter(SSOConstants.SAMLSSOValveConstants.REDIRECT_PATH_AFTER_SLO));

        Optional<SSOConfiguration.Property> property = SSOUtils.
                getContextPropertyValue(contextConfiguration.getSingleSignOnConfiguration().getProperties(),
                        SSOConstants.SAMLSSOValveConstants.REDIRECT_PATH_AFTER_SLO);
        if (property.isPresent()) {
            redirectPath = Optional.ofNullable(redirectPath).orElse(property.get().getValue());
        }

        if ((redirectPath != null) && (!redirectPath.isEmpty())) {
            redirectPath = request.getContext().getPath().concat(redirectPath);
        } else {
            redirectPath = request.getContext().getPath();
        }

        logger.log(Level.FINE, "Redirect path = " + redirectPath);

        return redirectPath;
    }

    /**
     * Handles redirection after processing a SAML 2.0 based Response.
     *
     * @param request      the servlet request processed
     * @param response     the servlet response generated
     * @param redirectPath the redirect path obtained before processing a logout response
     * @throws SSOException if an error occurs when redirecting
     */
    private void redirectAfterProcessingResponse(Request request, Response response, String redirectPath)
            throws SSOException {
        //  Redirect according to relay state attribute
        try {
            String relayStateId = ssoAgentConfiguration.getSAML2().getRelayState();
            if ((relayStateId != null) && (request.getSession(false) != null)) {
                RelayState relayState = (RelayState) request.getSession(false).getAttribute(relayStateId);
                if (relayState != null) {
                    request.getSession(false).removeAttribute(relayStateId);
                    StringBuilder requestedURI = new StringBuilder(relayState.getRequestedURL());
                    relayState.getRequestQueryString().
                            ifPresent(queryString -> requestedURI.append("?").append(queryString));
                    relayState.getRequestParameters().ifPresent(queryParameters -> request.getSession(false).
                            setAttribute(SSOConstants.SAMLSSOValveConstants.REQUEST_PARAM_MAP, queryParameters));
                    response.sendRedirect(requestedURI.toString());
                } else {
                    response.sendRedirect(
                            contextConfiguration.getSingleSignOnConfiguration().getApplicationServerURL() + request.
                                    getContextPath());
                }
            } else if (request.getRequestURI().
                    endsWith(contextConfiguration.getSingleSignOnConfiguration().getConsumerURLPostFix())
                    && contextConfiguration.getSingleSignOnConfiguration().handleConsumerURLAfterSLO()) {
                //  Handling redirect from acs page after SLO response. This will be done if
                //  SAMLSSOValveConstants.HANDLE_CONSUMER_URL_AFTER_SLO is defined
                //  SAMLSSOValveConstants.REDIRECT_PATH_AFTER_SLO value is used determine the redirect path
                response.sendRedirect(redirectPath);
            }
        } catch (IOException e) {
            throw new SSOException("Error during redirecting after processing SAML Response", e);
        }
    }
}
