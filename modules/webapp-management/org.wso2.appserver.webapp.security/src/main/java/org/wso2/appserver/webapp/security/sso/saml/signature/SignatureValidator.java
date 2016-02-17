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
package org.wso2.appserver.webapp.security.sso.saml.signature;

import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Response;
import org.wso2.appserver.webapp.security.sso.agent.SSOAgentConfiguration;
import org.wso2.appserver.webapp.security.sso.util.SSOException;

/**
 * This interface defines the common function(s) for an XML Signature validator.
 *
 * @since 6.0.0
 */
public interface SignatureValidator {
    /**
     * Validates an XML Digital Signature based on its content.
     *
     * @param response              a SAML 2.0 based Response
     * @param assertion             a SAML 2.0 based Assertion
     * @param ssoAgentConfiguration a {@link SSOAgentConfiguration} instance
     * @throws SSOException if an error occurs during signature validation
     */
    void validateSignature(Response response, Assertion assertion, SSOAgentConfiguration ssoAgentConfiguration)
            throws SSOException;
}
