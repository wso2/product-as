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

import org.apache.xml.security.signature.XMLSignature;
import org.joda.time.DateTime;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.common.Extensions;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.AudienceRestriction;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.EncryptedAssertion;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.RequestAbstractType;
import org.opensaml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.SessionIndex;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.LogoutRequestBuilder;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.opensaml.saml2.core.impl.SessionIndexBuilder;
import org.opensaml.saml2.ecp.RelayState;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.validation.ValidationException;
import org.wso2.appserver.webapp.security.sso.Constants;
import org.wso2.appserver.webapp.security.sso.agent.SSOAgentConfiguration;
import org.wso2.appserver.webapp.security.sso.agent.SSOAgentSessionManager;
import org.wso2.appserver.webapp.security.sso.bean.LoggedInSession;
import org.wso2.appserver.webapp.security.sso.saml.signature.SignatureValidator;
import org.wso2.appserver.webapp.security.sso.saml.signature.X509CredentialImplementation;
import org.wso2.appserver.webapp.security.sso.utils.SSOAgentDataHolder;
import org.wso2.appserver.webapp.security.sso.utils.SSOUtils;
import org.wso2.appserver.webapp.security.sso.utils.exception.SSOException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This class manages the generation of varied request and response types that are utilized
 * within the SAML 2.0 single-sign-on (SSO) and single-logout (SLO) process.
 *
 * @since 6.0.0
 */
public class SAMLSSOManager {
    private static final Logger logger = Logger.getLogger(SSOUtils.class.getName());

    private SSOAgentConfiguration ssoAgentConfiguration;

    public SAMLSSOManager(SSOAgentConfiguration ssoAgentConfiguration) throws SSOException {
        this.ssoAgentConfiguration = ssoAgentConfiguration;
        loadCustomSignatureValidatorClass();
        SSOUtils.doBootstrap();
    }

    /**
     * Loads a custom signature validator class specified in the SSO Agent configurations.
     */
    private void loadCustomSignatureValidatorClass() {
        //  Load custom Signature Validator Class
        Optional.ofNullable(ssoAgentConfiguration).ifPresent(
                agent -> Optional.ofNullable(agent.getSAML2().getSignatureValidatorImplClass()).ifPresent(implClass -> {
                    try {
                        SSOAgentDataHolder.getInstance().
                                setObject(Class.forName(implClass).newInstance());
                    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                        logger.log(Level.SEVERE, "Error loading custom signature validator class", e);
                    }
                }));
    }

    /**
     * Handles a SAML 2.0 Authentication Request (AuthnRequest) for SAML 2.0 HTTP POST binding.
     *
     * @param request the HTTP servlet request with SAML message
     * @return the HTML payload to be transmitted
     * @throws SSOException if an error occurs when handling AuthnRequest
     */
    protected String handleAuthnRequestForPOSTBinding(HttpServletRequest request) throws SSOException {
        RequestAbstractType requestMessage = buildAuthnRequest(request);
        if (ssoAgentConfiguration.getSAML2().isRequestSigned()) {
            requestMessage = SSOUtils.
                    setSignature((AuthnRequest) requestMessage, XMLSignature.ALGO_ID_SIGNATURE_RSA,
                            new X509CredentialImplementation(
                                    ssoAgentConfiguration.getSAML2().getSSOAgentX509Credential()));
        }

        return preparePOSTRequest(requestMessage);
    }

    /**
     * Handles a SAML 2.0 Logout Request (LogoutRequest) for SAML 2.0 HTTP POST binding.
     *
     * @param request the HTTP servlet request with SAML 2.0 message
     * @return the HTML payload to be transmitted
     * @throws SSOException if an error occurs when handling LogoutRequest
     */
    protected String handleLogoutRequestForPOSTBinding(HttpServletRequest request) throws SSOException {
        LoggedInSession session = (LoggedInSession) request.getSession(false).
                getAttribute(Constants.SESSION_BEAN);
        RequestAbstractType requestMessage;
        if (session != null) {
            requestMessage = buildLogoutRequest(session.getSAML2SSO().getSubjectId(),
                    session.getSAML2SSO().getSessionIndex());
            if (ssoAgentConfiguration.getSAML2().isRequestSigned()) {
                requestMessage = SSOUtils.
                        setSignature((LogoutRequest) requestMessage, XMLSignature.ALGO_ID_SIGNATURE_RSA,
                                new X509CredentialImplementation(
                                        ssoAgentConfiguration.getSAML2().getSSOAgentX509Credential()));
            }
        } else {
            throw new SSOException(
                    "Single-logout (SLO) Request cannot be built. Single-sign-on (SSO) Session is null.");
        }

        return preparePOSTRequest(requestMessage);
    }

    /**
     * Handles the specified {@code RequestAbstractType} for SAML 2.0 HTTP POST binding.
     *
     * @param rawRequestMessage the {@link RequestAbstractType} which is either a SAML 2.0 AuthnRequest or
     *                          a SAML 2.0 LogoutRequest
     * @return the HTML payload string
     * @throws SSOException if an error occurs when encoding the request message
     */
    private String preparePOSTRequest(RequestAbstractType rawRequestMessage) throws SSOException {
        String encodedRequestMessage = SSOUtils.
                encodeRequestMessage(rawRequestMessage, SAMLConstants.SAML2_POST_BINDING_URI);

        Map<String, String[]> parameters = new HashMap<>();
        parameters.
                put(Constants.HTTP_POST_PARAM_SAML_REQUEST, new String[] { encodedRequestMessage });
        if (ssoAgentConfiguration.getSAML2().getRelayState() != null) {
            parameters.put(RelayState.DEFAULT_ELEMENT_LOCAL_NAME,
                    new String[] { ssoAgentConfiguration.getSAML2().getRelayState() });
        }

        //  Add any additional parameters defined
        if ((ssoAgentConfiguration.getQueryParameters() != null) && (!ssoAgentConfiguration.
                getQueryParameters().isEmpty())) {
            parameters.putAll(ssoAgentConfiguration.getQueryParameters());
        }

        StringBuilder htmlParameters = new StringBuilder();
        parameters.entrySet().stream().
                filter(entry -> ((entry.getKey() != null) &&
                        (entry.getValue() != null) && (entry.getValue().length > 0))).
                forEach(filteredEntry -> Stream.of(filteredEntry.getValue()).
                        forEach(parameter -> htmlParameters.append("<input type='hidden' name='").
                                append(filteredEntry.getKey()).append("' value='").append(parameter).append("'>\n")));

        return "<html>\n" +
                "<body>\n" +
                "<p>You are now redirected back to " + ssoAgentConfiguration.getSAML2().getIdPURL() + " \n" +
                "If the redirection fails, please click the post button.</p>\n" +
                "<form method='post' action='" + ssoAgentConfiguration.getSAML2().getIdPURL() + "'>\n" +
                "<p>\n" +
                htmlParameters.toString() +
                "<button type='submit'>POST</button>\n" +
                "</p>\n" +
                "</form>\n" +
                "<script type='text/javascript'>\n" +
                "document.forms[0].submit();\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";
    }

    /**
     * Handles a SAML 2.0 Authentication Request (AuthnRequest) for SAML 2.0 HTTP Redirect binding.
     *
     * @param request the HTTP servlet request with SAML 2.0 message
     * @return the Identity Provider URL with the query string appended based on the SAML 2.0 Request and configurations
     * @throws SSOException if an error occurs when handling AuthnRequest
     */
    protected String handleAuthnRequestForRedirectBinding(HttpServletRequest request) throws SSOException {
        RequestAbstractType requestMessage = buildAuthnRequest(request);
        return prepareRedirectRequest(requestMessage);
    }

    /**
     * Handles a SAML 2.0 Logout Request (LogoutRequest) for SAML 2.0 HTTP Redirect binding.
     *
     * @param request the HTTP servlet request with SAML message
     * @return the Identity Provider URL with the query string appended based on the SAML 2.0 Request and configurations
     * @throws SSOException if an error occurs when handling LogoutRequest
     */
    protected String handleLogoutRequestForRedirectBinding(HttpServletRequest request) throws SSOException {
        LoggedInSession session = (LoggedInSession) request.getSession(false).
                getAttribute(Constants.SESSION_BEAN);
        RequestAbstractType requestMessage;
        if (session != null) {
            requestMessage = buildLogoutRequest(session.getSAML2SSO().getSubjectId(),
                    session.getSAML2SSO().getSessionIndex());
        } else {
            throw new SSOException("Single Logout Request can not be built, single-sign-on session is null");
        }

        return prepareRedirectRequest(requestMessage);
    }

    /**
     * Handles the specified {@code RequestAbstractType} for SAML 2.0 Redirect POST binding.
     *
     * @param rawRequestMessage the {@link RequestAbstractType} which is either a SAML 2.0 AuthnRequest or
     *                          a SAML 2.0 LogoutRequest
     * @return the Identity Provider URL with the query string appended based on the SAML 2.0 Request and configurations
     * @throws SSOException if an error occurs when preparing the HTTP Redirect request
     */
    private String prepareRedirectRequest(RequestAbstractType rawRequestMessage) throws SSOException {
        //  Compress the message using default DEFLATE encoding since SAMLEncoding query string parameter
        //  is not specified, perform Base64 encoding and then URL encoding
        String encodedRequestMessage = SSOUtils.
                encodeRequestMessage(rawRequestMessage, SAMLConstants.SAML2_REDIRECT_BINDING_URI);
        StringBuilder httpQueryString = new StringBuilder(Constants.HTTP_POST_PARAM_SAML_REQUEST +
                "=" + encodedRequestMessage);

        //  Arrange the query string if any RelayState data is to accompany the SAML protocol message
        String relayState = ssoAgentConfiguration.getSAML2().getRelayState();
        if (relayState != null) {
            try {
                httpQueryString.append("&").append(RelayState.DEFAULT_ELEMENT_LOCAL_NAME).append("=").
                        append(URLEncoder.encode(relayState, "UTF-8").trim());
            } catch (UnsupportedEncodingException e) {
                throw new SSOException("Error occurred while URLEncoding " + RelayState.DEFAULT_ELEMENT_LOCAL_NAME, e);
            }
        }

        //  Add any additional parameters defined
        if ((ssoAgentConfiguration.getQueryParameters() != null) && (!ssoAgentConfiguration.
                getQueryParameters().isEmpty())) {
            StringBuilder builder = new StringBuilder();
            ssoAgentConfiguration.getQueryParameters().entrySet().stream().
                    filter(entry -> ((entry.getKey() != null) &&
                            (entry.getValue() != null) && (entry.getValue().length > 0))).
                    forEach(filteredEntry -> Stream.of(filteredEntry.getValue()).
                            forEach(parameter -> builder.append("&").append(filteredEntry.getKey()).
                                    append("=").append(parameter)));
            httpQueryString.append(builder);
        }

        if (ssoAgentConfiguration.getSAML2().isRequestSigned()) {
            SSOUtils.addDeflateSignatureToHTTPQueryString(httpQueryString,
                    new X509CredentialImplementation(ssoAgentConfiguration.getSAML2().getSSOAgentX509Credential()));
        }

        String idpUrl;
        if (ssoAgentConfiguration.getSAML2().getIdPURL().contains("?")) {
            idpUrl = ssoAgentConfiguration.getSAML2().getIdPURL().concat("&").concat(httpQueryString.toString());
        } else {
            idpUrl = ssoAgentConfiguration.getSAML2().getIdPURL().concat("?").concat(httpQueryString.toString());
        }
        return idpUrl;
    }

    /**
     * Returns a SAML 2.0 Authentication Request (AuthnRequest) instance based on the HTTP servlet request.
     *
     * @param request the HTTP servlet request used to build up the Authentication Request
     * @return a SAML 2.0 Authentication Request (AuthnRequest) instance
     */
    private AuthnRequest buildAuthnRequest(HttpServletRequest request) {
        //  Issuer identifies the entity that generated the request message
        Issuer issuer = new IssuerBuilder().buildObject();
        issuer.setValue(ssoAgentConfiguration.getSAML2().getSPEntityId());

        //  NameIDPolicy element tailors the subject name identifier of assertions resulting from AuthnRequest
        NameIDPolicy nameIdPolicy = new NameIDPolicyBuilder().buildObject();
        //  URI reference corresponding to a name identifier format
        nameIdPolicy.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
        //  Unique identifier of the service provider or affiliation of providers for whom the identifier was generated
        nameIdPolicy.setSPNameQualifier("Issuer");
        //  Identity provider is allowed, in the course of fulfilling the request to generate a new identifier to
        //  represent the principal
        nameIdPolicy.setAllowCreate(true);

        //  This represents a URI reference identifying an authentication context class that describes the
        //  authentication context declaration that follows
        AuthnContextClassRef authnContextClassRef = new AuthnContextClassRefBuilder().buildObject();
        authnContextClassRef.
                setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");

        //  Specifies the authentication context requirements of authentication statements returned in response
        //  to a request or query
        RequestedAuthnContext requestedAuthnContext = new RequestedAuthnContextBuilder().buildObject();
        //  Resulting authentication context in the authentication statement must be the exact match of the
        //  authentication context specified
        requestedAuthnContext.setComparison(AuthnContextComparisonTypeEnumeration.EXACT);
        requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRef);

        DateTime issueInstant = new DateTime();

        //  Create an AuthnRequest instance
        AuthnRequest authnRequest = new AuthnRequestBuilder().buildObject();

        authnRequest.setForceAuthn(ssoAgentConfiguration.getSAML2().isForceAuthn());
        authnRequest.setIsPassive(ssoAgentConfiguration.getSAML2().isPassiveAuthn());
        authnRequest.setIssueInstant(issueInstant);
        authnRequest.setProtocolBinding(ssoAgentConfiguration.getSAML2().getHttpBinding());
        authnRequest.setAssertionConsumerServiceURL(ssoAgentConfiguration.getSAML2().getACSURL());
        authnRequest.setIssuer(issuer);
        authnRequest.setNameIDPolicy(nameIdPolicy);
        authnRequest.setRequestedAuthnContext(requestedAuthnContext);
        authnRequest.setID(SSOUtils.createID());
        authnRequest.setVersion(SAMLVersion.VERSION_20);
        authnRequest.setDestination(ssoAgentConfiguration.getSAML2().getIdPURL());

        //  If any optional protocol message extension elements that are agreed on between the communicating parties
        if (request.getAttribute(Extensions.LOCAL_NAME) != null) {
            authnRequest.setExtensions((Extensions) request.getAttribute(Extensions.LOCAL_NAME));
        }

        //  Requesting SAML Attributes which the requester desires to be supplied by the identity provider,
        //  this Index value is registered in the identity provider
        String index = ssoAgentConfiguration.getSAML2().getAttributeConsumingServiceIndex();
        if ((index != null) && !(index.trim().isEmpty())) {
            authnRequest.setAttributeConsumingServiceIndex(Integer.parseInt(index));
        }

        return authnRequest;
    }

    /**
     * Returns a SAML 2.0 Logout Request (LogoutRequest) instance.
     *
     * @param user         the identifier that specify the principal as currently recognized by the identity and
     *                     service providers
     * @param sessionIndex the identifier that indexes this session at the message recipient
     * @return a SAML 2.0 Logout Request (LogoutRequest) instance
     */
    private LogoutRequest buildLogoutRequest(String user, String sessionIndex) {
        //  Creates a Logout Request instance
        LogoutRequest logoutRequest = new LogoutRequestBuilder().buildObject();

        logoutRequest.setID(SSOUtils.createID());
        logoutRequest.setDestination(ssoAgentConfiguration.getSAML2().getIdPURL());

        DateTime issueInstant = new DateTime();
        logoutRequest.setIssueInstant(issueInstant);
        //  Time at which the request expires, after which the recipient may discard the message
        logoutRequest.setNotOnOrAfter(new DateTime(issueInstant.getMillis() + (5 * 60 * 1000)));

        Issuer issuer = new IssuerBuilder().buildObject();
        issuer.setValue(ssoAgentConfiguration.getSAML2().getSPEntityId());
        logoutRequest.setIssuer(issuer);

        NameID nameId = new NameIDBuilder().buildObject();
        nameId.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:entity");
        nameId.setValue(user);
        logoutRequest.setNameID(nameId);

        SessionIndex sessionIndexElement = new SessionIndexBuilder().buildObject();
        sessionIndexElement.setSessionIndex(sessionIndex);
        logoutRequest.getSessionIndexes().add(sessionIndexElement);

        //  Indicates the reason for the logout
        logoutRequest.setReason("Single Logout");

        return logoutRequest;
    }

    /**
     * Sends character data specified by the {@code htmlPayload} in the servlet response body.
     *
     * @param response    the servlet response body in which character data are to be sent
     * @param htmlPayload the character data to be sent in the servlet body
     * @throws SSOException if an error occurs while writing character data to the servlet
     *                      response body
     */
    protected void sendCharacterData(HttpServletResponse response, String htmlPayload) throws SSOException {
        try {
            Writer writer = response.getWriter();
            writer.write(htmlPayload);
            response.flushBuffer();
            //  Not closing the Writer instance, as its creator is the HttpServletResponse
        } catch (IOException e) {
            throw new SSOException("Error occurred while writing to HttpServletResponse", e);
        }
    }

    /**
     * Processes a SAML 2.0 response depending on its type, either a SAML 2.0 Response for a single-sign-on (SSO)
     * SAML 2.0 Request by the client application or a SAML 2.0 Response for a single-logout (SLO) SAML 2.0 Request
     * from a service provider.
     *
     * @param request the servlet request processed
     * @throws SSOException if SAML 2.0 response is null
     */
    protected void processResponse(HttpServletRequest request) throws SSOException {
        String saml2SSOResponse = request.getParameter(Constants.HTTP_POST_PARAM_SAML_RESPONSE);

        if (saml2SSOResponse != null) {
            String decodedResponse = new String(Base64.decode(saml2SSOResponse), Charset.forName("UTF-8"));
            XMLObject samlObject = SSOUtils.unmarshall(decodedResponse);
            if (samlObject instanceof LogoutResponse) {
                //  This is a SAML response for a single logout request from the service provider
                performSingleLogout(request);
            } else {
                processSingleSignInResponse(request);
            }
            String relayState = request.getParameter(RelayState.DEFAULT_ELEMENT_LOCAL_NAME);

            if ((relayState != null) && (!relayState.isEmpty()) && (!("null").equalsIgnoreCase(relayState))) {
                //  Additional checks for incompetent identity providers
                ssoAgentConfiguration.getSAML2().setRelayState(relayState);
            }
        } else {
            throw new SSOException("Invalid SAML Response. SAML Response cannot be null");
        }
    }

    /**
     * Processes a single-sign-in SAML 2.0 Response received for an Authentication Request sent.
     *
     * @param request the HTTP servlet request used to process the SAML 2.0 Response
     * @throws SSOException if the received SAML 2.0 Response is invalid
     */
    private void processSingleSignInResponse(HttpServletRequest request) throws SSOException {
        LoggedInSession session = new LoggedInSession();
        session.setSAML2SSO(new LoggedInSession.SAML2SSO());

        String saml2ResponseString = new String(
                Base64.decode(request.getParameter(Constants.HTTP_POST_PARAM_SAML_RESPONSE)),
                Charset.forName("UTF-8"));
        Response saml2Response = (Response) SSOUtils.unmarshall(saml2ResponseString);
        session.getSAML2SSO().setResponseString(saml2ResponseString);
        session.getSAML2SSO().setSAMLResponse(saml2Response);

        Assertion assertion = null;
        if (ssoAgentConfiguration.getSAML2().isAssertionEncrypted()) {
            List<EncryptedAssertion> encryptedAssertions = saml2Response.getEncryptedAssertions();
            EncryptedAssertion encryptedAssertion;
            if (!SSOUtils.isCollectionEmpty(encryptedAssertions)) {
                encryptedAssertion = encryptedAssertions.stream().findFirst().get();
                try {
                    assertion = SSOUtils
                            .decryptAssertion(ssoAgentConfiguration.getSAML2().getSSOAgentX509Credential(),
                                    encryptedAssertion);
                } catch (Exception e) {
                    logger.log(Level.FINE, "Assertion decryption failure : ", e);
                    throw new SSOException("Unable to decrypt the SAML Assertion");
                }
            }
        } else {
            List<Assertion> assertions = saml2Response.getAssertions();
            if (!SSOUtils.isCollectionEmpty(assertions)) {
                assertion = assertions.stream().findFirst().get();
            }
        }
        if (assertion == null) {
            if (isNoPassive(saml2Response)) {
                logger.log(Level.FINE, "Cannot authenticate in passive mode");
                return;
            }
            throw new SSOException("SAML Assertion not found in the Response");
        }

        String idPEntityIdValue = assertion.getIssuer().getValue();
        if ((idPEntityIdValue == null) || (idPEntityIdValue.isEmpty())) {
            throw new SSOException("SAML Response does not contain an Issuer value");
        } else if (!idPEntityIdValue.equals(ssoAgentConfiguration.getSAML2().getIdPEntityId())) {
            throw new SSOException("SAML Response Issuer verification failed");
        }
        session.getSAML2SSO().setAssertion(assertion);
        //  Cannot marshall SAML assertion here, before signature validation due to an issue in OpenSAML

        //  Gets the subject name from the Response Object and forward it to login_action.jsp
        String subject = null;
        if ((assertion.getSubject() != null) && (assertion.getSubject().getNameID() != null)) {
            subject = assertion.getSubject().getNameID().getValue();
        }
        if (subject == null) {
            throw new SSOException("SAML Response does not contain the name of the subject");
        }

        //  Sets the subject in the session bean
        session.getSAML2SSO().setSubjectId(subject);
        request.getSession().setAttribute(Constants.SESSION_BEAN, session);

        //  Validates the audience restriction
        validateAudienceRestriction(assertion);

        //  Validates the signature
        validateSignature(saml2Response, assertion);

        //  Marshalling SAML2 assertion after signature validation due to a weird issue in OpenSAML
        session.getSAML2SSO().setAssertionString(SSOUtils.marshall(assertion));

        ((LoggedInSession) request.getSession().getAttribute(Constants.SESSION_BEAN))
                .getSAML2SSO().
                setSubjectAttributes(SSOUtils.getAssertionStatements(assertion));

        //  For removing the session when the single-logout request made by the service provider itself
        if (ssoAgentConfiguration.getSAML2().isSLOEnabled()) {
            String sessionId = assertion.getAuthnStatements().stream().findFirst().get().getSessionIndex();
            if (sessionId == null) {
                throw new SSOException("Single Logout is enabled but IdP Session ID not found in SAML Assertion");
            }
            ((LoggedInSession) request.getSession().
                    getAttribute(Constants.SESSION_BEAN)).getSAML2SSO()
                    .setSessionIndex(sessionId);
            SSOAgentSessionManager.addAuthenticatedSession(request.getSession(false));
        }

        request.getSession().setAttribute(Constants.SESSION_BEAN, session);
    }

    /**
     * Returns true if the identity provider cannot authenticate the principal passively, as requested, else false.
     *
     * @param response the SAML 2.0 Response to be evaluated
     * @return true if the identity provider cannot authenticate the principal passively, as requested, else false
     */
    private boolean isNoPassive(Response response) {
        return (response.getStatus() != null) && (response.getStatus().getStatusCode() != null) &&
                (response.getStatus().getStatusCode().getValue().equals(StatusCode.RESPONDER_URI)) &&
                (response.getStatus().getStatusCode().getStatusCode() != null) &&
                (response.getStatus().getStatusCode().getStatusCode().getValue().equals(StatusCode.NO_PASSIVE_URI));
    }

    /**
     * Validates the SAML 2.0 Audience Restrictions set in the specified SAML 2.0 Assertion.
     *
     * @param assertion the SAML 2.0 Assertion in which Audience Restrictions' validity is checked for
     * @throws SSOException if the Audience Restriction validation fails
     */
    private void validateAudienceRestriction(Assertion assertion) throws SSOException {
        if (assertion == null) {
            return;
        }

        Conditions conditions = assertion.getConditions();
        if (conditions == null) {
            throw new SSOException("SAML2 Response doesn't contain Conditions.");
        }

        List<AudienceRestriction> audienceRestrictions = conditions.getAudienceRestrictions();
        if ((audienceRestrictions == null) || (audienceRestrictions.isEmpty())) {
            throw new SSOException("SAML2 Response doesn't contain AudienceRestrictions.");
        }

        Stream<AudienceRestriction> audienceExistingStream = audienceRestrictions.stream().
                filter(audienceRestriction ->
                        (((audienceRestriction.getAudiences() != null) && (!audienceRestriction.getAudiences().
                                isEmpty()))) && (audienceRestriction.getAudiences().stream().
                                filter(audience -> ssoAgentConfiguration.getSAML2().getSPEntityId().
                                        equals(audience.getAudienceURI()))).count() > 0);

        if (audienceExistingStream.count() == 0) {
            throw new SSOException("SAML2 Assertion Audience Restriction validation failed.");
        }
    }

    /**
     * Validates the XML Digital Signature of specified SAML 2.0 based Response and Assertion.
     *
     * @param response  the SAML based Response whose XML Digital Signature is to be validated
     * @param assertion the SAML based Assertion whose XML Digital Signature is to be validated
     * @throws SSOException if an error occurs during the signature validation
     */
    private void validateSignature(Response response, Assertion assertion) throws SSOException {
        if (SSOAgentDataHolder.getInstance().getObject() != null) {
            //  Custom implementation of signature validation
            SignatureValidator signatureValidatorUtility = (SignatureValidator) SSOAgentDataHolder.
                    getInstance().getObject();
            signatureValidatorUtility.validateSignature(response, assertion, ssoAgentConfiguration);
        } else {
            //  If custom implementation not found, execute the default implementation
            if (ssoAgentConfiguration.getSAML2().isResponseSigned()) {
                if (response.getSignature() == null) {
                    throw new SSOException("SAML 2.0 Response signing is enabled, but signature element not "
                            + "found in SAML 2.0 Response element.");
                } else {
                    try {
                        org.opensaml.xml.signature.SignatureValidator validator =
                                new org.opensaml.xml.signature.SignatureValidator(new X509CredentialImplementation(
                                        ssoAgentConfiguration.getSAML2().getSSOAgentX509Credential()));
                        validator.validate(response.getSignature());
                    } catch (ValidationException e) {
                        logger.log(Level.FINE, "Validation exception : ", e);
                        throw new SSOException("Signature validation failed for SAML Response.");
                    }
                }
            }
            if (ssoAgentConfiguration.getSAML2().isAssertionSigned()) {
                if (assertion.getSignature() == null) {
                    throw new SSOException("SAML 2.0 Assertion signing is enabled, but signature element not "
                            + "found in SAML 2.0 Assertion element.");
                } else {
                    try {
                        org.opensaml.xml.signature.SignatureValidator validator =
                                new org.opensaml.xml.signature.SignatureValidator(new X509CredentialImplementation(
                                        ssoAgentConfiguration.getSAML2().getSSOAgentX509Credential()));
                        validator.validate(assertion.getSignature());
                    } catch (ValidationException e) {
                        logger.log(Level.FINE, "Validation exception : ", e);
                        throw new SSOException("Signature validation failed for SAML 2.0 Assertion.");
                    }
                }
            }
        }
    }

    /**
     * Performs single-logout (SLO) function based on the HTTP servlet request.
     *
     * @param request the HTTP servlet request
     * @throws SSOException if the SAML 2.0 Single Logout Request/Response is invalid
     */
    protected void performSingleLogout(HttpServletRequest request) throws SSOException {
        XMLObject saml2Object = null;

        if (request.getParameter(Constants.HTTP_POST_PARAM_SAML_REQUEST) != null) {
            saml2Object = SSOUtils.unmarshall(
                    new String(Base64.decode(request.getParameter(Constants.HTTP_POST_PARAM_SAML_REQUEST)),
                            Charset.forName("UTF-8")));
        }
        if (saml2Object == null) {
            saml2Object = SSOUtils.unmarshall(
                    new String(Base64.decode(request.getParameter(Constants.HTTP_POST_PARAM_SAML_RESPONSE)),
                            Charset.forName("UTF-8")));
        }
        if (saml2Object instanceof LogoutRequest) {
            LogoutRequest logoutRequest = (LogoutRequest) saml2Object;
            logoutRequest.getSessionIndexes().stream().findFirst().ifPresent(
                    sessionIndex -> SSOAgentSessionManager.invalidateAllSessions(sessionIndex.getSessionIndex()).
                            stream().forEach(HttpSession::invalidate));
        } else if (saml2Object instanceof LogoutResponse) {
            Optional.ofNullable(request.getSession(false)).ifPresent(session -> {
                //  Not invalidating session explicitly since there may be other listeners
                //  still waiting to get triggered and at the end of the chain session needs to be
                //  invalidated by the system.
                Set<HttpSession> sessions = SSOAgentSessionManager.invalidateAllSessions(request.getSession(false));
                sessions.stream().forEach(httpSession -> {
                    try {
                        httpSession.invalidate();
                    } catch (IllegalStateException ignore) {
                        logger.log(Level.FINE, "Ignoring exception : ", ignore);
                    }
                });
            });
        } else {
            throw new SSOException("Invalid SAML 2.0 Single Logout Request/Response.");
        }
    }
}
