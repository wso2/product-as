/*
 * Copyright (c) 2005-2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.appserver.webapp.loader.util;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
/**
 * Contains XML utility methods based on DOM.
 */
public class XMLUtils {


    /**
     * Build a XML Document from a file
     *
     * @param xmlFile file
     * @return a Document object
     * @throws IOException,ParserConfigurationException,SAXException if an error occurs
     */
//    public static Document buildDocumentFromFile(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
//
//        Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
//        xmlDoc.getDocumentElement().normalize();
//        return xmlDoc;
//
//    }

    /**
     * Build a XML Document from a InputStream.
     *
     * @param // InputStream
     * @return a Document object
     * @throws IOException,ParserConfigurationException,SAXException if an error occurs
     */
//    public static Document buildDocumentFromInputStream(InputStream is) throws ParserConfigurationException, IOException, SAXException {
//        Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
//        xmlDoc.getDocumentElement().normalize();
//        return xmlDoc;
//    }


    public static <T> T JAXBUnmarshal(File xmlFile, Class<T> bindingClass) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(bindingClass);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Object xmlContents = jaxbUnmarshaller.unmarshal(xmlFile);
        return bindingClass.cast(xmlContents);

    }

    public static <T> T JAXBUnmarshal(InputStream is, Class<T> bindingClass) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(bindingClass);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Object xmlContents = jaxbUnmarshaller.unmarshal(is);
        return bindingClass.cast(xmlContents);

    }

}
