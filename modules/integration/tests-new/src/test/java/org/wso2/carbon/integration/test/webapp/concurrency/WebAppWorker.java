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

package org.wso2.carbon.integration.test.webapp.concurrency;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.api.clients.webapp.mgt.WebAppAdminClient;

import java.io.File;
import java.rmi.RemoteException;

import static org.testng.Assert.assertTrue;

public class WebAppWorker extends Thread {

    private final Log log = LogFactory.getLog(WebAppWorker.class);
    private String session;
    private String backendURL;
    private String filePath;
    private WebAppAdminClient webAppAdminClient;

    public WebAppWorker(String session, String backendURL, String filePath) {
        this.session = session;
        this.backendURL = backendURL;
        this.filePath = filePath;
    }

    public void run() {
        Boolean status = true;
        try {
            webAppAdminClient = new WebAppAdminClient(backendURL, session);
            webAppAdminClient.warFileUplaoder(filePath);
            log.info("Deploying webapp - " + filePath.substring(filePath.lastIndexOf(File.separator) + 1,
                                                                filePath.length()));
        } catch (AxisFault axisFault) {
            status = false;
        } catch (RemoteException e) {
            status = false;
        }
        assertTrue(status, "Error while webapp deployment");
    }

    public void deleteWebApp() throws RemoteException {
        webAppAdminClient.deleteWebAppFile(filePath.substring(filePath.lastIndexOf(File.separator) + 1,
                                                              filePath.length()));
    }
}
