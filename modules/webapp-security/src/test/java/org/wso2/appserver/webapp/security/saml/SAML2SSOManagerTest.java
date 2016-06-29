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
package org.wso2.appserver.webapp.security.saml;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.configuration.context.AppServerWebAppConfiguration;
import org.wso2.appserver.configuration.listeners.ContextConfigurationLoader;
import org.wso2.appserver.configuration.listeners.ServerConfigurationLoader;
import org.wso2.appserver.webapp.security.Constants;
import org.wso2.appserver.webapp.security.TestConstants;
import org.wso2.appserver.webapp.security.utils.exception.SSOException;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This Java class defines unit tests for SAML 2.0 based SSO Valve.
 *
 * @since 6.0.0
 */
public class SAML2SSOManagerTest {
    private Engine engine;
    private Host host;
    private Context fooContext;
    private Context barContext;

    private SAML2SSOManager manager;

    @BeforeClass
    public void init() {
        System.setProperty(Globals.CATALINA_BASE_PROP, Paths.get(TestConstants.TEST_RESOURCES_LOCATION).toString());

        prepareCatalinaComponents();

        //  load the server level configurations
        ServerConfigurationLoader loader = new ServerConfigurationLoader();
        List<Lifecycle> components = new ArrayList<>();
        components.add(new StandardServer());
        components.add(engine);
        components.stream()
                .forEach(component -> loader.lifecycleEvent(
                        new LifecycleEvent(component, Lifecycle.BEFORE_START_EVENT, null)));

        //  load the context configurations
        ContextConfigurationLoader contextLoader = new ContextConfigurationLoader();
        contextLoader.lifecycleEvent(new LifecycleEvent(barContext, Lifecycle.BEFORE_START_EVENT, null));
        contextLoader.lifecycleEvent(new LifecycleEvent(fooContext, Lifecycle.BEFORE_START_EVENT, null));
    }

    @Test(description = "Tests handling a SAML 2.0 Authentication Request for HTTP-POST binding")
    public void testHandlingAuthRequestForPOSTBinding() throws SSOException {
        Optional<AppServerWebAppConfiguration> configuration =
                ContextConfigurationLoader.getContextConfiguration(fooContext);
        if (configuration.isPresent()) {
            manager = new SAML2SSOManager(configuration.get().getSingleSignOnConfiguration());

            Request request = mock(Request.class);
            when(request.getContextPath()).thenReturn("/" + TestConstants.FOO_CONTEXT);
            when(request.getHost()).thenReturn(host);
            request.setAttribute(Constants.IS_FORCE_AUTH_ENABLED, "false");
            request.setAttribute(Constants.IS_PASSIVE_AUTH_ENABLED, "true");

            String payload = manager.handleAuthenticationRequestForPOSTBinding(request);

            boolean checkRedirectURL = payload.contains("<p>You are now redirected back to " + TestConstants
                    .DEFAULT_IDP_URL);
            boolean checkActionURL = payload.contains("<form method='post' action='" + TestConstants
                    .DEFAULT_IDP_URL + "'");

            Assert.assertTrue(checkRedirectURL && checkActionURL);
        } else {
            Assert.fail();
        }
    }

    @Test(description = "Tests handling a SAML 2.0 Authentication Request for HTTP-Redirect binding")
    public void testHandlingAuthRequestForRedirectBinding() throws SSOException {
        Optional<AppServerWebAppConfiguration> configuration =
                ContextConfigurationLoader.getContextConfiguration(barContext);
        if (configuration.isPresent()) {
            manager = new SAML2SSOManager(configuration.get().getSingleSignOnConfiguration());

            Request request = mock(Request.class);
            when(request.getContextPath()).thenReturn("/" + TestConstants.BAR_CONTEXT);
            when(request.getHost()).thenReturn(host);
            request.setAttribute(Constants.IS_FORCE_AUTH_ENABLED, "false");
            request.setAttribute(Constants.IS_PASSIVE_AUTH_ENABLED, "true");

            String url = manager.handleAuthenticationRequestForRedirectBinding(request);
            Assert.assertTrue(url.startsWith(TestConstants.DEFAULT_IDP_URL));
        } else {
            Assert.fail();
        }
    }

    private void prepareCatalinaComponents() {
        engine = new StandardEngine();
        host = new StandardHost();
        fooContext = new StandardContext();
        barContext = new StandardContext();

        Connector connector = new Connector();
        connector.setProtocol(TestConstants.SSL_PROTOCOL);
        connector.setPort(TestConstants.SSL_PORT);
        connector.setScheme(TestConstants.SSL_PROTOCOL);

        Service service = new StandardService();
        engine.setService(service);
        engine.getService().addConnector(connector);

        host.setAppBase(TestConstants.WEB_APP_BASE);
        host.setName(TestConstants.DEFAULT_TOMCAT_HOST);
        host.setParent(engine);

        fooContext.setParent(host);
        fooContext.setDocBase(TestConstants.FOO_CONTEXT);
        barContext.setParent(host);
        barContext.setDocBase(TestConstants.BAR_CONTEXT);
    }
}
