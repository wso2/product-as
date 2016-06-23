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
 * This class defines constants used during the unit tests.
 *
 * @since 6.0.0
 */
public class TestConstants {
    //  unit-test level system properties
    public static final String TEST_RESOURCES_LOCATION = System.getProperty("test.resources");

    //  constants used to construct Tomcat internal components for unit-tests
    public static final String WEB_APP_BASE = "webapps";
    public static final String CONTEXT_PATH = "/foo-app";
    public static final String DEFAULT_TOMCAT_HOST = "localhost";
    public static final String SSL_PROTOCOL = "https";
    public static final int SSL_PORT = 8443;

    public static final String DEFAULT_KEY_STORE_LOCATION = TEST_RESOURCES_LOCATION + "/wso2carbon.jks";
    public static final String DEFAULT_KEY_STORE_TYPE = "JKS";
    public static final String DEFAULT_KEY_STORE_PASSWORD = "wso2carbon";
    public static final String DEFAULT_APPLICATION_SERVER_URL = SSL_PROTOCOL + "://" + DEFAULT_TOMCAT_HOST + ":" +
            SSL_PORT;
    public static final String DEFAULT_SP_ENTITY_ID = "foo-app";
    public static final String DEFAULT_CONSUMER_URL_POSTFIX = "acs";
    public static final String DEFAULT_ACS_URL = DEFAULT_APPLICATION_SERVER_URL + CONTEXT_PATH + "/" +
            DEFAULT_CONSUMER_URL_POSTFIX;
    public static final String DEFAULT_QUERY_PARAMS = "keyOne=valOne&keyTwo=valTwo";
    public static final String DEFAULT_HTTP_BINDING = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
    public static final String DEFAULT_SLO_URL_POSTFIX = "logout";
    public static final String INVALID_KEYSTORE_LOCATION = TEST_RESOURCES_LOCATION + "/conf/wso2carbon.jks";
    public static final String INVALID_KEYSTORE_PASSWORD = "wso2carbonjks";
    public static final String INVALID_KEYSTORE_TYPE = "PKCS";

    //  test URIs to be passed to SSO agent request resolver
    public static final String SKIP_URI_ONE = SSL_PROTOCOL + "://" + DEFAULT_TOMCAT_HOST + ":" + SSL_PORT + "/skip1";
    public static final String SKIP_URI_TWO = SSL_PROTOCOL + "://" + DEFAULT_TOMCAT_HOST + ":" + SSL_PORT + "/skip2";
    public static final String NON_SKIP_URI = SSL_PROTOCOL + "://" + DEFAULT_TOMCAT_HOST + ":" + SSL_PORT + "/non-skip";
    public static final String LOGOUT_REQ_URI = SSL_PROTOCOL + "://" + DEFAULT_TOMCAT_HOST + ":" + SSL_PORT + "/logout";
    public static final String NON_LOGOUT_REQ_URI = SSL_PROTOCOL + "://" + DEFAULT_TOMCAT_HOST + ":" + SSL_PORT +
            "/end";

    /**
     * Prevents instantiating this class.
     */
    private TestConstants() {
    }
}
