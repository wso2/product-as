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
package org.wso2.appserver.utils.configuration.loaders;

/**
 * This class defines the constants used within the configuration loaders.
 *
 * @since 6.0.0
 */
public class LoaderConstants {
    /**
     * A nested class which defines constants for Application Server single-sign-on (SSO).
     */
    public static class SSOConfigurationConstants {
        protected static final String APPLICATION_SERVER_URL_DEFAULT = "https://localhost:8443";
        protected static final String LOGIN_URL_DEFAULT = "loginURL";

        /**
         * Prevents instantiating the SSOConfigurationConstants nested class.
         */
        private SSOConfigurationConstants() {
        }

        /**
         * A nested class which defines SAML specific configuration constants for Application Server SSO.
         */
        public static class SAMLConstants {
            protected static final String IDP_URL_DEFAULT = "https://localhost:9443/samlsso";
            protected static final String IDP_ENTITY_ID_DEFAULT = "localhost";
            protected static final String BINDING_TYPE_DEFAULT = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
            protected static final String ATTR_CONSUMING_SERVICE_INDEX_DEFAULT = "1701087467";
            protected static final String CONSUMER_URL_POSTFIX_DEFAULT = "/acs";
            protected static final String REQUEST_URL_POSTFIX_DEFAULT = "samlsso";
            protected static final String SLO_URL_POSTFIX_DEFAULT = "logout";
            protected static final String SIGNATURE_VALIDATOR_IMPL_CLASS_DEFAULT =
                    "org.wso2.appserver.webapp.security.sso.saml.signature.SAMLSignatureValidatorImplementation";
            protected static final String ADDITIONAL_REQUEST_PARAMETERS_DEFAULT = "&forceAuth=true";

            /**
             * Prevents instantiating the SAMLConstants nested class.
             */
            private SAMLConstants() {
            }
        }
    }
}
