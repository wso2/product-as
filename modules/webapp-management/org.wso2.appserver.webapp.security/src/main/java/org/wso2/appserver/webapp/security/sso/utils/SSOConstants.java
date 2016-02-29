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
package org.wso2.appserver.webapp.security.sso.utils;

/**
 * This class defines the constants utilized used within the org.wso2.appserver.webapp.security.sso.
 *
 * @since 6.0.0
 */
public class SSOConstants {
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
        public static final String HTTP_POST_PARAM_SAML_REQUEST = "SAMLRequest";
        public static final String HTTP_POST_PARAM_SAML_RESPONSE = "SAMLResponse";

        /**
         * Prevents instantiating the SAML2SSO nested class.
         */
        private SAML2SSO() {
        }
    }

    /**
     * This class defines the single-sign-on (SSO) agent configuration property default values.
     */
    public static class SSOAgentConfiguration {
        public static final String APPLICATION_SERVER_URL_DEFAULT = "https://localhost:8443";
        public static final String REQUEST_URL_POSTFIX_DEFAULT = "samlsso";
        public static final String BINDING_TYPE_DEFAULT = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
        public static final String IDP_URL_DEFAULT = "https://localhost:9443/samlsso";
        public static final String IDP_ENTITY_ID_DEFAULT = "localhost";
        public static final String SLO_URL_POSTFIX_DEFAULT = "logout";
        public static final String CONSUMER_URL_POSTFIX_DEFAULT = "/acs";

        /**
         * Prevents instantiating the SSOAgentConfiguration nested class.
         */
        private SSOAgentConfiguration() {
        }
    }

    /**
     * This class defines constants used in the implementation of the SAML single-sign-on (SSO) valve.
     */
    public static class SAMLSSOValveConstants {
        //  HTTP servlet request session notes' property name and attribute name constants
        public static final String SSO_AGENT_CONFIG = "SSOAgentConfig";
        public static final String REQUEST_PARAM_MAP = "REQUEST_PARAM_MAP";

        public static final String REDIRECT_PATH_AFTER_SLO = "redirectPathAfterSLO";
        public static final String SESSION_BEAN = "org.wso2.appserver.webapp.security.sso.bean.LoggedInSession";

        /**
         * Prevents instantiating the SAMLSSOValveConstants nested class.
         */
        private SAMLSSOValveConstants() {
        }
    }
}
