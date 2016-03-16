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
    @XmlElement(name = "KeyStore")
    private Keystore keystore;
    @XmlElement(name = "TrustStore")
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
        @XmlElement(name = "Location")
        private String location;
        @XmlElement(name = "Type")
        private String type;
        @XmlElement(name = "Password")
        private String password;
        @XmlElement(name = "KeyAlias")
        private String keyAlias;
        @XmlElement(name = "KeyPassword")
        private String keyPassword;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getKeyAlias() {
            return keyAlias;
        }

        public void setKeyAlias(String keyAlias) {
            this.keyAlias = keyAlias;
        }

        public String getKeyPassword() {
            return keyPassword;
        }

        public void setKeyPassword(String keyPassword) {
            this.keyPassword = keyPassword;
        }
    }

    /**
     * A nested class which defines the trust store configurations for Application Server.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Truststore {
        @XmlElement(name = "Location")
        private String location;
        @XmlElement(name = "Type")
        private String type;
        @XmlElement(name = "Password")
        private String password;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
