package org.wso2.appserver.integration.common.utils;/*
*Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.commons.io.FileUtils;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.integration.common.extensions.carbonserver.CarbonServerManager;
import org.wso2.carbon.integration.common.extensions.utils.ExtensionCommonConstants;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    /**
     * This method is used to configure and start a carbon server with provided configs and offset
     * @param asServer automation context
     * @param portOffSet offset
     * @param configDirPath configs to go under conf directory
     * @return
     * @throws Exception
     */
    public static CarbonServerManager configureServer(AutomationContext asServer, int portOffSet, String configDirPath) throws Exception {
        CarbonServerManager serverManager = new CarbonServerManager(asServer);
        String carbonHome = serverManager.setUpCarbonHome(
                System.getProperty(FrameworkConstants.SYSTEM_PROPERTY_CARBON_ZIP_LOCATION));

        //copy config directory into server
        FileUtils.copyDirectory(new File(configDirPath),
                new File(carbonHome + File.separator + "repository" + File.separator + "conf"));

        //start server with offset
        Map<String, String> commandMap = new HashMap<String, String>();
        commandMap.put(ExtensionCommonConstants.PORT_OFFSET_COMMAND, String.valueOf(portOffSet));

        serverManager.startServerUsingCarbonHome(carbonHome, commandMap);
        System.setProperty(ServerConstants.CARBON_HOME, carbonHome);

        return serverManager;
    }
}
