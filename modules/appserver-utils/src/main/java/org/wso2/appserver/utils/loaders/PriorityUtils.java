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
package org.wso2.appserver.utils.loaders;

import org.wso2.appserver.utils.Constants;
import org.wso2.appserver.utils.model.ClassLoadingConfiguration;
import org.wso2.appserver.utils.model.Configuration;
import org.wso2.appserver.utils.model.SSOConfiguration;

import java.util.Optional;

/**
 * A Java class which defines the utility functions when prioritizing the configurations to be set.
 *
 * @since 6.0.0
 */
public class PriorityUtils {

    //  todo: write comments
    //  todo: add the other configuration methods

    protected static Configuration merge(Configuration globalConfiguration,
            Optional<Configuration> localConfiguration) {
        //  Create the final, effective configuration set
        Configuration effectiveConfiguration = new Configuration();
        effectiveConfiguration.setClassLoadingConfiguration(new ClassLoadingConfiguration());
        effectiveConfiguration.setRestWebServicesConfiguration(new Configuration.RestWebServicesConfiguration());
        effectiveConfiguration.setSingleSignOnConfiguration(new SSOConfiguration());
        effectiveConfiguration.getSingleSignOnConfiguration().setSAML(new SSOConfiguration.SAML());

        if ((globalConfiguration != null) && (localConfiguration.isPresent())) {
            //  single-sign-on configurations
            SSOConfiguration globalSSOConfiguration = globalConfiguration.getSingleSignOnConfiguration();
            SSOConfiguration localSSOConfiguration = localConfiguration.get().getSingleSignOnConfiguration();
            SSOConfiguration effectiveSSOConfiguration = effectiveConfiguration.getSingleSignOnConfiguration();

            prioritizeSkipURIs(globalSSOConfiguration, localSSOConfiguration, effectiveSSOConfiguration);
            prioritizeConsumerURLHandlingAfterSLO(globalSSOConfiguration, localSSOConfiguration,
                    effectiveSSOConfiguration);
            prioritizeApplicationServerURL(globalSSOConfiguration, effectiveSSOConfiguration);
            prioritizeEnablingSAMLSSO(globalSSOConfiguration, localSSOConfiguration, effectiveSSOConfiguration);
            prioritizeIdPURL(globalSSOConfiguration, effectiveSSOConfiguration);
            prioritizeIdPEntityId(globalSSOConfiguration, effectiveSSOConfiguration);
            prioritizeSAMLBinding(globalSSOConfiguration, localSSOConfiguration, effectiveSSOConfiguration);
            prioritizeIssuerId(localSSOConfiguration, effectiveSSOConfiguration);
            prioritizeConsumerURL(localSSOConfiguration, effectiveSSOConfiguration);
            prioritizeAttributeConsumerServiceIndex(globalSSOConfiguration, localSSOConfiguration,
                    effectiveSSOConfiguration);
        } else if (globalConfiguration != null) {
            //  single-sign-on configurations
            SSOConfiguration globalSSOConfiguration = globalConfiguration.getSingleSignOnConfiguration();
            SSOConfiguration effectiveSSOConfiguration = effectiveConfiguration.getSingleSignOnConfiguration();

            prioritizeSkipURIs(globalSSOConfiguration, null, effectiveSSOConfiguration);
            prioritizeConsumerURLHandlingAfterSLO(globalSSOConfiguration, null, effectiveSSOConfiguration);
            prioritizeApplicationServerURL(globalSSOConfiguration, effectiveSSOConfiguration);
            prioritizeEnablingSAMLSSO(globalSSOConfiguration, null, effectiveSSOConfiguration);
            prioritizeIdPURL(globalSSOConfiguration, effectiveSSOConfiguration);
            prioritizeIdPEntityId(globalSSOConfiguration, effectiveSSOConfiguration);
            prioritizeSAMLBinding(globalSSOConfiguration, null, effectiveSSOConfiguration);
            prioritizeIssuerId(null, effectiveSSOConfiguration);
            prioritizeConsumerURL(null, effectiveSSOConfiguration);
            prioritizeAttributeConsumerServiceIndex(globalSSOConfiguration, null, effectiveSSOConfiguration);
        } else {
            //  single-sign-on configurations
            SSOConfiguration localSSOConfiguration = localConfiguration.get().getSingleSignOnConfiguration();
            SSOConfiguration effectiveSSOConfiguration = effectiveConfiguration.getSingleSignOnConfiguration();

            prioritizeSkipURIs(null, localSSOConfiguration, effectiveSSOConfiguration);
            prioritizeConsumerURLHandlingAfterSLO(null, localSSOConfiguration, effectiveSSOConfiguration);
            prioritizeApplicationServerURL(null, effectiveSSOConfiguration);
            prioritizeEnablingSAMLSSO(null, localSSOConfiguration, effectiveSSOConfiguration);
            prioritizeIdPURL(null, effectiveSSOConfiguration);
            prioritizeIdPEntityId(null, effectiveSSOConfiguration);
            prioritizeSAMLBinding(null, localSSOConfiguration, effectiveSSOConfiguration);
            prioritizeIssuerId(localSSOConfiguration, effectiveSSOConfiguration);
            prioritizeConsumerURL(localSSOConfiguration, effectiveSSOConfiguration);
            prioritizeAttributeConsumerServiceIndex(null, localSSOConfiguration, effectiveSSOConfiguration);
        }

        return effectiveConfiguration;
    }

    /**
     * Prioritizes the URIs to be skipped by considering the global and local configurations.
     *
     * @param global                 the global configurations
     * @param local                  the context configurations
     * @param effectiveConfiguration the resultant group of configurations
     */
    protected static void prioritizeSkipURIs(SSOConfiguration global, SSOConfiguration local,
            SSOConfiguration effectiveConfiguration) {
        //  Process URIs to skip
        if (effectiveConfiguration != null) {
            if ((global != null) && (local != null)) {
                if (local.getSkipURIs() != null) {
                    effectiveConfiguration.setSkipURIs(local.getSkipURIs());
                } else {
                    effectiveConfiguration.setSkipURIs(global.getSkipURIs());
                }
            } else if (global != null) {
                effectiveConfiguration.setSkipURIs(global.getSkipURIs());
            } else if (local != null) {
                effectiveConfiguration.setSkipURIs(local.getSkipURIs());
            }
        }
    }

    /**
     * Prioritizes the handling of consumer URL after SLO by considering the global and local configurations.
     *
     * @param global                 the global configurations
     * @param local                  the context configurations
     * @param effectiveConfiguration the resultant configuration
     */
    protected static void prioritizeConsumerURLHandlingAfterSLO(SSOConfiguration global, SSOConfiguration local,
            SSOConfiguration effectiveConfiguration) {
        //  Handle consumer URL after single-logout
        if (effectiveConfiguration != null) {
            if ((global != null) && (local != null)) {
                if (local.handleConsumerURLAfterSLO() != null) {
                    effectiveConfiguration.setHandleConsumerURLAfterSLO(local.handleConsumerURLAfterSLO());
                } else {
                    effectiveConfiguration.setHandleConsumerURLAfterSLO(global.handleConsumerURLAfterSLO());
                }
            } else if (global != null) {
                effectiveConfiguration.setHandleConsumerURLAfterSLO(global.handleConsumerURLAfterSLO());
            } else if (local != null) {
                effectiveConfiguration.setHandleConsumerURLAfterSLO(local.handleConsumerURLAfterSLO());
            } else {
                effectiveConfiguration.setHandleConsumerURLAfterSLO(true);
            }
        }
    }

    /**
     * Prioritizes the application server URL to be added to final configurations.
     *
     * @param global                 the global configurations
     * @param effectiveConfiguration the resultant configuration
     */
    protected static void prioritizeApplicationServerURL(SSOConfiguration global,
            SSOConfiguration effectiveConfiguration) {
        //  Process the Application Server URL
        if (effectiveConfiguration != null) {
            if ((global != null) && (global.getApplicationServerURL() != null)) {
                effectiveConfiguration.setApplicationServerURL(global.getApplicationServerURL());
            } else {
                effectiveConfiguration.
                        setApplicationServerURL(Constants.SSOConfigurationConstants.APPLICATION_SERVER_URL_DEFAULT);
            }
        }
    }

    /**
     * Prioritizes the enabling of SAML based single-sign-on by considering the global and local configurations.
     *
     * @param global                 the global configurations
     * @param local                  the context configurations
     * @param effectiveConfiguration the resultant configuration
     */
    protected static void prioritizeEnablingSAMLSSO(SSOConfiguration global, SSOConfiguration local,
            SSOConfiguration effectiveConfiguration) {
        //  Enable SAML based SSO
        if (effectiveConfiguration != null) {
            if ((global != null) && (local != null)) {
                if ((local.getSAML() != null) && (local.getSAML().isSAMLSSOEnabled() != null)) {
                    effectiveConfiguration.getSAML().setEnableSAMLSSO(local.getSAML().isSAMLSSOEnabled());
                } else {
                    Optional.ofNullable(global.getSAML()).ifPresent(
                            saml -> effectiveConfiguration.getSAML().setEnableSAMLSSO(saml.isSAMLSSOEnabled()));
                }
            } else if (global != null) {
                Optional.ofNullable(global.getSAML()).
                        ifPresent(saml -> effectiveConfiguration.getSAML().setEnableSAMLSSO(saml.isSAMLSSOEnabled()));
            } else if (local != null) {
                Optional.ofNullable(local.getSAML()).
                        ifPresent(saml -> effectiveConfiguration.getSAML().setEnableSAMLSSO(saml.isSAMLSSOEnabled()));
            }
            if (effectiveConfiguration.getSAML().isSAMLSSOEnabled() == null) {
                effectiveConfiguration.getSAML().setEnableSAMLSSO(true);
            }
        }
    }

    /**
     * Prioritizes the identity provider URL to be added to the final configurations.
     *
     * @param global                 the global configurations
     * @param effectiveConfiguration the resultant configuration
     */
    protected static void prioritizeIdPURL(SSOConfiguration global, SSOConfiguration effectiveConfiguration) {
        //  Process the IdP URL
        if (effectiveConfiguration != null) {
            if ((global != null) && (global.getSAML() != null) && (global.getSAML().getIdpURL() != null)) {
                effectiveConfiguration.getSAML().setIdpURL(global.getSAML().getIdpURL());
            } else {
                effectiveConfiguration.
                        getSAML().setIdpURL(Constants.SSOConfigurationConstants.SAMLConstants.IDP_URL_DEFAULT);
            }
        }
    }

    /**
     * Prioritizes the identity provider entity ID to be added to the final configurations.
     *
     * @param global                 the global configurations
     * @param effectiveConfiguration the resultant configuration
     */
    protected static void prioritizeIdPEntityId(SSOConfiguration global, SSOConfiguration effectiveConfiguration) {
        //  Process the IdP entity id
        if (effectiveConfiguration != null) {
            if ((global != null) && (global.getSAML() != null) && (global.getSAML().getIdpEntityId() != null)) {
                effectiveConfiguration.getSAML().setIdpEntityId(global.getSAML().getIdpEntityId());
            } else {
                effectiveConfiguration.getSAML().
                        setIdpEntityId(Constants.SSOConfigurationConstants.SAMLConstants.IDP_ENTITY_ID_DEFAULT);
            }
        }
    }

    /**
     * Prioritizes the SAML binding type to be used by considering the global and local configurations.
     *
     * @param global                 the global configurations
     * @param local                  the context configurations
     * @param effectiveConfiguration the resultant configuration
     */
    protected static void prioritizeSAMLBinding(SSOConfiguration global, SSOConfiguration local,
            SSOConfiguration effectiveConfiguration) {
        if (effectiveConfiguration != null) {
            if ((global != null) && (local != null)) {
                if ((local.getSAML() != null) && (local.getSAML().getHttpBinding() != null)) {
                    effectiveConfiguration.getSAML().setHttpBinding(local.getSAML().getHttpBinding());
                } else {
                    Optional.ofNullable(global.getSAML()).
                            ifPresent(saml -> effectiveConfiguration.getSAML().setHttpBinding(saml.getHttpBinding()));
                }
            } else if (global != null) {
                Optional.ofNullable(global.getSAML()).
                        ifPresent(saml -> effectiveConfiguration.getSAML().setHttpBinding(saml.getHttpBinding()));
            } else if (local != null) {
                Optional.ofNullable(local.getSAML()).
                        ifPresent(saml -> effectiveConfiguration.getSAML().setHttpBinding(saml.getHttpBinding()));
            }
            if (effectiveConfiguration.getSAML().getHttpBinding() == null) {
                effectiveConfiguration.getSAML().
                        setHttpBinding(Constants.SSOConfigurationConstants.SAMLConstants.BINDING_TYPE_DEFAULT);
            }
        }
    }

    /**
     * Sets the issuer id to be used if set under context level configurations.
     *
     * @param local                  the context configurations
     * @param effectiveConfiguration the resultant configuration
     */
    protected static void prioritizeIssuerId(SSOConfiguration local, SSOConfiguration effectiveConfiguration) {
        if (effectiveConfiguration != null) {
            if ((local != null) && (local.getSAML() != null) && (local.getSAML().getIssuerId() != null)) {
                effectiveConfiguration.getSAML().setIssuerId(local.getSAML().getIssuerId());
            }
        }
    }

    /**
     * Sets the consumer URL of the assertion consumer service if set under context level configurations.
     *
     * @param local                  the context configurations
     * @param effectiveConfiguration the resultant configuration
     */
    protected static void prioritizeConsumerURL(SSOConfiguration local, SSOConfiguration effectiveConfiguration) {
        if (effectiveConfiguration != null) {
            if ((local != null) && (local.getSAML() != null) && (local.getSAML().getConsumerURL() != null)) {
                effectiveConfiguration.getSAML().setConsumerURL(local.getSAML().getConsumerURL());
            }
        }
    }

    /**
     * Prioritizes the attribute consumer service index to be used by considering the global and local configurations.
     *
     * @param global                 the global configurations
     * @param local                  the context configurations
     * @param effectiveConfiguration the resultant configuration
     */
    protected static void prioritizeAttributeConsumerServiceIndex(SSOConfiguration global, SSOConfiguration local,
            SSOConfiguration effectiveConfiguration) {
        if (effectiveConfiguration != null) {
            if ((global != null) && (local != null)) {
                if ((local.getSAML() != null) && (local.getSAML().getHttpBinding() != null)) {
                    effectiveConfiguration.getSAML().
                            setAttributeConsumingServiceIndex(local.getSAML().getAttributeConsumingServiceIndex());
                } else {
                    Optional.ofNullable(global.getSAML()).ifPresent(saml -> effectiveConfiguration.getSAML().
                            setAttributeConsumingServiceIndex(saml.getAttributeConsumingServiceIndex()));
                }
            } else if (global != null) {
                Optional.ofNullable(global.getSAML()).ifPresent(saml -> effectiveConfiguration.getSAML().
                        setAttributeConsumingServiceIndex(saml.getAttributeConsumingServiceIndex()));
            } else if (local != null) {
                Optional.ofNullable(local.getSAML()).ifPresent(saml -> effectiveConfiguration.getSAML().
                        setAttributeConsumingServiceIndex(saml.getAttributeConsumingServiceIndex()));
            }
            if (effectiveConfiguration.getSAML().getHttpBinding() == null) {
                effectiveConfiguration.getSAML().setAttributeConsumingServiceIndex(
                        Constants.SSOConfigurationConstants.SAMLConstants.ATTR_CONSUMING_SERVICE_INDEX_DEFAULT);
            }
        }
    }
}
