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

import org.apache.catalina.Globals;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a test utility class which loads PathUtils java class in different catalina environments.
 *
 * @since 6.0.0
 */
public class ClassLoader {
    private static final List<Class> FAULTY_CLASS_INSTANCE;
    private static final List<Class> NON_FAULTY_CLASS_INSTANCE;
    private static final Path FAULTY_CATALINA_BASE;
    private static final Path CATALINA_BASE;

    static {
        CATALINA_BASE = Paths.get(TestConstants.TEST_RESOURCES, TestConstants.CATALINA_BASE);
        FAULTY_CATALINA_BASE = Paths.get(TestConstants.TEST_RESOURCES, TestConstants.FAULTY_CATALINA_BASE);

        try {
            FAULTY_CLASS_INSTANCE = loadFaultyCatalinaClassInstance();
            NON_FAULTY_CLASS_INSTANCE = loadCatalinaClassInstance();
        } catch (MalformedURLException | ClassNotFoundException e) {
            throw new RuntimeException("Error when loading the PathUtils class", e);
        }
    }

    public static List<Class> getFaultyClassInstances() {
        return FAULTY_CLASS_INSTANCE;
    }

    public static List<Class> getNonFaultyClassInstances() {
        return NON_FAULTY_CLASS_INSTANCE;
    }

    private static List<Class> loadFaultyCatalinaClassInstance() throws MalformedURLException, ClassNotFoundException {
        System.setProperty(Globals.CATALINA_BASE_PROP, FAULTY_CATALINA_BASE.toString());
        java.lang.ClassLoader classLoader = new URLClassLoader(
                new URL[] { Paths.get(System.getProperty("build.directory"), "classes").toUri().toURL() }, null);
        List<Class> classes = new ArrayList<>();
        classes.add(classLoader.loadClass("org.wso2.appserver.utils.PathUtils"));
        classes.add(classLoader.loadClass("org.wso2.appserver.configuration.listeners.ServerConfigurationLoader"));
        return classes;
    }

    private static List<Class> loadCatalinaClassInstance() throws MalformedURLException, ClassNotFoundException {
        System.setProperty(Globals.CATALINA_BASE_PROP, CATALINA_BASE.toString());
        java.lang.ClassLoader classLoader = new URLClassLoader(
                new URL[] { Paths.get(System.getProperty("build.directory"), "classes").toUri().toURL() }, null);
        List<Class> classes = new ArrayList<>();
        classes.add(classLoader.loadClass("org.wso2.appserver.utils.PathUtils"));
        classes.add(classLoader.loadClass("org.wso2.appserver.configuration.listeners.ServerConfigurationLoader"));
        return classes;
    }
}
