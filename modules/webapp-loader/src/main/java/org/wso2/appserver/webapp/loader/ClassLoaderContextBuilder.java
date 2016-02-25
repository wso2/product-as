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

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.wso2.appserver.webapp.loader.conf.EnvironmentConfiguration;
import org.wso2.appserver.webapp.loader.conf.WebAppConfigurationData;
import org.wso2.appserver.webapp.loader.exceptions.ApplicationServerException;
import org.wso2.appserver.webapp.loader.exceptions.ApplicationServerStartupException;
import org.wso2.appserver.webapp.loader.exceptions.ClassLoaderConfigurationException;
import org.wso2.appserver.webapp.loader.exceptions.ClassLoaderEnvironmentException;
import org.wso2.appserver.webapp.loader.util.Utils;
import org.wso2.appserver.webapp.loader.util.XMLUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;
import javax.xml.bind.JAXBException;

/**
 * Responsible for building the ClassLoaderConfiguration and WebappClassLoaderContext objects based on the
 * webapp-classloader-environments.xml and webapp-classloader.xml files.
 */

// TODO: ASK requirement of rebuilding config while tomcat is running (new method background or Java WatchService)
public class ClassLoaderContextBuilder {

    private static final Log log = LogFactory.getLog(ClassLoaderContextBuilder.class);

    private static ClassLoaderConfiguration classLoaderConfig;
    private static boolean isSuccessful = false;

    public static void initialize() {
        try {
            if (!isSuccessful) {
                buildSystemConfig();
            }
        } catch (ApplicationServerException | FileNotFoundException ex) {
            isSuccessful = false;
            log.error(ex.getMessage(), ex);
            throw new ApplicationServerStartupException(ex.getMessage(), ex);
        }
    }

    /**
     * Loads the system configuration files and build the global configuration classloader environment.
     * f
     *
     * @throws ApplicationServerException If the configuration file contains invalid information or structure
     * @throws FileNotFoundException      If the global configuration files does not exist
     */
    public static synchronized void buildSystemConfig() throws ApplicationServerException, FileNotFoundException {

        classLoaderConfig = new ClassLoaderConfiguration();

        Path defaultEnvironmentConfigPath = Paths
                .get(System.getProperty("catalina.home"), "conf", "wso2", LoaderConstants.ENVIRONMENT_CONFIG_FILE);

        //Loading specified environment from the environment config file.
        File environmentConfigFile = defaultEnvironmentConfigPath.toFile();
        if (!environmentConfigFile.exists()) {
            String errorMessage = "Failed to load environment configuration file: " + defaultEnvironmentConfigPath;
            throw new FileNotFoundException(errorMessage);
        }
        populateEnvironments(environmentConfigFile);

        Path defaultClassloaderConfigPath = Paths
                .get(System.getProperty("catalina.home"), "conf", "wso2", LoaderConstants.CLASSLOADER_CONFIG_FILE);

        //Loading class loading policy form the classloader config file.
        File classLoaderConfigFile = defaultClassloaderConfigPath.toFile();
        if (!classLoaderConfigFile.exists()) {
            String errorMessage = "Failed to load default classloader configuration file: "
                    + defaultEnvironmentConfigPath;
            throw new FileNotFoundException(errorMessage);
        }
        loadClassLoaderPolicy(classLoaderConfigFile);

        isSuccessful = true;
    }

    //TODO Validate the schema.. works for the best case.
    private static void populateEnvironments(File environmentConfigFile) throws ClassLoaderEnvironmentException {

        try {
            EnvironmentConfiguration environmentConfiguration = XMLUtils.unmarshalJAXB(environmentConfigFile,
                    EnvironmentConfiguration.class);
            populateDelegatedEnvironments(environmentConfiguration);
            populateExclusiveEnvironments(environmentConfiguration);
        } catch (JAXBException ex) {
            String errorMessage = "Failed to parse environment configurations from " + environmentConfigFile;
            throw new ClassLoaderEnvironmentException(errorMessage, ex);
        }

    }

    private static void populateDelegatedEnvironments(EnvironmentConfiguration environmentConfiguration) {

        environmentConfiguration.getDelegatedEnvironments().getDelegatedEnvironment()
                .forEach((delegatedEnvironment) -> {

                    List<String> packages = delegatedEnvironment.getDelegatedPackages().getDelegatedPackage();
                    List<String> resources = delegatedEnvironment.getDelegatedResources().getDelegatedResource();

                    ClassLoaderEnvironment environment = new ClassLoaderEnvironment(delegatedEnvironment.getName(),
                            EnvironmentType.DELEGATED, packages, resources);

                    classLoaderConfig.addEnvironment(environment);
                });

    }

    private static void populateExclusiveEnvironments(EnvironmentConfiguration environmentConfiguration) {

        environmentConfiguration.getExclusiveEnvironments().getExclusiveEnvironment()
                .forEach((exclusiveEnvironment) -> {

                    List<String> urls = new ArrayList<>();

                    exclusiveEnvironment.getClassPaths().getClassPath()
                            .forEach((classPath) -> urls.addAll(Utils.generateClasspath(classPath)));

                    ClassLoaderEnvironment environment = new ClassLoaderEnvironment(exclusiveEnvironment.getName(),
                            EnvironmentType.EXCLUSIVE, urls, null);

                    classLoaderConfig.addEnvironment(environment);
                });

    }

    //TODO Validate the schema.. works for the best case. Improve error handling
    private static void loadClassLoaderPolicy(File classLoaderConfigFile) throws ClassLoaderConfigurationException {

        try {
            WebAppConfigurationData defaultWebAppConfigurationData = XMLUtils.unmarshalJAXB(classLoaderConfigFile,
                    WebAppConfigurationData.class);
            boolean parentFirst = defaultWebAppConfigurationData.getClassloading().isParentFirst();
            classLoaderConfig.setParentFirstBehaviour(parentFirst);
            classLoaderConfig.setDefaultEnvironmentNames(defaultWebAppConfigurationData
                    .getClassloading().getEnvironments().getEnvironment());
        } catch (JAXBException ex) {
            String errorMessage = "Failed to parse classloader default configurations from " + classLoaderConfigFile;
            throw new ClassLoaderConfigurationException(errorMessage, ex);
        }
    }

    /**
     * Build the WebappClassLoaderContext per webapp using the configurations specified by per webapp or global.
     *
     * @param webappFilePath Absolute path to the specific webapp
     * @return an WebappClassLoaderContext for the webapp
     * @throws ApplicationServerException If the configuring fails
     */
    public static WebappClassLoaderContext buildClassLoaderContext(String webappFilePath)
            throws ApplicationServerException {

        WebappClassLoaderContext webappClassLoaderContext = new WebappClassLoaderContext();
        Optional<URL> appCLConfigFileURL = Optional.ofNullable(getClassLoaderConfigFileURL(webappFilePath));

        if (!appCLConfigFileURL.isPresent()) {
            //Webapp is not specified a custom custom classloader behaviour, hence defaults to the system values.
            return buildDefaultBehaviour(webappClassLoaderContext);

        } else {
            //Webapp contains custom classloader specification.
            return buildSpecificBehaviour(webappClassLoaderContext, appCLConfigFileURL.get());
        }
    }

    private static WebappClassLoaderContext buildDefaultBehaviour(WebappClassLoaderContext webappClassLoaderContext) {

        webappClassLoaderContext.setParentFirst(classLoaderConfig.isParentFirst());
        ClassLoaderEnvironment defaultEnvironment = classLoaderConfig.getEnvironment(LoaderConstants.TOMCAT_ENV);

        List<String> defaultEnvironmentNames = new ArrayList<>();
        List<String> delegatedPackageList = new ArrayList<>();
        List<String> delegatedResourceList = new ArrayList<>();
        List<String> providedRepositories = new ArrayList<>();

//        addWepAppDefaultExternalRepositories(providedRepositories);

        if (classLoaderConfig.getDefaultEnvironmentNames().size() > 0) {

            classLoaderConfig.getDefaultEnvironmentNames()
                    .forEach((environmentName) -> {

                        ClassLoaderEnvironment environment = classLoaderConfig.getEnvironment(environmentName);

                        Optional.ofNullable(environment).ifPresent((env) -> {
                            defaultEnvironmentNames.add(env.getEnvironmentName());
                            if (env.getEnvironmentType() == EnvironmentType.DELEGATED) {
                                delegatedPackageList.addAll(env.getPackageList());
                                delegatedResourceList.addAll(env.getResourcesList());
                            } else if (env.getEnvironmentType() == EnvironmentType.EXCLUSIVE) {
                                providedRepositories.addAll(env.getPackageList());
                            }
                        });

                    });

            boolean isDefaultAdded = false;
            if (delegatedPackageList.isEmpty()) {
                delegatedPackageList.addAll(defaultEnvironment.getPackageList());
                isDefaultAdded = true;
            }

            if (delegatedResourceList.isEmpty()) {
                delegatedResourceList.addAll(defaultEnvironment.getResourcesList());
                isDefaultAdded = true;
            }

            if (isDefaultAdded) {
                defaultEnvironmentNames.add(defaultEnvironment.getEnvironmentName());
            }

            webappClassLoaderContext.setEnvironments(defaultEnvironmentNames);
            webappClassLoaderContext.setDelegatedPackages(delegatedPackageList);
            webappClassLoaderContext.setDelegatedResources(delegatedResourceList);
            webappClassLoaderContext.setProvidedRepositories(providedRepositories);

        } else {

            defaultEnvironmentNames.add(defaultEnvironment.getEnvironmentName());

            webappClassLoaderContext.setEnvironments(defaultEnvironmentNames);
            webappClassLoaderContext.setDelegatedPackages(defaultEnvironment.getPackageList());
            webappClassLoaderContext.setDelegatedResources(defaultEnvironment.getResourcesList());
            webappClassLoaderContext.setProvidedRepositories(providedRepositories);

        }

        return webappClassLoaderContext;
    }

    private static WebappClassLoaderContext buildSpecificBehaviour(WebappClassLoaderContext webappClassLoaderContext
            , URL webappConfigFileURL) throws ApplicationServerException {

        //TODO: boolean null return if not specified

        try {

            WebAppConfigurationData webAppConfigurationData = XMLUtils
                    .unmarshalJAXB(webappConfigFileURL.openStream(), WebAppConfigurationData.class);

            if (webAppConfigurationData.getClassloading().isParentFirst() != null) {
                webappClassLoaderContext.setParentFirst(webAppConfigurationData.getClassloading().isParentFirst());
            } else {
                webappClassLoaderContext.setParentFirst(classLoaderConfig.isParentFirst());
            }

            List<String> environmentNames = new ArrayList<>();
            List<String> delegatedPackageList = new ArrayList<>();
            List<String> delegatedResourceList = new ArrayList<>();
            List<String> providedRepositories = new ArrayList<>();

//            addWepAppDefaultExternalRepositories(providedRepositories);

            if (webAppConfigurationData.getClassloading().getEnvironments() != null) {

                environmentNames.addAll(webAppConfigurationData.getClassloading().getEnvironments().getEnvironment());
                addSystemEnvironment(environmentNames);

            } else {
                environmentNames.addAll(classLoaderConfig.getDefaultEnvironmentNames());
            }

            environmentNames.forEach((environmentName) -> {

                ClassLoaderEnvironment environment = classLoaderConfig.getEnvironment(environmentName);

                Optional.ofNullable(environment).ifPresent((env) -> {

                    if (env.getEnvironmentType() == EnvironmentType.DELEGATED) {

                        delegatedPackageList.addAll(env.getPackageList());
                        delegatedResourceList.addAll(env.getResourcesList());

                    } else if (env.getEnvironmentType() == EnvironmentType.EXCLUSIVE) {

                        providedRepositories.addAll(env.getPackageList());

                    }
                });

            });

            webappClassLoaderContext.setEnvironments(environmentNames);
            webappClassLoaderContext.setDelegatedPackages(delegatedPackageList);
            webappClassLoaderContext.setDelegatedResources(delegatedResourceList);
            webappClassLoaderContext.setProvidedRepositories(providedRepositories);


        } catch (JAXBException | IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new ApplicationServerException("Failed to load webapp configuration", ex);
        }

        return webappClassLoaderContext;
    }

//    private static void addWepAppDefaultExternalRepositories(List<String> repositories) {
//        repositories.addAll(Utils.generateClasspath(LoaderConstants.DEFAULT_EXT_DIR));
//    }

    private static URL getClassLoaderConfigFileURL(String webappFilePath) throws ApplicationServerException {

        File webappFile = new File(webappFilePath);
        URL configFileURL = null;
        Path webappClassloaderConfigPath = Paths.get("META-INF", LoaderConstants.CLASSLOADER_CONFIG_FILE);

        if (webappFile.isDirectory()) {

            File configFile = Paths.get(webappFilePath, "META-INF", LoaderConstants.CLASSLOADER_CONFIG_FILE).toFile();
            if (configFile.exists()) {
                try {
                    configFileURL = configFile.toURI().toURL();
                } catch (MalformedURLException | RuntimeException ex) {
                    log.error(ex.getMessage(), ex);
                    throw new ApplicationServerException("Error while loading webapp class loader configuration", ex);
                }
            }

        } else {

            JarFile webappJarFile = null;
            try {
                webappJarFile = new JarFile(webappFilePath);
                if (Optional.ofNullable(webappJarFile.getJarEntry(webappClassloaderConfigPath.toString()))
                        .isPresent()) {
                    configFileURL = new URL("jar:file:" + URLEncoder.encode(webappFilePath, "UTF-8")
                            .replace("+", "%20") + "!/" + webappClassloaderConfigPath);
                }
            } catch (IOException | RuntimeException ex) {
                log.error(ex.getMessage(), ex);
                throw new ApplicationServerException("Error while loading webapp class loader configuration", ex);
            } finally {
                if (webappJarFile != null) {
                    try {
                        webappJarFile.close();
                    } catch (Throwable t) {
                        log.error(t.getMessage(), t);
                        ExceptionUtils.handleThrowable(t);
                    }
                }
            }

        }
        return configFileURL;
    }

    /**
     * Add 'Tomcat' also as an environments if
     * <p>
     * 1. specified environments list does not contains 'Tomcat'
     * 2. specified environments list does not contains 'Carbon', Carbon includes Tomcat
     */
    private static void addSystemEnvironment(List<String> environments) {

        if (!(environments.contains(LoaderConstants.TOMCAT_ENV))) {
            environments.add(LoaderConstants.TOMCAT_ENV);
        }

    }


}
