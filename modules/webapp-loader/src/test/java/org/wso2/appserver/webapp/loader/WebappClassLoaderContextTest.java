/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.appserver.webapp.loader;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardServer;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.appserver.configuration.listeners.ContextConfigurationLoader;
import org.wso2.appserver.configuration.listeners.ServerConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Unit test cases for WebappClassLoaderContext.
 */
public class WebappClassLoaderContextTest {

    WebappClassLoaderContext classLoaderContext;

    @BeforeTest
    public void prepareConfigurations() {

        ServerConfigurationLoader serverConfigurationLoader = new ServerConfigurationLoader();
        StandardServer server = new StandardServer();
        serverConfigurationLoader.lifecycleEvent(new LifecycleEvent(server, Lifecycle.BEFORE_START_EVENT, null));

        ContextConfigurationLoader contextConfigurationLoader = new ContextConfigurationLoader();
        StandardContext context = new StandardContext();
        context.setParent(new StandardHost());
        context.setDocBase("mock-doc");
        contextConfigurationLoader.lifecycleEvent(new LifecycleEvent(context, Lifecycle.BEFORE_START_EVENT, null));

        classLoaderContext = new WebappClassLoaderContext(context);
    }

    @Test
    public void testRepositoriesCount() {
        Assert.assertEquals(classLoaderContext.getProvidedRepositories().size(), 2, "Repository count");
    }

    @Test
    public void testRepositories() throws IOException {
        List<String> repositories = classLoaderContext.getProvidedRepositories();
        for (String repository : repositories) {
            URL libUrl = new URL(repository);
            InputStream is = libUrl.openStream();
            is.close();
        }
        Assert.assertTrue(true, "Open All Repository URL's");
    }
}
