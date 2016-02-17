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
package org.wso2.appserver.webapp.security.sso.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * This class contains general utility functions used within the single-sign-on (SSO) implementation.
 *
 * @since 6.0.0
 */
public class SSOUtils {
    private static final Logger logger = Logger.getLogger(SSOUtils.class.getName());
    private static final SecureRandom random = new SecureRandom();

    /**
     * Prevents instantiating the SSOUtils utility class.
     */
    private SSOUtils() {
    }

    /**
     * Returns a {@code Path} instance representing the base of Apache Tomcat instances.
     *
     * @return a {@link Path} instance representing the base of Apache Tomcat instances
     * @throws SSOException if CATALINA_BASE environmental variable has not been set
     */
    public static Path getCatalinaBase() throws SSOException {
        String envVariable = System.getProperty(SSOConstants.SAMLSSOValveConstants.CATALINA_BASE);
        if (envVariable != null) {
            return Paths.get(envVariable);
        } else {
            throw new SSOException("CATALINA_BASE environmental variable has not been set");
        }
    }

    /**
     * Returns a {@code Path} instance representing the Apache Tomcat configuration home CATALINA_BASE/conf.
     *
     * @return a {@link Path} instance representing the Apache Tomcat configuration home CATALINA_BASE/conf
     * @throws SSOException if CATALINA_BASE environmental variable has not been set
     */
    public static Path getCatalinaConfigurationHome() throws SSOException {
        return Paths.
                get(getCatalinaBase().toString(), SSOConstants.SAMLSSOValveConstants.TOMCAT_CONFIGURATION_FOLDER_NAME);
    }

    /**
     * Loads the property content defined in the specified file to the specified {@code Properties} data structure.
     *
     * @param properties the {@link Properties} structure to which the file content is to be loaded
     * @param filePath   the {@link Path} to the file from which properties are to be loaded
     * @throws SSOException if an error occurs during the loading of the file content or if the specified file cannot
     *                      be found
     */
    public static void loadPropertiesFromFile(Properties properties, Path filePath) throws SSOException {
        if ((properties != null) && (filePath != null)) {
            try (InputStream fileInputStream = Files.newInputStream(filePath)) {
                properties.load(fileInputStream);
                logger.log(Level.INFO, "Successfully loaded the properties from the file");
            } catch (IOException e) {
                throw new SSOException("Error when loading properties from the specified file " + filePath);
            }
        } else {
            throw new SSOException("Specified properties table structure and file path cannot be null");
        }
    }

    /**
     * Generates a unique id for SAML based tokens.
     *
     * @return a unique id for SAML based tokens
     */
    public static String createID() {
        byte[] bytes = new byte[20]; // 160 bit
        random.nextBytes(bytes);
        char[] characterMapping = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p' };

        char[] characters = new char[40];
        IntStream.range(0, bytes.length).forEach(index -> {
            int left = (bytes[index] >> 4) & 0x0f;
            int right = bytes[index] & 0x0f;
            characters[index * 2] = characterMapping[left];
            characters[index * 2 + 1] = characterMapping[right];
        });

        return String.valueOf(characters);
    }

    /**
     * Returns true if the specified {@code String} is blank, else false.
     *
     * @param stringValue the {@link String} to be checked whether it is blank
     * @return true if the specified {@link String} is blank, else false
     */
    public static boolean isBlank(String stringValue) {
        return (stringValue == null) || stringValue.isEmpty() || stringValue.chars().
                mapToObj(intCharacter -> (char) intCharacter).parallel().allMatch(Character::isWhitespace);
    }

    /**
     * Returns true if the specified {@code Collection} is null or empty, else false.
     *
     * @param collection the {@link Collection} to be checked
     * @return true if the specified {@code Collection} is null or empty, else false
     */
    public static boolean isCollectionEmpty(Collection collection) {
        return ((collection == null) || (collection.isEmpty()));
    }

    /**
     * Generates a {@code javax.xml.parsers.DocumentBuilder} instance based on the specified configurations.
     *
     * @param expandEntityReferences true if the parser is to expand entity reference nodes, else false
     * @param namespaceAware         true if the parser provides support for XML namespaces, else false
     * @param entityResolver         the {@link EntityResolver} to be used within the parser, if {@code entityResolver}
     *                               is set to null default implementation is used
     * @return the generated {@link DocumentBuilder} instance
     * @throws SSOException if an error occurs when generating the new DocumentBuilder
     */
    public static DocumentBuilder getDocumentBuilder(boolean expandEntityReferences, boolean namespaceAware,
            Optional<EntityResolver> entityResolver) throws SSOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        if (!expandEntityReferences) {
            documentBuilderFactory.setExpandEntityReferences(false);
        }
        if (namespaceAware) {
            documentBuilderFactory.setNamespaceAware(true);
        }

        DocumentBuilder docBuilder;
        try {
            docBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new SSOException("Error when generating the new DocumentBuilder", e);
        }
        entityResolver.ifPresent(docBuilder::setEntityResolver);

        return docBuilder;
    }
}
