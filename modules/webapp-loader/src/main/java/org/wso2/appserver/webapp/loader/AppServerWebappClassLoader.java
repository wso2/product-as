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
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Customized WebappClassloader for Application Server. This class introduces a new classloader pattern which defines
 * on wso2as-web.xml file. The default behaviour is specified in the container level configuration file.
 * But webapp has the ability to override that behaviour by adding the customised  wso2as-web.xml file into
 * the webapp.
 */
public class AppServerWebappClassLoader extends WebappClassLoaderBase {
    private static final Log log = LogFactory.getLog(AppServerWebappClassLoader.class);

    private WebappClassLoaderContext webappClassLoaderContext;

    public AppServerWebappClassLoader(ClassLoader parent) {
        super(parent);
    }

    public synchronized void setWebappClassLoaderContext(WebappClassLoaderContext classLoaderContext) {
        this.webappClassLoaderContext = classLoaderContext;
        webappClassLoaderContext.getProvidedRepositories().forEach(this::addRepository);
    }

    @Override
    public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        log.debug("loadClass(" + name + ", " + resolve + ")");


        Class<?> clazz;

        // Check previously loaded local class cache (comes from WebappClassLoaderBase)
        clazz = super.findLoadedClass0(name);
        if (clazz != null) {
            if (log.isDebugEnabled()) {
                log.debug("  Returning class from local cache (WebappClassLoader)");
            }
            if (resolve) {
                resolveClass(clazz);
            }
            return (clazz);
        }

        // Check previously loaded class cache (comes from Java ClassLoader)
        clazz = super.findLoadedClass(name);
        if (clazz != null) {
            if (log.isDebugEnabled()) {
                log.debug("  Returning class from cache (Java)");
            }
            if (resolve) {
                resolveClass(clazz);
            }
            return (clazz);
        }

        // Try loading the class with the system class loader, to prevent
        //       the webapp from overriding J2SE classes   (looks for jre ext libs)
        try {
            clazz = super.getJavaseClassLoader().loadClass(name);
            if (clazz != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Returning class from JavaSEClassLoader");
                }
                if (resolve) {
                    resolveClass(clazz);
                }
                return (clazz);
            }
        } catch (ClassNotFoundException e) {
            // Ignore
        }

        // Permission to access this class when using a SecurityManager
        if (securityManager != null) {
            int i = name.lastIndexOf('.');
            if (i >= 0) {
                try {
                    securityManager.checkPackageAccess(name.substring(0, i));
                } catch (SecurityException ex) {
                    String error = "Security Violation, attempt to use " + "Restricted Class: " + name;
                    log.info(error, ex);
                    throw new ClassNotFoundException(error, ex);
                }
            }
        }


        // Load from the parent if the parent-first is true and if package matches with the
        //    list of delegated packages
        if (webappClassLoaderContext.isParentFirst()) {
            clazz = findClassFromParent(name, resolve);
            if (clazz != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Returning class from Parent");
                }
                return clazz;
            }
        }

        // Load the class from the local(webapp) classpath
        clazz = findLocalClass(name, resolve);
        if (clazz != null) {
            if (log.isDebugEnabled()) {
                log.debug("  Returning class from Child");
            }
            return clazz;
        }

        // Load from the parent if the parent-first is false and if the package matches with the
        //    list of delegated packages.
        if (!webappClassLoaderContext.isParentFirst()) {
            clazz = findClassFromParent(name, resolve);
            if (clazz != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Returning class from Fail Child 1st Config");
                }
                return clazz;
            }
        }

        throw new ClassNotFoundException(name);
    }

    protected Class<?> findClassFromParent(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = null;
        if (log.isDebugEnabled()) {
            log.debug("  Delegating to parent classloader " + parent);
        }
        ClassLoader loader = parent;
        if (loader == null) {
            loader = super.getJavaseClassLoader();
        }
        try {
            clazz = Class.forName(name, false, loader);
            if (clazz != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Loading class from parent");
                }
                if (resolve) {
                    resolveClass(clazz);
                }
            }
        } catch (ClassNotFoundException e) {
//            Ignore
        }
        return (clazz);
    }


    protected Class<?> findLocalClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = null;
        if (log.isDebugEnabled()) {
            log.debug("  Searching local repositories");
        }
        try {
            clazz = findClass(name);
            if (clazz != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Loading class from local repository");
                }
                if (resolve) {
                    resolveClass(clazz);
                }
            }
        } catch (ClassNotFoundException e) {
            // Ignore
        }
        return (clazz);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream stream = super.getResourceAsStream(name);
        if (stream != null) {
            return stream;
        } else if (name.endsWith(".class")) {
            ClassLoader loader = super.getJavaseClassLoader();
            stream = loader.getResourceAsStream(name);

            if (stream != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  --> Returning stream from system classloader");
                }
                return stream;
            }
        }

        return null;
    }

    public ClassLoader copyWithoutTransformers() {

        ClassLoader parent = this.getParent();
        AppServerWebappClassLoader result = AccessController.doPrivileged(
                (PrivilegedAction<AppServerWebappClassLoader>) () -> new AppServerWebappClassLoader(parent)
        );

        super.copyStateWithoutTransformers(result);

        try {
            result.start();
            return result;
        } catch (LifecycleException ex) {
            throw new IllegalStateException(ex);
        }
    }

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
