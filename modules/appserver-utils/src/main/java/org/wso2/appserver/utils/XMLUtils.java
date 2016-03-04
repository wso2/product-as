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

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.wso2.appserver.exceptions.ApplicationServerConfigurationException;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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
    private static final Log log = LogFactory.getLog(XMLUtils.class.getName());

    /**
     * Returns an XML unmarshaller for the defined Java classes.
     *
     * @param schemaPath file path of the XML schema file against which the source XML is to be
     *                   validated
     * @param classes    the list of classes to be recognized by the {@link JAXBContext}
     * @return an XML unmarshaller for the defined Java classes
     * @throws ApplicationServerConfigurationException if an error occurs when creating the XML unmarshaller
     */
    public static Unmarshaller getXMLUnmarshaller(Path schemaPath, Class... classes)
            throws ApplicationServerConfigurationException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            if (Files.exists(schemaPath)) {
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema xmlSchema = schemaFactory.newSchema(schemaPath.toFile());
                unmarshaller.setSchema(xmlSchema);
            } else {
                String message = "Configuration schema not found: " + schemaPath.toString();
                if (log.isDebugEnabled()) {
                    log.debug(message);
                }
                throw new ApplicationServerConfigurationException(message);
            }
            return unmarshaller;

        } catch (JAXBException | SAXException ex) {
            String message = "Error when creating the XML unmarshaller";
            if (log.isDebugEnabled()) {
                log.debug(message);
            }
            throw new ApplicationServerConfigurationException(message, ex);
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
     * @throws ApplicationServerConfigurationException if an error occurred when creating the unmarshaller or
     *                                                 unmarshalling the XML source
     */
    public static <T> T getUnmarshalledObject(Path source, Path schema, Class<T> bindingClass)
            throws ApplicationServerConfigurationException {
        try {
            Unmarshaller unmarshaller = getXMLUnmarshaller(schema, bindingClass);
            Object unmarshalled = unmarshaller.unmarshal(source.toFile());
            return bindingClass.cast(unmarshalled);
        } catch (JAXBException e) {
            String message = "Error when unmarshalling the XML configuration";
            if (log.isDebugEnabled()) {
                log.debug(message);
            }
            throw new ApplicationServerConfigurationException(message, e);
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
     * @throws ApplicationServerConfigurationException if an error occurred when creating the unmarshaller or
     *                                                 unmarshalling the XML source
     */
    public static <T> T getUnmarshalledObject(InputStream inputStream, Path schema, Class<T> bindingClass)
            throws ApplicationServerConfigurationException {
        try {
            Unmarshaller unmarshaller = getXMLUnmarshaller(schema, bindingClass);
            Object unmarshalled = unmarshaller.unmarshal(inputStream);
            return bindingClass.cast(unmarshalled);
        } catch (JAXBException e) {
            String message = "Error when unmarshalling the XML configuration";
            if (log.isDebugEnabled()) {
                log.debug(message);
            }
            throw new ApplicationServerConfigurationException(message, e);
        }
    }
}
