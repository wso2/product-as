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
package org.wso2.appserver.configuration.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * A Java class which models a holder for context level single-sign-on (SSO) configurations.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class SSOConfiguration {
    @XmlElement(name = "skip-uris")
    private SkipURIs skipURIs;
    @XmlElement(name = "handle-consumer-url-after-slo")
    private Boolean handleConsumerURLAfterSLO;
    @XmlElement(name = "query-params")
    private String queryParams;
    @XmlElement(name = "application-server-url")
    private String applicationServerURL;
    @XmlElement(name = "enable-sso")
    private Boolean enableSSO;
    @XmlElement(name = "request-url-postfix")
    private String requestURLPostFix;
    @XmlElement(name = "http-binding")
    private String httpBinding;
    @XmlElement(name = "issuer-id")
    private String issuerId;
    @XmlElement(name = "consumer-url")
    private String consumerURL;
    @XmlElement(name = "consumer-url-postfix")
    private String consumerURLPostFix;
    @XmlElement(name = "attribute-consuming-service-index")
    private String attributeConsumingServiceIndex;
    @XmlElement(name = "enable-slo")
    private Boolean enableSLO;
    @XmlElement(name = "slo-url-postfix")
    private String sloURLPostFix;
    @XmlElement(name = "enable-response-signing")
    private Boolean enableResponseSigning;
    @XmlElement(name = "enable-assertion-signing")
    private Boolean enableAssertionSigning;
    @XmlElement(name = "enable-assertion-encryption")
    private Boolean enableAssertionEncryption;
    @XmlElement(name = "enable-request-signing")
    private Boolean enableRequestSigning;
    @XmlElement(name = "is-force-authn")
    private Boolean enableForceAuthn;
    @XmlElement(name = "is-passive-authn")
    private Boolean enablePassiveAuthn;
    @XmlElement(name = "property")
    private List<Property> properties;

    public SkipURIs getSkipURIs() {
        return skipURIs;
    }

    public Boolean handleConsumerURLAfterSLO() {
        return handleConsumerURLAfterSLO;
    }

    public void enableHandlingConsumerURLAfterSLO(Boolean handleConsumerURLAfterSLO) {
        this.handleConsumerURLAfterSLO = handleConsumerURLAfterSLO;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public String getApplicationServerURL() {
        return applicationServerURL;
    }

    public void setApplicationServerURL(String applicationServerURL) {
        this.applicationServerURL = applicationServerURL;
    }

    public Boolean isSSOEnabled() {
        return enableSSO;
    }

    public String getRequestURLPostFix() {
        return requestURLPostFix;
    }

    public String getHttpBinding() {
        return httpBinding;
    }

    public String getIssuerId() {
        return issuerId;
    }

    public String getConsumerURL() {
        return consumerURL;
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

    public Boolean isSLOEnabled() {
        return enableSLO;
    }

    public String getSLOURLPostFix() {
        return sloURLPostFix;
    }

    public Boolean isResponseSigningEnabled() {
        return enableResponseSigning;
    }

    public Boolean isAssertionSigningEnabled() {
        return enableAssertionSigning;
    }

    public Boolean isAssertionEncryptionEnabled() {
        return enableAssertionEncryption;
    }

    public Boolean isForceAuthnEnabled() {
        return enableForceAuthn;
    }

    public Boolean isPassiveAuthnEnabled() {
        return enablePassiveAuthn;
    }

    public Boolean isRequestSigningEnabled() {
        return enableRequestSigning;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    /**
     * A nested class which models a collection of URIs to skip during single-sign-on (SSO).
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SkipURIs {
        @XmlElement(name = "skip-uri")
        private List<String> skipURIs;

        public List<String> getSkipURIs() {
            return skipURIs;
        }
    }

    /**
     * A nested class which defines an additional configuration property for SSO.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Property {
        @XmlAttribute(name = "key")
        private String key;
        @XmlValue
        private String value;

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Merges the context level single-sign-on (SSO) configurations defined globally and overridden at context level
     * (if any).
     *
     * @param configurations the local, context level group of SSO configurations to be merged with
     */
    protected void merge(SSOConfiguration configurations) {
        Optional.ofNullable(configurations).ifPresent(configs -> {
            skipURIs = Optional.ofNullable(configs.skipURIs).orElse(skipURIs);
            handleConsumerURLAfterSLO = Optional.ofNullable(configs.handleConsumerURLAfterSLO).
                    orElse(handleConsumerURLAfterSLO);
            queryParams = Optional.ofNullable(configs.queryParams).orElse(queryParams);
            applicationServerURL = Optional.ofNullable(configs.applicationServerURL).orElse(applicationServerURL);
            enableSSO = Optional.ofNullable(configs.enableSSO).orElse(enableSSO);
            requestURLPostFix = Optional.ofNullable(configs.requestURLPostFix).orElse(requestURLPostFix);
            httpBinding = Optional.ofNullable(configs.httpBinding).orElse(httpBinding);
            issuerId = configs.issuerId;
            consumerURL = configs.consumerURL;
            consumerURLPostFix = Optional.ofNullable(configs.consumerURLPostFix).orElse(consumerURLPostFix);
            attributeConsumingServiceIndex = Optional.ofNullable(configs.attributeConsumingServiceIndex).
                    orElse(attributeConsumingServiceIndex);
            enableSLO = Optional.ofNullable(configs.enableSLO).orElse(enableSLO);
            sloURLPostFix = Optional.ofNullable(configs.sloURLPostFix).orElse(sloURLPostFix);
            enableAssertionEncryption = Optional.ofNullable(configs.enableAssertionEncryption).
                    orElse(enableAssertionEncryption);
            enableAssertionSigning = Optional.ofNullable(configs.enableAssertionSigning).orElse(enableAssertionSigning);
            enableRequestSigning = Optional.ofNullable(configs.enableRequestSigning).orElse(enableRequestSigning);
            enableResponseSigning = Optional.ofNullable(configs.enableResponseSigning).orElse(enableResponseSigning);
            enableForceAuthn = Optional.ofNullable(configs.enableForceAuthn).orElse(enableForceAuthn);
            enablePassiveAuthn = Optional.ofNullable(configs.enablePassiveAuthn).orElse(enablePassiveAuthn);
            List<SSOConfiguration.Property> properties = prioritizeProperties(this.getProperties(),
                    configs.getProperties());
            if (properties.isEmpty()) {
                this.setProperties(null);
            } else {
                this.setProperties(properties);
            }
        });
    }

    /**
     * Prioritizes the additional webapp descriptor properties.
     *
     * @param global the globally defined set of additional SSO properties
     * @param local  the set of additional SSO properties defined at context level
     * @return the final, effective set of webapp descriptor additional SSO properties
     */
    private static List<SSOConfiguration.Property> prioritizeProperties(List<SSOConfiguration.Property> global,
            List<SSOConfiguration.Property> local) {
        List<SSOConfiguration.Property> effective = new ArrayList<>();
        if ((global != null) && (local != null)) {
            global.stream().forEach(property -> {
                Optional<SSOConfiguration.Property> matching = getProperty(property.getKey(), local);
                if (matching.isPresent()) {
                    effective.add(matching.get());
                } else {
                    effective.add(property);
                }
            });
        } else if (global != null) {
            global.stream().forEach(effective::add);
        } else if (local != null) {
            local.stream().forEach(effective::add);
        }
        return effective;
    }

    /**
     * Returns an additional {@code Property} if exists in the list of properties.
     *
     * @param key  the key of the property to be checked
     * @param list the list of properties
     * @return the SSO property if exists
     */
    private static Optional<SSOConfiguration.Property> getProperty(String key, List<SSOConfiguration.Property> list) {
        if (key == null) {
            return Optional.empty();
        }
        if (list != null) {
            return list.stream().filter(property -> property.getKey().equals(key)).findFirst();
        } else {
            return Optional.empty();
        }
    }
}
