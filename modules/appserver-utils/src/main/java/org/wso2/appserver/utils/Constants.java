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
package org.wso2.appserver.utils;

/**
 * This class defines WSO2 Application Server specific utility constants.
 *
 * @since 6.0.0
 */
public class Constants {
    /**
     * Prevents instantiating the Constants class.
     */
    private Constants() {
    }

    //  Environmental variable property name constant
    public static final String CATALINA_BASE = "catalina.base";
    //  File path related constants
    public static final String TOMCAT_CONFIGURATION_HOME = "conf";
    public static final String WSO2_CONFIGURATION_HOME = "wso2";
    public static final String WEBAPP_RESOURCE_FOLDER = "WEB-INF";
    public static final String WEBAPP_DESCRIPTOR = "wso2as-web.xml";
    public static final String WEBAPP_DESCRIPTOR_SCHEMA = "wso2as-web.xsd";
    //  Namespace for wso2as-web.xml file XML content
    public static final String WSO2_NAMESPACE = "http://wso2.org/2016/wso2as-web";
    //  wso2as-web.xml file root element tag name
    public static final String WEBAPP_DESCRIPTOR_XML_ROOT_ELEMENT = "wso2asWeb";

    /**
     * A nested class which defines constants for Application Server single-sign-on (SSO).
     */
    public static class SSOConfigurationConstants {
        public static final String SINGLE_SIGN_ON = "singleSignOn";
        public static final String SKIP_URI = "skipURI";
        public static final String APPLICATION_SERVER_URL_DEFAULT = "https://localhost:8443";
        public static final String LOGIN_URL_DEFAULT = "loginURL";

        /**
         * Prevents instantiating the SSOConfigurationConstants nested class.
         */
        private SSOConfigurationConstants() {
        }

        /**
         * A nested class which defines SAML specific configuration constants for Application Server SSO.
         */
        public static class SAMLConstants {
            public static final String IDP_URL_DEFAULT = "https://localhost:9443/samlsso";
            public static final String IDP_ENTITY_ID_DEFAULT = "localhost";
            public static final String BINDING_TYPE_DEFAULT = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
            public static final String ATTR_CONSUMING_SERVICE_INDEX_DEFAULT = "1701087467";
            public static final String CONSUMER_URL_POSTFIX_DEFAULT = "/acs";
            public static final String REQUEST_URL_POSTFIX_DEFAULT = "samlsso";
            public static final String SLO_URL_POSTFIX_DEFAULT = "logout";
            public static final String SIGNATURE_VALIDATOR_IMPL_CLASS_DEFAULT =
                    "org.wso2.appserver.webapp.security.sso.saml.signature.SAMLSignatureValidatorImplementation";
            public static final String ADDITIONAL_REQUEST_PARAMETERS_DEFAULT = "&forceAuth=true";

            /**
             * Prevents instantiating the SAMLConstants nested class.
             */
            private SAMLConstants() {
            }
        }
    }

    /**
     * A nested class which defines configuration constants for Application Server class-loading.
     */
    public static class ClassLoadingConfigurationConstants {
        public static final String CLASSLOADING = "classloading";
        public static final String ENVIRONMENTS = "environments";
        public static final String ENVIRONMENT = "environment";
        public static final String CLASSPATH = "classpath";

        /**
         * Prevents instantiating the ClassLoadingConfigurationConstants nested class.
         */
        private ClassLoadingConfigurationConstants() {
        }
    }

    /**
     * A nested class which defines configuration constants for Application Server REST Web Services.
     */
    public static class RestWebServicesConfigurationConstants {
        public static final String ISMANAGEDAPI = "isManagedAPI";

        /**
         * Prevents instantiating the RestWebServicesConfigurationConstants nested class.
         */
        private RestWebServicesConfigurationConstants() {
        }
    }
}
