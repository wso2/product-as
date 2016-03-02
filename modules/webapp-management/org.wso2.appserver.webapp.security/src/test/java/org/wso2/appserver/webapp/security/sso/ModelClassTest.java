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
package org.wso2.appserver.webapp.security.sso;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.appserver.webapp.security.sso.bean.RelayState;
import org.wso2.appserver.webapp.security.sso.utils.SSOAgentDataHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains test cases for all the model classes.
 */
public class ModelClassTest {
    @Test
    public void createRelayState() {
        RelayState relayState = new RelayState();
        String requestURL = "/sample/acs";
        relayState.setRequestedURL(requestURL);
        String queryString = "tenant=t1&dialect=saml";
        relayState.setRequestQueryString(queryString);
        Map<String, String> params = new HashMap<>();
        params.put("tenant", "t1");
        params.put("dialect", "SAML");
        relayState.setRequestParameters(params);

        Assert.assertTrue((relayState.getRequestedURL().equals(requestURL)) && (relayState.getRequestQueryString().get()
                .equals(queryString)) && (relayState.getRequestParameters().get().equals(params)));
    }

    @Test
    public void handleDataHolderTest() {
        SSOAgentDataHolder dataHolder = SSOAgentDataHolder.getInstance();
        String object = "This is a the data holder object within!!!";
        dataHolder.setObject(object);
        Assert.assertTrue(object.equals(SSOAgentDataHolder.getInstance().getObject()));
    }
}
