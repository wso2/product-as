/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.appserver.integration.tests.session.persistence;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.fail;

public class SessionPersistenceTest extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(SessionPersistenceTest.class);

    private static final String SESSION_EXAMPLE_SAMPLE = "/example/servlets/servlet/SessionExample";
    private static final String ADD_SESSION_ATTRIBUTE = "/example/servlets/servlet/SessionExample?dataname=foo&datavalue=123";
    protected static final String TOMCAT_CONTEXT_XML = FrameworkPathUtil.getCarbonServerConfLocation() +
            File.separator + "tomcat" + File.separator + "context.xml";
    private static final String SAMPLE_CONTEXT_XML = TestConfigurationProvider.getResourceLocation() + File.separator + "artifacts" + File.separator +
            "AS" + File.separator + "tomcat" + File.separator + "session" + File.separator + "context.xml";
    private HttpClient httpClient = new HttpClient();

    protected ServerConfigurationManager serverManager;

    @BeforeClass
    public void init() throws Exception {
        super.init();
        serverManager = new ServerConfigurationManager(asServer);
        File sourceFile = new File(SAMPLE_CONTEXT_XML);
        File targetFile = new File(TOMCAT_CONTEXT_XML);
        serverManager.applyConfiguration(sourceFile, targetFile, true, true);
    }


    @Test(groups = "wso2.as", description = "Execute SessionExample for availability")
    public void checkAvailability() throws IOException {
        String sessionAppUrl = webAppURL + SESSION_EXAMPLE_SAMPLE;
        GetMethod getMethodOne = new GetMethod(sessionAppUrl);
        try {
            log.info("SessionExample Available");
            int statusCode = httpClient.executeMethod(getMethodOne);
            if (statusCode != HttpStatus.SC_OK) {
                fail("SessionExample not available: " + getMethodOne.getStatusLine());
            }
        } finally {
            getMethodOne.releaseConnection();
        }
    }

    @Test(groups = "wso2.as", description = "Add session attribute",
            dependsOnMethods = "checkAvailability")
    public void addSessionAttribute() throws IOException {
        String addSessionAttributeUrl = webAppURL + ADD_SESSION_ATTRIBUTE;
        GetMethod addAttribute = new GetMethod(addSessionAttributeUrl);
        try {
            log.info("Adding session attribute");
            int statusCode = httpClient.executeMethod(addAttribute);
            if (statusCode != HttpStatus.SC_OK) {
                fail("Adding session attribute failed: " + addAttribute.getStatusLine());
            }
            Assert.assertTrue(addAttribute.getResponseBodyAsString().contains("foo = 123"));
        } finally {
            addAttribute.releaseConnection();
        }
    }

    @Test(groups = "wso2.as", description = "Check persisted session attribute",
            dependsOnMethods = "addSessionAttribute")
    public void checkPersistedSession() throws IOException, AutomationUtilException {
        // restart server to check persisted sessions
        serverManager.restartGracefully();
        String checkPersistenceUrl = webAppURL + SESSION_EXAMPLE_SAMPLE;
        GetMethod getSessions = new GetMethod(checkPersistenceUrl);
        try {
            log.info("Checking persisted sessions");
            int statusCode = httpClient.executeMethod(getSessions);
            if (statusCode != HttpStatus.SC_OK) {
                fail("Failed to retrieve persisted sessions: " + getSessions.getStatusLine());
            }
            Assert.assertTrue(getSessions.getResponseBodyAsString().contains("foo = 123"));
        } finally {
            getSessions.releaseConnection();
        }
    }

    @AfterClass
    public void resetServer() throws IOException, AutomationUtilException {
        serverManager.restoreToLastConfiguration(true);
    }

}
