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

package org.wso2.carbon.integration.test.qualityofservice.throtllingservice;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.jarservices.JARServiceUploaderClient;
import org.wso2.carbon.automation.api.clients.throttling.ThrottlingClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.automation.utils.concurrency.ConcurrencyTest;
import org.wso2.carbon.integration.test.ASIntegrationTest;
import org.wso2.carbon.throttle.stub.types.InternalData;
import org.wso2.carbon.throttle.stub.types.ThrottlePolicy;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This class can be used to enable access throttling service and validate the response
 * Refer the JIRA issue raised on concurrent access scenarios on,  https://wso2.org/jira/browse/WSAS-1230
 */
public class ThrottlingTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(ThrottlingTestCase.class);
    private static final int ACCESS_CONTROLLED = 0;
    private static final int ACCESS_DENIED = 1;
    private static final int ACCESS_ALLOWED = 2;
    private static String ipAddress;
    private static InternalData internalData = new InternalData();
    private static InternalData[] internalDataArr = {internalData};

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        messageContextJarUpload();
        String endpoint = asServer.getServiceUrl() + "/MessageContextService";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement response = axisServiceClient.sendReceive(createPayLoadRemoteAddress(),
                endpoint, "remoteAddress");
        // deriving the ip address of client from the response
        ipAddress = response.toString().substring(response.toString().indexOf("<ns:return>"),
                response.toString().indexOf("</ns:return")).replaceAll("<ns:return>", "");
    }

    @AfterClass(alwaysRun = true)
    public void jarDelete() throws Exception {
        deleteService("MessageContextService");
        log.info("MessageContext.jar deleted");
    }

    @Test(groups = "wso2.as", description = "IP Denied for ipAddress",
            expectedExceptions = AxisFault.class)
    public void accessLevelDenyIpLocalHost() throws Exception {
        internalData.setRange(ipAddress); // ip host address
        internalData.setRangeType("IP");   // Range Type - IP/ Domain
        internalData.setAccessLevel(ACCESS_DENIED);  // access level

        assertTrue(enableThrottling(internalDataArr, 1)); // enabling the throttling
        // calling the ecoInt for denied scenario
        callService(createPayLoad(), "ecoInt");  // calling ecoInt operation
    }

    @Test(groups = "wso2.as", description = "IP Denied for other",
            dependsOnMethods = "accessLevelDenyIpLocalHost", expectedExceptions = AxisFault.class)
    public void accessLevelDenyIpOther() throws Exception {
        internalData.setRange("other");      // ip host address
        internalData.setRangeType("IP");     // Range Type - IP/ Domain
        internalData.setAccessLevel(ACCESS_DENIED);  // access level

        assertTrue(enableThrottling(internalDataArr, 1)); // enabling the throttling
        // calling the ecoInt for denied scenario
        callService(createPayLoad(), "ecoInt");
    }

    @Test(groups = "wso2.as", description = "DOMAIN Denied for ipAddress", dependsOnMethods = "accessLevelDenyIpOther",
            expectedExceptions = AxisFault.class)
    public void accessLevelDenyIpRangeDomainLocalHost() throws Exception {
        internalData.setRange(ipAddress);  // ip host address
        internalData.setRangeType("DOMAIN");     // Range Type - IP/ Domain
        internalData.setAccessLevel(ACCESS_DENIED);  // access level

        assertTrue(enableThrottling(internalDataArr, 1)); // enabling the throttling
        // calling the ecoInt for denied scenario
        callService(createPayLoad(), "ecoInt");
    }

    @Test(groups = "wso2.as", description = "IP allowed for ipAddress",
            dependsOnMethods = "accessLevelDenyIpRangeDomainLocalHost", expectedExceptions = AxisFault.class)
    public void accessLevelAllowedIpLocalHost() throws Exception {
        accessLevelDenyIpLocalHost();
        // now check service is allowed
        internalData.setAccessLevel(ACCESS_ALLOWED); // calling the ecoInt for allowed scenario

        assertTrue(enableThrottling(internalDataArr, 1)); // enabling the throttling
        callService(createPayLoad(), "ecoInt"); // calling the ecoInt operation
    }

    @Test(groups = "wso2.as", description = "IP allowed for other",
            dependsOnMethods = "accessLevelAllowedIpLocalHost", expectedExceptions = AxisFault.class)
    public void accessLevelAllowedIpOtherHost() throws Exception {
        accessLevelDenyIpOther();
        // now checking service is allowed
        internalData.setAccessLevel(ACCESS_ALLOWED); // calling the ecoInt for allowed scenario

        assertTrue(enableThrottling(internalDataArr, 1)); // enabling the throttling
        callService(createPayLoad(), "ecoInt"); // calling the ecoInt operation
    }

    @Test(groups = "wso2.as", description = "DOMAIN allowed for ipAddress",
            dependsOnMethods = "accessLevelAllowedIpOtherHost", expectedExceptions = AxisFault.class)
    public void accessLevelAllowedDomainLocalHost() throws Exception {
        accessLevelDenyIpRangeDomainLocalHost();
        // now check service is allowed
        internalData.setAccessLevel(ACCESS_ALLOWED); // calling the ecoInt for allowed scenario

        assertTrue(enableThrottling(internalDataArr, 1)); // enabling the throttling
        callService(createPayLoad(), "ecoInt"); // calling the ecoInt operation
    }

    @Test(groups = "wso2.as", description = "DOMAIN allowed for Other",
            dependsOnMethods = "accessLevelAllowedDomainLocalHost", expectedExceptions = AxisFault.class)
    public void accessLevelAllowedDomainOtherHost() throws Exception {
        internalData.setRange("other");  // ip host address
        internalData.setRangeType("DOMAIN");     // Range Type - IP/ Domain
        internalData.setAccessLevel(ACCESS_DENIED);  // access level

        assertTrue(enableThrottling(internalDataArr, 1)); // enabling the throttling
        // first check service is denied
        callService(createPayLoad(), "ecoInt");

        // now checking service is allowed
        internalData.setAccessLevel(ACCESS_ALLOWED); // calling the ecoInt for allowed scenario
        assertTrue(enableThrottling(internalDataArr, 1)); // enabling the throttling
        callService(createPayLoad(), "ecoInt"); // calling the ecoInt operation
    }

    @Test(groups = "wso2.as", description = "IP controlled for local host request count test",
            dependsOnMethods = "accessLevelAllowedDomainOtherHost", expectedExceptions = AxisFault.class)
    public void accessLevelControlledRequestCountIpLocalHost() throws Exception {
        // make sure to enter sufficient unitTime for input maxRequestCount
        int maxConcurrent = 5;
        int maxRequestCount = 1;
        int unitTime = 50000;

        internalData.setRange(ipAddress);  // ip host address
        internalData.setRangeType("IP");    //Type of Range. IP/DOMAIN
        internalData.setMaxRequestCount(maxRequestCount);  // maximum requests served under unit time parameter
        internalData.setUnitTime(unitTime);            // time period maximum requests served in
        internalData.setProhibitTimePeriod(10000);  // requests not allowed time period.
        internalData.setAccessLevel(ACCESS_CONTROLLED);  // access level

        assertTrue(enableThrottling(internalDataArr, maxConcurrent));

        for (int x = 0; x <= maxRequestCount; x++) {
            if (x != maxRequestCount)
                callService(createPayLoad(), "echoInt");  // these requests should receive valid responses
            else {
                // these requests should receive axisFaults
                callService(createPayLoad(), "echoInt");
            }
        }
    }

    @Test(groups = "wso2.as", description = "IP controlled for other request count test",
            dependsOnMethods = "accessLevelControlledRequestCountIpLocalHost", expectedExceptions = AxisFault.class)
    public void accessLevelControlledRequestCountIpOtherHost() throws Exception {
        // make sure to enter sufficient unitTime for input maxRequestCount
        int maxConcurrent = 5;
        int maxRequestCount = 1;
        int unitTime = 50000;

        internalData.setRange("other");  // ip host address
        internalData.setRangeType("IP");        //Type of Range. IP/DOMAIN
        internalData.setMaxRequestCount(maxRequestCount);     // maximum requests served under unit time parameter
        internalData.setUnitTime(unitTime);            // time period maximum requests served in
        internalData.setProhibitTimePeriod(10000);  // requests not allowed time period.
        internalData.setAccessLevel(ACCESS_CONTROLLED);  // access level

        assertTrue(enableThrottling(internalDataArr, maxConcurrent));

        for (int x = 0; x <= maxRequestCount; x++) {
            if (x != maxRequestCount)
                callService(createPayLoad(), "echoInt");  // these requests should receive valid responses
            else {
                // these requests should receive axisFaults
                callService(createPayLoad(), "echoInt");

            }
        }
    }

    @Test(groups = "wso2.as", description = "DOMAIN controlled for ipAddress request count test",
            dependsOnMethods = "accessLevelControlledRequestCountIpOtherHost", expectedExceptions = AxisFault.class)
    public void accessLevelControlledRequestCountDomainLocalHost() throws Exception {
        // make sure to enter sufficient unitTime for input maxRequestCount
        int maxConcurrent = 5;
        int maxRequestCount = 1;
        int unitTime = 50000;

        internalData.setRange(ipAddress);  // ip host address
        internalData.setRangeType("DOMAIN");        //Type of Range. IP/DOMAIN
        internalData.setMaxRequestCount(maxRequestCount); // maximum requests served under unit time parameter
        internalData.setUnitTime(unitTime);            // time period maximum requests served in
        internalData.setProhibitTimePeriod(10000);  // requests not allowed time period.
        internalData.setAccessLevel(ACCESS_CONTROLLED);  // access level

        assertTrue(enableThrottling(internalDataArr, maxConcurrent));

        for (int x = 0; x <= maxRequestCount; x++) {
            if (x != maxRequestCount)
                callService(createPayLoad(), "echoInt");  // these requests should receive valid responses
            else {
                // these requests should receive axisFaults
                callService(createPayLoad(), "echoInt");
            }
        }
    }

    @Test(groups = "wso2.as", description = "IP controlled for ipAddress prohibit time test",
            dependsOnMethods = "accessLevelControlledRequestCountDomainLocalHost", expectedExceptions = AxisFault.class)
    public void accessLevelControlledProhibitTimeIpLocalHost() throws Exception {   // ip range is set to ipAddress test scenario
        // make sure to enter sufficient unitTime for input maxRequestCount and maxConcurrent
        int maxConcurrent = 5;
        int maxRequestCount = 1;
        int unitTime = 50000;
        int prohibitTime = 20000;

        internalData.setRange(ipAddress);  // ip host address
        internalData.setRangeType("IP");        //Type of Range. IP/DOMAIN
        internalData.setMaxRequestCount(maxRequestCount);     // maximum requests served under unit time parameter
        internalData.setUnitTime(unitTime);            // time period maximum requests served in
        internalData.setProhibitTimePeriod(prohibitTime);  // requests not allowed time period.
        internalData.setAccessLevel(ACCESS_CONTROLLED);  // access level

        assertTrue(enableThrottling(internalDataArr, maxConcurrent));

        for (int x = 0; x <= maxRequestCount; x++) {
            if (x != maxRequestCount) {
                callService(createPayLoad(), "echoInt");
            } else {
                //checking the negative scenario in order to verify the prohibit time test is successful
                callService(createPayLoad(), "echoInt");
            }
        }
        Thread.sleep(prohibitTime);   // making sure prohibit time is exceeded
        callService(createPayLoad(), "echoInt");  // after prohibit time request
    }

    @Test(groups = "wso2.as", description = "IP controlled for other prohibit time test",
            dependsOnMethods = "accessLevelControlledProhibitTimeIpLocalHost", expectedExceptions = AxisFault.class)
    public void accessLevelControlledProhibitTimeIpOther() throws Exception {
        // make sure to enter sufficient unitTime for input maxRequestCount and maxConcurrent
        int maxConcurrent = 5;
        int maxRequestCount = 1;
        int unitTime = 50000;
        int prohibitTime = 20000;

        internalData.setRange("other");  // ip host address
        internalData.setRangeType("IP");        //Type of Range. IP/DOMAIN
        internalData.setMaxRequestCount(maxRequestCount);     // maximum requests served under unit time parameter
        internalData.setUnitTime(unitTime);            // time period maximum requests served in
        internalData.setProhibitTimePeriod(prohibitTime);  // requests not allowed time period.
        internalData.setAccessLevel(ACCESS_CONTROLLED);  // access level

        assertTrue(enableThrottling(internalDataArr, maxConcurrent));

        for (int x = 0; x <= maxRequestCount; x++) {
            if (x != maxRequestCount) {
                callService(createPayLoad(), "echoInt");
            } else {
                //checking the negative scenario in order to verify the prohibit time test is successful
                callService(createPayLoad(), "echoInt");
            }
        }
        Thread.sleep(prohibitTime);   // making sure prohibit time is exceeded
        callService(createPayLoad(), "echoInt");  // after prohibit time request
    }

    @Test(groups = "wso2.as", description = "DOMAIN controlled for ipAddress prohibit time test",
            dependsOnMethods = "accessLevelControlledProhibitTimeIpOther", expectedExceptions = AxisFault.class)
    public void accessLevelControlledProhibitTimeDomainLocalHost() throws Exception {
        // make sure to enter sufficient unitTime for input maxRequestCount and maxConcurrent
        int maxConcurrent = 5;
        int maxRequestCount = 1;
        int unitTime = 50000;
        int prohibitTime = 20000;

        internalData.setRange(ipAddress);  // ip host address
        internalData.setRangeType("DOMAIN");        //Type of Range. IP/DOMAIN
        internalData.setMaxRequestCount(maxRequestCount); // maximum requests served under unit time parameter
        internalData.setUnitTime(unitTime);            // time period maximum requests served in
        internalData.setProhibitTimePeriod(prohibitTime);  // requests not allowed time period.
        internalData.setAccessLevel(ACCESS_CONTROLLED);  // access level

        assertTrue(enableThrottling(internalDataArr, maxConcurrent));

        for (int x = 0; x <= maxRequestCount; x++) {
            if (x != maxRequestCount) {
                callService(createPayLoad(), "echoInt");
            } else {
                //checking the negative scenario in order to verify the prohibit time test is successful
                callService(createPayLoad(), "echoInt");
            }
        }
        Thread.sleep(prohibitTime);   // making sure prohibit time is exceeded
        callService(createPayLoad(), "echoInt");  // after prohibit time request
    }

    //known issue WSAS-1230
    @Test(groups = "wso2.as", description = "IP controlled for ipAddress concurrent test",
            dependsOnMethods = "accessLevelControlledProhibitTimeDomainLocalHost", enabled = false)
    public void accessLevelControlledConcurrentLocalHost() throws Exception {
        // always maxConcurrent value should be lesser than no of concurrent threads (threadGroup *  threadLoop)
        // while maxRequestCount should be a larger value than no of concurrent threads
        int maxConcurrent = 5;
        int threadGroup = 20;
        int threadLoop = 1;
        int maxRequestCount = 10;

        internalData.setRange(ipAddress);  // ip host address
        internalData.setRangeType("IP");        //Type of Range. IP/DOMAIN
        internalData.setMaxRequestCount(maxRequestCount); // maximum requests served under unit time parameter
        internalData.setUnitTime(50000);        // time period maximum requests served in
        internalData.setProhibitTimePeriod(1000);  // requests not allowed time period.
        internalData.setAccessLevel(ACCESS_CONTROLLED);  // access level

        assertTrue(enableThrottling(internalDataArr, maxConcurrent));

        ConcurrencyTest concurrencyTest = new ConcurrencyTest(threadGroup, threadLoop);
        concurrencyTest.run(asServer.getServiceUrl() + "/echo", createPayLoad(), "echoInt"); // sending concurrent requests
        Queue<OMElement> messageQueue = concurrencyTest.getMessages(); //TODO WSAS-1230
        log.info(messageQueue);
    }

    //known issue WSAS-1230
    @Test(groups = "wso2.as", description = "IP controlled for other concurrent test",
            dependsOnMethods = "accessLevelControlledConcurrentLocalHost", enabled = false)
    public void accessLevelControlledConcurrentOther() throws Exception {
        // always maxConcurrent value should be lesser than no of concurrent threads (threadGroup *  threadLoop)
        // while maxRequestCount should be a larger value than no of concurrent threads
        int maxConcurrent = 10;
        int threadGroup = 20;
        int threadLoop = 1;
        int maxRequestCount = 100;

        internalData.setRange("other");  // ip host address
        internalData.setRangeType("IP");        //Type of Range. IP/DOMAIN
        internalData.setMaxRequestCount(maxRequestCount); // maximum requests served under unit time parameter
        internalData.setUnitTime(50000);        // time period maximum requests served in
        internalData.setProhibitTimePeriod(1000);  // requests not allowed time period.
        internalData.setAccessLevel(ACCESS_CONTROLLED);  // access level

        assertTrue(enableThrottling(internalDataArr, maxConcurrent));

        ConcurrencyTest concurrencyTest = new ConcurrencyTest(threadGroup, threadLoop);
        concurrencyTest.run(asServer.getServiceUrl() + "/echo", createPayLoad(), "echoInt"); // sending concurrent requests
        Queue<OMElement> messageQueue = concurrencyTest.getMessages();
        log.info(messageQueue.toString());//TODO WSAS-1230
    }

    private void messageContextJarUpload() throws Exception {  // upload and verify jar file
        JARServiceUploaderClient jarServiceUploaderClient =
                new JARServiceUploaderClient(asServer.getBackEndUrl(),
                        asServer.getSessionCookie());
        List<DataHandler> jarList = new ArrayList<DataHandler>();
        URL url = new URL("file://" + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
                "artifacts" + File.separator + "AS" + File.separator + "jar" + File.separator +
                "artifact4" + File.separator + "MessageContext.jar");
        DataHandler dh = new DataHandler(url);
        jarList.add(dh);

        jarServiceUploaderClient.uploadJARServiceFile("", jarList, dh);
        boolean deployedStatus = isServiceDeployed("MessageContextService");
        assertTrue(deployedStatus, "MessageContext.jar upload failed");
        log.info("MessageContext.jar uploaded and deployed successfully");
    }


    private boolean enableThrottling(InternalData[] internalDataArr, int maxConcurrent) throws Exception {
        boolean success = false; // Enabling throttling operation throttlingStatus
        ThrottlingClient throttlingClient = new ThrottlingClient(asServer.getBackEndUrl(),
                asServer.getSessionCookie());
        ThrottlePolicy throttlePolicy = new ThrottlePolicy();
        throttlePolicy.setInternalConfigs(internalDataArr);
        throttlePolicy.setMaxConcurrentAccesses(maxConcurrent); // number of requests served at any given time

        try {
            throttlingClient.enableThrottling("echo", throttlePolicy); // for echo operation
            log.info("Throttling enabled successfully");
            success = true;
        } catch (Exception e) {
            log.error("Error ThrottlingTestCase : enableThrottling " + e);
        }
        return success;
    }

    private void callService(OMElement payload, String operation) throws AxisFault {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        String endpoint = asServer.getServiceUrl() + "/echo";
        OMElement response = axisServiceClient.sendReceive(payload, endpoint, operation);
        log.info("Response : " + response);
        assertEquals(response.toString(), "<ns:echoIntResponse xmlns:ns=" +
                "\"http://echo.services.core.carbon.wso2.org\"><return>100</return>" +
                "</ns:echoIntResponse>");
    }

    private static OMElement createPayLoad() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://echo.services.core.carbon.wso2.org", "ns");
        OMElement getOme = fac.createOMElement("echoInt", omNs);
        OMElement getOmeTwo = fac.createOMElement("in", omNs);
        getOmeTwo.setText("100"); // input value
        getOme.addChild(getOmeTwo);
        return getOme;
    }

    private static OMElement createPayLoadRemoteAddress() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.apache.org/axis2", "ns");
        OMElement getOme;
        getOme = fac.createOMElement("remoteAddress", omNs);
        return getOme;
    }
}
