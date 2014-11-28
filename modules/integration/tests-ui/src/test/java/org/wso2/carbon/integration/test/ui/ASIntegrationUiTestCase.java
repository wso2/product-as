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

package org.wso2.carbon.integration.test.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.UserInfo;
import org.wso2.carbon.automation.core.utils.UserListCsvReader;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentBuilder;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentVariables;
import org.wso2.carbon.automation.core.utils.environmentutils.ProductUrlGeneratorUtil;
import org.wso2.carbon.automation.core.utils.frameworkutils.FrameworkFactory;

public class ASIntegrationUiTestCase {
    private static final Log log = LogFactory.getLog(ASIntegrationUiTestCase.class);
    protected EnvironmentVariables asServer;
    protected UserInfo userInfo;

    protected void init() throws Exception {
        int userId = 2;
        userInfo = UserListCsvReader.getUserInfo(userId);
        EnvironmentBuilder builder = new EnvironmentBuilder().as(userId);
        asServer = builder.build().getAs();
    }

    protected void init(int userId) throws Exception {
        userInfo = UserListCsvReader.getUserInfo(userId);
        EnvironmentBuilder builder = new EnvironmentBuilder().as(userId);
        asServer = builder.build().getAs();
    }


    protected void cleanup() {
        userInfo = null;
        asServer = null;
    }

    protected String getLoginURL(String productName) {
        EnvironmentBuilder environmentBuilder = new EnvironmentBuilder();
        boolean isRunningOnStratos =
                environmentBuilder.getFrameworkSettings().getEnvironmentSettings().is_runningOnStratos();

        if (isRunningOnStratos) {
            return ProductUrlGeneratorUtil.getServiceHomeURL(productName);
        } else {
            return ProductUrlGeneratorUtil.getProductHomeURL(productName);
        }
    }

    protected boolean isRunningOnCloud() {
        return FrameworkFactory.getFrameworkProperties(ProductConstant.APP_SERVER_NAME).getEnvironmentSettings().is_runningOnStratos();

    }

}
