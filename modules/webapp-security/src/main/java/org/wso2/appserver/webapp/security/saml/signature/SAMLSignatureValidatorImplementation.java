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
package org.wso2.appserver.webapp.security.saml.signature;

import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.wso2.appserver.webapp.security.utils.exception.SSOException;

/**
 * This is a class which implements the XML Signature validator interface {@code SignatureValidator}.
 *
 * @since 6.0.0
 */
public class SAMLSignatureValidatorImplementation implements SignatureValidator {
    @Override
    public void validateSignature(Response response, Assertion assertion, SSOX509Credential ssox509Credential,
                                  boolean isResponseSigningEnabled, boolean isAssertionSigningEnabled)
            throws SSOException {
        if (isResponseSigningEnabled) {
            if (response.getSignature() == null) {
                throw new SSOException("SAML 2.0 Response signing is enabled, but signature element not found "
                        + "in SAML 2.0 Response element");
            } else {
                try {
                    org.opensaml.xmlsec.signature.support.SignatureValidator.validate(response.getSignature(),
                            new X509CredentialImplementation(ssox509Credential.getEntityCertificate()));
                } catch (SignatureException e) {
                    throw new SSOException("Signature validation failed for SAML 2.0 Response");
                }
            }
        }
        if (isAssertionSigningEnabled) {
            if (assertion.getSignature() == null) {
                throw new SSOException("SAML 2.0 Assertion signing is enabled, but signature element not found in "
                        + "SAML 2.0 Assertion element");
            } else {
                try {
                    org.opensaml.xmlsec.signature.support.SignatureValidator.validate(assertion.getSignature(),
                            new X509CredentialImplementation(ssox509Credential.getEntityCertificate()));
                } catch (SignatureException e) {
                    throw new SSOException("Signature validation failed for SAML 2.0 Assertion");
                }
            }
        }
    }
}
