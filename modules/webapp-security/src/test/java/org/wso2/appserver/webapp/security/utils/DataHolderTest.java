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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This class defines unit tests for DataHolder Java class.
 *
 * @since 6.0.0
 */
public class DataHolderTest {
    @Test(description = "Tests whether the data holder instance is singleton")
    public void testSingletonInstance() {
        DataHolder.getInstance().setObject("An object");

        DataHolder refOne = DataHolder.getInstance();
        DataHolder refTwo = DataHolder.getInstance();

        DataHolder.getInstance().setObject("Another object");

        DataHolder refThree = DataHolder.getInstance();

        Assert.assertTrue((refOne == refTwo) && (refThree == refOne));
    }
}
