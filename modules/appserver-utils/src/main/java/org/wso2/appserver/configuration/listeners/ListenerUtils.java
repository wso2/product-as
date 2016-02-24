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
package org.wso2.appserver.configuration.listeners;

import org.wso2.appserver.configuration.context.ContextConfiguration;
import org.wso2.appserver.configuration.context.SSOConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A Java class which defines the utility methods for merging the context level configurations.
 *
 * @since 6.0.0
 */
public class ListenerUtils {
    /**
     * Merges the globally defined context level configurations and context level configurations overridden at
     * context level.
     *
     * @param globalConfiguration the globally defined context level configurations
     * @param localConfiguration  the locally overridden context level configurations
     * @return the merged context level configurations
     */
    protected static ContextConfiguration merge(ContextConfiguration globalConfiguration,
            ContextConfiguration localConfiguration) {
        //  Prepare the effective final context configuration
        ContextConfiguration effective = new ContextConfiguration();
        SSOConfiguration ssoConfiguration = null;
        if ((globalConfiguration != null) && (localConfiguration != null)) {
            ssoConfiguration = mergeSSOConfigurations(globalConfiguration.getSingleSignOnConfiguration(),
                    localConfiguration.getSingleSignOnConfiguration());
        } else if (globalConfiguration != null) {
            ssoConfiguration = mergeSSOConfigurations(globalConfiguration.getSingleSignOnConfiguration(), null);
        }
        effective.setSingleSignOnConfiguration(ssoConfiguration);
        return effective;
    }

    /**
     * Merges the context level single-sign-on (SSO) configurations defined globally and overridden at
     * context level (if any).
     *
     * @param global the globally defined SSO configurations
     * @param local  the SSO configurations defined at context level
     * @return the merged effective group of SSO configurations
     */
    private static SSOConfiguration mergeSSOConfigurations(SSOConfiguration global, SSOConfiguration local) {
        SSOConfiguration effective = new SSOConfiguration();

        if ((global != null) && (local != null)) {
            if ((global.getSkipURIs() != null) && (local.getSkipURIs() != null)) {
                effective.setSkipURIs(local.getSkipURIs());
            } else if (global.getSkipURIs() != null) {
                effective.setSkipURIs(global.getSkipURIs());
            }

            if ((global.handleConsumerURLAfterSLO() != null) && (local.handleConsumerURLAfterSLO() != null)) {
                effective.enableHandlingConsumerURLAfterSLO(local.handleConsumerURLAfterSLO());
            } else if (global.handleConsumerURLAfterSLO() != null) {
                effective.enableHandlingConsumerURLAfterSLO(global.handleConsumerURLAfterSLO());
            }

            if ((global.getQueryParams() != null) && (local.getQueryParams() != null)) {
                effective.setQueryParams(local.getQueryParams());
            } else if (global.getQueryParams() != null) {
                effective.setQueryParams(global.getQueryParams());
            }

            if ((global.getApplicationServerURL() != null) && (local.getApplicationServerURL() != null)) {
                effective.setApplicationServerURL(local.getApplicationServerURL());
            } else if (global.getApplicationServerURL() != null) {
                effective.setApplicationServerURL(global.getApplicationServerURL());
            }

            List<SSOConfiguration.Property> properties = prioritizeProperties(global.getProperties(),
                    local.getProperties());
            if (properties.isEmpty()) {
                effective.setProperties(null);
            } else {
                effective.setProperties(properties);
            }

            if ((global.getSAML() != null) && (local.getSAML() != null)) {
                effective.setSAML(new SSOConfiguration.SAML());
                SSOConfiguration.SAML globalSAML = global.getSAML();
                SSOConfiguration.SAML localSAML = local.getSAML();

                if ((globalSAML.isSSOEnabled() != null) && (localSAML.isSSOEnabled() != null)) {
                    effective.getSAML().enableSSO(localSAML.isSSOEnabled());
                } else if (globalSAML.isSSOEnabled() != null) {
                    effective.getSAML().enableSSO(globalSAML.isSSOEnabled());
                }

                if ((globalSAML.getRequestURLPostFix() != null) && (localSAML.getRequestURLPostFix() != null)) {
                    effective.getSAML().setRequestURLPostFix(localSAML.getRequestURLPostFix());
                } else if (globalSAML.getRequestURLPostFix() != null) {
                    effective.getSAML().setRequestURLPostFix(globalSAML.getRequestURLPostFix());
                }

                if ((globalSAML.getHttpBinding() != null) && (localSAML.getHttpBinding() != null)) {
                    effective.getSAML().setHttpBinding(localSAML.getHttpBinding());
                } else if (globalSAML.getHttpBinding() != null) {
                    effective.getSAML().setHttpBinding(globalSAML.getHttpBinding());
                }

                if ((globalSAML.getIssuerId() != null) && (localSAML.getIssuerId() != null)) {
                    effective.getSAML().setIssuerId(localSAML.getIssuerId());
                } else if (localSAML.getIssuerId() != null) {
                    effective.getSAML().setIssuerId(localSAML.getIssuerId());
                }

                if ((globalSAML.getConsumerURL() != null) && (localSAML.getConsumerURL() != null)) {
                    effective.getSAML().setConsumerURL(localSAML.getConsumerURL());
                } else if (localSAML.getConsumerURL() != null) {
                    effective.getSAML().setConsumerURL(localSAML.getConsumerURL());
                }

                if ((globalSAML.getConsumerURLPostFix() != null) && (localSAML.getConsumerURLPostFix() != null)) {
                    effective.getSAML().setConsumerURLPostFix(localSAML.getConsumerURLPostFix());
                } else if (globalSAML.getConsumerURLPostFix() != null) {
                    effective.getSAML().setConsumerURLPostFix(globalSAML.getConsumerURLPostFix());
                }

                if ((globalSAML.getAttributeConsumingServiceIndex() != null) && (
                        localSAML.getAttributeConsumingServiceIndex() != null)) {
                    effective.getSAML().
                            setAttributeConsumingServiceIndex(localSAML.getAttributeConsumingServiceIndex());
                } else if (globalSAML.getAttributeConsumingServiceIndex() != null) {
                    effective.getSAML().
                            setAttributeConsumingServiceIndex(globalSAML.getAttributeConsumingServiceIndex());
                }

                if ((globalSAML.isSLOEnabled() != null) && (localSAML.isSLOEnabled() != null)) {
                    effective.getSAML().enableSLO(localSAML.isSLOEnabled());
                } else if (globalSAML.isSLOEnabled() != null) {
                    effective.getSAML().enableSLO(globalSAML.isSLOEnabled());
                }

                if ((globalSAML.getSLOURLPostFix() != null) && (localSAML.getSLOURLPostFix() != null)) {
                    effective.getSAML().setSLOURLPostFix(localSAML.getSLOURLPostFix());
                } else if (globalSAML.getSLOURLPostFix() != null) {
                    effective.getSAML().setSLOURLPostFix(globalSAML.getSLOURLPostFix());
                }

                if ((globalSAML.isAssertionEncryptionEnabled() != null) && (localSAML.isAssertionEncryptionEnabled()
                        != null)) {
                    effective.getSAML().enableAssertionEncryption(localSAML.isAssertionEncryptionEnabled());
                } else if (globalSAML.isAssertionEncryptionEnabled() != null) {
                    effective.getSAML().enableAssertionEncryption(globalSAML.isAssertionEncryptionEnabled());
                }

                if ((globalSAML.isAssertionSigningEnabled() != null) && (localSAML.isAssertionSigningEnabled()
                        != null)) {
                    effective.getSAML().enableAssertionSigning(localSAML.isAssertionSigningEnabled());
                } else if (globalSAML.isAssertionSigningEnabled() != null) {
                    effective.getSAML().enableAssertionSigning(globalSAML.isAssertionSigningEnabled());
                }

                if ((globalSAML.isRequestSigningEnabled() != null) && (localSAML.isRequestSigningEnabled() != null)) {
                    effective.getSAML().enableRequestSigning(localSAML.isRequestSigningEnabled());
                } else if (globalSAML.isRequestSigningEnabled() != null) {
                    effective.getSAML().enableRequestSigning(globalSAML.isRequestSigningEnabled());
                }

                if ((globalSAML.isResponseSigningEnabled() != null) && (localSAML.isResponseSigningEnabled() != null)) {
                    effective.getSAML().enableResponseSigning(localSAML.isResponseSigningEnabled());
                } else if (globalSAML.isResponseSigningEnabled() != null) {
                    effective.getSAML().enableResponseSigning(globalSAML.isResponseSigningEnabled());
                }

                if ((globalSAML.isForceAuthnEnabled() != null) && (localSAML.isForceAuthnEnabled() != null)) {
                    effective.getSAML().enableForceAuthn(localSAML.isForceAuthnEnabled());
                } else if (globalSAML.isForceAuthnEnabled() != null) {
                    effective.getSAML().enableForceAuthn(globalSAML.isForceAuthnEnabled());
                }

                if ((globalSAML.isPassiveAuthnEnabled() != null) && (localSAML.isPassiveAuthnEnabled() != null)) {
                    effective.getSAML().enablePassiveAuthn(localSAML.isPassiveAuthnEnabled());
                } else if (globalSAML.isPassiveAuthnEnabled() != null) {
                    effective.getSAML().enablePassiveAuthn(localSAML.isPassiveAuthnEnabled());
                }

                List<SSOConfiguration.SAML.SAMLProperty> samlProperties = prioritizeSAMLProperties(
                        globalSAML.getProperties(), localSAML.getProperties());
                if (samlProperties.isEmpty()) {
                    effective.getSAML().setProperties(null);
                } else {
                    effective.getSAML().setProperties(samlProperties);
                }
            } else if (global.getSAML() != null) {
                effective.setSAML(global.getSAML());
            }
        } else if (global != null) {
            effective = global;
        }
        return effective;
    }

    /**
     * Prioritizes the additional webapp descriptor properties.
     *
     * @param global the globally defined set of additional SSO properties
     * @param local  the set of additional SSO properties defined at context level
     * @return the final, effective set of webapp descriptor additional SSO properties
     */
    private static List<SSOConfiguration.Property> prioritizeProperties(List<SSOConfiguration.Property> global,
            List<SSOConfiguration.Property> local) {
        List<SSOConfiguration.Property> effective = new ArrayList<>();
        if ((global != null) && (local != null)) {
            global.stream().forEach(property -> {
                Optional<SSOConfiguration.Property> matching = getProperty(property.getKey(), local);
                if (matching.isPresent()) {
                    effective.add(matching.get());
                } else {
                    effective.add(property);
                }
            });
        } else if (global != null) {
            global.stream().forEach(effective::add);
        }
        return effective;
    }

    /**
     * Prioritizes the additional SAML specific webapp descriptor properties.
     *
     * @param global the globally defined set of additional SAML SSO properties
     * @param local  the set of additional SAML SSO properties defined at context level
     * @return the final, effective set of webapp descriptor additional SAML SSO properties
     */
    private static List<SSOConfiguration.SAML.SAMLProperty> prioritizeSAMLProperties(
            List<SSOConfiguration.SAML.SAMLProperty> global, List<SSOConfiguration.SAML.SAMLProperty> local) {
        List<SSOConfiguration.SAML.SAMLProperty> effective = new ArrayList<>();
        if ((global != null) && (local != null)) {
            global.stream().forEach(property -> {
                Optional<SSOConfiguration.SAML.SAMLProperty> matching = getSAMLProperty(property.getKey(), local);
                if (matching.isPresent()) {
                    effective.add(matching.get());
                } else {
                    effective.add(property);
                }
            });
        } else if (global != null) {
            global.stream().forEach(effective::add);
        }
        return effective;
    }

    /**
     * Returns an additional {@code Property} if exists in the list of properties.
     *
     * @param key  the key of the property to be checked
     * @param list the list of properties
     * @return the SSO property if exists
     */
    private static Optional<SSOConfiguration.Property> getProperty(String key, List<SSOConfiguration.Property> list) {
        if (key == null) {
            return Optional.empty();
        }
        if (list != null) {
            return list.stream().
                    filter(property -> property.getKey().equals(key)).findFirst();
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns an additional {@code SAMLProperty} if exists in the list of properties.
     *
     * @param key  the key of the property to be checked
     * @param list the list of properties
     * @return the SAML SSO property if exists
     */
    private static Optional<SSOConfiguration.SAML.SAMLProperty> getSAMLProperty(String key,
            List<SSOConfiguration.SAML.SAMLProperty> list) {
        if (key == null) {
            return Optional.empty();
        }
        if (list != null) {
            return list.stream().
                    filter(property -> property.getKey().equals(key)).findFirst();
        } else {
            return Optional.empty();
        }
    }
}
