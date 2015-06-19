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
    public static final String AS_PRODUCT_GROUP = "AS";
    public static final String AS_INSTANCE_0002 = "appServerInstance0002";
    public static final String AS_INSTANCE_0003 = "appServerInstance0003";
    public static final String CONTEXT_XPATH_DATA_SOURCE = "//datasources/datasource[@name='%s']";
    public static final String PASSWORD_PROPERTY_SECRET_ALIAS_KEY = "svns:secretAlias";
    public static final String PASSWORD_PROPERTY_SECRET_ALIAS_VALUE = "svns:secretAlias";
    public static final String SVN_SECRET_ALIAS_WSO2_DATASOURCE = "Datasources.WSO2_CARBON_DB.Configuration.Password";
    public static final String ENCRYPTED_PASSWD_URL =
            "//datasources-configuration/datasources/datasource[name='WSO2_CARBON_DB']/definition[@type='RDBMS']" +
            "/configuration/password";
}
