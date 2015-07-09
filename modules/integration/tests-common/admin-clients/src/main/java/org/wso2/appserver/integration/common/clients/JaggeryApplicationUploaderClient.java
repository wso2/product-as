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
package org.wso2.appserver.integration.common.clients;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaggeryjs.jaggery.app.mgt.stub.JaggeryAppAdminStub;
import org.jaggeryjs.jaggery.app.mgt.stub.types.carbon.WebappUploadData;

import javax.activation.DataHandler;
import java.net.MalformedURLException;
import java.net.URL;

public class JaggeryApplicationUploaderClient {
    private static final Log log = LogFactory.getLog(JaggeryApplicationUploaderClient.class);
    private JaggeryAppAdminStub jaggeryAppAdminStub;
    private final String serviceName = "JaggeryAppAdmin";

    public JaggeryApplicationUploaderClient(String backEndUrl, String sessionCookie)
            throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        jaggeryAppAdminStub = new JaggeryAppAdminStub(endPoint);
        AuthenticateStubUtil.authenticateStub(sessionCookie, jaggeryAppAdminStub);
    }

    public void uploadJaggeryFile(String fileName, String filePath) throws Exception {
        WebappUploadData webappUploadData = new WebappUploadData();
        webappUploadData.setFileName(fileName);
        webappUploadData.setDataHandler(createDataHandler(filePath));
        jaggeryAppAdminStub.uploadWebapp(new WebappUploadData[]{webappUploadData});// uploads to server

    }

    private DataHandler createDataHandler(String filePath) throws MalformedURLException {
        URL url = new URL("file://" + filePath);
        return new DataHandler(url);
    }

}
