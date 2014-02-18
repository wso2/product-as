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

public interface LibraryManager {

    /**
     * get ISBN values of the books in the library
     * @return list of ISBN values
     */
    public Object[] getISBNValues();

    /**
     *  get List of books in library
     * @return list of books
     */
    public Object[] getBooks();

    /**
     *  seatch books with ISBN value
     * @param ISBN ISBN value
     * @return true if found
     */
    public boolean searchBookISBN(String ISBN);

    /**
     *  add a book into the library
     * @param book - book to add
     * @return true if successfully added.
     */
    public boolean addBook(Book book);
}
