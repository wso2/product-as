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
package org.wso2.appserver.webapp.security.sso.bean;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

/**
 * This Java bean class which represents a SAML 2.0 RelayState token.
 *
 * @since 6.0.0
 */
public class RelayState implements Serializable {
    private static final long serialVersionUID = 7853678462461888093L;

    private String requestedURL;
    private String requestQueryString;
    private Map requestParameters;

    public String getRequestedURL() {
        return requestedURL;
    }

    public void setRequestedURL(String requestedURL) {
        this.requestedURL = requestedURL;
    }

    public Optional<String> getRequestQueryString() {
        return Optional.ofNullable(requestQueryString);
    }

    public void setRequestQueryString(String requestQueryString) {
        this.requestQueryString = requestQueryString;
    }

    public Optional<Map> getRequestParameters() {
        return Optional.ofNullable(requestParameters);
    }

    public void setRequestParameters(Map requestParameters) {
        this.requestParameters = requestParameters;
    }
}
