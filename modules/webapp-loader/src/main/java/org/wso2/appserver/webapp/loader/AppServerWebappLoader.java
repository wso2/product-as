/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.wso2.appserver.webapp.loader;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.loader.WebappLoader;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.openejb.component.ClassLoaderEnricher;
import org.apache.openejb.loader.SystemInstance;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


/**
 * Customized {@link WebappLoader} for Application Server.
 *
 * @since 6.0.0
 */
public class AppServerWebappLoader extends WebappLoader {

    private static final Log log = LogFactory.getLog(AppServerWebappLoader.class);
    @SuppressWarnings("unused")
    public AppServerWebappLoader() {
        super();
    }

    @SuppressWarnings("unused")
    public AppServerWebappLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected void startInternal() throws LifecycleException {
        // build the class loading context for the webapp
        WebappClassLoaderContext webappClassLoaderContext = new WebappClassLoaderContext(getContext());
        super.startInternal();
        final ClassLoaderEnricher classLoaderEnricher = SystemInstance.get().getComponent(ClassLoaderEnricher.class);
        if (null != classLoaderEnricher) {
            List<String> urls = webappClassLoaderContext.getProvidedRepositories();
            for (String url : urls) {
                try {
                    classLoaderEnricher.addUrl(new URL(url));
                } catch (MalformedURLException e) {
                    log.error("Incorrect jar path " + e);
                }
            }
        }
        ((AppServerWebappClassLoader) getClassLoader()).setWebappClassLoaderContext(webappClassLoaderContext);
    }
}
