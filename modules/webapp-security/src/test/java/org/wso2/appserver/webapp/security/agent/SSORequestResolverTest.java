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
import org.wso2.appserver.configuration.context.WebAppSingleSignOn;
import org.wso2.appserver.webapp.security.Constants;
import org.wso2.appserver.webapp.security.TestConstants;
import org.wso2.appserver.webapp.security.utils.exception.SSOException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This is a class which defines unit test cases for single-sign-on (SSO) agent request resolver.
 *
 * @since 6.0.0
 */
public class SSORequestResolverTest {
    private WebAppSingleSignOn ssoConfiguration;

    @BeforeClass
    public void init() throws SSOException {
        ssoConfiguration = getDefaultWebAppSSOConfiguration();
    }

    @Test(description = "Tests for a request URI to be skipped")
    public void testSkipURIRequest() {
        //  mocks a Request object
        Request request = mock(Request.class);
        when(request.getRequestURI()).thenReturn(TestConstants.SKIP_URI_ONE);

        SSORequestResolver resolver = new SSORequestResolver(request, ssoConfiguration);
        Assert.assertTrue(resolver.isURLToSkip());
    }

    @Test(description = "Tests for a request URI which is not to be skipped")
    public void testNonSkipURIRequest() {
        //  mocks a Request object
        Request request = mock(Request.class);
        when(request.getRequestURI()).thenReturn(TestConstants.NON_SKIP_URI);

        SSORequestResolver resolver = new SSORequestResolver(request, ssoConfiguration);
        Assert.assertFalse(resolver.isURLToSkip());
    }

    @Test(description = "Tests whether the resolver recognizes the agent's HTTP binding type as HTTP-POST")
    public void testCheckForPOSTBinding() {
        //  mocks a Request object
        Request request = mock(Request.class);

        SSORequestResolver resolver = new SSORequestResolver(request, ssoConfiguration);
        Assert.assertTrue(resolver.isHttpPOSTBinding());
    }

    @Test(description = "Tests whether the SAMLResponse parameter of the request is set")
    public void testForSAMLResponse() {
        //  mocks a Request object
        Request request = mock(Request.class);
        when(request.getParameter(Constants.HTTP_POST_PARAM_SAML_RESPONSE)).thenReturn("content");

        SSORequestResolver resolver = new SSORequestResolver(request, ssoConfiguration);
        Assert.assertTrue(resolver.isSAML2SSOResponse());
    }

    @Test(description = "Tests whether the resolver recognizes the URL as a logout request")
    public void testForSAMLSLORequest() {
        //  mocks a Request object
        Request request = mock(Request.class);
        when(request.getRequestURI()).thenReturn(TestConstants.LOGOUT_REQ_URI);

        SSORequestResolver resolver = new SSORequestResolver(request, ssoConfiguration);
        Assert.assertTrue(resolver.isSLOURL());
    }

    @Test(description = "Tests whether the resolver recognizes the URL as a non-logout request")
    public void testForNonSAMLSLORequest() {
        //  mocks a Request object
        Request request = mock(Request.class);
        when(request.getRequestURI()).thenReturn(TestConstants.NON_LOGOUT_REQ_URI);

        SSORequestResolver resolver = new SSORequestResolver(request, ssoConfiguration);
        Assert.assertFalse(resolver.isSLOURL());
    }

    public static WebAppSingleSignOn getDefaultWebAppSSOConfiguration() {
        WebAppSingleSignOn configuration = new WebAppSingleSignOn();

        configuration.enableSSO(true);
        configuration.setHttpBinding(TestConstants.DEFAULT_HTTP_BINDING);
        configuration.setIssuerId(TestConstants.DEFAULT_SP_ENTITY_ID);
        configuration.setConsumerURL(TestConstants.DEFAULT_ACS_URL);
        configuration.setConsumerURLPostfix(TestConstants.DEFAULT_CONSUMER_URL_POSTFIX);

        List<String> skipURIs = new ArrayList<>();
        skipURIs.add(TestConstants.SKIP_URI_ONE);
        skipURIs.add(TestConstants.SKIP_URI_TWO);
        WebAppSingleSignOn.SkipURIs uris = new WebAppSingleSignOn.SkipURIs();
        uris.setSkipURIs(skipURIs);
        configuration.setSkipURIs(uris);

        configuration.setOptionalParams(TestConstants.DEFAULT_QUERY_PARAMS);
        configuration.enableSLO(true);
        configuration.setSLOURLPostfix(TestConstants.DEFAULT_SLO_URL_POSTFIX);
        configuration.enableAssertionEncryption(false);
        configuration.enableAssertionSigning(false);
        configuration.enableRequestSigning(false);
        configuration.enableResponseSigning(false);

        return configuration;
    }
}
