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
package org.wso2.appserver.webapp.security.sso.agent;

import org.wso2.appserver.configuration.context.ContextConfiguration;
import org.wso2.appserver.configuration.server.SSOConfiguration;
import org.wso2.appserver.configuration.server.ServerConfiguration;
import org.wso2.appserver.webapp.security.sso.saml.signature.SSOX509Credential;
import org.wso2.appserver.webapp.security.sso.utils.SSOConstants;
import org.wso2.appserver.webapp.security.sso.utils.SSOException;
import org.wso2.appserver.webapp.security.sso.utils.SSOUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * This class defines the configuration aspects of the single-sign-on (SSO) agent.
 *
 * @since 6.0.0
 */
public class SSOAgentConfiguration {
    private static final Logger logger = Logger.getLogger(SSOAgentConfiguration.class.getName());

    private Boolean isSAMLSSOLoginEnabled;
    private String requestURLPostfix;
    private Set<String> skipURIs;
    private Map<String, String[]> queryParameters;
    private SAML2 saml2;

    public Boolean isSAML2SSOLoginEnabled() {
        return isSAMLSSOLoginEnabled;
    }

    public String getRequestURLPostfix() {
        return requestURLPostfix;
    }

    public Set<String> getSkipURIs() {
        return skipURIs;
    }

    public Map<String, String[]> getQueryParameters() {
        return queryParameters;
    }

    public SAML2 getSAML2() {
        return saml2;
    }

    public SSOAgentConfiguration() {
        queryParameters = new HashMap<>();
        skipURIs = new HashSet<>();
        saml2 = new SAML2();
    }

    /**
     * Sets up the single-sign-on (SSO) agent configurations based on the configurations defined.
     *
     * @param serverConfiguration  the set of server level configurations
     * @param contextConfiguration the set of context level configurations
     */
    public void initConfig(ServerConfiguration serverConfiguration, ContextConfiguration contextConfiguration) {
        Optional.ofNullable(contextConfiguration).ifPresent(configProperties -> {
            org.wso2.appserver.configuration.context.SSOConfiguration contextSSO = configProperties.
                    getSingleSignOnConfiguration();

            Optional.ofNullable(contextSSO.getSkipURIs()).
                    ifPresent(uris -> uris.getSkipURIs().stream().forEach(skipURIs::add));

            String queryParameterString = contextSSO.getQueryParams();
            if (!SSOUtils.isBlank(queryParameterString)) {
                Map<String, List<String>> queryParameterMap = new HashMap<>();
                Stream.of(queryParameterString.split("&")).
                        map(queryParameter -> queryParameter.split("=")).forEach(splitParameters -> {
                    if (splitParameters.length == 2) {
                        if (queryParameterMap.get(splitParameters[0]) != null) {
                            queryParameterMap.get(splitParameters[0]).add(splitParameters[1]);
                        } else {
                            List<String> newList = new ArrayList<>();
                            newList.add(splitParameters[1]);
                            queryParameterMap.put(splitParameters[0], newList);
                        }
                    }
                    queryParameterMap.entrySet().stream().forEach(entry -> {
                        String[] values = entry.getValue().toArray(new String[entry.getValue().size()]);
                        queryParameters.put(entry.getKey(), values);
                    });
                });
            }

            isSAMLSSOLoginEnabled = Optional.ofNullable(contextSSO.isSSOEnabled()).orElse(false);
            requestURLPostfix = Optional.ofNullable(contextSSO.getRequestURLPostFix()).
                    orElse(SSOConstants.SSOAgentConfiguration.REQUEST_URL_POSTFIX_DEFAULT);
            saml2.httpBinding = Optional.ofNullable(contextSSO.getHttpBinding()).
                    orElse(SSOConstants.SSOAgentConfiguration.BINDING_TYPE_DEFAULT);
            saml2.spEntityId = contextSSO.getIssuerId();
            saml2.acsURL = contextSSO.getConsumerURL();
            saml2.attributeConsumingServiceIndex = contextSSO.getAttributeConsumingServiceIndex();
            saml2.isSLOEnabled = Optional.ofNullable(contextSSO.isSLOEnabled()).orElse(false);
            saml2.sloURLPostfix = Optional.ofNullable(contextSSO.getSLOURLPostFix()).
                    orElse(SSOConstants.SSOAgentConfiguration.SLO_URL_POSTFIX_DEFAULT);
            saml2.isResponseSigned = Optional.ofNullable(contextSSO.isResponseSigningEnabled()).orElse(false);
            saml2.isRequestSigned = Optional.ofNullable(contextSSO.isRequestSigningEnabled()).orElse(false);
            saml2.isAssertionEncrypted = Optional.ofNullable(contextSSO.isAssertionEncryptionEnabled()).orElse(false);
            saml2.isAssertionSigned = Optional.ofNullable(contextSSO.isAssertionSigningEnabled()).orElse(false);
            saml2.isForceAuthenticationEnabled = Optional.ofNullable(contextSSO.isForceAuthnEnabled()).orElse(false);
            saml2.isPassiveAuthenticationEnabled = Optional.ofNullable(contextSSO.isPassiveAuthnEnabled()).
                    orElse(false);
        });

        Optional.ofNullable(serverConfiguration).ifPresent(configProperties -> {
            SSOConfiguration serverSSO = configProperties.getSingleSignOnConfiguration();

            saml2.idPURL = Optional.ofNullable(serverSSO.getIdpURL()).
                    orElse(SSOConstants.SSOAgentConfiguration.IDP_URL_DEFAULT);
            saml2.idPEntityId = Optional.ofNullable(serverSSO.getIdpEntityId()).
                    orElse(SSOConstants.SSOAgentConfiguration.IDP_ENTITY_ID_DEFAULT);

            if (saml2.isResponseSigned()) {
                saml2.signatureValidatorImplClass = serverSSO.getSignatureValidatorImplClass();
                if (saml2.signatureValidatorImplClass == null) {
                    logger.log(Level.FINE, "Signature validator implementation class has not been configured");
                }
            }
        });
    }

    /**
     * Verifies the validity of single-sign-on (SSO) agent configurations at the current state of this
     * {@code SSOAgentConfiguration} instance.
     *
     * @throws SSOException if the relevant configurations are invalidly set at the current state of the
     *                      SSOAgentConfiguration instance
     */
    public void verifyConfig() throws SSOException {
        if (isSAMLSSOLoginEnabled) {
            if (requestURLPostfix == null) {
                throw new SSOException("SAML Request URL post-fix not configured");
            }

            if (saml2.spEntityId == null) {
                throw new SSOException("SAML Request issuer id not configured");
            }

            if (saml2.acsURL == null) {
                throw new SSOException("SAML Consumer URL post-fix not configured");
            }

            if (saml2.idPEntityId == null) {
                throw new SSOException("Identity provider entity id not configured");
            }

            if (saml2.idPURL == null) {
                throw new SSOException("Identity provider URL not configured");
            }

            if (saml2.attributeConsumingServiceIndex == null) {
                logger.log(Level.FINE, "SAML attribute consuming index not configured. " +
                        "No attributes of the Subject will be requested");
            }

            if (saml2.isSLOEnabled && saml2.sloURLPostfix == null) {
                throw new SSOException("Single Logout enabled, but SLO URL not configured");
            }

            if ((saml2.isAssertionSigned || saml2.isAssertionEncrypted ||
                    saml2.isResponseSigned || saml2.isRequestSigned) && (saml2.ssoX509Credential == null)) {
                logger.log(Level.FINE,
                        "\'SSOX509Credential\' not configured, defaulting to " + SSOX509Credential.class.getName());
            }

            if ((saml2.isAssertionSigned || saml2.isResponseSigned) && (saml2.ssoX509Credential.getEntityCertificate()
                    == null)) {
                throw new SSOException("Public certificate of IdP not configured");
            }

            if ((saml2.isRequestSigned || saml2.isAssertionEncrypted) && (saml2.ssoX509Credential.getPrivateKey()
                    == null)) {
                throw new SSOException("Private key of SP not configured");
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
        private Boolean isSLOEnabled;
        private String sloURLPostfix;
        private String attributeConsumingServiceIndex;
        private SSOX509Credential ssoX509Credential;
        private Boolean isAssertionSigned;
        private Boolean isAssertionEncrypted;
        private Boolean isResponseSigned;
        private Boolean isRequestSigned;
        private Boolean isPassiveAuthenticationEnabled;
        private Boolean isForceAuthenticationEnabled;
        private String relayState;
        private String signatureValidatorImplClass;

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

        public String getIdPEntityId() {
            return idPEntityId;
        }

        public String getIdPURL() {
            return idPURL;
        }

        public Boolean isSLOEnabled() {
            return isSLOEnabled;
        }

        public String getSLOURL() {
            return sloURLPostfix;
        }

        public String getAttributeConsumingServiceIndex() {
            return attributeConsumingServiceIndex;
        }

        public SSOX509Credential getSSOAgentX509Credential() {
            return ssoX509Credential;
        }

        public void setSSOAgentX509Credential(SSOX509Credential ssoAgentX509Credential) {
            this.ssoX509Credential = ssoAgentX509Credential;
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

        public Boolean isPassiveAuthn() {
            return isPassiveAuthenticationEnabled;
        }

        public void setPassiveAuthn(Boolean isPassiveAuthn) {
            this.isPassiveAuthenticationEnabled = isPassiveAuthn;
        }

        public Boolean isForceAuthn() {
            return isForceAuthenticationEnabled;
        }

        public String getRelayState() {
            return relayState;
        }

        public void setRelayState(String relayState) {
            this.relayState = relayState;
        }

        public String getSignatureValidatorImplClass() {
            return signatureValidatorImplClass;
        }
    }
}
