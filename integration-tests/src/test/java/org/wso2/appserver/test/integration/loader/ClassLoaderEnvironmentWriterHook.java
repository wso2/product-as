/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.appserver.test.integration.loader;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.appserver.test.integration.ServerStatusHook;
import org.wso2.appserver.test.integration.TestConstants;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ClassLoaderEnvironmentWriterHook implements ServerStatusHook {

    String originalRuntimePath = "";

    @Override
    public void beforeServerStart() throws Exception {
        updateClassLoaderRuntimePath("FakePath_S2E4GF5");
    }

    private void updateClassLoaderRuntimePath(String fakePath)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Path wso2ServerXML = Paths.get(System.getProperty(TestConstants.APPSERVER_HOME), "conf", "wso2", "wso2as.xml");

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().
                parse(new InputSource(wso2ServerXML.toString()));

        //  change to a fake class path
        NodeList classpaths = document.getElementsByTagName("Classpath");
        for (int i = 0; i < classpaths.getLength(); i++) {
            Node classpath = classpaths.item(i);
            originalRuntimePath = classpath.getTextContent();
            classpath.setTextContent(fakePath);
        }


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(wso2ServerXML.toFile().getPath()));

    }

    @Override
    public void afterServerStart() throws Exception {

    }

    @Override
    public void beforeServerShutdown() throws Exception {

    }

    @Override
    public void afterServerShutdown() throws Exception {
        updateClassLoaderRuntimePath(originalRuntimePath);
    }


}
