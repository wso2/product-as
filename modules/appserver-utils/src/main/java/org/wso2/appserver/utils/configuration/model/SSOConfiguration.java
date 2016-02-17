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
package org.wso2.appserver.utils.configuration.model;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * A Java class which represents a holder for Application Server single-sign-on (SSO) configurations.
 *
 * @since 6.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SSOConfiguration {
    @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
    private SkipURIs skipURIs;
    @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
    private String applicationServerURL;
    @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
    private Boolean handleConsumerURLAfterSLO;
    @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
    private String loginURL;
    @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
    private SAML saml;

    public SkipURIs getSkipURIs() {
        return skipURIs;
    }

    public void setSkipURIs(SkipURIs skipURIs) {
        this.skipURIs = skipURIs;
    }

    public String getApplicationServerURL() {
        return applicationServerURL;
    }

    public void setApplicationServerURL(String applicationServerURL) {
        this.applicationServerURL = applicationServerURL;
    }

    public Boolean handleConsumerURLAfterSLO() {
        return handleConsumerURLAfterSLO;
    }

    public void setHandleConsumerURLAfterSLO(Boolean handleConsumerURLAfterSLO) {
        this.handleConsumerURLAfterSLO = handleConsumerURLAfterSLO;
    }

    public String getLoginURL() {
        return loginURL;
    }

    public SAML getSAML() {
        return saml;
    }

    public void setSAML(SAML saml) {
        this.saml = saml;
    }

    /**
     * A nested class which models a collection of URIs to skip during single-sign-on (SSO).
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SkipURIs {
        @XmlElement(name = ConfigurationConstants.SSOConfigurationConstants.SKIP_URI,
                namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private List<String> skipURIs;

        public List<String> getSkipURIs() {
            return skipURIs;
        }
    }

    /**
     * A nested class which defines the SAML specific single-sign-on (SSO) configurations.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SAML {
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private Boolean enableSAMLSSO;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String idpURL;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String idpEntityId;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String issuerId;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String consumerURL;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String httpBinding;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String attributeConsumingServiceIndex;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private Boolean enableSLO;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String consumerURLPostFix;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String requestURLPostFix;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String sloURLPostFix;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private Boolean enableResponseSigning;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private Boolean enableAssertionSigning;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private Boolean enableAssertionEncryption;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private Boolean enableRequestSigning;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String signatureValidatorImplClass;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String additionalRequestParams;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private Boolean isForceAuthn;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private Boolean isPassiveAuthn;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String keystorePath;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String keystorePassword;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String idpCertificateAlias;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String privateKeyAlias;
        @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String privateKeyPassword;

        public Boolean isSAMLSSOEnabled() {
            return enableSAMLSSO;
        }

        public void setEnableSAMLSSO(Boolean enableSAMLSSO) {
            this.enableSAMLSSO = enableSAMLSSO;
        }

        public String getIdpURL() {
            return idpURL;
        }

        public void setIdpURL(String idpURL) {
            this.idpURL = idpURL;
        }

        public String getIdpEntityId() {
            return idpEntityId;
        }

        public void setIdpEntityId(String idpEntityId) {
            this.idpEntityId = idpEntityId;
        }

        public String getIssuerId() {
            return issuerId;
        }

        public void setIssuerId(String issuerId) {
            this.issuerId = issuerId;
        }

        public String getConsumerURL() {
            return consumerURL;
        }

        public void setConsumerURL(String consumerURL) {
            this.consumerURL = consumerURL;
        }

        public String getHttpBinding() {
            return httpBinding;
        }

        public void setHttpBinding(String httpBinding) {
            this.httpBinding = httpBinding;
        }

        public String getAttributeConsumingServiceIndex() {
            return attributeConsumingServiceIndex;
        }

        public void setAttributeConsumingServiceIndex(String attributeConsumingServiceIndex) {
            this.attributeConsumingServiceIndex = attributeConsumingServiceIndex;
        }

        public Boolean isSLOEnabled() {
            return enableSLO;
        }

        public void setEnableSLO(Boolean enableSLO) {
            this.enableSLO = enableSLO;
        }

        public String getConsumerURLPostFix() {
            return consumerURLPostFix;
        }

        public void setConsumerURLPostFix(String consumerURLPostFix) {
            this.consumerURLPostFix = consumerURLPostFix;
        }

        public String getRequestURLPostFix() {
            return requestURLPostFix;
        }

        public void setRequestURLPostFix(String requestURLPostFix) {
            this.requestURLPostFix = requestURLPostFix;
        }

        public String getSLOURLPostFix() {
            return sloURLPostFix;
        }

        public void setSLOURLPostFix(String sloURLPostFix) {
            this.sloURLPostFix = sloURLPostFix;
        }

        public Boolean isResponseSigningEnabled() {
            return enableResponseSigning;
        }

        public void setEnableResponseSigning(Boolean enableResponseSigning) {
            this.enableResponseSigning = enableResponseSigning;
        }

        public Boolean isAssertionSigningEnabled() {
            return enableAssertionSigning;
        }

        public void setEnableAssertionSigning(Boolean enableAssertionSigning) {
            this.enableAssertionSigning = enableAssertionSigning;
        }

        public Boolean isAssertionEncryptionEnabled() {
            return enableAssertionEncryption;
        }

        public void setEnableAssertionEncryption(Boolean enableAssertionEncryption) {
            this.enableAssertionEncryption = enableAssertionEncryption;
        }

        public Boolean isRequestSigningEnabled() {
            return enableRequestSigning;
        }

        public void setEnableRequestSigning(Boolean enableRequestSigning) {
            this.enableRequestSigning = enableRequestSigning;
        }

        public String getSignatureValidatorImplClass() {
            return signatureValidatorImplClass;
        }

        public void setSignatureValidatorImplClass(String signatureValidatorImplClass) {
            this.signatureValidatorImplClass = signatureValidatorImplClass;
        }

        public String getAdditionalRequestParams() {
            return additionalRequestParams;
        }

        public void setAdditionalRequestParams(String additionalRequestParams) {
            this.additionalRequestParams = additionalRequestParams;
        }

        public Boolean isForceAuthn() {
            return isForceAuthn;
        }

        public void setIsForceAuthn(Boolean isForceAuthn) {
            this.isForceAuthn = isForceAuthn;
        }

        public Boolean isPassiveAuthn() {
            return isPassiveAuthn;
        }

        public void setIsPassiveAuthn(Boolean isPassiveAuthn) {
            this.isPassiveAuthn = isPassiveAuthn;
        }

        public String getKeystorePath() {
            return keystorePath;
        }

        public void setKeystorePath(String keystorePath) {
            this.keystorePath = keystorePath;
        }

        public String getKeystorePassword() {
            return keystorePassword;
        }

        public void setKeystorePassword(String keystorePassword) {
            this.keystorePassword = keystorePassword;
        }

        public String getIdpCertificateAlias() {
            return idpCertificateAlias;
        }

        public void setIdpCertificateAlias(String idpCertificateAlias) {
            this.idpCertificateAlias = idpCertificateAlias;
        }

        public String getPrivateKeyAlias() {
            return privateKeyAlias;
        }

        public void setPrivateKeyAlias(String privateKeyAlias) {
            this.privateKeyAlias = privateKeyAlias;
        }

        public String getPrivateKeyPassword() {
            return privateKeyPassword;
        }

        public void setPrivateKeyPassword(String privateKeyPassword) {
            this.privateKeyPassword = privateKeyPassword;
        }
    }
}
