package org.wso2.appserver.sample;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.wso2.appserver.configuration.context.AppServerWebAppConfiguration;
import org.wso2.appserver.configuration.listeners.ContextConfigurationLoader;
import org.wso2.appserver.configuration.listeners.ServerConfigurationLoader;
import org.wso2.appserver.configuration.server.ApplicationServerConfiguration;
import org.wso2.appserver.sample.utils.ContextConfigurationUtils;
import org.wso2.appserver.sample.utils.ServerConfigurationUtils;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletException;

/**
 * An Apache Tomcat Valve to be used as an artifact for integration tests.
 * <p>
 * This sample valve tests the functionality of the WSO2 specific server and context level descriptor loading.
 *
 * @since 6.0.0
 */
public class ConfigurationLoaderValve extends ValveBase {
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        ApplicationServerConfiguration expectedServerConfiguration = ServerConfigurationUtils.generateDefault();
        ApplicationServerConfiguration actualServerConfiguration = ServerConfigurationLoader.getServerConfiguration();
        boolean isServerConfigurationUniform = ServerConfigurationUtils.
                compare(actualServerConfiguration, expectedServerConfiguration);
        request.setAttribute("isServerConfigurationUniform", isServerConfigurationUniform);

        Optional<AppServerWebAppConfiguration> expectedWebAppConfiguration = ContextConfigurationLoader.
                getContextConfiguration(request.getContext());
        AppServerWebAppConfiguration actualWebAppConfiguration = ContextConfigurationUtils.prepareDefault();
        expectedWebAppConfiguration.ifPresent(configuration -> {
            boolean isContextConfigurationUniform = ContextConfigurationUtils.
                    compare(actualWebAppConfiguration, configuration);
            request.setAttribute("isContextConfigurationUniform", isContextConfigurationUniform);
        });

        getNext().invoke(request, response);
    }
}
