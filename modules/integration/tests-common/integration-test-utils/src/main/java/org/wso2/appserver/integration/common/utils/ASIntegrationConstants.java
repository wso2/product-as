/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.appserver.integration.common.utils;

public class ASIntegrationConstants {
    public static final String JAGGERY_APPLICATION = "jaggery";
    public static final String WEB_APPLICATION = "webapps";
    public static final String CONTEXT_XPATH_DB_CONNECTION_URL = "//databases/database[@name='%s']/url";
    public static final String ENCRYPTED_PASSWD_URL =
            "//datasources-configuration/datasources/datasource/definition[@type='RDBMS']" +
            "/configuration/password";
}
