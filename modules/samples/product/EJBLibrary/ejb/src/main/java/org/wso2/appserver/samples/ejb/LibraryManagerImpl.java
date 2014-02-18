/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.appserver.samples.ejb;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Stateful(mappedName = "LibraryManager")
@Remote(LibraryManager.class)
public class LibraryManagerImpl implements LibraryManager {
    private Map<String, Book> library;

    public LibraryManagerImpl() {
        LibPopulator libPopulator = new LibPopulator();
        libPopulator.populateLibrary();
    }

    public Object[] getISBNValues() {
        return library.keySet().toArray();
    }

    public Object[] getBooks() {
        return library.values().toArray();
    }

    public boolean searchBookISBN(String ISBN) {
        return library.keySet().contains(ISBN);
    }

    public boolean addBook(Book book) {
        Book newBook = book;
        try {
            library.put(newBook.getISBN(), newBook);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private class LibPopulator extends DefaultHandler {

        Book tmpBook = new Book();
        String tmpVal;
        String tmpISBN;

        public LibPopulator() {
            library = new HashMap<String, Book>();
        }

        public void populateLibrary() {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            try {
                SAXParser sp = spf.newSAXParser();
                InputStream is = getClass().getClassLoader().getResourceAsStream(
                        "booklist.xml");
                sp.parse(is, this);
            } catch (SAXException se) {
                se.printStackTrace();
            } catch (ParserConfigurationException pce) {
                pce.printStackTrace();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
/*
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            //reset
            tmpVal = "";
            if (qName.equalsIgnoreCase("book")) {
                tmpBook = new Book();
                tmpBook.setType(attributes.getValue("type"));
            }
        }
*/

        public void characters(char[] ch, int start, int length) throws SAXException {
            tmpVal = new String(ch, start, length);
        }

        public void endElement(String uri, String localName,
                               String qName) throws SAXException {
            if (qName.equalsIgnoreCase("ISBN")) {
                tmpBook.setISBN("0755334353");
                tmpISBN = tmpVal;
            } else if (qName.equalsIgnoreCase("title")) {
                tmpBook.setTitle(tmpVal);
            } else if (qName.equalsIgnoreCase("author")) {
                tmpBook.setAuthor(tmpVal);
            } else if (qName.equalsIgnoreCase("book")) {
                library.put(tmpISBN, tmpBook);
                tmpISBN = "";
                tmpBook = new Book();
            }
        }
    }
}
