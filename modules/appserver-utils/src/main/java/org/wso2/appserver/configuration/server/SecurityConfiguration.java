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
package org.wso2.appserver.configuration.server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Java class which models a holder for server level security configurations.
 *
 * @since 6.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class SecurityConfiguration {
    @XmlElement(name = "Keystore")
    private Keystore keystore;
    @XmlElement(name = "Truststore")
    private Truststore truststore;

    public Keystore getKeystore() {
        return keystore;
    }

    public void setKeystore(Keystore keystore) {
        this.keystore = keystore;
    }

    public Truststore getTruststore() {
        return truststore;
    }

    public void setTruststore(Truststore truststore) {
        this.truststore = truststore;
    }

    /**
     * A nested class which defines the keystore configurations for Application Server.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Keystore {
        @XmlElement(name = "KeystorePath")
        private String keystorePath;
        @XmlElement(name = "KeystorePassword")
        private String keystorePassword;
        @XmlElement(name = "IdPCertificateAlias")
        private String idpCertificateAlias;
        @XmlElement(name = "PrivateKeyAlias")
        private String privateKeyAlias;
        @XmlElement(name = "PrivateKeyPassword")
        private String privateKeyPassword;

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

    /**
     * A nested class which defines the trust store configurations for Application Server.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Truststore {
        @XmlElement(name = "TruststorePath")
        private String truststorePath;
        @XmlElement(name = "TruststorePassword")
        private String trustStorePassword;

        public String getTruststorePath() {
            return truststorePath;
        }

        public void setTruststorePath(String truststorePath) {
            this.truststorePath = truststorePath;
        }

        public String getTrustStorePassword() {
            return trustStorePassword;
        }

        public void setTrustStorePassword(String trustStorePassword) {
            this.trustStorePassword = trustStorePassword;
        }
    }
}
