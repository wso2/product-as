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
package org.wso2.appserver.utils.configuration.model;

/**
 * This class defines WSO2 Application Server specific utility constants.
 *
 * @since 6.0.0
 */
public class ConfigurationConstants {
    /**
     * Prevents instantiating the {@code ConfigurationConstants} class.
     */
    private ConfigurationConstants() {
    }

    //  Namespace for wso2as-web.xml file XML content
    public static final String WEBAPP_DESCRIPTOR_NAMESPACE = "http://wso2.org/2016/wso2as-web";
    //  Webapp descriptor file root element tag name
    protected static final String WEBAPP_DESCRIPTOR_XML_ROOT_ELEMENT = "wso2asWeb";

    /**
     * A nested class which defines constants for Application Server single-sign-on (SSO).
     */
    public static class SSOConfigurationConstants {
        protected static final String SINGLE_SIGN_ON = "singleSignOn";
        protected static final String SKIP_URI = "skipURI";

        /**
         * Prevents instantiating the SSOConfigurationConstants nested class.
         */
        private SSOConfigurationConstants() {
        }
    }

    /**
     * A nested class which defines configuration constants for Application Server class-loading.
     */
    public static class ClassLoadingConfigurationConstants {
        protected static final String CLASSLOADING = "classloading";
        protected static final String ENVIRONMENT = "environment";
        protected static final String CLASSPATH = "classpath";

        /**
         * Prevents instantiating the ClassLoadingConfigurationConstants nested class.
         */
        private ClassLoadingConfigurationConstants() {
        }
    }
}
