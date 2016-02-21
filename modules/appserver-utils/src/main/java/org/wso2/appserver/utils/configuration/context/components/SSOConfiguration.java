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
package org.wso2.appserver.utils.configuration.context.components;

import org.wso2.appserver.utils.configuration.ConfigurationConstants;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Java class which models a holder for webapp level single-sign-on (SSO) configurations.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
public class SSOConfiguration {
    @XmlElement(name = "skip-uris", namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
    private SkipURIs skipURIs;
    @XmlElement(name = "query-params", namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
    private String queryParams;
    @XmlElement(name = "application-server-url", namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
    private String applicationServerURL;
    @XmlElement(namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
    private SAML saml;

    public SkipURIs getSkipURIs() {
        return skipURIs;
    }

    public void setSkipURIs(SkipURIs skipURIs) {
        this.skipURIs = skipURIs;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

    public String getApplicationServerURL() {
        return applicationServerURL;
    }

    public void setApplicationServerURL(String applicationServerURL) {
        this.applicationServerURL = applicationServerURL;
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
        @XmlElement(name = "skip-uri", namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private List<String> skipURIs;

        public List<String> getSkipURIs() {
            return skipURIs;
        }

        public void setSkipURIs(List<String> skipURIs) {
            this.skipURIs = skipURIs;
        }
    }

    /**
     * A nested class which defines the SAML specific single-sign-on (SSO) configurations.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SAML {
        @XmlElement(name = "enable-sso", namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private Boolean enableSSO;
        @XmlElement(name = "request-url-postfix", namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String requestURLPostFix;
        @XmlElement(name = "http-binding", namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String httpBinding;
        @XmlElement(name = "issuer-id", namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String issuerId;
        @XmlElement(name = "consumer-url", namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String consumerURL;
        @XmlElement(name = "consumer-url-postfix", namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String consumerURLPostFix;
        @XmlElement(name = "attribute-consuming-service-index",
                namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String attributeConsumingServiceIndex;
        @XmlElement(name = "enable-slo", namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private Boolean enableSLO;
        @XmlElement(name = "slo-url-postfix", namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private String sloURLPostFix;
        @XmlElement(name = "enable-response-signing", namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private Boolean enableResponseSigning;
        @XmlElement(name = "enable-assertion-signing", namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private Boolean enableAssertionSigning;
        @XmlElement(name = "enable-assertion-encryption",
                namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private Boolean enableAssertionEncryption;
        @XmlElement(name = "enable-request-signing", namespace = ConfigurationConstants.WEBAPP_DESCRIPTOR_NAMESPACE)
        private Boolean enableRequestSigning;

        public Boolean isSSOEnabled() {
            return enableSSO;
        }

        public void enableSSO(Boolean enableSSO) {
            this.enableSSO = enableSSO;
        }

        public String getRequestURLPostFix() {
            return requestURLPostFix;
        }

        public void setRequestURLPostFix(String requestURLPostFix) {
            this.requestURLPostFix = requestURLPostFix;
        }

        public String getHttpBinding() {
            return httpBinding;
        }

        public void setHttpBinding(String httpBinding) {
            this.httpBinding = httpBinding;
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

        public String getConsumerURLPostFix() {
            return consumerURLPostFix;
        }

        public void setConsumerURLPostFix(String consumerURLPostFix) {
            this.consumerURLPostFix = consumerURLPostFix;
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

        public void enableSLO(Boolean enableSLO) {
            this.enableSLO = enableSLO;
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

        public void enableResponseSigning(Boolean enableResponseSigning) {
            this.enableResponseSigning = enableResponseSigning;
        }

        public Boolean isAssertionSigningEnabled() {
            return enableAssertionSigning;
        }

        public void enableAssertionSigning(Boolean enableAssertionSigning) {
            this.enableAssertionSigning = enableAssertionSigning;
        }

        public Boolean isAssertionEncryptionEnabled() {
            return enableAssertionEncryption;
        }

        public void enableAssertionEncryption(Boolean enableAssertionEncryption) {
            this.enableAssertionEncryption = enableAssertionEncryption;
        }

        public Boolean isRequestSigningEnabled() {
            return enableRequestSigning;
        }

        public void enableRequestSigning(Boolean enableRequestSigning) {
            this.enableRequestSigning = enableRequestSigning;
        }
    }
}
