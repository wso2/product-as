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

import org.wso2.appserver.utils.AppServerException;
import org.wso2.appserver.utils.Constants;
import org.wso2.appserver.utils.model.Configuration;
import org.wso2.appserver.utils.model.SingleSignOnConfiguration;
import org.xml.sax.SAXException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

/**
 * A Java class which defines utilities used within the Application Server Utils.
 *
 * @since 6.0.0
 */
public class Utils {
    private static final Logger logger = Logger.getLogger(Utils.class.getName());

    protected static SingleSignOnConfiguration prioritizeSingleSignOnConfigurations(Configuration globalConfiguration,
            Configuration contextConfiguration) {
        SingleSignOnConfiguration effectiveConfiguration = null;

        if ((globalConfiguration != null) && (contextConfiguration != null)) {
            SingleSignOnConfiguration ssoGlobalConfiguration = globalConfiguration.getSingleSignOnConfiguration();
            SingleSignOnConfiguration ssoContextConfiguration = contextConfiguration.getSingleSignOnConfiguration();
            if ((ssoGlobalConfiguration != null) && (ssoContextConfiguration != null)) {
                effectiveConfiguration = new SingleSignOnConfiguration();

                //  Process URIs to skip
                if (ssoContextConfiguration.getSkipURIs() != null) {
                    effectiveConfiguration.setSkipURIs(ssoContextConfiguration.getSkipURIs());
                } else {
                    effectiveConfiguration.setSkipURIs(ssoGlobalConfiguration.getSkipURIs());
                }

                //  Handle consumer URL after single-logout
                if (ssoContextConfiguration.handleConsumerURLAfterSLO() != null) {
                    effectiveConfiguration.
                            setHandleConsumerURLAfterSLO(ssoContextConfiguration.handleConsumerURLAfterSLO());
                } else if (ssoGlobalConfiguration.handleConsumerURLAfterSLO() != null) {
                    effectiveConfiguration.
                            setHandleConsumerURLAfterSLO(ssoGlobalConfiguration.handleConsumerURLAfterSLO());
                } else {
                    effectiveConfiguration.setHandleConsumerURLAfterSLO(true);
                }

                //  Process the Application Server URL
                if (ssoGlobalConfiguration.getApplicationServerURL() != null) {
                    effectiveConfiguration.setApplicationServerURL(ssoGlobalConfiguration.getApplicationServerURL());
                } else {
                    effectiveConfiguration.setApplicationServerURL(
                            Constants.SSOConfigurationConstants.APPLICATION_SERVER_URL_DEFAULT);
                }

                //  Process login URL
                //  TODO: to consider whether this is a app level or global level property

                //  SAML specific configurations
                effectiveConfiguration.setSAML(new SingleSignOnConfiguration.SAML());

                if ((ssoGlobalConfiguration.getSAML() != null) && (ssoContextConfiguration.getSAML() != null)) {
                    //  Enable SAML based SSO
                    if (ssoContextConfiguration.getSAML().isSAMLSSOEnabled() != null) {
                        effectiveConfiguration.getSAML().
                                setEnableSAMLSSO(ssoContextConfiguration.getSAML().isSAMLSSOEnabled());
                    } else if (ssoGlobalConfiguration.getSAML().isSAMLSSOEnabled() != null) {
                        effectiveConfiguration.getSAML().
                                setEnableSAMLSSO(ssoGlobalConfiguration.getSAML().isSAMLSSOEnabled());
                    } else {
                        effectiveConfiguration.getSAML().setEnableSAMLSSO(true);
                    }

                    //  Process the IdP URL
                    if (ssoGlobalConfiguration.getSAML().getIdpURL() != null) {
                        effectiveConfiguration.getSAML().setIdpURL(ssoGlobalConfiguration.getSAML().getIdpURL());
                    } else {
                        effectiveConfiguration.getSAML().
                                setIdpURL(Constants.SSOConfigurationConstants.SAMLConstants.IDP_URL_DEFAULT);
                    }

                    //  Process the IdP Entity ID
                    if (ssoGlobalConfiguration.getSAML().getIdpEntityId() != null) {
                        effectiveConfiguration.getSAML().
                                setIdpEntityId(ssoGlobalConfiguration.getSAML().getIdpEntityId());
                    } else {
                        effectiveConfiguration.getSAML().
                                setIdpEntityId(Constants.SSOConfigurationConstants.SAMLConstants.IDP_ENTITY_ID_DEFAULT);
                    }

                    //  Process the issuer ID
                    if (ssoContextConfiguration.getSAML().getIssuerId() != null) {
                        effectiveConfiguration.getSAML().setIssuerId(ssoContextConfiguration.getSAML().getIssuerId());
                    }

                    //  Process the SAML assertion consumer service URL
                    if (ssoContextConfiguration.getSAML().getConsumerURL() != null) {
                        effectiveConfiguration.getSAML().
                                setConsumerURL(ssoContextConfiguration.getSAML().getConsumerURL());
                    }

                    //  Process the SAML binding type
                    if (ssoContextConfiguration.getSAML().getHttpBinding() != null) {
                        effectiveConfiguration.getSAML().
                                setHttpBinding(ssoContextConfiguration.getSAML().getHttpBinding());
                    } else if (ssoGlobalConfiguration.getSAML().getHttpBinding() != null) {
                        effectiveConfiguration.getSAML().
                                setHttpBinding(ssoGlobalConfiguration.getSAML().getHttpBinding());
                    } else {
                        effectiveConfiguration.getSAML().
                                setHttpBinding(Constants.SSOConfigurationConstants.SAMLConstants.BINDING_TYPE_DEFAULT);
                    }

                    //  Process the SAML attribute consuming service index
                    if (ssoContextConfiguration.getSAML().getAttributeConsumingServiceIndex() != null) {
                        effectiveConfiguration.getSAML().setAttributeConsumingServiceIndex(
                                ssoContextConfiguration.getSAML().getAttributeConsumingServiceIndex());
                    } else if (ssoGlobalConfiguration.getSAML().getAttributeConsumingServiceIndex() != null) {
                        effectiveConfiguration.getSAML().setAttributeConsumingServiceIndex(
                                ssoGlobalConfiguration.getSAML().getAttributeConsumingServiceIndex());
                    } else {
                        effectiveConfiguration.getSAML().setAttributeConsumingServiceIndex(Constants.
                                SSOConfigurationConstants.SAMLConstants.ATT_CONSUMING_SERVICE_INDEX_DEFAULT);
                    }

                    //  Enable single-logout
                    if (ssoContextConfiguration.getSAML().isSLOEnabled() != null) {
                        effectiveConfiguration.getSAML().setEnableSLO(ssoContextConfiguration.getSAML().isSLOEnabled());
                    } else if (ssoGlobalConfiguration.getSAML().isSLOEnabled() != null) {
                        effectiveConfiguration.getSAML().setEnableSLO(ssoGlobalConfiguration.getSAML().isSLOEnabled());
                    } else {
                        effectiveConfiguration.getSAML().setEnableSLO(true);
                    }

                    //  Process consumer URL post-fix
                    if (ssoContextConfiguration.getSAML().getConsumerURLPostFix() != null) {
                        effectiveConfiguration.getSAML().
                                setConsumerURLPostFix(ssoContextConfiguration.getSAML().getConsumerURLPostFix());
                    } else if (ssoGlobalConfiguration.getSAML().getConsumerURLPostFix() != null) {
                        effectiveConfiguration.getSAML().
                                setConsumerURLPostFix(ssoGlobalConfiguration.getSAML().getConsumerURLPostFix());
                    } else {
                        effectiveConfiguration.getSAML().setConsumerURLPostFix(
                                Constants.SSOConfigurationConstants.SAMLConstants.CONSUMER_URL_POSTFIX_DEFAULT);
                    }

                    //  Process request URL post-fix
                    if (ssoGlobalConfiguration.getSAML().getRequestURLPostFix() != null) {
                        effectiveConfiguration.getSAML().
                                setRequestURLPostFix(ssoGlobalConfiguration.getSAML().getRequestURLPostFix());
                    } else {
                        effectiveConfiguration.getSAML().setRequestURLPostFix(
                                Constants.SSOConfigurationConstants.SAMLConstants.REQUEST_URL_POSTFIX_DEFAULT);
                    }

                    //  Process SLO URL post-fix
                    if (ssoGlobalConfiguration.getSAML().getSLOURLPostFix() != null) {
                        effectiveConfiguration.getSAML().
                                setSLOURLPostFix(ssoGlobalConfiguration.getSAML().getSLOURLPostFix());
                    } else {
                        effectiveConfiguration.getSAML().setRequestURLPostFix(
                                Constants.SSOConfigurationConstants.SAMLConstants.SLO_URL_POSTFIX_DEFAULT);
                    }

                    //  Enable SAML Response signing
                    if (ssoContextConfiguration.getSAML().isResponseSigningEnabled() != null) {
                        effectiveConfiguration.getSAML().
                                setEnableResponseSigning(ssoContextConfiguration.getSAML().isResponseSigningEnabled());
                    } else if (ssoGlobalConfiguration.getSAML().isResponseSigningEnabled() != null) {
                        effectiveConfiguration.getSAML().
                                setEnableResponseSigning(ssoGlobalConfiguration.getSAML().isResponseSigningEnabled());
                    } else {
                        effectiveConfiguration.getSAML().setEnableResponseSigning(true);
                    }

                    //  Enable SAML Assertion signing
                    if (ssoContextConfiguration.getSAML().isAssertionSigningEnabled() != null) {
                        effectiveConfiguration.getSAML().setEnableAssertionSigning(
                                ssoContextConfiguration.getSAML().isAssertionSigningEnabled());
                    } else if (ssoGlobalConfiguration.getSAML().isAssertionSigningEnabled() != null) {
                        effectiveConfiguration.getSAML().
                                setEnableAssertionSigning(ssoGlobalConfiguration.getSAML().isAssertionSigningEnabled());
                    } else {
                        effectiveConfiguration.getSAML().setEnableAssertionSigning(true);
                    }

                    //  Enable SAML Assertion encryption
                    if (ssoContextConfiguration.getSAML().isAssertionEncryptionEnabled() != null) {
                        effectiveConfiguration.getSAML().setEnableAssertionEncryption(
                                ssoContextConfiguration.getSAML().isAssertionEncryptionEnabled());
                    } else if (ssoGlobalConfiguration.getSAML().isAssertionEncryptionEnabled() != null) {
                        effectiveConfiguration.getSAML().setEnableAssertionSigning(
                                ssoGlobalConfiguration.getSAML().isAssertionEncryptionEnabled());
                    } else {
                        effectiveConfiguration.getSAML().setEnableAssertionEncryption(true);
                    }

                    //  Enable SAML Request Signing
                    if (ssoContextConfiguration.getSAML().isRequestSigningEnabled() != null) {
                        effectiveConfiguration.getSAML().
                                setEnableRequestSigning(ssoContextConfiguration.getSAML().isRequestSigningEnabled());
                    } else if (ssoGlobalConfiguration.getSAML().isRequestSigningEnabled() != null) {
                        effectiveConfiguration.getSAML().
                                setEnableRequestSigning(ssoGlobalConfiguration.getSAML().isRequestSigningEnabled());
                    } else {
                        effectiveConfiguration.getSAML().setEnableRequestSigning(true);
                    }

                    //  Process the signature validator implementation class
                    if (ssoGlobalConfiguration.getSAML().getSignatureValidatorImplClass() != null) {
                        effectiveConfiguration.getSAML().setSignatureValidatorImplClass(
                                ssoGlobalConfiguration.getSAML().getSignatureValidatorImplClass());
                    } else {
                        effectiveConfiguration.getSAML().setSignatureValidatorImplClass(Constants.
                                SSOConfigurationConstants.SAMLConstants.SIGNATURE_VALIDATOR_IMPL_CLASS_DEFAULT);
                    }

                    //  Process the additional request parameters
                    if (ssoContextConfiguration.getSAML().getAdditionalRequestParams() != null) {
                        effectiveConfiguration.getSAML().setAdditionalRequestParams(
                                ssoContextConfiguration.getSAML().getAdditionalRequestParams());
                    } else if (ssoGlobalConfiguration.getSAML().getAdditionalRequestParams() != null) {
                        effectiveConfiguration.getSAML().setAdditionalRequestParams(
                                ssoGlobalConfiguration.getSAML().getAdditionalRequestParams());
                    } else {
                        effectiveConfiguration.getSAML().setAdditionalRequestParams(Constants.
                                SSOConfigurationConstants.SAMLConstants.ADDITIONAL_REQUEST_PARAMETERS_DEFAULT);
                    }
                }
            }
        } else if ((globalConfiguration != null)) {

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
    private static void prioritizeSkipURIs(SingleSignOnConfiguration global, SingleSignOnConfiguration local,
            SingleSignOnConfiguration effectiveConfiguration) {
        //  Process URIs to skip
        if (effectiveConfiguration != null) {
            if ((global != null) && (local == null)) {
                effectiveConfiguration.setSkipURIs(global.getSkipURIs());
            } else if ((global == null) && (local != null)) {
                effectiveConfiguration.setSkipURIs(local.getSkipURIs());
            }
            if ((global != null) && (local != null)) {
                if (local.getSkipURIs() != null) {
                    effectiveConfiguration.setSkipURIs(local.getSkipURIs());
                } else {
                    effectiveConfiguration.setSkipURIs(global.getSkipURIs());
                }
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
    private static void prioritizeConsumerURLHandlingAfterSLO(SingleSignOnConfiguration global,
            SingleSignOnConfiguration local, SingleSignOnConfiguration effectiveConfiguration) {
        //  Handle consumer URL after single-logout
        if (effectiveConfiguration != null) {
            if ((global != null) && (local == null)) {
                effectiveConfiguration.setHandleConsumerURLAfterSLO(global.handleConsumerURLAfterSLO());
            } else if ((global == null) && (local != null)) {
                effectiveConfiguration.setHandleConsumerURLAfterSLO(local.handleConsumerURLAfterSLO());
            }
            if ((global != null) && (local != null)) {
                if (local.handleConsumerURLAfterSLO() != null) {
                    effectiveConfiguration.setHandleConsumerURLAfterSLO(local.handleConsumerURLAfterSLO());
                } else {
                    effectiveConfiguration.setHandleConsumerURLAfterSLO(global.handleConsumerURLAfterSLO());
                }
            }
        }
    }

    /**
     * Prioritizes the application server URL to be added to final configurations.
     *
     * @param global                 the global configurations
     * @param effectiveConfiguration the resultant configuration
     */
    private static void prioritizeApplicationServerURL(SingleSignOnConfiguration global,
            SingleSignOnConfiguration effectiveConfiguration) {
        //  Process the Application Server URL
        if (effectiveConfiguration != null) {
            if (global.getApplicationServerURL() != null) {
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
    private static void prioritizeEnablingSAMLSSO(SingleSignOnConfiguration global, SingleSignOnConfiguration local,
            SingleSignOnConfiguration effectiveConfiguration) {
        //  Enable SAML based SSO
        if (effectiveConfiguration != null) {
            if ((global != null) && (local == null)) {
                effectiveConfiguration.getSAML().setEnableSAMLSSO(global.getSAML().isSAMLSSOEnabled());
            } else if ((global == null) && (local != null)) {
                effectiveConfiguration.getSAML().setEnableSAMLSSO(local.getSAML().isSAMLSSOEnabled());
            }
            if ((global != null) && (local != null)) {
                if (local.getSAML().isSAMLSSOEnabled() != null) {
                    effectiveConfiguration.getSAML().setEnableSAMLSSO(local.getSAML().isSAMLSSOEnabled());
                } else {
                    effectiveConfiguration.getSAML().setEnableSAMLSSO(global.getSAML().isSAMLSSOEnabled());
                }
            }
        }
    }

    /**
     * Prioritizes the identity provider URL to be added to the final configurations.
     *
     * @param global                 the global configurations
     * @param effectiveConfiguration the resultant configuration
     */
    private static void prioritizeIdPURL(SingleSignOnConfiguration global,
            SingleSignOnConfiguration effectiveConfiguration) {
        //  Process the IdP URL
        if (effectiveConfiguration != null) {
            if (global.getSAML().getIdpURL() != null) {
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
    private static void prioritizeIdPEntityId(SingleSignOnConfiguration global,
            SingleSignOnConfiguration effectiveConfiguration) {
        //  Process the IdP entity id
        if (effectiveConfiguration != null) {
            if (global.getSAML().getIdpEntityId() != null) {
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
    private static void prioritizeSAMLBinding(SingleSignOnConfiguration global, SingleSignOnConfiguration local,
            SingleSignOnConfiguration effectiveConfiguration) {
        if (effectiveConfiguration != null) {
            if ((global != null) && (local == null)) {
                Optional.ofNullable(global.getSAML()).
                        ifPresent(saml -> effectiveConfiguration.getSAML().setHttpBinding(saml.getHttpBinding()));
            } else if ((global == null) && (local != null)) {
                Optional.ofNullable(local.getSAML()).
                        ifPresent(saml -> effectiveConfiguration.getSAML().setHttpBinding(saml.getHttpBinding()));
            }
            if ((global != null) && (local != null)) {
                if ((local.getSAML() != null) && (local.getSAML().getHttpBinding() != null)) {
                    effectiveConfiguration.getSAML().setHttpBinding(local.getSAML().getHttpBinding());
                } else {
                    Optional.ofNullable(global.getSAML()).
                            ifPresent(saml -> effectiveConfiguration.getSAML().setHttpBinding(saml.getHttpBinding()));
                }
            }
            if (effectiveConfiguration.getSAML().getHttpBinding() == null) {
                effectiveConfiguration.getSAML().
                        setHttpBinding(Constants.SSOConfigurationConstants.SAMLConstants.BINDING_TYPE_DEFAULT);
            }
        }
    }


    /**
     * JAXB utility functions
     */

    /**
     * Returns an XML unmarshaller for the defined Java classes.
     *
     * @param schema  an optional file path representation of an XML schema file against which the source XML is to be
     *                validated
     * @param classes the list of classes to be recognized by the {@link JAXBContext}
     * @return an XML unmarshaller for the defined Java classes
     * @throws AppServerException if an error occurs when creating the XML unmarshaller
     */
    protected static Unmarshaller getXMLUnmarshaller(Optional<Path> schema, Class... classes)
            throws AppServerException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            schema.ifPresent(schemaPath -> {
                if (Files.exists(schemaPath)) {
                    SchemaFactory schemaFactory = SchemaFactory.
                            newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                    Schema xmlSchema;
                    try {
                        xmlSchema = schemaFactory.newSchema(schemaPath.toFile());
                    } catch (SAXException e) {
                        logger.log(Level.WARNING, "An error has occurred during parsing", e);
                        xmlSchema = null;
                    }
                    Optional.ofNullable(xmlSchema).ifPresent(unmarshaller::setSchema);
                }
            });
            return unmarshaller;
        } catch (JAXBException e) {
            throw new AppServerException("Error when creating the XML unmarshaller", e);
        }
    }
}
