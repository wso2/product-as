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
package org.wso2.appserver.jibx.services;

import org.wso2.appserver.jibx.library.beans.Book;

import java.util.Hashtable;

/**
 * Jibx service implementation
 */
public class LibraryService implements LibraryServiceSkeletonInterface {

    /**
     * library will act as an simple real life library. This will hold Book objects. key will be the
     * isbn
     */
    private static Hashtable<String, Book> library = new Hashtable<String, Book>();


    public Book getBook(String isbn) {
        Book book = library.get(isbn);
        if (book == null) {
            book = new Book("Empty_Type", "Empty_ISBN", "Empty_Title",
                            new String[]{"Empty_Authors"});
        }
        return book;
    }


    public boolean addBook(String type, String isbn, String[] authors, String title) {
        if (library.containsKey(isbn)) {
            return false;
        }
        library.put(isbn, new Book(type, isbn, title, authors));
        return true;
    }
}
