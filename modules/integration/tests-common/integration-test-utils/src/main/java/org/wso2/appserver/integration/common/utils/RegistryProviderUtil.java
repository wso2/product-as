package org.wso2.appserver.integration.common.utils;


import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.configurations.UrlGenerationUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.engine.frameworkutils.TestFrameworkUtils;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.registry.app.RemoteRegistry;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.ws.client.registry.WSRegistryServiceClient;

import java.io.File;
import java.net.URL;

/**
 * Provide remote registries - ws-api, remote registry and governance registry
 */
public class RegistryProviderUtil {

    private static final Log log = LogFactory.getLog(RegistryProviderUtil.class);
    private static final int TIME_OUT_VALUE = 1000 * 60; //in milliseconds

    public WSRegistryServiceClient getWSRegistry (AutomationContext automationContext)
            throws Exception {

        System.setProperty("carbon.repo.write.mode", "true");
        WSRegistryServiceClient registry = null;
        ConfigurationContext configContext;
        String axis2Repo = FrameworkPathUtil.getSystemResourceLocation() + File.separator + "client";
        String axis2Conf = FrameworkPathUtil.getSystemResourceLocation() + "axis2config" +
                           File.separator + "axis2_client.xml";
        TestFrameworkUtils.setKeyStoreProperties(automationContext);
        try {
            configContext = ConfigurationContextFactory.
                    createConfigurationContextFromFileSystem(axis2Repo, axis2Conf);

            configContext.setProperty(HTTPConstants.CONNECTION_TIMEOUT, TIME_OUT_VALUE);

            log.info("Group ConfigurationContext Timeout " +
                     configContext.getServiceGroupContextTimeoutInterval());

            registry = new WSRegistryServiceClient(
                    automationContext.getContextUrls().getBackEndUrl(),
                    automationContext.getContextTenant().getContextUser().getUserName(),
                    automationContext.getContextTenant().getContextUser().getPassword(), configContext);

            log.info("WS Registry Created - Login Successful");

        } catch (Exception e) {
            handleException("Failed instantiate WSRegistry client instance ", e);
        }
        return registry;
    }

    public Registry getGovernanceRegistry (Registry registry, AutomationContext automationContext)
            throws Exception {

        Registry governance = null;
        TestFrameworkUtils.setKeyStoreProperties(automationContext);
        System.setProperty("carbon.repo.write.mode", "true");
        try {
            governance =
                    GovernanceUtils.getGovernanceUserRegistry(registry,
                                                              automationContext.getContextTenant().getContextUser().getUserName());

        } catch (Exception e) {
            handleException("Failed to instantiate governance registry instance ", e);
        }
        return governance;
    }

    public RemoteRegistry getRemoteRegistry (AutomationContext automationContext) throws Exception {

        RemoteRegistry registry = null;
        TestFrameworkUtils.setKeyStoreProperties(automationContext);
        System.setProperty("carbon.repo.write.mode", "true");
        try {
            registry = new RemoteRegistry(new URL(
                    UrlGenerationUtil.getRemoteRegistryURL(automationContext.getDefaultInstance())),
                                          automationContext.getContextTenant().getContextUser().getUserName(),
                                          automationContext.getContextTenant().getContextUser().getPassword());

        } catch (Exception e) {
            handleException("Failed to initialized remote registry instance ", e);
        }
        return registry;
    }

    private void handleException (String msg, Exception e) throws Exception {

        log.error(msg, e);
        throw new Exception(msg, e);
    }

}