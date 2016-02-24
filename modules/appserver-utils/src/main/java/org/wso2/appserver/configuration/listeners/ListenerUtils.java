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
        ContextConfiguration.ClassloadingConfiguration classloading = null;
        SSOConfiguration ssoConfiguration = null;
        if ((globalConfiguration != null) && (localConfiguration != null)) {
            classloading = mergeClassloading(globalConfiguration.getClassloadingConfiguration(),
                    localConfiguration.getClassloadingConfiguration());
            ssoConfiguration = mergeSSOConfigurations(globalConfiguration.getSingleSignOnConfiguration(),
                    localConfiguration.getSingleSignOnConfiguration());
        } else if (globalConfiguration != null) {
            classloading = mergeClassloading(globalConfiguration.getClassloadingConfiguration(), null);
            ssoConfiguration = mergeSSOConfigurations(globalConfiguration.getSingleSignOnConfiguration(), null);
        }
        effective.setClassloadingConfiguration(classloading);
        effective.setSingleSignOnConfiguration(ssoConfiguration);
        return effective;
    }

    /**
     * Merges the context level classloading configurations defined globally and overridden at
     * context level (if any).
     *
     * @param global the globally defined classloading configurations
     * @param local  the classloading configurations defined at context level
     * @return the merged effective group of classloading configurations
     */
    private static ContextConfiguration.ClassloadingConfiguration mergeClassloading(
            ContextConfiguration.ClassloadingConfiguration global,
            ContextConfiguration.ClassloadingConfiguration local) {
        ContextConfiguration.ClassloadingConfiguration effective = new ContextConfiguration.ClassloadingConfiguration();

        if ((global != null) && (local != null)) {
            if ((global.getIsParentFirst() != null) && (local.getIsParentFirst() != null)) {
                effective.setIsParentFirst(local.getIsParentFirst());
            } else if (global.getIsParentFirst() != null) {
                effective.setIsParentFirst(global.getIsParentFirst());
            }

            if ((global.getEnvironments() != null) && (local.getEnvironments() != null)) {
                effective.setEnvironments(local.getEnvironments());
            } else if (global.getEnvironments() != null) {
                effective.setEnvironments(global.getEnvironments());
            }
        } else if (global != null) {
            effective = global;
        }
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

            if ((global.isSSOEnabled() != null) && (local.isSSOEnabled() != null)) {
                effective.enableSSO(local.isSSOEnabled());
            } else if (global.isSSOEnabled() != null) {
                effective.enableSSO(global.isSSOEnabled());
            }

            if ((global.getRequestURLPostFix() != null) && (local.getRequestURLPostFix() != null)) {
                effective.setRequestURLPostFix(local.getRequestURLPostFix());
            } else if (global.getRequestURLPostFix() != null) {
                effective.setRequestURLPostFix(global.getRequestURLPostFix());
            }

            if ((global.getHttpBinding() != null) && (local.getHttpBinding() != null)) {
                effective.setHttpBinding(local.getHttpBinding());
            } else if (global.getHttpBinding() != null) {
                effective.setHttpBinding(global.getHttpBinding());
            }

            if ((global.getIssuerId() != null) && (local.getIssuerId() != null)) {
                effective.setIssuerId(local.getIssuerId());
            } else if (local.getIssuerId() != null) {
                effective.setIssuerId(local.getIssuerId());
            }

            if ((global.getConsumerURL() != null) && (local.getConsumerURL() != null)) {
                effective.setConsumerURL(local.getConsumerURL());
            } else if (local.getConsumerURL() != null) {
                effective.setConsumerURL(local.getConsumerURL());
            }

            if ((global.getConsumerURLPostFix() != null) && (local.getConsumerURLPostFix() != null)) {
                effective.setConsumerURLPostFix(local.getConsumerURLPostFix());
            } else if (global.getConsumerURLPostFix() != null) {
                effective.setConsumerURLPostFix(global.getConsumerURLPostFix());
            }

            if ((global.getAttributeConsumingServiceIndex() != null) && (local.getAttributeConsumingServiceIndex()
                    != null)) {
                effective.setAttributeConsumingServiceIndex(local.getAttributeConsumingServiceIndex());
            } else if (global.getAttributeConsumingServiceIndex() != null) {
                effective.setAttributeConsumingServiceIndex(global.getAttributeConsumingServiceIndex());
            }

            List<SSOConfiguration.Property> properties = prioritizeProperties(global.getProperties(),
                    local.getProperties());
            if (properties.isEmpty()) {
                effective.setProperties(null);
            } else {
                effective.setProperties(properties);
            }

            if ((global.isSLOEnabled() != null) && (local.isSLOEnabled() != null)) {
                effective.enableSLO(local.isSLOEnabled());
            } else if (global.isSLOEnabled() != null) {
                effective.enableSLO(global.isSLOEnabled());
            }

            if ((global.getSLOURLPostFix() != null) && (local.getSLOURLPostFix() != null)) {
                effective.setSLOURLPostFix(local.getSLOURLPostFix());
            } else if (global.getSLOURLPostFix() != null) {
                effective.setSLOURLPostFix(global.getSLOURLPostFix());
            }

            if ((global.isAssertionEncryptionEnabled() != null) && (local.isAssertionEncryptionEnabled() != null)) {
                effective.enableAssertionEncryption(local.isAssertionEncryptionEnabled());
            } else if (global.isAssertionEncryptionEnabled() != null) {
                effective.enableAssertionEncryption(global.isAssertionEncryptionEnabled());
            }

            if ((global.isAssertionSigningEnabled() != null) && (local.isAssertionSigningEnabled() != null)) {
                effective.enableAssertionSigning(local.isAssertionSigningEnabled());
            } else if (global.isAssertionSigningEnabled() != null) {
                effective.enableAssertionSigning(global.isAssertionSigningEnabled());
            }

            if ((global.isRequestSigningEnabled() != null) && (local.isRequestSigningEnabled() != null)) {
                effective.enableRequestSigning(local.isRequestSigningEnabled());
            } else if (global.isRequestSigningEnabled() != null) {
                effective.enableRequestSigning(global.isRequestSigningEnabled());
            }

            if ((global.isResponseSigningEnabled() != null) && (local.isResponseSigningEnabled() != null)) {
                effective.enableResponseSigning(local.isResponseSigningEnabled());
            } else if (global.isResponseSigningEnabled() != null) {
                effective.enableResponseSigning(global.isResponseSigningEnabled());
            }

            if ((global.isForceAuthnEnabled() != null) && (local.isForceAuthnEnabled() != null)) {
                effective.enableForceAuthn(local.isForceAuthnEnabled());
            } else if (global.isForceAuthnEnabled() != null) {
                effective.enableForceAuthn(global.isForceAuthnEnabled());
            }

            if ((global.isPassiveAuthnEnabled() != null) && (local.isPassiveAuthnEnabled() != null)) {
                effective.enablePassiveAuthn(local.isPassiveAuthnEnabled());
            } else if (global.isPassiveAuthnEnabled() != null) {
                effective.enablePassiveAuthn(local.isPassiveAuthnEnabled());
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

}
