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
package org.wso2.appserver;

/**
 * This class defines constants used within the unit-tests of Application Server Utils module.
 *
 * @since 6.0.0
 */
public class TestConstants {
    protected static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
    protected static final String CATALINA_BASE = "tomcat-base";
    protected static final String SAMPLE_CONFIGURATION_FILE = "sample.xml";
    protected static final String BUILD_DIRECTORY = "target";
    protected static final String TEST_RESOURCE_FOLDER = "test-resources";
    protected static final String SAMPLE_XML_FILE = "sample-wso2as.xml";
    protected static final String SAMPLE_XSD_FILE = "sample-wso2as.xsd";

    protected static final String CXF_ENV_NAME = "CXF";
    protected static final String CXF_ENV_CLASSPATH = "${catalina.home}/lib/runtimes/cxf/";
    protected static final String JAXRS_ENV_NAME = "JAX-RS";
    protected static final String JAXRS_ENV_CLASSPATH = "${catalina.home}/lib/runtimes/hello-parent-runtime.jar";

    protected static final String IDP_URL = "https://localhost:9443/samlsso";
    protected static final String IDP_ENTITY_ID = "localhost";
    protected static final String VALIDATOR_CLASS = "org.wso2.appserver.webapp.SAMLSignatureValidatorImplementation";

    protected static final String USERNAME = "admin";
    protected static final String PASSWORD = "admin";
    protected static final String AUTHN_URL = "ssl://127.0.0.1:7611";
    protected static final String PUBLISHER_URL = "tcp://127.0.0.1:7611";
    protected static final String STREAM_ID = "org.wso2.http.stat:1.0.0";

    protected static final String KEYSTORE_PATH = "${catalina.home}";
    protected static final String KEYSTORE_PASSWORD = "wso2carbon";
    protected static final String IDP_CERT_ALIAS = "wso2carbon";
    protected static final String PRIVATE_KEY_ALIAS = "wso2carbon";
    protected static final String PRIVATE_KEY_PASSWORD = "wso2carbon";

    protected static final String TRUSTSTORE_PATH = "${catalina.home}";
    protected static final String TRUSTSTORE_PASSWORD = "wso2carbon";

    /**
     * Prevents instantiating this class.
     */
    private TestConstants() {
    }
}
