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
package org.wso2.appserver.webapp.security.sso.util;

/**
 * This class defines the constants utilized used within the org.wso2.appserver.webapp.security.sso.
 *
 * @since 6.0.0
 */
public class SSOConstants {
    public static final String SESSION_BEAN_NAME = "org.wso2.appserver.webapp.security.LoggedInSession";

    /**
     * Prevents instantiating the SSOConstants class.
     */
    private SSOConstants() {
    }

    /**
     * This class defines the constants associated during the SAML based single-sign-on (SSO) communication.
     */
    public static class SAML2SSO {
        //  SAML 2.0 single-sign-on (SSO) parameter name constants
        public static final String HTTP_POST_PARAM_SAML2_REQUEST = "SAMLRequest";
        public static final String HTTP_POST_PARAM_SAML2_RESPONSE = "SAMLResponse";

        /**
         * Prevents instantiating the SAML2SSO nested class.
         */
        private SAML2SSO() {
        }
    }

    /**
     * This class defines the single-sign-on (SSO) agent configuration property name constants.
     */
    public static class SSOAgentConfiguration {
        public static final String SKIP_URIS = "SkipURIs";
        public static final String QUERY_PARAMS = "QueryParams";

        /**
         * Prevents instantiating the SSOAgentConfiguration nested class.
         */
        private SSOAgentConfiguration() {
        }

        /**
         * This class defines the SAML specific single-sign-on (SSO) configuration property name constants.
         */
        public static class SAML2 {
            public static final String REQUEST_URL_POSTFIX_DEFAULT = "samlsso";
            public static final String BINDING_TYPE_DEFAULT = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
            public static final String IDP_URL_DEFAULT = "https://localhost:9443/samlsso";
            public static final String IDP_ENTITY_ID_DEFAULT = "localhost";
            public static final String ATTR_CONSUMING_SERVICE_INDEX_DEFAULT = "1701087467";
            public static final String SLO_URL_POSTFIX_DEFAULT = "logout";

            public static final String CONSUMER_URL_POSTFIX = "SAML.ConsumerUrlPostFix";

            //  Digital signature configuration properties
            public static final String KEYSTORE_PATH = "SAML.KeyStorePath";
            public static final String KEYSTORE_PASSWORD = "SAML.KeyStorePassword";
            public static final String IDP_PUBLIC_CERTIFICATE_ALIAS = "SAML.IdPCertAlias";
            public static final String SP_PRIVATE_KEY_ALIAS = "SAML.PrivateKeyAlias";
            public static final String SP_PRIVATE_KEY_PASSWORD = "SAML.PrivateKeyPassword";

            /**
             * Prevents instantiating the SAML2 nested class.
             */
            private SAML2() {
            }
        }
    }

    /**
     * This class defines constants used in the implementation of the SAML single-sign-on (SSO) valve.
     */
    public static class SAMLSSOValveConstants {
        //  Environmental variable property name constant
        public static final String CATALINA_BASE = "catalina.base";
        //  SSO configuration XML element tag name
        public static final String SINGLE_SIGN_ON_CONFIG_TAG_NAME = "wwc:single-sign-on";
        //  File path related constants
        public static final String TOMCAT_CONFIGURATION_FOLDER_NAME = "conf";
        public static final String WSO2_CONFIGURATION_FOLDER_NAME = "wso2";
        public static final String WSO2AS_CONFIG_FILE_NAME = "wso2as-web.xml";
        public static final String SSO_CONFIG_FILE_NAME = "sso-sp-config.properties";
        //  HTTP servlet request session notes' property name and attribute name constants
        public static final String SSO_AGENT_CONFIG = "SSOAgentConfig";
        public static final String REQUEST_PARAM_MAP = "REQUEST_PARAM_MAP";
        //  SSO configuration property name constants
        public static final String APP_SERVER_URL = "ApplicationServerURL";
        public static final String HANDLE_CONSUMER_URL_AFTER_SLO = "handleConsumerURLAfterSLO";

        public static final String REDIRECT_PATH_AFTER_SLO = "redirectPathAfterSLO";

        /**
         * Prevents instantiating the SAMLSSOValveConstants nested class.
         */
        private SAMLSSOValveConstants() {
        }
    }
}
