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
package org.wso2.appserver.jibx.library.beans;

/**
 * Jbix related heler class
 */
public class Book {
    private String m_type;
    private String m_isbn;
    private String m_title;
    private String[] m_authors;

    public Book(String m_type, String m_isbn, String m_title, String[] m_authors) {
        if (m_isbn == null || m_isbn.length() == 0) {
            throw new RuntimeException("books isbn cannot be empty or null");
        }
        this.m_type = m_type;
        this.m_isbn = m_isbn;
        this.m_title = m_title;
        this.m_authors = m_authors;
    }

    public Book() {
    }

    public String getType() {
        return m_type;
    }

    public String getIsbn() {
        return m_isbn;
    }

    public String getTitle() {
        return m_title;
    }

    public String[] getAuthors() {
        return m_authors;
    }
}
