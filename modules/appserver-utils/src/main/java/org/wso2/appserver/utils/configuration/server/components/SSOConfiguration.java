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
package org.wso2.appserver.utils.configuration.server.components;

import org.wso2.appserver.utils.configuration.ConfigurationConstants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Java class which models a holder for server level single-sign-on (SSO) configurations.
 *
 * @since 6.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
public class SSOConfiguration {
    @XmlElement(name = "SAML", namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
    private SAML saml;

    public SAML getSAML() {
        return saml;
    }

    public void setSAML(SAML saml) {
        this.saml = saml;
    }

    /**
     * A nested class which defines the SAML specific SSO configurations.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SAML {
        @XmlElement(name = "IdPURL", namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
        private String idpURL;
        @XmlElement(name = "IdPEntityId", namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
        private String idpEntityId;
        @XmlElement(name = "SignatureValidatorImplClass",
                namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
        private String signatureValidatorImplClass;
        @XmlElement(name = "KeystorePath", namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
        private String keystorePath;
        @XmlElement(name = "KeystorePassword", namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
        private String keystorePassword;
        @XmlElement(name = "IdPCertificateAlias", namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
        private String idpCertificateAlias;
        @XmlElement(name = "PrivateKeyAlias", namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
        private String privateKeyAlias;
        @XmlElement(name = "PrivateKeyPassword", namespace = ConfigurationConstants.SERVER_CONFIGURATION_NAMESPACE)
        private String privateKeyPassword;

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

        public String getSignatureValidatorImplClass() {
            return signatureValidatorImplClass;
        }

        public void setSignatureValidatorImplClass(String signatureValidatorImplClass) {
            this.signatureValidatorImplClass = signatureValidatorImplClass;
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
