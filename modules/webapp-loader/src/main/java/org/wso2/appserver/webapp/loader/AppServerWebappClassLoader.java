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
 */
package org.wso2.appserver.webapp.loader;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Customized WebappClassloader for Application Server. This class introduces a new classloader pattern which defines
 * on wso2as-web.xml file.
 * <p>
 * The default behaviour is specified in the container level configuration file. But webapp has the ability to override
 * that behaviour by adding the customised  wso2as-web.xml file into the webapp.
 *
 * @since 6.0.0
 */
public class AppServerWebappClassLoader extends WebappClassLoaderBase {
    private static final Log log = LogFactory.getLog(AppServerWebappClassLoader.class);

    private WebappClassLoaderContext webappClassLoaderContext;

    public AppServerWebappClassLoader(ClassLoader parent) {
        super(parent);
    }

    /**
     * Sets the {@link WebappClassLoaderContext} associated with this classloader.
     *
     * @param classLoaderContext the web application specific classloader context
     */
    public synchronized void setWebappClassLoaderContext(WebappClassLoaderContext classLoaderContext) {
        webappClassLoaderContext = classLoaderContext;
        webappClassLoaderContext.getProvidedRepositories().forEach(this::addRepository);
    }

    /**
     * Returns a new classloader without any class file transforms.
     *
     * @return copy of this classloader without any class file transformers
     */
    public ClassLoader copyWithoutTransformers() {
        ClassLoader parent = this.getParent();
        AppServerWebappClassLoader result = AccessController.doPrivileged(
                (PrivilegedAction<AppServerWebappClassLoader>) () -> new AppServerWebappClassLoader(parent));

        super.copyStateWithoutTransformers(result);

        try {
            result.start();
            return result;
        } catch (LifecycleException ex) {
            throw new IllegalStateException(ex);
        }
    }

    //  adds the jar url to this class loader
    private void addRepository(String repository) {
        URL url;
        try {
            url = new URL(repository);
            addURL(url);
            if (log.isDebugEnabled()) {
                log.debug("Adding repository: " + repository + " to the web app class path");
            }
        } catch (MalformedURLException ex) {
            log.warn("Repository: " + repository + ", does not exist.", ex);
        }
    }
}
