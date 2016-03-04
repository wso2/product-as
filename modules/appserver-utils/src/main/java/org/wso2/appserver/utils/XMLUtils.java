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

import org.wso2.appserver.exceptions.ApplicationServerConfigurationException;
import org.wso2.appserver.exceptions.ApplicationServerException;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
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
    /**
     * JAXB utility functions.
     */

    /**
     * Returns an XML unmarshaller for the defined Java classes.
     *
     * @param schemaPath file path of the XML schema file against which the source XML is to be
     *                   validated
     * @param classes    the list of classes to be recognized by the {@link JAXBContext}
     * @return an XML unmarshaller for the defined Java classes
     * @throws ApplicationServerException if an error occurs when creating the XML unmarshaller
     */
    public static Unmarshaller getXMLUnmarshaller(Path schemaPath, Class... classes) throws ApplicationServerException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            if (Files.exists(schemaPath)) {
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema xmlSchema = schemaFactory.newSchema(schemaPath.toFile());
                unmarshaller.setSchema(xmlSchema);
            } else {
                throw new ApplicationServerException(
                        "Configuration schema not found in the file path: " + schemaPath.toString());
            }
            return unmarshaller;
        } catch (JAXBException | SAXException e) {
            throw new ApplicationServerException("Error when creating the XML unmarshaller", e);
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
     * @throws ApplicationServerException if an error occurred when creating the unmarshaller or
     *                                    unmarshalling the XML source
     */
    public static <T> T getUnmarshalledObject(Path source, Path schema, Class<T> bindingClass)
            throws ApplicationServerException {
        try {
            Unmarshaller unmarshaller = getXMLUnmarshaller(schema, bindingClass);
            Object unmarshalled = unmarshaller.unmarshal(source.toFile());
            return bindingClass.cast(unmarshalled);
        } catch (JAXBException e) {
            throw new ApplicationServerException("Error when unmarshalling the XML configuration", e);
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
     * @throws ApplicationServerException if an error occurred when creating the unmarshaller or
     *                                    unmarshalling the XML source
     */
    public static <T> T getUnmarshalledObject(InputStream inputStream, Path schema, Class<T> bindingClass)
            throws ApplicationServerException {
        try {
            Unmarshaller unmarshaller = getXMLUnmarshaller(schema, bindingClass);
            Object unmarshalled = unmarshaller.unmarshal(inputStream);
            return bindingClass.cast(unmarshalled);
        } catch (JAXBException e) {
            throw new ApplicationServerConfigurationException("Error when unmarshalling the XML configuration", e);
        }
    }

    /**
     * JAXP utility function
     */

    /**
     * Generates a {@code javax.xml.parsers.DocumentBuilder} instance based on the specified configurations.
     *
     * @param expandEntityReferences true if the parser is to expand entity reference nodes, else false
     * @param namespaceAware         true if the parser provides support for XML namespaces, else false
     * @param entityResolver         the {@link EntityResolver} to be used within the parser, if {@code entityResolver}
     *                               is set to null default implementation is used
     * @return the generated {@link DocumentBuilder} instance
     * @throws ApplicationServerException if an error occurs when generating the new DocumentBuilder
     */
    public static DocumentBuilder getDocumentBuilder(boolean expandEntityReferences, boolean namespaceAware,
            Optional<EntityResolver> entityResolver) throws ApplicationServerException {
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
            throw new ApplicationServerException("Error when generating the new DocumentBuilder", e);
        }
        entityResolver.ifPresent(docBuilder::setEntityResolver);

        return docBuilder;
    }
}
