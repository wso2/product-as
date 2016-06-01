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
package org.wso2.appserver.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class is an extension of the {@code HttpServlet} class used for testing the validity of
 * system properties set.
 *
 * @since 6.0.0
 */
public class SystemPropertyValidator extends HttpServlet {
    private static final long serialVersionUID = -1973155411458311713L;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //  capture the system properties set
        captureSystemProperty(response, "javax.net.ssl.keyStore");
        captureSystemProperty(response, "javax.net.ssl.keyStorePassword");
        captureSystemProperty(response, "javax.net.ssl.keyStoreType");
        captureSystemProperty(response, "javax.net.ssl.trustStore");
        captureSystemProperty(response, "javax.net.ssl.trustStorePassword");
        captureSystemProperty(response, "javax.net.ssl.trustStoreType");

        response.setHeader("keyStoreFileAvailable", ((Boolean) fileExists("javax.net.ssl.keyStore")).toString());
        response.setHeader("trustStoreFileAvailable", ((Boolean) fileExists("javax.net.ssl.trustStore")).toString());
    }

    private static void captureSystemProperty(HttpServletResponse response, String systemPropertyKey) {
        response.setHeader(systemPropertyKey, System.getProperty(systemPropertyKey));
    }

    private static boolean fileExists(String systemPropertyKey) {
        return Files.exists(Paths.get(System.getProperty(systemPropertyKey)));
    }
}
