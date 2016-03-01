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
package org.wso2.appserver.webapp.security.sso.saml;

import org.apache.catalina.connector.Request;
import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.EncryptedAssertion;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.RequestAbstractType;
import org.opensaml.saml2.encryption.Decrypter;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.encryption.DecryptionException;
import org.opensaml.xml.encryption.EncryptedKey;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.security.keyinfo.StaticKeyInfoCredentialResolver;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureException;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.signature.X509Certificate;
import org.opensaml.xml.signature.X509Data;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.wso2.appserver.configuration.context.ContextConfiguration;
import org.wso2.appserver.configuration.context.SSOConfiguration;
import org.wso2.appserver.exceptions.AppServerException;
import org.wso2.appserver.utils.XMLUtils;
import org.wso2.appserver.webapp.security.sso.bean.RelayState;
import org.wso2.appserver.webapp.security.sso.saml.signature.SSOX509Credential;
import org.wso2.appserver.webapp.security.sso.saml.signature.X509CredentialImplementation;
import org.wso2.appserver.webapp.security.sso.Constants;
import org.wso2.appserver.webapp.security.sso.utils.exception.SSOException;
import org.wso2.appserver.webapp.security.sso.utils.XMLEntityResolver;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import javax.crypto.SecretKey;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;

/**
 * This class defines the implementation of utility functions associated with SAML 2.0 based
 * single-sign-on (SSO) process.
 *
 * @since 6.0.0
 */
public class SAMLSSOUtils {
    private static final Logger logger = Logger.getLogger(SAMLSSOUtils.class.getName());
    private static boolean isBootStrapped;

    static {
        try {
            doBootstrap();
        } catch (SSOException e) {
            logger.log(Level.SEVERE, "An error occurred when bootstrapping the OpenSAML library");
        }
    }

    /**
     * Prevents instantiating the SAMLSSOUtils class.
     */
    private SAMLSSOUtils() {
    }

    /**
     * Returns a unique id value for the SAML 2.0 service provider application based on its context path.
     * <p>
     * An {@code Optional String} id is returned based on the context path provided.
     *
     * @param contextPath the context path of the service provider application
     * @return a unique id value for the SAML 2.0 service provider application based on its context path
     */
    protected static Optional generateIssuerID(String contextPath) {
        if (contextPath != null) {
            String issuerId = contextPath.replaceFirst("/webapps", "").replace("/", "_");
            if (issuerId.startsWith("_")) {
                issuerId = issuerId.substring(1);
            }
            return Optional.of(issuerId);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns a SAML 2.0 Assertion Consumer URL based on service provider application context path.
     * <p>
     * An {@code Optional String} URL is returned based on the context path and configuration properties provided.
     *
     * @param contextPath      the context path of the service provider application
     * @param ssoConfiguration the context level single-sign-on configuration properties
     * @return a SAML 2.0 Assertion Consumer URL based on service provider application context path
     */
    protected static Optional<String> generateConsumerURL(String contextPath, SSOConfiguration ssoConfiguration) {
        if ((contextPath != null) && (ssoConfiguration != null)) {
            return Optional.of(ssoConfiguration.getApplicationServerURL() + contextPath +
                    ssoConfiguration.getConsumerURLPostFix());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Sets default values to configurations if not set in configuration files.
     * <p>
     * These configurations are not defined within the
     * {@link org.wso2.appserver.webapp.security.sso.agent.SSOAgentConfiguration}.
     *
     * @param configuration the context level set of configurations
     */
    protected static void setDefaultConfigurations(ContextConfiguration configuration) {
        Optional.ofNullable(configuration).ifPresent(contextConfiguration -> {
            SSOConfiguration ssoConfiguration = contextConfiguration.getSingleSignOnConfiguration();

            ssoConfiguration.setApplicationServerURL(Optional.ofNullable(ssoConfiguration.getApplicationServerURL()).
                    orElse(Constants.APPLICATION_SERVER_URL_DEFAULT));
            ssoConfiguration.enableHandlingConsumerURLAfterSLO(
                    Optional.ofNullable(ssoConfiguration.handleConsumerURLAfterSLO()).orElse(false));
            ssoConfiguration.setConsumerURLPostFix(Optional.ofNullable(ssoConfiguration.getConsumerURLPostFix()).
                    orElse(Constants.CONSUMER_URL_POSTFIX_DEFAULT));
        });
    }

    /**
     * Generates a {@code RelayState} based on the {@code Request}.
     *
     * @param request the {@link Request} instance
     * @return the created {@link RelayState} instance
     */
    protected static RelayState generateRelayState(Request request) {
        RelayState relayState = new RelayState();
        relayState.setRequestedURL(request.getRequestURI());
        relayState.setRequestQueryString(request.getQueryString());
        relayState.setRequestParameters(request.getParameterMap());

        return relayState;
    }

    /**
     * Initializes the OpenSAML2 library, if it is not initialized yet.
     * <p>
     * Calls the bootstrap method of {@code DefaultBootstrap}.
     *
     * @throws SSOException if an error occurs when bootstrapping the OpenSAML2 library
     */
    protected static void doBootstrap() throws SSOException {
        if (!isBootStrapped) {
            try {
                DefaultBootstrap.bootstrap();
                isBootStrapped = true;
            } catch (ConfigurationException e) {
                throw new SSOException("Error in bootstrapping the OpenSAML2 library", e);
            }
        }
    }

    /**
     * Returns the SAML 2.0 Assertion Attribute Statement content.
     *
     * @param assertion the SAML Assertion whose content is to be returned
     * @return the SAML 2.0 Assertion Attribute Statement content of the SAML 2.0 Assertion specified
     */
    protected static Map<String, String> getAssertionStatements(Assertion assertion) {
        Map<String, String> results = new HashMap<>();
        if ((assertion != null) && (assertion.getAttributeStatements() != null)) {
            Stream<AttributeStatement> attributeStatements = assertion.getAttributeStatements().stream();
            attributeStatements.
                    forEach(attributeStatement -> attributeStatement.getAttributes().stream().forEach(attribute -> {
                        Element value = attribute.getAttributeValues().stream().findFirst().get().getDOM();
                        String attributeValue = value.getTextContent();
                        results.put(attribute.getName(), attributeValue);
                    }));
        }
        return results;
    }

    /**
     * Encodes the SAML 2.0 based request XML object into its corresponding Base64 notation, based on the type of
     * SAML 2.0 binding.
     *
     * @param requestMessage the {@link RequestAbstractType} XML object to be encoded
     * @param binding        the SAML 2.0 binding type
     * @return encoded {@link String} corresponding to the request XML object
     * @throws SSOException if an error occurs while encoding SAML request
     */
    protected static String encodeRequestMessage(RequestAbstractType requestMessage, String binding)
            throws SSOException {
        Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(requestMessage);
        Element authDOM;
        try {
            //  Marshall this element, and its children, and root them in a newly created Document
            authDOM = marshaller.marshall(requestMessage);
        } catch (MarshallingException e) {
            throw new SSOException("Error occurred while encoding SAML request, failed to marshall the SAML 2.0. " +
                    "Request element XMLObject to its corresponding W3C DOM element", e);
        }

        StringWriter writer = new StringWriter();
        //  Writes the node out to the writer using the DOM
        XMLHelper.writeNode(authDOM, writer);

        if (SAMLConstants.SAML2_REDIRECT_BINDING_URI.equals(binding)) {
            //  Compress the message using default DEFLATE encoding, Base 64 encode and URL encode
            Deflater deflater = new Deflater(Deflater.DEFLATED, true);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream,
                    deflater)) {
                deflaterOutputStream.write(writer.toString().getBytes(Charset.forName("UTF-8")));
            } catch (IOException e) {
                throw new SSOException("Error occurred while deflate encoding SAML request", e);
            }

            String encodedRequestMessage = Base64.
                    encodeBytes(byteArrayOutputStream.toByteArray(), Base64.DONT_BREAK_LINES);
            try {
                return URLEncoder.encode(encodedRequestMessage, "UTF-8").trim();
            } catch (UnsupportedEncodingException e) {
                throw new SSOException("Error occurred while encoding SAML request", e);
            }
        } else if (SAMLConstants.SAML2_POST_BINDING_URI.equals(binding)) {
            return Base64.
                    encodeBytes(writer.toString().getBytes(Charset.forName("UTF-8")), Base64.DONT_BREAK_LINES);
        } else {
            logger.log(Level.FINE,
                    "Unsupported SAML2 HTTP Binding. Defaulting to " + SAMLConstants.SAML2_POST_BINDING_URI);
            return Base64.
                    encodeBytes(writer.toString().getBytes(Charset.forName("UTF-8")), Base64.DONT_BREAK_LINES);
        }
    }

    /**
     * Serializes the specified SAML 2.0 based XML content representation to its corresponding actual XML syntax
     * representation.
     *
     * @param xmlObject the SAML 2.0 based XML content object
     * @return a {@link String} representation of the actual XML representation of the SAML 2.0 based XML content
     * representation
     * @throws SSOException if an error occurs during the marshalling process
     */
    public static String marshall(XMLObject xmlObject) throws SSOException {
        try {
            MarshallerFactory marshallerFactory = Configuration.getMarshallerFactory();
            Marshaller marshaller = marshallerFactory.getMarshaller(xmlObject);
            Element element = marshaller.marshall(xmlObject);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS implementation = (DOMImplementationLS) registry.getDOMImplementation("LS");
            LSSerializer writer = implementation.createLSSerializer();
            LSOutput output = implementation.createLSOutput();
            output.setByteStream(byteArrayOutputStream);
            writer.write(element, output);
            return new String(byteArrayOutputStream.toByteArray(), Charset.forName("UTF-8"));
        } catch (ClassNotFoundException | InstantiationException | MarshallingException | IllegalAccessException e) {
            throw new SSOException("Error in marshalling SAML Assertion", e);
        }
    }

    /**
     * Returns a SAML 2.0 based XML content representation from the {@code String} value representing the XML syntax.
     *
     * @param xmlString the {@link String} representation of the XML content
     * @return an XML object from the {@link String} value representing the XML syntax
     * @throws SSOException if an error occurs when unmarshalling the XML string representation
     */
    public static XMLObject unmarshall(String xmlString) throws SSOException {
        try {
            DocumentBuilder docBuilder = XMLUtils.getDocumentBuilder(false, true, Optional.of(new XMLEntityResolver()));
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlString.getBytes(Charset.forName("UTF-8")));
            Document document = docBuilder.parse(inputStream);
            Element element = document.getDocumentElement();
            UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
            return unmarshaller.unmarshall(element);
        } catch (AppServerException | UnmarshallingException | SAXException | IOException e) {
            throw new SSOException("Error in unmarshalling the XML string representation", e);
        }
    }

    /**
     * Returns a decrypted SAML 2.0 {@code Assertion} from the specified SAML 2.0 encrypted {@code Assertion}.
     *
     * @param ssoAgentX509Credential credential for the resolver
     * @param encryptedAssertion     the {@link EncryptedAssertion} instance to be decrypted
     * @return a decrypted SAML 2.0 {@link Assertion} from the specified SAML 2.0 {@link EncryptedAssertion}
     * @throws SSOException if an error occurs during the decryption process
     */
    protected static Assertion decryptAssertion(SSOX509Credential ssoAgentX509Credential,
            EncryptedAssertion encryptedAssertion) throws SSOException {
        try {
            KeyInfoCredentialResolver keyResolver = new StaticKeyInfoCredentialResolver(
                    new X509CredentialImplementation(ssoAgentX509Credential));

            EncryptedKey key = encryptedAssertion.getEncryptedData().
                    getKeyInfo().getEncryptedKeys().stream().findFirst().get();
            Decrypter decrypter = new Decrypter(null, keyResolver, null);
            SecretKey decrypterKey = (SecretKey) decrypter.decryptKey(key, encryptedAssertion.getEncryptedData().
                    getEncryptionMethod().getAlgorithm());
            Credential shared = SecurityHelper.getSimpleCredential(decrypterKey);
            decrypter = new Decrypter(new StaticKeyInfoCredentialResolver(shared), null, null);
            decrypter.setRootInNewDocument(true);
            return decrypter.decrypt(encryptedAssertion);
        } catch (DecryptionException e) {
            throw new SSOException("Decrypted assertion error", e);
        }
    }

    /**
     * Utility functions for handling digital signature application and validation.
     */

    /**
     * Returns a {@code KeyStore} based on keystore properties specified.
     *
     * @param configuration the keystore properties
     * @return the {@link KeyStore} instance generated
     * @throws SSOException if an error occurs while generating the {@link KeyStore} instance
     */
    public static Optional generateKeyStore(org.wso2.appserver.configuration.server.SSOConfiguration configuration)
            throws SSOException {
        if (configuration == null) {
            return Optional.empty();
        }

        String keystorePathString = configuration.getKeystorePath();
        String keystorePasswordString = configuration.getKeystorePassword();
        if ((keystorePasswordString == null) || (keystorePathString == null)) {
            return Optional.empty();
        }

        Path keyStorePath = Paths.get(URI.create(keystorePathString).getPath());
        if (Files.exists(keyStorePath)) {
            try (InputStream keystoreInputStream = Files.newInputStream(keyStorePath)) {
                String keyStoreType = "JKS";
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(keystoreInputStream, keystorePasswordString.toCharArray());
                return Optional.of(keyStore);
            } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
                throw new SSOException("Error while loading key store", e);
            }
        } else {
            throw new SSOException("File path specified under \'keystorePath\' does not exist");
        }
    }

    /**
     * Applies the XML Digital Signature to the SAML 2.0 based Authentication Request (AuthnRequest).
     *
     * @param authnRequest       the SAML 2.0 based Authentication Request (AuthnRequest)
     * @param signatureAlgorithm the algorithm used to compute the signature
     * @param credential         the signature signing credential
     * @return the SAML 2.0 based Authentication Request (AuthnRequest) with XML Digital Signature set
     * @throws SSOException if an error occurs while signing the SAML AuthnRequest message
     */
    protected static AuthnRequest setSignature(AuthnRequest authnRequest, String signatureAlgorithm,
            X509Credential credential) throws SSOException {
        try {
            Signature signature = setSignatureRaw(signatureAlgorithm, credential);
            authnRequest.setSignature(signature);

            List<Signature> signatureList = new ArrayList<>();
            signatureList.add(signature);

            // Marshall and sign
            MarshallerFactory marshallerFactory = org.opensaml.xml.Configuration.getMarshallerFactory();
            Marshaller marshaller = marshallerFactory.getMarshaller(authnRequest);
            marshaller.marshall(authnRequest);

            //  Initializes and configures the library
            Init.init();
            //  Signer is responsible for creating the digital signatures for the given XML Objects.
            //  Signs the XML Objects based on the given order of the Signature list
            Signer.signObjects(signatureList);
            return authnRequest;
        } catch (MarshallingException | SignatureException e) {
            throw new SSOException("Error while signing the SAML 2.0 AuthnRequest message", e);
        }
    }

    /**
     * Applies the XML Digital Signature to the SAML 2.0 based Logout Request (LogoutRequest).
     *
     * @param logoutRequest      the SAML 2.0 based Logout Request (LogoutRequest)
     * @param signatureAlgorithm the algorithm used to compute the signature
     * @param credential         the signature signing credential
     * @return the SAML 2.0 based Logout Request (LogoutRequest) with XML Digital Signature set
     * @throws SSOException if an error occurs while signing the SAML 2.0 LogoutRequest message
     */
    protected static LogoutRequest setSignature(LogoutRequest logoutRequest, String signatureAlgorithm,
            X509Credential credential) throws SSOException {
        try {
            Signature signature = setSignatureRaw(signatureAlgorithm, credential);
            logoutRequest.setSignature(signature);

            List<Signature> signatureList = new ArrayList<>();
            signatureList.add(signature);

            // Marshall and Sign
            MarshallerFactory marshallerFactory = org.opensaml.xml.Configuration.getMarshallerFactory();
            Marshaller marshaller = marshallerFactory.getMarshaller(logoutRequest);
            marshaller.marshall(logoutRequest);

            //  Initializes and configures the library
            Init.init();
            //  Signer is responsible for creating the digital signatures for the given XML Objects.
            //  Signs the XML Objects based on the given order of the Signature list
            Signer.signObjects(signatureList);
            return logoutRequest;
        } catch (MarshallingException | SignatureException e) {
            throw new SSOException("Error while signing the SAML 2.0 based LogoutRequest message", e);
        }
    }

    /**
     * Generates an XML Object representing an enveloped or detached XML Digital Signature.
     *
     * @param signatureAlgorithm the algorithm used to compute the signature
     * @param credential         the signature signing credentials
     * @return an XML Object representing an enveloped or detached XML Digital Signature
     * @throws SSOException if an error occurs while getting the signature
     */
    private static Signature setSignatureRaw(String signatureAlgorithm, X509Credential credential) throws SSOException {
        Signature signature = (Signature) buildXMLObject(Signature.DEFAULT_ELEMENT_NAME);
        signature.setSigningCredential(credential);
        signature.setSignatureAlgorithm(signatureAlgorithm);
        signature.setCanonicalizationAlgorithm(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

        try {
            KeyInfo keyInfo = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
            X509Data data = (X509Data) buildXMLObject(X509Data.DEFAULT_ELEMENT_NAME);
            X509Certificate cert = (X509Certificate) buildXMLObject(X509Certificate.DEFAULT_ELEMENT_NAME);
            String value = org.apache.xml.security.utils.Base64.encode(credential.getEntityCertificate().getEncoded());
            cert.setValue(value);
            data.getX509Certificates().add(cert);
            keyInfo.getX509Datas().add(data);
            signature.setKeyInfo(keyInfo);
            return signature;
        } catch (CertificateEncodingException e) {
            throw new SSOException("Error getting certificate", e);
        }
    }

    /**
     * Builds a SAML 2.0 based XML object using the fully qualified name.
     *
     * @param objectQualifiedName fully qualified name
     * @return a SAML 2.0 based XML object
     * @throws SSOException if an error occurs while retrieving the builder for the fully qualified name
     */
    private static XMLObject buildXMLObject(QName objectQualifiedName) throws SSOException {
        XMLObjectBuilder builder = org.opensaml.xml.Configuration.getBuilderFactory().getBuilder(objectQualifiedName);
        if (builder == null) {
            throw new SSOException("Unable to retrieve builder for object QName " + objectQualifiedName);
        }
        return builder.buildObject(objectQualifiedName.getNamespaceURI(), objectQualifiedName.getLocalPart(),
                objectQualifiedName.getPrefix());
    }

    /**
     * Applies the XML Digital Signature to the HTTP query string specified.
     *
     * @param httpQueryString the primary HTTP query string which is to be digitally signed
     * @param credential      an entity credential associated with X.509 Public Key Infrastructure
     * @throws SSOException if an error occurs while applying the SAML 2.0 Redirect binding signature
     */
    protected static void addDeflateSignatureToHTTPQueryString(StringBuilder httpQueryString, X509Credential credential)
            throws SSOException {
        try {
            httpQueryString.append("&SigAlg=").
                    append(URLEncoder.encode(XMLSignature.ALGO_ID_SIGNATURE_RSA, "UTF-8").trim());

            java.security.Signature signature = java.security.Signature.getInstance("SHA1withRSA");
            signature.initSign(credential.getPrivateKey());
            signature.update(httpQueryString.toString().getBytes(Charset.forName("UTF-8")));
            byte[] signatureByteArray = signature.sign();

            String signatureBase64encodedString = Base64.encodeBytes(signatureByteArray, Base64.DONT_BREAK_LINES);
            httpQueryString.append("&Signature=").
                    append(URLEncoder.encode(signatureBase64encodedString, "UTF-8").trim());
        } catch (NoSuchAlgorithmException | InvalidKeyException |
                java.security.SignatureException | UnsupportedEncodingException e) {
            throw new SSOException("Error applying SAML 2.0 Redirect Binding signature", e);
        }
    }
}
