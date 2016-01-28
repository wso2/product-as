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

import java.io.File;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
/**
 * Contains XML utility methods based on JAXB.
 */
public class XMLUtils {


    /**
     *  Build a XML binding from a file.
     * @param xmlFile file object of the xml
     * @param bindingClass class to be bound
     * @param <T> type of the binding class
     * @return bound object (Type T) of the xml
     * @throws JAXBException if the binding fails
     */
    public static <T> T unmarshalJAXB(File xmlFile, Class<T> bindingClass) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(bindingClass);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Object xmlContents = jaxbUnmarshaller.unmarshal(xmlFile);
        return bindingClass.cast(xmlContents);

    }

    /**
     * Build a XML binding from input stream.
     * @param is input stream of the xml
     * @param bindingClass class to be bound
     * @param <T> type of the binding class
     * @return bound object (Type T) of the xml
     * @throws JAXBException if the binding fails
     */
    public static <T> T unmarshalJAXB(InputStream is, Class<T> bindingClass) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(bindingClass);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Object xmlContents = jaxbUnmarshaller.unmarshal(is);
        return bindingClass.cast(xmlContents);

    }

}
