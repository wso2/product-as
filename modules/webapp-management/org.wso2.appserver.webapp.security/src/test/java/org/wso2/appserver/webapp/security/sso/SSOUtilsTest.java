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
package org.wso2.appserver.webapp.security.sso;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.appserver.configuration.context.ContextConfiguration;
import org.wso2.appserver.configuration.context.SSOConfiguration;
import org.wso2.appserver.webapp.security.sso.utils.SSOUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Contains test cases for SSOUtils.java class.
 *
 * @since 6.0.0
 */
public class SSOUtilsTest {
    @Test
    public void createIDTest() {
        Set<String> ids = new HashSet<>();
        int numberOfCycles = 10;
        IntStream.range(0, numberOfCycles).forEach(count -> ids.add(SSOUtils.createID()));
        Assert.assertTrue(ids.size() == numberOfCycles);
    }

    /**
     * Tests for isBlank function.
     */
    @Test
    public void nonEmptyStringWithNoBlanksForBlankStringTest() {
        Assert.assertFalse(SSOUtils.isBlank("appserver6.0.0"));
    }

    @Test
    public void nonEmptyStringWithBlanksForBlankStringTest() {
        Assert.assertFalse(SSOUtils.isBlank("app server 6.0.0"));
    }

    @Test
    public void nonEmptyStringWithOnlyBlanksForBlankStringTest() {
        Assert.assertTrue(SSOUtils.isBlank("       "));
    }

    @Test
    public void emptyStringForBlankStringTest() {
        Assert.assertTrue(SSOUtils.isBlank(""));
    }

    @Test
    public void nullReferenceForBlankStringTest() {
        Assert.assertTrue(SSOUtils.isBlank(null));
    }

    /**
     * Tests for isCollectionEmpty function.
     */
    @Test
    public void nullReferenceForEmptyCollectionTest() {
        Assert.assertTrue(SSOUtils.isCollectionEmpty(null));
    }

    @Test
    public void emptyCollectionForEmptyCollectionTest() {
        Assert.assertTrue(SSOUtils.isCollectionEmpty(new ArrayList<>()));
    }

    @Test
    public void nonEmptyCollectionForEmptyCollectionTest() {
        Collection<Integer> nonEmptyCollection = new ArrayList<>();
        int numberOfCycles = 10;
        IntStream.range(0, numberOfCycles).forEach(nonEmptyCollection::add);
        Assert.assertFalse(SSOUtils.isCollectionEmpty(nonEmptyCollection));
    }

    /**
     * Tests for generateIssuerID function.
     */
    @Test
    public void generateIssuerIDTest() {
        String contextPath = File.separator + "webapps" + File.separator + "sample";
        Optional<String> issuerID = SSOUtils.generateIssuerID(contextPath);
        Assert.assertTrue(issuerID.get().equals("sample"));
    }

    @Test
    public void generateEmptyIssuerIDTest() {
        Optional<String> issuerID = SSOUtils.generateIssuerID(null);
        Assert.assertTrue(issuerID.equals(Optional.<String>empty()));
    }

    /**
     * Tests for generateConsumerURL function.
     */
    @Test
    public void generateConsumerURLTest() {
        ContextConfiguration contextConfiguration = new ContextConfiguration();
        contextConfiguration.setSingleSignOnConfiguration(new SSOConfiguration());
        SSOUtils.setDefaultConfigurations(contextConfiguration);
        String contextPath = File.separator + "sample";
        Optional<String> consumerURL = SSOUtils.
                generateConsumerURL(contextPath, contextConfiguration.getSingleSignOnConfiguration());
        Assert.assertTrue(consumerURL.get().equals(Constants.APPLICATION_SERVER_URL_DEFAULT + contextPath +
                Constants.CONSUMER_URL_POSTFIX_DEFAULT));
    }

    @Test
    public void generateEmptyNuConsumerURLTest() {
        Optional<String> consumerURL = SSOUtils.generateConsumerURL(null, null);
        Assert.assertTrue(consumerURL.equals(Optional.<String>empty()));
    }
}
