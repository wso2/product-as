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
package org.wso2.appserver;

import org.testng.annotations.Test;
import org.wso2.appserver.configuration.listeners.Utils;
import org.wso2.appserver.configuration.server.AppServerConfiguration;
import org.wso2.appserver.exceptions.ApplicationServerConfigurationException;
import org.wso2.appserver.exceptions.ApplicationServerException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class defines unit-tests for XML utilities.
 *
 * @since 6.0.0
 */
public class UtilsTest {
    @Test(description = "Attempts to load the XML file content with a non-existent XML schema file for validation",
            expectedExceptions = { ApplicationServerConfigurationException.class })
    public void testLoadingObjectFromNonExistentSchemaAsPath()
            throws IOException, ApplicationServerConfigurationException {
        Path xmlSource = Paths.get(TestConstants.TEST_RESOURCES, Constants.APP_SERVER_DESCRIPTOR);
        Path xmlSchema = Paths.get(TestConstants.TEST_RESOURCES, TestConstants.NON_EXISTENT_SCHEMA);
        Utils.getUnmarshalledObject(xmlSource, xmlSchema, AppServerConfiguration.class);
    }

    @Test(description = "Uses an invalid XML schema file for validation",
            expectedExceptions = { ApplicationServerConfigurationException.class })
    public void testLoadingObjectWithInvalidSchema() throws IOException, ApplicationServerException {
        Path xmlSchema = Paths.get(TestConstants.TEST_RESOURCES, TestConstants.INVALID_SCHEMA_FILE);
        Utils.getXMLUnmarshaller(xmlSchema, AppServerConfiguration.class);
    }

    @Test(description = "Attempts to load content from a file source with invalid XML syntax",
            expectedExceptions = { ApplicationServerConfigurationException.class })
    public void testLoadingObjectFromInvalidFile() throws IOException, ApplicationServerException {
        Path xmlSource = Paths.get(TestConstants.TEST_RESOURCES, TestConstants.INVALID_DESCRIPTOR);
        Path xmlSchema = Paths.get(TestConstants.TEST_RESOURCES, Constants.APP_SERVER_DESCRIPTOR_SCHEMA);
        Utils.getUnmarshalledObject(xmlSource, xmlSchema, AppServerConfiguration.class);
    }
}
