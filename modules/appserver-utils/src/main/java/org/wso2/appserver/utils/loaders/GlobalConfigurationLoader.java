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
package org.wso2.appserver.utils.loaders;

import org.wso2.appserver.utils.AppServerException;
import org.wso2.appserver.utils.GenericUtils;
import org.wso2.appserver.utils.paths.PathUtils;
import org.wso2.appserver.utils.model.Configuration;

import java.nio.file.Path;
import java.util.Optional;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * A Java class which loads the content of the global WSO2 specific configurations file.
 * <p>
 * All instances of this class will read and load the content of the WSO2 specific configurations
 * file, only once. Adapts lazy loading.
 *
 * @since 6.0.0
 */
public class GlobalConfigurationLoader {
    private static Configuration globalConfiguration;

    public static Configuration getGlobalConfiguration() throws AppServerException {
        if (globalConfiguration == null) {
            setGlobalConfiguration();
        }
        return globalConfiguration;
    }

    private static synchronized void setGlobalConfiguration() throws AppServerException {
        Optional<Path> schemaPath = Optional.of(PathUtils.getWSO2WebAppDescriptorSchema());
        Unmarshaller unmarshaller = GenericUtils.getXMLUnmarshaller(schemaPath, Configuration.class);
        try {
            globalConfiguration = (Configuration) unmarshaller.
                    unmarshal(PathUtils.getGlobalWSO2WebAppDescriptor().toFile());
        } catch (JAXBException e) {
            throw new AppServerException("An error has occurred during unmarshalling XML data", e);
        }
    }
}
