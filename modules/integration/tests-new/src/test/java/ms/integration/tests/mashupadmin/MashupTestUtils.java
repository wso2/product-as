/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package ms.integration.tests.mashupadmin;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentVariables;
import org.wso2.carbon.mashup.jsservices.stub.MashupServiceAdminStub;

/**
 * MS- MashupTestUtils
 */
public class MashupTestUtils {
    private static final Log log = LogFactory.getLog(MashupTestUtils.class);

    public static MashupServiceAdminStub getMashupServiceAdminStub(EnvironmentVariables asServer) {
        String serviceURL;
        serviceURL = asServer.getBackEndUrl()+"MashupServiceAdmin";

        MashupServiceAdminStub mashupServiceAdminStub = null;
        try {
            mashupServiceAdminStub = new MashupServiceAdminStub(serviceURL);
            ServiceClient client = mashupServiceAdminStub._getServiceClient();
            Options option = client.getOptions();
            option.setManageSession(true);
            option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING,
                               asServer.getSessionCookie());
            mashupServiceAdminStub._getServiceClient().getOptions().setTimeOutInMilliSeconds(600000);

        } catch (AxisFault e) {
            log.error("Unexpected exception thrown in MashupServiceAdminStub creation", e);
        }
        log.info("MashupServiceAdminStub created");
        return mashupServiceAdminStub;
    }
}
