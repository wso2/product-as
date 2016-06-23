/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.appserver.webapp.security.utils;

import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.configuration.context.WebAppSingleSignOn;
import org.wso2.appserver.webapp.security.Constants;
import org.wso2.appserver.webapp.security.TestConstants;
import org.wso2.appserver.webapp.security.utils.exception.SSOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class defines unit test cases for single-sign-on (SSO) utility functions.
 *
 * @since 6.0.0
 */
public class SSOUtilsTest {
    @BeforeClass
    public void init() {
        System.clearProperty(org.wso2.appserver.Constants.JAVA_KEYSTORE_LOCATION);
        System.clearProperty(org.wso2.appserver.Constants.JAVA_KEYSTORE_PASSWORD);
        System.clearProperty(org.wso2.appserver.Constants.JAVA_KEYSTORE_TYPE);
    }

    @Test(description = "Tests the uniqueness of the id generated")
    public void testUniqueIDCreation() {
        List<String> ids = new ArrayList<>();
        IntStream
                .range(0, 10)
                .forEach(index -> ids.add(SSOUtils.createID()));

        Set<String> uniqueIds = new HashSet<>();
        ids
                .stream()
                .forEach(uniqueIds::add);

        Assert.assertTrue(ids.size() == uniqueIds.size());
    }

    @Test(description = "Tests the construction of Application Server URL for a sample request")
    public void testConstructionOfApplicationServerURL() {
        Request request = new Request();

        Connector connector = new Connector();
        connector.setProtocol(TestConstants.SSL_PROTOCOL);
        connector.setPort(TestConstants.SSL_PORT);
        connector.setScheme(TestConstants.SSL_PROTOCOL);

        Engine engine = new StandardEngine();
        Service service = new StandardService();
        engine.setService(service);
        engine.getService().addConnector(connector);

        Host host = new StandardHost();
        host.setName(TestConstants.DEFAULT_TOMCAT_HOST);
        request.getMappingData().host = host;
        host.setParent(engine);

        Optional<String> actual = SSOUtils.constructApplicationServerURL(request);
        if (actual.isPresent()) {
            Assert.assertEquals(actual.get(), TestConstants.DEFAULT_APPLICATION_SERVER_URL);
        } else {
            Assert.fail();
        }
    }

    @Test(description = "Tests the construction of Application Server URL for an invalid 'null' request")
    public void testConstructionOfApplicationServerURLFromInvalidRequest() {
        Optional<String> actual = SSOUtils.constructApplicationServerURL(null);
        Assert.assertTrue(!actual.isPresent());
    }

    @Test(description = "Tests the construction of Application Server URL for no SSL/TLS Connector")
    public void testConstructionOfApplicationServerURLWithNoConnector() {
        Request request = new Request();

        Engine engine = new StandardEngine();
        Service service = new StandardService();
        engine.setService(service);

        Host host = new StandardHost();
        host.setName(TestConstants.DEFAULT_TOMCAT_HOST);
        request.getMappingData().host = host;
        host.setParent(engine);

        Optional<String> actual = SSOUtils.constructApplicationServerURL(request);
        Assert.assertTrue(!actual.isPresent());
    }

    @Test(description = "Tests the validity of the split query parameter string")
    public void testQueryParamStringSplit() {
        Map<String, String[]> expected = getQueryParams();

        String testQueryString = "key1=key1val1&key1=key1val2&key2=key2val1&key2=key2val2&key3=key3val1";
        Map<String, String[]> actual = SSOUtils.getSplitQueryParameters(testQueryString);

        Assert.assertTrue(equalMaps(expected, actual));
    }

    @Test(description = "Tests the generation of relay state")
    @SuppressWarnings("unchecked")
    public void testGeneratingRelayState() {
        //  mocks a Request object
        Request request = mock(Request.class);
        when(request.getRequestURI()).thenReturn(TestConstants.CONTEXT_PATH);
        when(request.getQueryString()).thenReturn(TestConstants.DEFAULT_QUERY_PARAMS);
        when(request.getParameterMap()).thenReturn(getQueryParams());

        Map<String, Object> additionalParams = new HashMap<>();
        additionalParams.put("Company", "WSO2");
        when(request.getAttribute(Constants.RELAY_STATE)).thenReturn(additionalParams);

        Map<String, Object> actual = SSOUtils.generateRelayState(request);

        boolean uriMatches = request.getRequestURI().equals(actual.get(Constants.REQUEST_URL));
        boolean queryStringMatches = request.getQueryString().equals(actual.get(Constants.REQUEST_QUERY_STRING));
        boolean paramMapMatches = equalMaps(getQueryParams(),
                (Map<String, String[]>) actual.get(Constants.REQUEST_PARAMETERS));
        boolean additionalParamMatches = actual.get("Company").equals("WSO2");

        Assert.assertTrue(uriMatches && queryStringMatches && paramMapMatches && additionalParamMatches);
    }

    @Test(description = "Tests the validity of the issuer ID generated from a valid context path")
    public void testGeneratingIssuerID() {
        String contextPath = "/" + TestConstants.WEB_APP_BASE + TestConstants.CONTEXT_PATH;

        Optional<String> actualIssuerID = SSOUtils.generateIssuerID(contextPath, TestConstants.WEB_APP_BASE);
        Assert.assertTrue(
                (actualIssuerID.isPresent()) && (actualIssuerID.get().equals(TestConstants.CONTEXT_PATH.substring(1))));
    }

    @Test(description = "Tests the validity of the issuer ID generated from an invalid context path")
    public void testGeneratingIssuerIDFromInvalidContextPath() {
        Optional<String> actualIssuerID = SSOUtils.generateIssuerID(null, TestConstants.WEB_APP_BASE);
        Assert.assertTrue(!actualIssuerID.isPresent());
    }

    @Test(description = "Tests the validity of the consumer URL generated from a valid context path")
    public void testGeneratingConsumerURL() {
        String expected = TestConstants.DEFAULT_APPLICATION_SERVER_URL + TestConstants.CONTEXT_PATH + "/" +
                TestConstants.DEFAULT_CONSUMER_URL_POSTFIX;
        Optional<String> actual = SSOUtils.generateConsumerURL(TestConstants.CONTEXT_PATH, TestConstants.
                DEFAULT_APPLICATION_SERVER_URL, Constants.DEFAULT_CONSUMER_URL_POSTFIX);

        Assert.assertTrue((actual.isPresent()) && (actual.get().equals(expected)));
    }

    @Test(description = "Tests the validity of the consumer URL generated from an invalid context path")
    public void testGeneratingConsumerURLFromInvalidContextPath() {
        WebAppSingleSignOn ssoConfiguration = new WebAppSingleSignOn();
        ssoConfiguration.setConsumerURLPostfix(Constants.DEFAULT_CONSUMER_URL_POSTFIX);

        Optional<String> actual = SSOUtils.generateConsumerURL(null, TestConstants.
                DEFAULT_APPLICATION_SERVER_URL, Constants.DEFAULT_CONSUMER_URL_POSTFIX);

        Assert.assertTrue(!actual.isPresent());
    }

    @Test(description = "Tests the generation of keystore from invalid set of configurations")
    public void testGeneratingKeyStoreFromInvalidConfigurations() throws SSOException {
        Optional keystore = SSOUtils.generateKeyStore();
        Assert.assertTrue(!keystore.isPresent());
    }

    @Test(description = "Tests the generation of keystore from invalid file path",
            expectedExceptions = {SSOException.class}, priority = 1)
    public void testGeneratingKeyStoreFromInvalidPath() throws SSOException {
        System.setProperty(org.wso2.appserver.Constants.JAVA_KEYSTORE_LOCATION,
                TestConstants.INVALID_KEYSTORE_LOCATION);
        SSOUtils.generateKeyStore();
    }

    @Test(description = "Tests the generation of keystore from invalid password",
            expectedExceptions = {SSOException.class}, priority = 1)
    public void testGeneratingKeyStoreFromInvalidPassword() throws SSOException {
        System.setProperty(org.wso2.appserver.Constants.JAVA_KEYSTORE_LOCATION, TestConstants
                .DEFAULT_KEY_STORE_LOCATION);
        System.setProperty(org.wso2.appserver.Constants.JAVA_KEYSTORE_PASSWORD, TestConstants
                .INVALID_KEYSTORE_PASSWORD);
        System.setProperty(org.wso2.appserver.Constants.JAVA_KEYSTORE_TYPE, TestConstants.DEFAULT_KEY_STORE_TYPE);

        Optional keystore = SSOUtils.generateKeyStore();
        Assert.assertTrue(!keystore.isPresent());
    }

    @Test(description = "Tests the generation of keystore by providing a non-matching keystore type name",
            expectedExceptions = {SSOException.class}, priority = 2)
    public void testGeneratingKeyStoreFromInvalidStoreType() throws SSOException {
        System.setProperty(org.wso2.appserver.Constants.JAVA_KEYSTORE_LOCATION, TestConstants
                .DEFAULT_KEY_STORE_LOCATION);
        System.setProperty(org.wso2.appserver.Constants.JAVA_KEYSTORE_PASSWORD, TestConstants
                .DEFAULT_KEY_STORE_PASSWORD);
        System.setProperty(org.wso2.appserver.Constants.JAVA_KEYSTORE_TYPE, TestConstants.INVALID_KEYSTORE_TYPE);

        SSOUtils.generateKeyStore();
    }

    private static Map<String, String[]> getQueryParams() {
        Map<String, String[]> queryParams = new HashMap<>();

        queryParams.put("key1", new String[]{"key1val1", "key1val2"});
        queryParams.put("key2", new String[]{"key2val1", "key2val2"});
        queryParams.put("key3", new String[]{"key3val1"});

        return queryParams;
    }

    private static boolean equalMaps(Map<String, String[]> expected, Map<String, String[]> actual) {
        for (Map.Entry<String, String[]> expectedEntry : expected.entrySet()) {
            String[] expectedArray = expectedEntry.getValue();
            String[] actualArray = actual.get(expectedEntry.getKey());
            if (actualArray == null) {
                return false;
            } else {
                boolean result = equalStringArrays(expectedArray, actualArray);
                if (!result) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean equalStringArrays(String[] expected, String[] actual) {
        return Arrays.asList(expected)
                .stream()
                .filter(expectedValue -> Arrays.asList(actual)
                        .stream()
                        .filter(expectedValue::equals)
                        .count() > 0)
                .count() == expected.length;
    }
}
