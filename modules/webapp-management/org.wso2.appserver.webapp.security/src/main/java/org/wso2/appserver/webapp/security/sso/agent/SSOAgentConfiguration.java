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

import org.wso2.appserver.utils.configuration.model.Configuration;
import org.wso2.appserver.utils.configuration.model.SSOConfiguration;
import org.wso2.appserver.webapp.security.sso.saml.signature.SSOX509Credential;
import org.wso2.appserver.webapp.security.sso.util.SSOConstants;
import org.wso2.appserver.webapp.security.sso.util.SSOException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class defines the configuration aspects of the single-sign-on (SSO) agent.
 *
 * @since 6.0.0
 */
public class SSOAgentConfiguration {
    private static final Logger logger = Logger.getLogger(SSOAgentConfiguration.class.getName());

    private Boolean isSAML2SSOLoginEnabled;
    private String requestURLPostFix;
    private Set<String> skipURIs;
    private Map<String, String[]> queryParameters;
    private SAML2 saml2;

    public Boolean isSAML2SSOLoginEnabled() {
        return isSAML2SSOLoginEnabled;
    }

    public String getRequestURLPostFix() {
        return requestURLPostFix;
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
     * Sets up the single-sign-on (SSO) agent configurations based on the {@code configuration} defined.
     *
     * @param configuration the set of configuration properties to be used to set up the SSO Agent configuration
     *                      properties
     */
    public void initConfig(Configuration configuration) {
        Optional.ofNullable(configuration).ifPresent(configProperties -> {
            SSOConfiguration ssoConfiguration = configProperties.getSingleSignOnConfiguration();

            Optional.ofNullable(ssoConfiguration).ifPresent(effectiveConfiguration -> {
                isSAML2SSOLoginEnabled = effectiveConfiguration.getSAML().isSAMLSSOEnabled();
                if (isSAML2SSOLoginEnabled == null) {
                    isSAML2SSOLoginEnabled = false;
                }

                requestURLPostFix = effectiveConfiguration.getSAML().getRequestURLPostFix();
                if (requestURLPostFix == null) {
                    requestURLPostFix = SSOConstants.SSOAgentConfiguration.SAML2.REQUEST_URL_POSTFIX_DEFAULT;
                }

                effectiveConfiguration.getSkipURIs().getSkipURIs().stream().forEach(skipURIs::add);

                //  todo: consider the addition of query parameters

                saml2.httpBinding = effectiveConfiguration.getSAML().getHttpBinding();
                if (saml2.httpBinding == null) {
                    saml2.httpBinding = SSOConstants.SSOAgentConfiguration.SAML2.BINDING_TYPE_DEFAULT;
                }

                saml2.spEntityId = effectiveConfiguration.getSAML().getIssuerId();
                saml2.acsURL = effectiveConfiguration.getSAML().getConsumerURL();

                saml2.idPEntityId = effectiveConfiguration.getSAML().getIdpEntityId();
                if (saml2.idPEntityId == null) {
                    saml2.idPEntityId = SSOConstants.SSOAgentConfiguration.SAML2.IDP_ENTITY_ID_DEFAULT;
                }
                saml2.idPURL = effectiveConfiguration.getSAML().getIdpURL();
                if (saml2.idPURL == null) {
                    saml2.idPURL = SSOConstants.SSOAgentConfiguration.SAML2.IDP_URL_DEFAULT;
                }
                saml2.attributeConsumingServiceIndex = effectiveConfiguration.getSAML().
                        getAttributeConsumingServiceIndex();
                if (saml2.attributeConsumingServiceIndex == null) {
                    saml2.attributeConsumingServiceIndex = SSOConstants.SSOAgentConfiguration.
                            SAML2.ATTR_CONSUMING_SERVICE_INDEX_DEFAULT;
                }

                saml2.isSLOEnabled = effectiveConfiguration.getSAML().isSLOEnabled();
                if (saml2.isSLOEnabled == null) {
                    saml2.isSLOEnabled = false;
                }
                saml2.sloURLPostFix = effectiveConfiguration.getSAML().getSLOURLPostFix();
                if (saml2.sloURLPostFix == null) {
                    saml2.sloURLPostFix = SSOConstants.SSOAgentConfiguration.SAML2.SLO_URL_POSTFIX_DEFAULT;
                }

                saml2.isAssertionSigned = effectiveConfiguration.getSAML().isAssertionSigningEnabled();
                if (saml2.isAssertionSigned == null) {
                    saml2.isAssertionSigned = false;
                }

                saml2.isAssertionEncrypted = effectiveConfiguration.getSAML().isAssertionEncryptionEnabled();
                if (saml2.isAssertionEncrypted == null) {
                    saml2.isAssertionEncrypted = false;
                }

                saml2.isResponseSigned = effectiveConfiguration.getSAML().isResponseSigningEnabled();
                if (saml2.isResponseSigned == null) {
                    saml2.isResponseSigned = false;
                }

                if (saml2.isResponseSigned) {
                    saml2.signatureValidatorImplClass = effectiveConfiguration.getSAML().
                            getSignatureValidatorImplClass();
                    if (saml2.signatureValidatorImplClass == null) {
                        logger.log(Level.FINE, "Signature validator implementation class has not been configured");
                    }
                }

                saml2.isRequestSigned = effectiveConfiguration.getSAML().isRequestSigningEnabled();
                if (saml2.isRequestSigned == null) {
                    saml2.isRequestSigned = false;
                }

                saml2.isPassiveAuthenticationEnabled = effectiveConfiguration.getSAML().isPassiveAuthn();
                if (saml2.isPassiveAuthenticationEnabled == null) {
                    saml2.isPassiveAuthenticationEnabled = false;
                }

                saml2.isForceAuthenticationEnabled = effectiveConfiguration.getSAML().isForceAuthn();
                if (saml2.isForceAuthenticationEnabled == null) {
                    saml2.isForceAuthenticationEnabled = false;
                }
            });
        });

        /*Optional.ofNullable(properties).ifPresent(configProperties -> {
            String queryParameterString = configProperties.getProperty(SSOConstants.SSOAgentConfiguration.QUERY_PARAMS);
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

            saml2.relayState = configProperties.getProperty(SSOConstants.SSOAgentConfiguration.SAML2.RELAY_STATE);
            saml2.postBindingRequestHTMLPayload = configProperties.
                    getProperty(SSOConstants.SSOAgentConfiguration.SAML2.POST_BINDING_REQUEST_HTML_PAYLOAD);
        });*/
    }

    /**
     * Verifies the validity of single-sign-on (SSO) agent configurations at the current state of this
     * {@code SSOAgentConfiguration} instance.
     *
     * @throws SSOException if the relevant configurations are invalidly set at the current state of the
     *                      SSOAgentConfiguration instance
     */
    public void verifyConfig() throws SSOException {
        if (isSAML2SSOLoginEnabled && (requestURLPostFix == null)) {
            throw new SSOException("SAML Request URL post-fix not configured");
        }

        if (isSAML2SSOLoginEnabled && (saml2.spEntityId == null)) {
            throw new SSOException("SAML Request issuer id not configured");
        }

        if (isSAML2SSOLoginEnabled && (saml2.acsURL == null)) {
            throw new SSOException("SAML Consumer URL post-fix not configured");
        }

        if (isSAML2SSOLoginEnabled && (saml2.idPEntityId == null)) {
            throw new SSOException("Identity provider entity id not configured");
        }

        if (isSAML2SSOLoginEnabled && (saml2.idPURL == null)) {
            throw new SSOException("Identity provider URL not configured");
        }

        if (isSAML2SSOLoginEnabled && (saml2.attributeConsumingServiceIndex == null)) {
            logger.log(Level.FINE, "SAML attribute consuming index not configured. "
                    + "No attributes of the Subject will be requested");
        }

        if (isSAML2SSOLoginEnabled && saml2.isSLOEnabled && saml2.sloURLPostFix == null) {
            throw new SSOException("Single Logout enabled, but SLO URL not configured");
        }

        if (isSAML2SSOLoginEnabled && (saml2.isAssertionSigned || saml2.isAssertionEncrypted ||
                saml2.isResponseSigned || saml2.isRequestSigned) && (saml2.ssoX509Credential == null)) {
            logger.log(Level.FINE,
                    "\'SSOX509Credential\' not configured, defaulting to " + SSOX509Credential.class.getName());
        }

        if (isSAML2SSOLoginEnabled && (saml2.isAssertionSigned || saml2.isResponseSigned) &&
                (saml2.ssoX509Credential.getEntityCertificate() == null)) {
            throw new SSOException("Public certificate of IdP not configured");
        }

        if (isSAML2SSOLoginEnabled && (saml2.isRequestSigned || saml2.isAssertionEncrypted) &&
                (saml2.ssoX509Credential.getPrivateKey() == null)) {
            throw new SSOException("Private key of SP not configured");
        }
    }

    /**
     * A nested class which defines the SAML single-sign-on (SSO) configuration properties.
     */
    public static class SAML2 {
        private String httpBinding;
        private String spEntityId;
        private String acsURL;
        private String idPEntityId;
        private String idPURL;
        private Boolean isSLOEnabled;
        private String sloURLPostFix;
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
        /**
         * The html page that will auto-submit the SAML2 to the IdP.
         * This should be in valid HTML syntax, with following section within the
         * auto-submit form.
         * "&lt;!--$saml_params--&gt;"
         * This section will be replaced by the SAML2 parameters.
         * <p>
         * If the parameter value is empty, null or doesn't have the above
         * section, the default page will be shown
         */
        private String postBindingRequestHTMLPayload;

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
            return sloURLPostFix;
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

        public String getPostBindingRequestHTMLPayload() {
            return postBindingRequestHTMLPayload;
        }

        public String getSignatureValidatorImplClass() {
            return signatureValidatorImplClass;
        }
    }
}
