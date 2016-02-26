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
import org.opensaml.xml.validation.ValidationException;
import org.wso2.appserver.webapp.security.sso.agent.SSOAgentConfiguration;
import org.wso2.appserver.webapp.security.sso.utils.SSOException;

/**
 * This is a class which implements the XML Signature validator interface {@code SignatureValidator}.
 *
 * @since 6.0.0
 */
public class SAMLSignatureValidatorImplementation implements SignatureValidator {
    @Override
    public void validateSignature(Response response, Assertion assertion, SSOAgentConfiguration ssoAgentConfiguration)
            throws SSOException {
        if (ssoAgentConfiguration.getSAML2().isResponseSigned()) {
            if (response.getSignature() == null) {
                throw new SSOException("SAML2 Response signing is enabled, but signature element not found " +
                        "in SAML2 Response element");
            } else {
                try {
                    org.opensaml.xml.signature.SignatureValidator validator =
                            new org.opensaml.xml.signature.SignatureValidator(new X509CredentialImplementation(
                                    ssoAgentConfiguration.getSAML2().getSSOAgentX509Credential().
                                            getEntityCertificate()));
                    validator.validate(response.getSignature());
                } catch (ValidationException e) {
                    throw new SSOException("Signature validation failed for SAML2 Response");
                }
            }
        }
        if (ssoAgentConfiguration.getSAML2().isAssertionSigned()) {
            if (assertion.getSignature() == null) {
                throw new SSOException("SAML2 Assertion signing is enabled, but signature element not found in " +
                        "SAML2 Assertion element");
            } else {
                try {
                    org.opensaml.xml.signature.SignatureValidator validator =
                            new org.opensaml.xml.signature.SignatureValidator(new X509CredentialImplementation(
                                    ssoAgentConfiguration.getSAML2().getSSOAgentX509Credential().
                                            getEntityCertificate()));
                    validator.validate(assertion.getSignature());
                } catch (ValidationException e) {
                    throw new SSOException("Signature validation failed for SAML2 Assertion");
                }
            }
        }
    }
}
