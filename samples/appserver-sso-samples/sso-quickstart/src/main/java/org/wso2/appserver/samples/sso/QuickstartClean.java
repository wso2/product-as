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
package org.wso2.appserver.samples.sso;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * This class is used for clean up the changes made by quick start guide.
 */
public class QuickstartClean {

    private static final Log log;

    static {
        System.setProperty("org.apache.juli.formatter", "org.apache.juli.VerbatimFormatter");
        log = LogFactory.getLog(Quickstart.class);
    }

    private Path wso2asPath = Paths.get("..", "..");

    public static void main(String[] args) {
        new QuickstartClean().clean();
    }

    public void clean() {
        try {
            log.info("Reverting Changes....");

            Path serverxmlOriginalSrc = Paths.get("configfiles", "originals", "wso2as", "server.xml");
            Path wso2aswebxmlOriginalSrc = Paths.get("configfiles", "originals", "wso2as", "wso2as-web.xml");

            Path serverxmlOriginalDest = wso2asPath.resolve("conf").resolve("server.xml");
            Path wso2aswebxmlOriginalDest = wso2asPath.resolve("conf").resolve("wso2").resolve("wso2as-web.xml");

            //revert the changes made during the sample
            if (Files.exists(serverxmlOriginalSrc)) {
                Files.move(serverxmlOriginalSrc, serverxmlOriginalDest, StandardCopyOption.ATOMIC_MOVE);
            }
            if (Files.exists(wso2aswebxmlOriginalSrc)) {
                Files.move(wso2aswebxmlOriginalSrc, wso2aswebxmlOriginalDest, StandardCopyOption.ATOMIC_MOVE);
            }

            Path musicStoreApp = wso2asPath.resolve("webapps").resolve("musicstore-app.war");
            Path bookStoreApp = wso2asPath.resolve("webapps").resolve("bookstore-app.war");
            Path musicStoreAppDir = wso2asPath.resolve("webapps").resolve("musicstore-app");
            Path bookStoreAppDir = wso2asPath.resolve("webapps").resolve("bookstore-app");

            Files.deleteIfExists(musicStoreApp);
            Files.deleteIfExists(bookStoreApp);
            Files.deleteIfExists(musicStoreAppDir);
            Files.deleteIfExists(bookStoreAppDir);

            log.info("Successfully reverted the changes.");
        } catch (IOException e) {
            log.warn("Error while reverting changes." + e.getMessage(), e);
        }
    }
}
