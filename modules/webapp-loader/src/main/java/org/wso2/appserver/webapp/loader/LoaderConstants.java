/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.wso2.appserver.webapp.loader;

import java.io.File;

/**
 * Constants for the wso2 class loader.
 */
public final class LoaderConstants {

    public static final String ENVIRONMENT_CONFIG_FILE = "webapp-classloader-environments.xml";
    public static final String CLASSLOADER_CONFIG_FILE = "wso2as-web.xml";

    public static final String DEFAULT_EXT_DIR = "${catalina.home}" + File.separator + "lib" + File.separator + "ext";

    public static final String TOMCAT_ENV = "Tomcat";

    public static final String XSD_NAMESPACE = "http://wso2.org/projects/as/classloading-environments";

}
