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
package org.wso2.appserver.sample.utils;

/**
 * This class defines constants used within the unit-tests of Application Server Utils module.
 *
 * @since 6.0.0
 */
public class Constants {
    static final String JAGGERY_ENV_NAME = "jaggery";
    static final String JAGGERY_ENV_CLASSPATH = "${catalina.base}/lib/runtimes/jaggery/";
    static final String SPRING_ENV_NAME = "Spring";

    static final String IDP_URL = "https://localhost:9443/samlsso";
    static final String IDP_ENTITY_ID = "localhost";
    static final String IDP_CERT_ALIAS = "wso2carbon";
    static final String SKIP_URI = "http://www.example.com";
    static final String QUERY_PARAMS = "tenant=admin&dialect=SAML";
    static final String ACS_BASE = "https://localhost:8443";
    static final String SAML_BINDING = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect";
    static final String ISSUER_ID = "foo-app";
    static final String CONSUMER_URL = "https://localhost:8443/foo-app/acs";
    static final String CONSUMER_URL_POSTFIX = "acs";
    static final String SLO_URL_POSTFIX = "logout";
    static final String LOGIN_URL_KEY = "LoginURL";
    static final String LOGIN_URL_VALUE = "index.jsp";
    static final String RELAY_STATE_KEY = "RelayState";
    static final String RELAY_STATE_VALUE = "index.jsp";

    static final String USERNAME = "admin";
    static final String PASSWORD = "admin";
    static final String DATA_AGENT_TYPE = "Thrift";
    static final String AUTHN_URL = "ssl://127.0.0.1:7711";
    static final String PUBLISHER_URL = "tcp://127.0.0.1:7611";
    static final String STREAM_ID = "org.wso2.http.stats:1.0.0";

    static final String KEYSTORE_PATH = "${catalina.base}/conf/wso2/wso2carbon.jks";
    static final String TYPE = "JKS";
    static final String KEYSTORE_PASSWORD = "wso2carbon";
    static final String PRIVATE_KEY_ALIAS = "wso2carbon";
    static final String PRIVATE_KEY_PASSWORD = "wso2carbon";

    static final String TRUSTSTORE_PATH = "${catalina.base}/conf/wso2/client-truststore.jks";
    static final String TRUSTSTORE_PASSWORD = "wso2carbon";

    /**
     * Prevents instantiating this class.
     */
    private Constants() {
    }
}
