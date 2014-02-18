/*                                                                             
 * Copyright 2004,2005 The Apache Software Foundation.                         
 *                                                                             
 * Licensed under the Apache License, Version 2.0 (the "License");             
 * you may not use this file except in compliance with the License.            
 * You may obtain a copy of the License at                                     
 *                                                                             
 *      http://www.apache.org/licenses/LICENSE-2.0                             
 *                                                                             
 * Unless required by applicable law or agreed to in writing, software         
 * distributed under the License is distributed on an "AS IS" BASIS,           
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    
 * See the License for the specific language governing permissions and         
 * limitations under the License.                                              
 */
package org.wso2.stratos.appserver.sample.installer.ui.internal;

import org.apache.axis2.context.ConfigurationContext;
import org.wso2.carbon.utils.ConfigurationContextService;

/**
 * @scr.component name="org.wso2.stratos.appserver.sample.installer.ui.internal.SampleInstallerServiceComponent" immediate="true"
 * @scr.reference name="config.context.service"
 * interface="org.wso2.carbon.utils.ConfigurationContextService" cardinality="1..1"
 * policy="dynamic" bind="setConfigurationContextService"
 * unbind="unsetConfigurationContextService"
 */
public class SampleInstallerServiceComponent {
    private static ConfigurationContext serverConfigCtx;

    protected void setConfigurationContextService(ConfigurationContextService contextService) {
        serverConfigCtx = contextService.getServerConfigContext();
    }

    protected void unsetConfigurationContextService(ConfigurationContextService contextService) {
        serverConfigCtx = null;
    }

    public static ConfigurationContext getServerConfigCtx() {
        return serverConfigCtx;
    }
}
