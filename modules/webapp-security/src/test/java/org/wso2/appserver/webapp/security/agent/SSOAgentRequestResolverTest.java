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
package org.wso2.appserver.webapp.security.agent;

import org.apache.catalina.connector.Request;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.configuration.server.AppServerSingleSignOn;
import org.wso2.appserver.webapp.security.Constants;
import org.wso2.appserver.webapp.security.TestConstants;
import org.wso2.appserver.webapp.security.TestUtils;
import org.wso2.appserver.webapp.security.saml.signature.SSOX509Credential;
import org.wso2.appserver.webapp.security.utils.exception.SSOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This is a class which defines unit test cases for single-sign-on (SSO) agent request resolver.
 *
 * @since 6.0.0
 */
public class SSOAgentRequestResolverTest {
    private SSOAgentConfiguration agentConfiguration;

    @BeforeClass
    public void init() throws SSOException {
        agentConfiguration = new SSOAgentConfiguration();

        AppServerSingleSignOn serverConfiguration = TestUtils.getDefaultServerSSOConfiguration();
        agentConfiguration.initialize(serverConfiguration, TestUtils.getDefaultWebAppSSOConfiguration());
        agentConfiguration.getSAML2().setSSOX509Credential(new SSOX509Credential(
                serverConfiguration.getIdpCertificateAlias(), TestUtils.getDefaultServerSecurityConfiguration()));

        agentConfiguration.validate();
    }

    @Test(description = "Tests for a request URI to be skipped")
    public void testSkipURIRequest() {
        //  mocks a Request object
        Request request = mock(Request.class);
        when(request.getRequestURI()).thenReturn(TestConstants.SKIP_URI_ONE);

        SSOAgentRequestResolver resolver = new SSOAgentRequestResolver(request, agentConfiguration);
        Assert.assertTrue(resolver.isURLToSkip());
    }

    @Test(description = "Tests for a request URI which is not to be skipped")
    public void testNonSkipURIRequest() {
        //  mocks a Request object
        Request request = mock(Request.class);
        when(request.getRequestURI()).thenReturn(TestConstants.NON_SKIP_URI);

        SSOAgentRequestResolver resolver = new SSOAgentRequestResolver(request, agentConfiguration);
        Assert.assertFalse(resolver.isURLToSkip());
    }

    @Test(description = "Tests whether the resolver recognizes the agent's HTTP binding type as HTTP-POST")
    public void testCheckForPOSTBinding() {
        //  mocks a Request object
        Request request = mock(Request.class);

        SSOAgentRequestResolver resolver = new SSOAgentRequestResolver(request, agentConfiguration);
        Assert.assertTrue(resolver.isHttpPOSTBinding());
    }

    @Test(description = "Tests whether the SAMLResponse parameter of the request is set")
    public void testForSAMLResponse() {
        //  mocks a Request object
        Request request = mock(Request.class);
        when(request.getParameter(Constants.HTTP_POST_PARAM_SAML_RESPONSE)).thenReturn("content");

        SSOAgentRequestResolver resolver = new SSOAgentRequestResolver(request, agentConfiguration);
        Assert.assertTrue(resolver.isSAML2SSOResponse());
    }

    @Test(description = "Tests whether the resolver recognizes the URL as a logout request")
    public void testForSAMLSLORequest() {
        //  mocks a Request object
        Request request = mock(Request.class);
        when(request.getRequestURI()).thenReturn(TestConstants.LOGOUT_REQ_URI);

        SSOAgentRequestResolver resolver = new SSOAgentRequestResolver(request, agentConfiguration);
        Assert.assertTrue(resolver.isSLOURL());
    }

    @Test(description = "Tests whether the resolver recognizes the URL as a non-logout request")
    public void testForNonSAMLSLORequest() {
        //  mocks a Request object
        Request request = mock(Request.class);
        when(request.getRequestURI()).thenReturn(TestConstants.NON_LOGOUT_REQ_URI);

        SSOAgentRequestResolver resolver = new SSOAgentRequestResolver(request, agentConfiguration);
        Assert.assertFalse(resolver.isSLOURL());
    }
}
