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
import org.wso2.appserver.webapp.security.sso.util.SSOConstants;
import org.wso2.appserver.webapp.security.sso.util.SSOException;
import org.wso2.appserver.webapp.security.sso.util.SSOUtils;
import org.wso2.appserver.webapp.security.sso.util.XMLEntityResolver;

import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A test class for utility methods of the SSOUtils.java.
 *
 * @since 6.0.0
 */
public class SSOUtilsTest {
    private static final Logger logger = Logger.getLogger(SSOUtilsTest.class.getName());
    private static final Path system_default_temp_directory = Paths.get(System.getProperty("java.io.tmpdir"));
    private static final Path test_catalina_base_path = Paths.
            get(system_default_temp_directory.toString(), "catalina_base");
    private static final Path tempPropertyFile = prepareTempPropertiesFile();

    /**
     * Tests for getCatalinaBase function.
     */

    @Test
    public void getCatalinaBaseTest() {
        Path catalinaBasePath = Paths.get(System.getProperty("java.io.tmpdir"), "catalina_base");
        System.setProperty(SSOConstants.SAMLSSOValveConstants.CATALINA_BASE, test_catalina_base_path.toString());
        Path catalinaBasePathFromMethod = null;
        try {
            catalinaBasePathFromMethod = SSOUtils.getCatalinaBase();
        } catch (SSOException e) {
            logger.log(Level.SEVERE, "Error occurred when retrieving catalina.base", e);
            assert false;
        }
        Optional.ofNullable(catalinaBasePathFromMethod).
                ifPresent(path -> Assert.assertTrue(catalinaBasePath.toString().equals(path.toString())));
    }

    @Test(expectedExceptions = SSOException.class)
    public void getCatalinaBaseEnvRemovedTest() throws SSOException {
        System.clearProperty(SSOConstants.SAMLSSOValveConstants.CATALINA_BASE);
        SSOUtils.getCatalinaBase();
    }

    /**
     * Tests for getCatalinaConfigurationHome function.
     */

    @Test
    public void getCatalinaBaseConfigurationHomeTest() {
        Path catalinaBasePath = test_catalina_base_path;
        Path catalinaBaseConfigurationHomePath = Paths.get(test_catalina_base_path.toString(),
                SSOConstants.SAMLSSOValveConstants.TOMCAT_CONFIGURATION_FOLDER_NAME);
        System.setProperty(SSOConstants.SAMLSSOValveConstants.CATALINA_BASE, catalinaBasePath.toString());
        Path catalinaBaseConfigHomePathFromMethod = null;
        try {
            catalinaBaseConfigHomePathFromMethod = SSOUtils.getCatalinaConfigurationHome();
        } catch (SSOException e) {
            logger.log(Level.SEVERE, "Error occurred when retrieving catalina.base configuration home", e);
            assert false;
        }
        Optional.ofNullable(catalinaBaseConfigHomePathFromMethod).
                ifPresent(path -> Assert.
                        assertTrue(catalinaBaseConfigurationHomePath.toString().equals(path.toString())));
    }

    @Test(expectedExceptions = SSOException.class)
    public void getCatalinaBaseConfigurationHomeEnvRemovedTest() throws SSOException {
        System.clearProperty(SSOConstants.SAMLSSOValveConstants.CATALINA_BASE);
        SSOUtils.getCatalinaConfigurationHome();
    }

    @Test
    public void uniqueIDTest() {
        String firstID = SSOUtils.createID();
        String secondID = SSOUtils.createID();
        String thirdID = SSOUtils.createID();
        boolean notEqual = ((!firstID.equals(secondID)) && (!firstID.equals(thirdID)) && (!secondID.equals(thirdID)));
        Assert.assertTrue(notEqual);
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
        Collection<String> nonEmptyCollection = new ArrayList<>();
        nonEmptyCollection.add("valve");
        nonEmptyCollection.add("filter");
        Assert.assertFalse(SSOUtils.isCollectionEmpty(nonEmptyCollection));
    }

    /**
     * Tests for loadPropertiesFromFile function
     */

    @Test
    public void loadPropertiesFromFileTest() {
        Properties actual = new Properties();
        try {
            SSOUtils.loadPropertiesFromFile(actual, tempPropertyFile);
        } catch (SSOException e) {
            logger.log(Level.SEVERE, "Error when loading properties", e);
        }
        Assert.assertTrue(equalPropertyTables(loadTestProperties(), actual));
    }

    @Test(expectedExceptions = SSOException.class)
    public void loadPropertiesFromFileInvalidPropertiesArgTest() throws SSOException {
        SSOUtils.loadPropertiesFromFile(null, tempPropertyFile);
    }

    @Test(expectedExceptions = SSOException.class)
    public void loadPropertiesFromFileInvalidFilePathArgTest() throws SSOException {
        SSOUtils.loadPropertiesFromFile(new Properties(), null);
    }

    @Test(expectedExceptions = SSOException.class)
    public void loadPropertiesFromFileInvalidArgsTest() throws SSOException {
        SSOUtils.loadPropertiesFromFile(null, null);
    }

    @Test(expectedExceptions = SSOException.class)
    public void loadPropertiesFromNonExistentFileTest() throws SSOException {
        Path nonExistentFilePath = Paths.get(system_default_temp_directory.toString(), "file.properties");
        SSOUtils.loadPropertiesFromFile(new Properties(), nonExistentFilePath);
    }

    /**
     * Tests for getDocumentBuilder function.
     */

    @Test
    public void getDefaultDocumentBuilderTest() {
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = SSOUtils.getDocumentBuilder(false, true, Optional.of(new XMLEntityResolver()));
        } catch (SSOException e) {
            logger.log(Level.SEVERE, "Error occurred when retrieving catalina.base configuration home", e);
            assert false;
        }
        boolean desiredDocBuilder = documentBuilder.isNamespaceAware();
        Assert.assertTrue(desiredDocBuilder);
    }

    @Test
    public void getNonDefaultDocumentBuilderTest() {
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = SSOUtils.getDocumentBuilder(true, false, Optional.of(new XMLEntityResolver()));
        } catch (SSOException e) {
            logger.log(Level.SEVERE, "Error occurred when retrieving catalina.base configuration home", e);
            assert false;
        }
        boolean desiredDocBuilder = documentBuilder.isNamespaceAware();
        Assert.assertFalse(desiredDocBuilder);
    }

    private static Path prepareTempPropertiesFile() {
        Path tempFile = null;
        try {
            tempFile = prepareTempFile("test", "properties");
            loadTestProperties().store(Files.newOutputStream(tempFile), "test-property-table");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred when preparing temporary test.properties file", e);
        }
        return tempFile;
    }

    private static Path prepareTempFile(String prefix, String suffix) throws IOException {
        Path tempFile = Files.createTempFile(system_default_temp_directory, prefix, suffix);
        tempFile.toFile().deleteOnExit();
        return tempFile;
    }

    private static Properties loadTestProperties() {
        Properties properties = new Properties();
        properties.setProperty("key1", "value1");
        properties.setProperty("key2", "value2");
        properties.setProperty("key3", "value3");
        return properties;
    }

    private static boolean equalPropertyTables(Properties expected, Properties actual) {
        return (Optional.ofNullable(expected).isPresent()) && (Optional.ofNullable(actual).isPresent()) && expected.
                equals(actual);
    }
}
