/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.appserver.webapp.security.saml;

import org.apache.catalina.authenticator.SingleSignOn;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.wso2.appserver.configuration.context.AppServerWebAppConfiguration;
import org.wso2.appserver.configuration.context.WebAppSingleSignOn;
import org.wso2.appserver.configuration.listeners.ContextConfigurationLoader;
import org.wso2.appserver.webapp.security.Constants;
import org.wso2.appserver.webapp.security.agent.SSORequestResolver;
import org.wso2.appserver.webapp.security.utils.SSOUtils;
import org.wso2.appserver.webapp.security.utils.exception.SSOException;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import javax.servlet.ServletException;

/**
 * This class implements an Apache Tomcat Valve, which performs SAML 2.0 based single-sign-on (SSO) and
 * single-logout (SLO) functions.
 * <p>
 * This is a sub-class of the {@code org.apache.catalina.authenticator.SingleSignOn} class.
 *
 * @since 6.0.0
 */
public class SAML2SSOValve extends SingleSignOn {
    //  Holds a reference to the context level single-sign-on configurations representation depending on the
    //  context of the request passed through the invoke method of the Valve.
    private WebAppSingleSignOn contextConfiguration;

    //
    private SSORequestResolver requestResolver;

    /**
     * Performs single-sign-on(SSO) or single-logout(SLO) processing based on the request, using SAML 2.0.
     * <p>
     * This Valve implements SAML 2.0 Web Browser single-sign-on (SSO) and SAML 2.0 single-logout (SLO) Profiles,
     * respectively. This method overrides the invoke() method of the parent {@link SingleSignOn} class.
     *
     * @param request  the servlet request processed
     * @param response the servlet response generated
     * @throws IOException      if an input/output error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        containerLog.debug("Invoking SAML 2.0 single-sign-on valve. Request URI : " + request.getRequestURI());

        Optional<AppServerWebAppConfiguration> contextConfiguration =
                ContextConfigurationLoader.getContextConfiguration(request.getContext());
        if (contextConfiguration.isPresent()) {
            //  retrieves the configuration instance for the context, if exists
            this.contextConfiguration = contextConfiguration.get().getSingleSignOnConfiguration();
            if (this.contextConfiguration == null) {
                if (containerLog.isDebugEnabled()) {
                    containerLog.debug("No context level, single-sign-on configuration found for " +
                            request.getContext() + ", skipping SAML 2.0 based single-sign-on/single-logout...");
                }
                getNext().invoke(request, response);
                return;
            }
        } else {
            //  invokes next valve and moves on to it, if no configuration instance exists
            if (containerLog.isDebugEnabled()) {
                containerLog.debug("No context level configuration found for " +
                        request.getContext() + ", skipping SAML 2.0 based single-sign-on/single-logout...");
            }
            getNext().invoke(request, response);
            return;
        }

        //  checks if single-sign-on feature is enabled
        if (!this.contextConfiguration.isSSOEnabled()) {
            containerLog.debug("SAML 2.0 single-sign-on not enabled in web app " + request.getContext().getName() +
                    ", skipping SAML 2.0 based single-sign-on...");
            //  moves onto the next valve, if single-sign-on is not enabled
            getNext().invoke(request, response);
            return;
        }

        requestResolver = new SSORequestResolver(request, this.contextConfiguration);
        //  if the request URL matches one of the URL(s) to skip, moves on to the next valve
        if (requestResolver.isURLToSkip()) {
            containerLog.debug("Request matched a URL to skip. Skipping...");
            getNext().invoke(request, response);
            return;
        }

        try {
            if (requestResolver.isSAML2SSOResponse()) {
                containerLog.debug("Processing a SAML 2.0 Response...");
                handleResponse(request);

                if (request.getSession(false) != null) {
                    //  handle redirection after being authenticated
                    String relayStateID = (String) request.getSession(false).getAttribute(Constants.RELAY_STATE_ID);
                    Map relayState = (Map) request.getSession(false).getAttribute(relayStateID);

                    String requestURL = (String) relayState.get(Constants.REQUEST_URL);
                    String requestQueryString = (String) relayState.get(Constants.REQUEST_QUERY_STRING);
                    Map requestParameters = (Map) relayState.get(Constants.REQUEST_PARAMETERS);

                    StringBuilder requestedURI = new StringBuilder(requestURL);
                    Optional.ofNullable(requestQueryString)
                            .ifPresent(queryString -> requestedURI.append("?").append(queryString));
                    Optional.ofNullable(requestParameters)
                            .ifPresent(queryParameters -> request.getSession(false).
                                    setAttribute(Constants.REQUEST_PARAM_MAP, queryParameters));
                    response.sendRedirect(requestedURI.toString());
                    return;
                }
            } else if (requestResolver.isSLOURL()) {
                //  handles single logout request initiated directly at the service provider
                containerLog.debug("Processing SAML 2.0 Single Logout URL...");
                handleLogoutRequest(request, response);
                return;
            } else if ((request.getSession(false) == null) ||
                    (request.getSession(false).getAttribute(Constants.SESSION_BEAN) == null)) {
                containerLog.debug("Processing an SAML 2.0 Authentication Request...");
                handleUnauthenticatedRequest(request, response);
                return;
            }
        } catch (SSOException e) {
            containerLog.error("An error has occurred when processing the request", e);
            getNext().invoke(request, response);
        }

        //  moves onto the next valve
        getNext().invoke(request, response);
    }

    /**
     * Handles the unauthenticated requests for all contexts.
     *
     * @param request  the servlet request processed
     * @param response the servlet response generated
     * @throws SSOException if an error occurs when handling an unauthenticated request
     */
    private void handleUnauthenticatedRequest(Request request, Response response) throws SSOException {
        if (contextConfiguration == null) {
            throw new SSOException("Context level configurations may not be initialized");
        }

        if (requestResolver == null) {
            throw new SSOException("SSO Agent request resolver has not been initialized");
        }

        SAML2SSOManager manager = new SAML2SSOManager(contextConfiguration);

        //  setup relay state
        String relayStateID = SSOUtils.createID();
        request.getSession(true).setAttribute(Constants.RELAY_STATE_ID, relayStateID);
        request.getSession(false).setAttribute(relayStateID, SSOUtils.generateRelayState(request));

        contextConfiguration.enableRequestSigning(Optional.ofNullable(contextConfiguration.isRequestSigningEnabled())
                .orElse(false));

        if (requestResolver.isHttpPOSTBinding()) {
            containerLog.debug("Handling the SAML 2.0 Authentication Request for HTTP-POST binding...");
            String htmlPayload = manager.handleAuthenticationRequestForPOSTBinding(request);
            response.setContentType(Constants.CONTENT_TYPE_HTML);
            SSOUtils.sendCharacterData(response, htmlPayload);
        } else {
            containerLog.debug("Handling the SAML 2.0 Authentication Request for " +
                    contextConfiguration.getHttpBinding() + "...");
            try {
                response.sendRedirect(manager.handleAuthenticationRequestForRedirectBinding(request));
            } catch (IOException e) {
                throw new SSOException("Error when handling SAML 2.0 HTTP-Redirect binding", e);
            }
        }
    }

    /**
     * Handles single-sign-on (SSO) and single-logout (SLO) responses.
     *
     * @param request  the servlet request processed
     * @throws SSOException if an error occurs when handling a response
     */
    private void handleResponse(Request request) throws SSOException {
        if (contextConfiguration == null) {
            throw new SSOException("Context level configurations may not be initialized");
        }

        contextConfiguration.enableResponseSigning(Optional.ofNullable(contextConfiguration.isResponseSigningEnabled())
                .orElse(false));
        contextConfiguration.enableAssertionSigning(
                Optional.ofNullable(contextConfiguration.isAssertionSigningEnabled())
                        .orElse(false));
        contextConfiguration.enableAssertionEncryption(
                Optional.ofNullable(contextConfiguration.isAssertionEncryptionEnabled())
                        .orElse(false));

        SAML2SSOManager manager = new SAML2SSOManager(contextConfiguration);
        manager.processResponse(request);
    }

    /**
     * Handles a logout request from a session participant.
     *
     * @param request  the servlet request processed
     * @param response the servlet response generated
     * @throws SSOException if an error occurs when handling a logout request
     */
    private void handleLogoutRequest(Request request, Response response) throws SSOException {
        if (requestResolver == null) {
            throw new SSOException("SSO Agent request resolver has not been initialized");
        }

        SAML2SSOManager manager = new SAML2SSOManager(contextConfiguration);
        try {
            if (requestResolver.isHttpPOSTBinding()) {
                if (request.getSession(false).getAttribute(Constants.SESSION_BEAN) != null) {
                    String htmlPayload = manager.handleLogoutRequestForPOSTBinding(request);
                    response.setContentType(Constants.CONTENT_TYPE_HTML);
                    SSOUtils.sendCharacterData(response, htmlPayload);
                } else {
                    containerLog.warn("Attempt to logout from an already logged out session");
                    response.sendRedirect(request.getContext().getPath());
                }
            } else {
                response.sendRedirect(manager.handleLogoutRequestForRedirectBinding(request));
            }
        } catch (IOException e) {
            throw new SSOException("Error when handling logout request", e);
        }
    }
}
