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
package org.wso2.appserver.webapp.security.agent;

import org.wso2.appserver.configuration.context.WebAppSingleSignOn;
import org.wso2.appserver.configuration.server.AppServerSingleSignOn;
import org.wso2.appserver.webapp.security.Constants;
import org.wso2.appserver.webapp.security.saml.signature.SSOX509Credential;
import org.wso2.appserver.webapp.security.utils.SSOUtils;
import org.wso2.appserver.webapp.security.utils.exception.SSOException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class defines the configuration aspects of the single-sign-on (SSO) agent.
 *
 * @since 6.0.0
 */
public class SSOAgentConfiguration {
    private Boolean isSSOEnabled;
    private Set<String> skipURIs;
    private Map<String, String[]> queryParameters;
    private SAML2 saml2;

    public SSOAgentConfiguration() {
        queryParameters = new HashMap<>();
        skipURIs = new HashSet<>();
        saml2 = new SAML2();
    }

    Boolean isSSOEnabled() {
        return isSSOEnabled;
    }

    Set<String> getSkipURIs() {
        return skipURIs;
    }

    public Map<String, String[]> getQueryParameters() {
        return queryParameters;
    }

    public SAML2 getSAML2() {
        return saml2;
    }

    /**
     * Initializes the single-sign-on (SSO) agent configurations based on the configurations defined.
     *
     * @param server  the server level SSO configurations
     * @param context the web app level SSO configurations
     */
    public void initialize(AppServerSingleSignOn server, WebAppSingleSignOn context) {
        Optional.ofNullable(context).ifPresent(configuration -> {
            isSSOEnabled = Optional.ofNullable(context.isSSOEnabled())
                    .orElse(false);
            saml2.httpBinding = Optional.ofNullable(configuration.getHttpBinding())
                    .orElse(Constants.SAML2_HTTP_POST_BINDING);
            saml2.spEntityId = configuration.getIssuerId();
            saml2.acsURL = configuration.getConsumerURL();
            //  add URIs to be skipped, if any
            Optional.ofNullable(configuration.getSkipURIs())
                    .ifPresent(uris ->
                            skipURIs = uris.getSkipURIs()
                                    .stream()
                                    .collect(Collectors.toSet()));
            queryParameters = SSOUtils.getSplitQueryParameters(configuration.getOptionalParams());

            saml2.isAssertionSigned = Optional.ofNullable(configuration.isAssertionSigningEnabled())
                    .orElse(false);
            saml2.isAssertionEncrypted = Optional.ofNullable(configuration.isAssertionEncryptionEnabled())
                    .orElse(false);
            saml2.isRequestSigned = Optional.ofNullable(configuration.isRequestSigningEnabled())
                    .orElse(false);
            saml2.isResponseSigned = Optional.ofNullable(configuration.isResponseSigningEnabled())
                    .orElse(false);

            saml2.isSLOEnabled = Optional.ofNullable(configuration.isSLOEnabled())
                    .orElse(false);
            saml2.sloURLPostfix = Optional.ofNullable(configuration.getSLOURLPostfix())
                    .orElse(Constants.DEFAULT_SLO_URL_POSTFIX);
        });

        Optional.ofNullable(server).ifPresent(configuration -> {
            saml2.idPURL = Optional.ofNullable(configuration.getIdpURL())
                    .orElse(Constants.DEFAULT_IDP_URL);
            saml2.idPEntityId = Optional.ofNullable(configuration.getIdpEntityId())
                    .orElse(Constants.DEFAULT_IDP_ENTITY_ID);
            if (saml2.isResponseSigned) {
                saml2.signatureValidatorImplClass = Optional.ofNullable(configuration.getSignatureValidatorImplClass())
                        .orElse(Constants.DEFAULT_SIGN_VALIDATOR_IMPL);
            }
        });
    }

    /**
     * Validates the single-sign-on (SSO) agent configurations and their combinations.
     *
     * @throws SSOException if an invalid configuration or a combination of configurations are encountered
     */
    public void validate() throws SSOException {
        if (isSSOEnabled) {
            if (saml2.spEntityId == null) {
                throw new SSOException("SAML 2.0 Request issuer id not configured");
            }

            if (saml2.acsURL == null) {
                throw new SSOException("SAML 2.0 Consumer URL post-fix not configured");
            }

            if (saml2.idPEntityId == null) {
                throw new SSOException("Identity provider entity id not configured");
            }

            if (saml2.idPURL == null) {
                throw new SSOException("Identity provider URL not configured");
            }

            if (saml2.isSLOEnabled && saml2.sloURLPostfix == null) {
                throw new SSOException("Single Logout enabled, but single logout URL post-fix not configured");
            }

            if ((saml2.isAssertionSigned || saml2.isAssertionEncrypted ||
                    saml2.isResponseSigned || saml2.isRequestSigned) && (saml2.ssoX509Credential == null)) {
                throw new SSOException("'SSOX509Credential' not configured when signature application is requested");
            }

            if ((saml2.isAssertionSigned || saml2.isResponseSigned) &&
                    saml2.ssoX509Credential.getEntityCertificate() == null) {
                throw new SSOException("Public certificate of identity provider not configured");
            }

            if ((saml2.isRequestSigned || saml2.isAssertionEncrypted) && (saml2.ssoX509Credential != null
                    && saml2.ssoX509Credential.getPrivateKey() == null)) {
                throw new SSOException("Private key of service provider (SP) not configured");
            }
        }
    }

    /**
     * A nested class which defines the SAML 2.0 single-sign-on (SSO) configuration properties.
     */
    public static class SAML2 {
        private String httpBinding;
        private String spEntityId;
        private String acsURL;
        private String idPEntityId;
        private String idPURL;
        private Boolean isPassiveAuthenticationEnabled;
        private Boolean isForceAuthenticationEnabled;
        private String relayState;
        private SSOX509Credential ssoX509Credential;
        private Boolean isAssertionSigned;
        private Boolean isAssertionEncrypted;
        private Boolean isResponseSigned;
        private Boolean isRequestSigned;
        private String signatureValidatorImplClass;
        private Boolean isSLOEnabled;
        private String sloURLPostfix;

        public String getHttpBinding() {
            return httpBinding;
        }

        public String getSPEntityId() {
            return spEntityId;
        }

        public void setSPEntityId(String spEntityId) {
            this.spEntityId = spEntityId;
        }

        public String getACSURL() {
            return acsURL;
        }

        public void setACSURL(String acsURL) {
            this.acsURL = acsURL;
        }

        public String getIdPURL() {
            return idPURL;
        }

        public String getIdPEntityId() {
            return idPEntityId;
        }

        public Boolean isPassiveAuthenticationEnabled() {
            return isPassiveAuthenticationEnabled;
        }

        public void enablePassiveAuthentication(Boolean passiveAuthenticationEnabled) {
            isPassiveAuthenticationEnabled = passiveAuthenticationEnabled;
        }

        public Boolean isForceAuthenticationEnabled() {
            return isForceAuthenticationEnabled;
        }

        public void enableForceAuthentication(Boolean forceAuthenticationEnabled) {
            isForceAuthenticationEnabled = forceAuthenticationEnabled;
        }

        public String getRelayState() {
            return relayState;
        }

        public void setRelayState(String relayState) {
            this.relayState = relayState;
        }

        public SSOX509Credential getSSOX509Credential() {
            return ssoX509Credential;
        }

        public void setSSOX509Credential(SSOX509Credential ssoX509Credential) {
            this.ssoX509Credential = ssoX509Credential;
        }

        public Boolean isAssertionSigned() {
            return isAssertionSigned;
        }

        public Boolean isAssertionEncrypted() {
            return isAssertionEncrypted;
        }

        public Boolean isResponseSigned() {
            return isResponseSigned;
        }

        public Boolean isRequestSigned() {
            return isRequestSigned;
        }

        public String getSignatureValidatorImplClass() {
            return signatureValidatorImplClass;
        }

        public Boolean isSLOEnabled() {
            return isSLOEnabled;
        }

        public String getSLOURLPostfix() {
            return sloURLPostfix;
        }
    }
}
