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
package org.wso2.appserver.utils.model;

/**
 * A Java class which defines constants associated with WSO2 Application Server configurations.
 *
 * @since 6.0.0
 */
public class ConfigurationConstants {
    //  Namespace for wso2as.xml file XML content
    public static final String SERVER_CONFIGURATION_NAMESPACE = "http://wso2.org/2016/wso2as-server";
    //  Server configuration file root element tag name
    protected static final String SERVER_CONFIGURATION_XML_ROOT_ELEMENT = "AppServer";
    //  Namespace for wso2as-web.xml file XML content
    public static final String WEBAPP_DESCRIPTOR_NAMESPACE = "http://wso2.org/2016/wso2as-web";
    //  Webapp descriptor file root element tag name
    protected static final String WEBAPP_DESCRIPTOR_XML_ROOT_ELEMENT = "wso2as-web";

    /**
     * Prevents instantiating this class.
     */
    private ConfigurationConstants() {
    }

    public static class ClassloadingConstants {
        //  Server level configuration XML element names
        protected static final String WSO2AS_CLASSLOADING = "Classloading";
        protected static final String WSO2AS_ENVIRONMENTS = "Environments";
        protected static final String WSO2AS_ENVIRONMENT = "Environment";
        protected static final String WSO2AS_ENVIRONMENT_NAME = "Name";
        protected static final String WSO2AS_CLASSPATH = "Classpath";

        //  Web app level configuration XML element names
        protected static final String WEBAPP_CLASSLOADING = "classloading";
        protected static final String WEBAPP_ENVIRONMENTS = "environments";
        protected static final String WEBAPP_ENVIRONMENT = "environment";
        protected static final String WEBAPP_ENVIRONMENT_NAME = "name";
        protected static final String WEBAPP_CLASSPATH = "classpath";

        /**
         * Prevents instantiating this nested class.
         */
        private ClassloadingConstants() {
        }
    }

    /**
     * A nested class which defines constants for Application Server single-sign-on (SSO).
     */
    public static class SSOConstants {
        //  Server level configuration XML element names
        protected static final String WSO2AS_SINGLE_SIGN_ON = "SingleSignOn";
        protected static final String WSO2AS_SAML = "SAML";
        protected static final String WSO2AS_IDP_URL = "IdPURL";
        protected static final String WSO2AS_IDP_ENTITY_ID = "IdPEntityId";
        protected static final String WSO2AS_SIGNATURE_VALIDATOR_IMPL = "SignatureValidatorImplClass";
        protected static final String WSO2AS_KEYSTORE_PATH = "KeystorePath";
        protected static final String WSO2AS_KEYSTORE_PASSWORD = "KeystorePassword";
        protected static final String WSO2AS_IDP_CERTIFICATE_ALIAS = "IdPCertificateAlias";
        protected static final String WSO2AS_PRIVATE_KEY_ALIAS = "PrivateKeyAlias";
        protected static final String WSO2AS_PRIVATE_KEY_PASSWORD = "PrivateKeyPassword";

        //  Web app level configuration XML element names
        protected static final String WEBAPP_SINGLE_SIGN_ON = "single-sign-on";
        protected static final String WEBAPP_SKIP_URIS = "skip-uris";
        protected static final String WEBAPP_SKIP_URI = "skip-uri";
        protected static final String WEBAPP_QUERY_PARAMS = "query-params";
        protected static final String WEBAPP_APP_SERVER_URL = "application-server-url";
        protected static final String WEBAPP_SAML = "saml";
        protected static final String WEBAPP_ENABLE_SSO = "enable-sso";
        protected static final String WEBAPP_REQUEST_URL_POSTFIX = "request-url-postfix";
        protected static final String WEBAPP_BINDING = "http-binding";
        protected static final String WEBAPP_ISSUER_ID = "issuer-id";
        protected static final String WEBAPP_CONSUMER_URL = "consumer-url";
        protected static final String WEBAPP_CONSUMER_URL_POSTFIX = "consumer-url-postfix";
        protected static final String WEBAPP_ATTR_CONSUMING_SERVICE_INDEX = "attribute-consuming-service-index";
        protected static final String WEBAPP_ENABLE_SLO = "enable-slo";
        protected static final String WEBAPP_SLO_URL_POSTFIX = "slo-url-postfix";
        protected static final String WEBAPP_ASSERTION_ENCRYPTION = "enable-assertion-encryption";
        protected static final String WEBAPP_ASSERTION_SIGNING = "enable-assertion-signing";
        protected static final String WEBAPP_REQUEST_SIGNING = "enable-request-signing";
        protected static final String WEBAPP_RESPONSE_SIGNING = "enable-response-signing";

        /**
         * Prevents instantiating this nested class.
         */
        private SSOConstants() {
        }
    }
}
