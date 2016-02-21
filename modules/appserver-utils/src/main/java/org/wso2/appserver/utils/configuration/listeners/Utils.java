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
package org.wso2.appserver.utils.configuration.listeners;

import org.wso2.appserver.utils.configuration.context.components.ClassloadingConfiguration;
import org.wso2.appserver.utils.configuration.context.ContextConfiguration;
import org.wso2.appserver.utils.configuration.context.components.SSOConfiguration;

/**
 * A Java class which defines the utility methods for merging the context level configurations.
 *
 * @since 6.0.0
 */
public class Utils {
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
        effective.
                setClassloadingConfiguration(mergeClassloadingConfigurations(globalConfiguration, localConfiguration));
        return effective;
    }

    //  TODO: ADD COMMENTS
    private static ClassloadingConfiguration mergeClassloadingConfigurations(ContextConfiguration globalConfiguration,
            ContextConfiguration localConfiguration) {
        ClassloadingConfiguration effective = new ClassloadingConfiguration();
        if ((globalConfiguration != null) && (localConfiguration != null)) {
            ClassloadingConfiguration global = globalConfiguration.getClassloadingConfiguration();
            ClassloadingConfiguration local = localConfiguration.getClassloadingConfiguration();

            if (local.getEnvironments() != null) {
                effective.setEnvironments(local.getEnvironments());
            } else {
                effective.setEnvironments(global.getEnvironments());
            }
        } else if (globalConfiguration != null) {
            effective.setEnvironments(globalConfiguration.getClassloadingConfiguration().getEnvironments());
        }
        return effective;
    }

    //  TODO: ADD COMMENTS
    private static SSOConfiguration mergeSSOConfigurations(SSOConfiguration global, SSOConfiguration local) {
        SSOConfiguration effective = new SSOConfiguration();

        if ((global != null) && (local != null)) {
            if ((global.getSkipURIs() != null) && (local.getSkipURIs() != null)) {
                effective.setSkipURIs(local.getSkipURIs());
            } else if (local.getSkipURIs() != null) {
                effective.setSkipURIs(local.getSkipURIs());
            } else {
                effective.setSkipURIs(global.getSkipURIs());
            }

            if ((global.getQueryParams() != null) && (local.getQueryParams() != null)) {
                effective.setQueryParams(local.getQueryParams());
            } else if (local.getQueryParams() != null) {
                effective.setQueryParams(local.getQueryParams());
            } else {
                effective.setQueryParams(global.getQueryParams());
            }

            if ((global.getApplicationServerURL() != null) && (local.getApplicationServerURL() != null)) {
                effective.setApplicationServerURL(local.getApplicationServerURL());
            } else if (local.getApplicationServerURL() != null) {
                effective.setApplicationServerURL(local.getApplicationServerURL());
            } else {
                effective.setApplicationServerURL(global.getApplicationServerURL());
            }

            if ((global.getSAML() != null) && (local.getSAML() != null)) {
                effective.setSAML(new SSOConfiguration.SAML());
                SSOConfiguration.SAML globalSAML = global.getSAML();
                SSOConfiguration.SAML localSAML = local.getSAML();

                if ((globalSAML.isSSOEnabled() != null) && (localSAML.isSSOEnabled() != null)) {
                    effective.getSAML().enableSSO(localSAML.isSSOEnabled());
                } else if (localSAML.isSSOEnabled() != null) {
                    effective.getSAML().enableSSO(localSAML.isSSOEnabled());
                } else {
                    effective.getSAML().enableSSO(globalSAML.isSSOEnabled());
                }

                if ((globalSAML.getRequestURLPostFix() != null) && (localSAML.getRequestURLPostFix() != null)) {
                    effective.getSAML().setRequestURLPostFix(localSAML.getRequestURLPostFix());
                } else if (localSAML.getRequestURLPostFix() != null) {
                    effective.getSAML().setRequestURLPostFix(localSAML.getRequestURLPostFix());
                } else {
                    effective.getSAML().setRequestURLPostFix(globalSAML.getRequestURLPostFix());
                }

                if ((globalSAML.getHttpBinding() != null) && (localSAML.getHttpBinding() != null)) {
                    effective.getSAML().setHttpBinding(localSAML.getHttpBinding());
                } else if (localSAML.getHttpBinding() != null) {
                    effective.getSAML().setHttpBinding(localSAML.getHttpBinding());
                } else {
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
                } else if (localSAML.getConsumerURLPostFix() != null) {
                    effective.getSAML().setConsumerURLPostFix(localSAML.getConsumerURLPostFix());
                } else {
                    effective.getSAML().setConsumerURLPostFix(globalSAML.getConsumerURLPostFix());
                }

                if ((globalSAML.getAttributeConsumingServiceIndex() != null) && (
                        localSAML.getAttributeConsumingServiceIndex() != null)) {
                    effective.getSAML().
                            setAttributeConsumingServiceIndex(localSAML.getAttributeConsumingServiceIndex());
                } else if (localSAML.getAttributeConsumingServiceIndex() != null) {
                    effective.getSAML().
                            setAttributeConsumingServiceIndex(localSAML.getAttributeConsumingServiceIndex());
                } else {
                    effective.getSAML().
                            setAttributeConsumingServiceIndex(globalSAML.getAttributeConsumingServiceIndex());
                }

                if ((globalSAML.isSLOEnabled() != null) && (localSAML.isSLOEnabled() != null)) {
                    effective.getSAML().enableSLO(localSAML.isSLOEnabled());
                } else if (localSAML.isSLOEnabled() != null) {
                    effective.getSAML().enableSLO(localSAML.isSLOEnabled());
                } else {
                    effective.getSAML().enableSLO(globalSAML.isSLOEnabled());
                }

                if ((globalSAML.getSLOURLPostFix() != null) && (localSAML.getSLOURLPostFix() != null)) {
                    effective.getSAML().setSLOURLPostFix(localSAML.getSLOURLPostFix());
                } else if (localSAML.getSLOURLPostFix() != null) {
                    effective.getSAML().setSLOURLPostFix(localSAML.getSLOURLPostFix());
                } else {
                    effective.getSAML().setSLOURLPostFix(globalSAML.getSLOURLPostFix());
                }

                if ((globalSAML.isAssertionEncryptionEnabled() != null) && (localSAML.isAssertionEncryptionEnabled()
                        != null)) {
                    effective.getSAML().enableAssertionEncryption(localSAML.isAssertionEncryptionEnabled());
                } else if (localSAML.isAssertionEncryptionEnabled() != null) {
                    effective.getSAML().enableAssertionEncryption(localSAML.isAssertionEncryptionEnabled());
                } else {
                    effective.getSAML().enableAssertionEncryption(globalSAML.isAssertionEncryptionEnabled());
                }

                if ((globalSAML.isAssertionSigningEnabled() != null) && (localSAML.isAssertionSigningEnabled()
                        != null)) {
                    effective.getSAML().enableAssertionSigning(localSAML.isAssertionSigningEnabled());
                } else if (localSAML.isAssertionSigningEnabled() != null) {
                    effective.getSAML().enableAssertionSigning(localSAML.isAssertionSigningEnabled());
                } else {
                    effective.getSAML().enableAssertionSigning(globalSAML.isAssertionSigningEnabled());
                }

                if ((globalSAML.isRequestSigningEnabled() != null) && (localSAML.isRequestSigningEnabled() != null)) {
                    effective.getSAML().enableRequestSigning(localSAML.isRequestSigningEnabled());
                } else if (localSAML.isRequestSigningEnabled() != null) {
                    effective.getSAML().enableRequestSigning(localSAML.isRequestSigningEnabled());
                } else {
                    effective.getSAML().enableRequestSigning(globalSAML.isRequestSigningEnabled());
                }

                if ((globalSAML.isResponseSigningEnabled() != null) && (localSAML.isResponseSigningEnabled() != null)) {
                    effective.getSAML().enableResponseSigning(localSAML.isResponseSigningEnabled());
                } else if (localSAML.isResponseSigningEnabled() != null) {
                    effective.getSAML().enableResponseSigning(localSAML.isResponseSigningEnabled());
                } else {
                    effective.getSAML().enableResponseSigning(globalSAML.isResponseSigningEnabled());
                }

                //  TODO: ADD FURTHER PROPERTIES, CONSIDER IGNORED PROPERTIES
            } else if (local.getSAML() != null) {
                effective.setSAML(local.getSAML());
            } else {
                effective.setSAML(global.getSAML());
            }
        } else if (local != null) {
            effective = local;
        } else if(global != null) {
            effective = global;
        }
        return effective;
    }

}
