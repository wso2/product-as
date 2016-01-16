/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 *
 */

package org.wso2.appserver.webapp.loader.util;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<String> generateClasspath(String classPath) {

        List<String> urlStr = new ArrayList<>();
        String realClassPath = StrSubstitutor.replaceSystemProperties(classPath);
        File classPathUrl = new File(realClassPath);

        if (classPathUrl.isFile()) {
            urlStr.add(classPathUrl.toURI().toString());
            return urlStr;
        }

        FileUtils.listFiles(classPathUrl, new String[]{"jar"}, false)
                .forEach((file) -> urlStr.add(file.toURI().toString()));

        return urlStr;
    }
}
