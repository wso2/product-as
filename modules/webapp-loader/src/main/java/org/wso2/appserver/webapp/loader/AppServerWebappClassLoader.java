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
import sun.misc.CompoundEnumeration;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Customized WebappClassloader for Application Server. This class introduces a new classloader pattern which is based
 * on the webapp-classloader.xml file. The default behaviour is specified in the container level configuration file.
 * But webapps has the ability to override that behaviour by adding the customised  webapp-classloader.xml file into
 * the webapp.
 */
public class AppServerWebappClassLoader extends WebappClassLoaderBase {
    private static final Log log = LogFactory.getLog(AppServerWebappClassLoader.class);

    private WebappClassLoaderContext webappClassLoaderContext;

    List<URL> urls = new ArrayList<>();

    public AppServerWebappClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void setWebappClassLoaderContext(WebappClassLoaderContext classLoaderContext) {
        this.webappClassLoaderContext = classLoaderContext;
    }

    /*
    url mappings
      this -> webbapp libs
      this.parent -> tomcat lib
      this.parent.parent -> tomcat bin jars
      this.parent.parent.parent -> jre external jars (similar to getJ2SE classloader)
    */

    @Override
    public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        log.debug("loadClass(" + name + ", " + resolve + ")");

//        Log access to stopped classloader
//        LifecycleState lifecycleState = this.getState();
//        if (lifecycleState != LifecycleState.STARTED) {
//            try {
//                throw new IllegalStateException();
//            } catch (IllegalStateException e) {
//                log.info(sm.getString("webappClassLoader.stopped", name), e);
//            }
//        }

        Class<?> clazz;

        //region Try to load classes from cache
        // (0) Check our previously loaded local class cache (comes from WebappClassLoaderBase)
        clazz = super.findLoadedClass0(name);
        if (clazz != null) {
            log.debug("  Returning class from local cache (WebappClassLoader)");
            if (resolve) {
                resolveClass(clazz);
            }
            return (clazz);
        }

        // (0.1) Check our previously loaded class cache (comes from Java ClassLoader)
        clazz = super.findLoadedClass(name);
        if (clazz != null) {
            log.debug("  Returning class from cache (Java)");
            if (resolve) {
                resolveClass(clazz);
            }
            return (clazz);
        }
        //endregion

        //region Try to load classes from J2SE and check security violations
        // (0.2) Try loading the class with the system class loader, to prevent
        //       the webapp from overriding J2SE classes   (looks for jre ext libs)
        try {
            clazz = super.getJavaseClassLoader().loadClass(name);
            if (clazz != null) {
                log.debug("  Returning class from JavaSEClassLoader");
                if (resolve) {
                    resolveClass(clazz);
                }
                return (clazz);
            }
        } catch (ClassNotFoundException e) {
            // Ignore
        }

        // (0.5) Permission to access this class when using a SecurityManager
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
        //endregion

        boolean isDelegatedPkg = webappClassLoaderContext.isDelegatedPackage(name);
        boolean isExcludedPkg = webappClassLoaderContext.isExcludedPackage(name);


        // 1) Load from the parent if the parent-first is true and if package matches with the
        //    list of delegated packages
        if (webappClassLoaderContext.isParentFirst() && isDelegatedPkg && !isExcludedPkg) {
            clazz = findClassFromParent(name, resolve);
            if (clazz != null) {
                log.debug("  Returning class from Parent 1st Config and Delegated");
                return clazz;
            }
        }

        // 2) Load the class from the local(webapp) classpath
        clazz = findLocalClass(name, resolve);
        if (clazz != null) {
            log.debug("  Returning class from Child 1st Config");
            return clazz;
        }

        // 3) TODO load from the shared repositories

        // 4) Load from the parent if the parent-first is false and if the package matches with the
        //    list of delegated packages.
        if (!webappClassLoaderContext.isParentFirst() && isDelegatedPkg && !isExcludedPkg) {
            clazz = findClassFromParent(name, resolve);
            if (clazz != null) {
                log.debug("  Returning class from Fail Child 1st Config");
                return clazz;
            }
        }

        throw new ClassNotFoundException(name);
    }

    protected Class<?> findClassFromParent(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = null;
        log.debug("  Delegating to parent classloader1 " + parent);
        ClassLoader loader = parent;
        if (loader == null) {
            loader = super.getJavaseClassLoader();
        }
        try {
            clazz = Class.forName(name, false, loader);
            if (clazz != null) {
                log.debug("  Loading class from parent");
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
        log.debug("  Searching local repositories");
        try {
            clazz = findClass(name);
            if (clazz != null) {
                log.debug("  Loading class from local repository");
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
                log.debug("  --> Returning stream from system classloader");
                return stream;
            }
        }

        return null;
    }

    public Enumeration<URL> getResources(String name) throws IOException {
        @SuppressWarnings("unchecked")
        Enumeration<URL>[] tmp = (Enumeration<URL>[]) new Enumeration<?>[2];
        /*
        The logic to access BootstrapClassPath is JDK vendor dependent hence
        we can't call it from here. Ensure 'parentCL != null', to find resources
        from BootstrapClassPath.

         */
        if (parent != null) {
            boolean delegatedRes = webappClassLoaderContext.isDelegatedResource(name);
            boolean excludedRes = webappClassLoaderContext.isExcludedResources(name);
            if (delegatedRes && !excludedRes) {
                tmp[0] = parent.getResources(name);
            }

        }
        tmp[1] = findResources(name);

        return new CompoundEnumeration<>(tmp);
    }


    public ClassLoader copyWithoutTransformers() {
        AppServerWebappClassLoader result = new AppServerWebappClassLoader(this.getParent());
        super.copyStateWithoutTransformers(result);

        try {
            result.start();
            return result;
        } catch (LifecycleException ex) {
            throw new IllegalStateException(ex);
        }
    }

    // TODO: Test if working (deprecated addRepository in catalina 8)
    public void addRepository(String repository) {

        URL url = null;
        try {
            url = new URL(repository);
        } catch (MalformedURLException ex) {
            log.error(ex.getMessage(), ex);
        }
        urls.add(url);
        addURL(url);
    }
}
