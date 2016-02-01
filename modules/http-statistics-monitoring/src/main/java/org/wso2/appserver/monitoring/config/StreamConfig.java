package org.wso2.appserver.monitoring.config;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by nathasha on 12/15/15.
 */

/**
 * Parse XML document to retrieve Event Stream name and version defined in Data Analytics Server.
 */
public class StreamConfig {

    private static String streamName;
    private static String streamVersion;
    Document doc;

    /**
     *
     * @throws InvalidXMLConfiguration
     * @throws IOException
     * @throws SAXException
     */
    public StreamConfig() throws InvalidXMLConfiguration {
        /*exit the current directory*/
        File userDir = new File(System.getProperty("catalina.home"));
        String parentDir = userDir.getAbsolutePath();
//        String parentDir = userDir.getAbsoluteFile().getParent();

        File xmlFile = new File(parentDir + "/conf/valveConfig.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        }  catch (ParserConfigurationException e) {
            throw new InvalidXMLConfiguration("Error while creating dBuilder", e);
        }

        try {
            doc = dBuilder.parse(xmlFile);
        } catch (SAXException e) {
            throw new InvalidXMLConfiguration("Parsing failed", e);
        } catch (IOException e) {
            throw new InvalidXMLConfiguration("Parsing failed", e);
        }

        doc.getDocumentElement().normalize();
    }

    /**
     * Parse Event Stream name from XML document.
     *
     * @return the name of the Event Stream
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public String getStreamName() {

        NodeList nList = doc.getElementsByTagName("stream");
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                streamName = eElement.getElementsByTagName("streamName").item(0).getTextContent();

            }
        }
        return streamName;
    }

    /**
     * Parse Event Stream version from XML document.
     *
     * @return the version of the Event Stream
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public String getStreamVersion() {

        NodeList nList = doc.getElementsByTagName("stream");
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                streamVersion = eElement.getElementsByTagName("version").item(0).getTextContent();

            }
        }
        return streamVersion;
    }
}
