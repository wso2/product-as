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
package org.wso2.stratos.appserver.sample.installer.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.FileManipulator;

import java.io.File;
import java.io.IOException;

/**
 * This class handles installing of samples
 */
public class SampleInstaller {

    private static final Log log = LogFactory.getLog(SampleInstaller.class);
    private String serverDir = CarbonUtils.getCarbonHome() + File.separator + "repository" +
                               File.separator + "deployment" + File.separator + "server";
    private String tenantsDir = CarbonUtils.getCarbonHome() + File.separator + "repository" +
                                File.separator + "tenants";

    public void installServiceSamples(int tenantId) throws IOException {
        log.info("Installing service samples for tenant ");
        // Copying the sample AAR
        File srcAAR =
                new File(serverDir + File.separator + "axis2services" + File.separator +
                         "SimpleStockQuoteService-1.0.aar");
        File targetAAR =
                new File(tenantsDir + File.separator + tenantId + File.separator +
                         "axis2services" + File.separator + "SimpleStockQuoteService.aar");
        if (srcAAR.exists() && !targetAAR.exists()) {
            FileManipulator.copyFile(srcAAR, targetAAR);
        }
    }

    public void installWebappSamples(int tenantId) throws IOException {
        log.info("Installing webapp samples for tenant ");
        // Copying the sample WAR
        File srcWAR =
                new File(serverDir + File.separator + "webapps" + File.separator +
                         "example.war");
        File targetWAR =
                new File(tenantsDir + File.separator + tenantId + File.separator + "webapps" +
                         File.separator + "example.war");
        if (srcWAR.exists() && !targetWAR.exists()) {
            FileManipulator.copyFile(srcWAR, targetWAR);
        }
    }

    public void installAllSamples(int tenantId) throws IOException {
        installServiceSamples(tenantId);
        installWebappSamples(tenantId);
    }
}
