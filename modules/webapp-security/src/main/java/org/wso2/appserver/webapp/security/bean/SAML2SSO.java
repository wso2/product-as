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
package org.wso2.appserver.webapp.security.bean;

import java.util.List;
import java.util.Map;

/**
 * Represents the SAML 2.0 specific single-sign-on (SSO) details to be held
 * in a user logged-in session.
 */
public class SAML2SSO {

    private String subjectId;
    private String responseString;
    private String assertionString;
    private String sessionIndex;
    private Map<String, List<String>> subjectAttributes;

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public Map getSubjectAttributes() {
        return subjectAttributes;
    }

    public void setSubjectAttributes(Map<String, List<String>> samlSSOAttributes) {
        this.subjectAttributes = samlSSOAttributes;
    }

    public String getSessionIndex() {
        return sessionIndex;
    }

    public void setSessionIndex(String sessionIndex) {
        this.sessionIndex = sessionIndex;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public void setAssertionString(String samlAssertionString) {
        this.assertionString = samlAssertionString;
    }

    public String getAssertionString() {
        return assertionString;
    }
}
