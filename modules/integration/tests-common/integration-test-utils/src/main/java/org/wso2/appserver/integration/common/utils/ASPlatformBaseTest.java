/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.appserver.integration.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.Instance;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for mb paltform clustering test cases
 */

public class ASPlatformBaseTest {

    protected Log log = LogFactory.getLog(ASPlatformBaseTest.class);
    protected Map<String, AutomationContext> contextMap;
    protected String defaultGroup = "AS";

    /**
     * Create automation context objects for every node in config, this will create AS cluster by default
     *
     * @param userMode User mode for which the automation context should use
     * @throws XPathExpressionException
     */
    protected void initCluster(TestUserMode userMode) throws XPathExpressionException {
        contextMap = new HashMap<String, AutomationContext>();
        AutomationContext automationContext = new AutomationContext(defaultGroup, userMode);
        log.info("Cluster instance loading");
        Map<String, Instance> instanceMap = automationContext.getProductGroup().getInstanceMap();

        if (instanceMap != null && instanceMap.size() > 0) {
            for (Map.Entry<String, Instance> entry : instanceMap.entrySet()) {
                String instanceKey = entry.getKey();
                contextMap.put(instanceKey, new AutomationContext(defaultGroup, instanceKey, userMode));
                log.info(instanceKey);
            }
        }

    }

    /**
     * Create automation context objects for every node in config, this will create other nodes
     *
     * @param userMode User mode for which the automation context should use
     * @throws XPathExpressionException
     */
    protected void initCluster(TestUserMode userMode, String productGroup) throws XPathExpressionException {
        contextMap = new HashMap<String, AutomationContext>();
        AutomationContext automationContext = new AutomationContext(productGroup, userMode);
        log.info("Cluster instance loading");
        Map<String, Instance> instanceMap = automationContext.getProductGroup().getInstanceMap();

        if (instanceMap != null && instanceMap.size() > 0) {
            for (Map.Entry<String, Instance> entry : instanceMap.entrySet()) {
                String instanceKey = entry.getKey();
                contextMap.put(instanceKey, new AutomationContext(productGroup, instanceKey, userMode));
                log.info(instanceKey);
            }
        }

    }

    /**
     * Get automation context object with given node key
     *
     * @param key The key value for automation context map
     * @return Respective automation context
     */

    protected AutomationContext getAutomationContextWithKey(String key) {

        if (contextMap != null && contextMap.size() > 0) {
            for (Map.Entry<String, AutomationContext> entry : contextMap.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(key)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    /**
     * Login and provide session cookie for node
     *
     * @param context The automation context to be used.
     * @return The session cookie of the login user
     * @throws IOException
     * @throws XPathExpressionException
     * @throws URISyntaxException
     * @throws SAXException
     * @throws XMLStreamException
     * @throws LoginAuthenticationExceptionException
     */
    protected String login(AutomationContext context)
            throws IOException, XPathExpressionException, URISyntaxException, SAXException,
            XMLStreamException, LoginAuthenticationExceptionException, AutomationUtilException {
        LoginLogoutClient loginLogoutClient = new LoginLogoutClient(context);
        return loginLogoutClient.login();
    }
}
