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

import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.loader.WebappLoader;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.wso2.appserver.webapp.loader.exceptions.ApplicationServerException;

import java.io.File;
import java.io.IOException;

/**
 * Customized WebappLoader for Application Server.
 *
 * @since 6.0.0
 */
public class AppServerWebappLoader extends WebappLoader {

    private static final Log log = LogFactory.getLog(AppServerWebappLoader.class);

    public AppServerWebappLoader() {
        super();
    }

    public AppServerWebappLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected void startInternal() throws LifecycleException {

        WebappClassLoaderContext webappClassLoaderContext;
        try {
            ClassLoaderContextBuilder.initialize();
            // build the specific webapp context using configurations
            webappClassLoaderContext = ClassLoaderContextBuilder.buildClassLoaderContext(this.getWebappFilePath());
        } catch (ApplicationServerException ex) {
            log.error(ex.getMessage(), ex);
            throw new LifecycleException(ex.getMessage(), ex);
        }

        super.startInternal();

        AppServerWebappClassLoader loader = ((AppServerWebappClassLoader) getClassLoader());
        //Adding provided classpath entries, if any
        for (String repository : webappClassLoaderContext.getProvidedRepositories()) {
            loader.addRepository(repository);
        }

        //Adding the WebappClassLoaderContext to the WebappClassloader
        loader.setWebappClassLoaderContext(webappClassLoaderContext);

    }

    // generate the web app file path if exist
    private String getWebappFilePath() throws ApplicationServerException {
        String webappFilePath = null;
        Context context = this.getContext();

        //Value of the following variable depends on various conditions. Sometimes you get just the webapp directory
        //name. Sometime you get absolute path the webapp directory or war file.
        try {
            if (context != null) {
                String docBase = context.getDocBase();
                Host host = (Host) context.getParent();
                String appBase = host.getAppBase();
                File canonicalAppBase = new File(appBase);
                if (canonicalAppBase.isAbsolute()) {
                    canonicalAppBase = canonicalAppBase.getCanonicalFile();
                } else {

                    canonicalAppBase = new File(System.getProperty("catalina.home"), appBase).getCanonicalFile();
                }

                File webappFile = new File(docBase);
                if (webappFile.isAbsolute()) {
                    webappFilePath = webappFile.getCanonicalPath();
                } else {
                    webappFilePath = (new File(canonicalAppBase, docBase)).getPath();
                }
            }
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new ApplicationServerException("Error while generating webapp file path", ex);
        }

        return webappFilePath;
    }

}
