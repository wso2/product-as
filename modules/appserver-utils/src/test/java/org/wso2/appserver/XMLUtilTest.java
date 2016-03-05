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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * This class defines unit-tests for XML utilities.
 *
 * @since 6.0.0
 */
public class XMLUtilTest {
    @Test
    public void loadObjectTest() {
        // TODO: ADD TESTS
        Path xmlSource = Paths.get(loadFile(TestConstants.SAMPLE_WSO2_SERVER_DESCRIPTOR).get());
        Assert.assertTrue(Files.exists(xmlSource));
    }

    private Optional<String> loadFile(String file) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(file);
        if (url == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(url.getPath());
        }
    }
}
