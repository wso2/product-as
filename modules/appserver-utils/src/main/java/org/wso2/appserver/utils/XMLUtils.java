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
package org.wso2.appserver.utils;

import org.wso2.appserver.exceptions.AppServerException;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

/**
 * A Java class which defines XML utilities used within the Application Server Utils.
 *
 * @since 6.0.0
 */
public class XMLUtils {
    private static final Logger logger = Logger.getLogger(XMLUtils.class.getName());

    /**
     * JAXB utility functions
     */

    /**
     * Returns an XML unmarshaller for the defined Java classes.
     *
     * @param schema  an optional file path representation of an XML schema file against which the source XML is to be
     *                validated
     * @param classes the list of classes to be recognized by the {@link JAXBContext}
     * @return an XML unmarshaller for the defined Java classes
     * @throws AppServerException if an error occurs when creating the XML unmarshaller
     */
    public static Unmarshaller getXMLUnmarshaller(Optional<Path> schema, Class... classes) throws AppServerException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            schema.ifPresent(schemaPath -> {
                if (Files.exists(schemaPath)) {
                    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                    Schema xmlSchema;
                    try {
                        xmlSchema = schemaFactory.newSchema(schemaPath.toFile());
                    } catch (SAXException e) {
                        logger.log(Level.WARNING, "An error has occurred during parsing", e);
                        xmlSchema = null;
                    }
                    Optional.ofNullable(xmlSchema).ifPresent(unmarshaller::setSchema);
                }
            });
            return unmarshaller;
        } catch (JAXBException e) {
            throw new AppServerException("Error when creating the XML unmarshaller", e);
        }
    }

    /**
     * Builds an XML binding from the XML source file specified.
     *
     * @param source       the XML source file path representation
     * @param schema       an optional file path representation of an XML schema file against which the source XML
     *                     is to be validated
     * @param bindingClass the class to be recognized by the {@link JAXBContext}
     * @param <T>          the type of the class to be bound
     * @return bound object (Type T) of XML
     * @throws AppServerException if an error occurred when creating the unmarshaller or unmarshalling the XML source
     */
    public static <T> T getUnmarshalledObject(Path source, Optional<Path> schema, Class<T> bindingClass)
            throws AppServerException {
        try {
            Unmarshaller unmarshaller = getXMLUnmarshaller(schema, bindingClass);
            Object unmarshalled = unmarshaller.unmarshal(source.toFile());
            return bindingClass.cast(unmarshalled);
        } catch (JAXBException e) {
            throw new AppServerException("Error when unmarshalling the XML source", e);
        }
    }

    /**
     * Builds an XML binding from the {@code InputStream} specified.
     *
     * @param inputStream  the {@link InputStream} to unmarshall XML data from
     * @param schema       an optional file path representation of an XML schema file against which the source XML
     *                     is to be validated
     * @param bindingClass the class to be recognized by the {@link JAXBContext}
     * @param <T>          the type of the class to be bound
     * @return bound object (Type T) of XML
     * @throws AppServerException if an error occurred when creating the unmarshaller or unmarshalling the XML source
     */
    public static <T> T getUnmarshalledObject(InputStream inputStream, Optional<Path> schema, Class<T> bindingClass)
            throws AppServerException {
        try {
            Unmarshaller unmarshaller = getXMLUnmarshaller(schema, bindingClass);
            Object unmarshalled = unmarshaller.unmarshal(inputStream);
            return bindingClass.cast(unmarshalled);
        } catch (JAXBException e) {
            throw new AppServerException("Error when unmarshalling the XML source", e);
        }
    }

    /**
     * JAXP utility functions
     */

    /**
     * Generates a {@code javax.xml.parsers.DocumentBuilder} instance based on the specified configurations.
     *
     * @param expandEntityReferences true if the parser is to expand entity reference nodes, else false
     * @param namespaceAware         true if the parser provides support for XML namespaces, else false
     * @param entityResolver         the {@link EntityResolver} to be used within the parser, if {@code entityResolver}
     *                               is set to null default implementation is used
     * @return the generated {@link DocumentBuilder} instance
     * @throws AppServerException if an error occurs when generating the new DocumentBuilder
     */
    public static DocumentBuilder getDocumentBuilder(boolean expandEntityReferences, boolean namespaceAware,
            Optional<EntityResolver> entityResolver) throws AppServerException {
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
            throw new AppServerException("Error when generating the new DocumentBuilder", e);
        }
        entityResolver.ifPresent(docBuilder::setEntityResolver);

        return docBuilder;
    }
}
