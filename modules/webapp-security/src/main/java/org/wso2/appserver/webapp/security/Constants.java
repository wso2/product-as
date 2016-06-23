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
package org.wso2.appserver.webapp.security;

/**
 * This class defines the constants used in the SAML 2.0 single-sign-on (SSO) implementation.
 *
 * @since 6.0.0
 */
public class Constants {
    //  SAML 2.0 single-sign-on (SSO) parameter name constants
    public static final String HTTP_POST_PARAM_SAML_REQUEST = "SAMLRequest";
    public static final String HTTP_POST_PARAM_SAML_RESPONSE = "SAMLResponse";

    //  SSO agent configuration property default values
    public static final String DEFAULT_SIGN_VALIDATOR_IMPL = "org.wso2.appserver.webapp.security.saml.signature" +
            ".SAMLSignatureValidatorImplementation";
    public static final String DEFAULT_CONSUMER_URL_POSTFIX = "acs";
    public static final String SAML2_HTTP_POST_BINDING = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
    public static final String DEFAULT_SLO_URL_POSTFIX = "logout";

    //  HTTP servlet request session notes' property name and attribute name constants
    public static final String SESSION_BEAN = "org.wso2.appserver.webapp.security.bean.LoggedInSession";
    public static final String IS_PASSIVE_AUTH_ENABLED = "IsPassiveAuthn";
    public static final String IS_FORCE_AUTH_ENABLED = "IsForceAuthn";
    public static final String RELAY_STATE = "RelayState";
    public static final String RELAY_STATE_ID = "RelayStateId";
    public static final String REQUEST_PARAM_MAP = "RequestParamMap";
    public static final String REQUEST_URL = "RequestURL";
    public static final String REQUEST_QUERY_STRING = "RequestQueryString";
    public static final String REQUEST_PARAMETERS = "RequestParams";

    //  miscellaneous constants
    public static final String UTF8_ENC = "UTF-8";
    public static final String CONTENT_TYPE_HTML = "text/html";
}
