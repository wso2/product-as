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
     * Merges the context level classloading configurations defined globally and overridden at context level
     * (if any).
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
            effective.enableParentFirst(Optional.ofNullable(local.isParentFirst()).orElse(global.isParentFirst()));
            effective.setEnvironments(Optional.ofNullable(local.getEnvironments()).orElse(global.getEnvironments()));
        } else if (global != null) {
            effective = global;
        }
        return effective;
    }

    /**
     * Merges the context level single-sign-on (SSO) configurations defined globally and overridden at context level
     * (if any).
     *
     * @param global the globally defined SSO configurations
     * @param local  the SSO configurations defined at context level
     * @return the merged effective group of SSO configurations
     */
    private static SSOConfiguration mergeSSOConfigurations(SSOConfiguration global, SSOConfiguration local) {
        SSOConfiguration effective = new SSOConfiguration();

        if ((global != null) && (local != null)) {
            effective.setSkipURIs(Optional.ofNullable(local.getSkipURIs()).orElse(global.getSkipURIs()));
            effective.enableHandlingConsumerURLAfterSLO(Optional.ofNullable(local.handleConsumerURLAfterSLO()).
                    orElse(global.handleConsumerURLAfterSLO()));
            effective.setQueryParams(Optional.ofNullable(local.getQueryParams()).orElse(global.getQueryParams()));
            effective.setApplicationServerURL(
                    Optional.ofNullable(local.getApplicationServerURL()).orElse(global.getApplicationServerURL()));
            effective.enableSSO(Optional.ofNullable(local.isSSOEnabled()).orElse(global.isSSOEnabled()));
            effective.setRequestURLPostFix(
                    Optional.ofNullable(local.getRequestURLPostFix()).orElse(global.getRequestURLPostFix()));
            effective.setHttpBinding(Optional.ofNullable(local.getHttpBinding()).orElse(global.getHttpBinding()));
            effective.setIssuerId(local.getIssuerId());
            effective.setConsumerURL(local.getConsumerURL());
            effective.setConsumerURLPostFix(
                    Optional.ofNullable(local.getConsumerURLPostFix()).orElse(global.getConsumerURLPostFix()));
            effective.setAttributeConsumingServiceIndex(Optional.ofNullable(local.getAttributeConsumingServiceIndex()).
                    orElse(global.getAttributeConsumingServiceIndex()));
            effective.enableSLO(Optional.ofNullable(local.isSLOEnabled()).orElse(global.isSLOEnabled()));
            effective.setSLOURLPostFix(Optional.ofNullable(local.getSLOURLPostFix()).orElse(global.getSLOURLPostFix()));
            effective.enableAssertionEncryption(Optional.ofNullable(local.isAssertionEncryptionEnabled()).
                    orElse(global.isAssertionEncryptionEnabled()));
            effective.enableAssertionSigning(Optional.ofNullable(local.isAssertionSigningEnabled()).
                    orElse(global.isAssertionSigningEnabled()));
            effective.enableRequestSigning(
                    Optional.ofNullable(local.isRequestSigningEnabled()).orElse(global.isRequestSigningEnabled()));
            effective.enableResponseSigning(
                    Optional.ofNullable(local.isResponseSigningEnabled()).orElse(global.isResponseSigningEnabled()));
            effective.enableForceAuthn(
                    Optional.ofNullable(local.isForceAuthnEnabled()).orElse(global.isForceAuthnEnabled()));
            effective.enablePassiveAuthn(
                    Optional.ofNullable(local.isPassiveAuthnEnabled()).orElse(global.isPassiveAuthnEnabled()));
            List<SSOConfiguration.Property> properties = prioritizeProperties(global.getProperties(),
                    local.getProperties());
            if (properties.isEmpty()) {
                effective.setProperties(null);
            } else {
                effective.setProperties(properties);
            }
        } else if (global != null) {
            effective = global;
            effective.setIssuerId(null);
            effective.setConsumerURL(null);
            List<SSOConfiguration.Property> properties = prioritizeProperties(global.getProperties(), null);
            if (properties.isEmpty()) {
                effective.setProperties(null);
            } else {
                effective.setProperties(properties);
            }
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
        } else if (local != null) {
            local.stream().forEach(effective::add);
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
            return list.stream().filter(property -> property.getKey().equals(key)).findFirst();
        } else {
            return Optional.empty();
        }
    }
}
