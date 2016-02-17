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
package org.wso2.appserver.webapp.security.sso.util;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * A custom implementation for resolving external XML entities.
 * <p>
 * This class implements the {@code org.xml.sax.EntityResolver} interface.
 *
 * @since 6.0.0
 */
public class XMLEntityResolver implements EntityResolver {
    /**
     * Allows the application to resolve external entities.
     *
     * @param publicId the public identifier of the external entity being referenced, or null if none was supplied
     * @param systemId the system identifier of the external entity being referenced
     * @return an InputSource object describing the new input source, or null to request that the parser open a regular
     * URI connection to the system identifier
     * @throws SAXException if XML syntax contains invalid elements
     * @throws IOException  an error occurred while creating a new InputStream or Reader for the InputSource
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        throw new SAXException("XML syntax contains invalid elements, possibly an XML External Entity (XXE) attack");
    }
}
