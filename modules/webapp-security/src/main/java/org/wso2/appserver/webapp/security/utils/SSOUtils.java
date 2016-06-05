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
package org.wso2.appserver.webapp.security.utils;

import org.apache.catalina.connector.Request;
import org.apache.commons.lang3.StringUtils;
import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.XMLHelper;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.wso2.appserver.configuration.server.AppServerSecurity;
import org.wso2.appserver.webapp.security.Constants;
import org.wso2.appserver.webapp.security.bean.RelayState;
import org.wso2.appserver.webapp.security.saml.signature.SSOX509Credential;
import org.wso2.appserver.webapp.security.saml.signature.X509CredentialImplementation;
import org.wso2.appserver.webapp.security.utils.exception.SSOException;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
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
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This class contains utility functions used within the single-sign-on (SSO) implementation.
 *
 * @since 6.0.0
 */
public class SSOUtils {
    //  indicates whether the OpenSAML library modules have been already bootstrapped or not
    private static boolean isBootstrapped = false;

    /**
     * Prevents instantiating the SSOUtils utility class.
     */
    private SSOUtils() {
    }

    /**
     * General utility functions used within the single-sign-on (SSO) implementation.
     */

    /**
     * Generates a unique id.
     *
     * @return a unique id
     */
    public static String createID() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20]; // 160 bit
        random.nextBytes(bytes);
        char[] characterMapping = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p'};

        char[] characters = new char[40];
        IntStream.range(0, bytes.length)
                .forEach(index -> {
                    int left = (bytes[index] >> 4) & 0x0f;
                    int right = bytes[index] & 0x0f;
                    characters[index * 2] = characterMapping[left];
                    characters[index * 2 + 1] = characterMapping[right];
                });

        return String.valueOf(characters);
    }

    /**
     * Utility functions when initializing SSO agent configurations.
     */

    /**
     * Returns an application server URL constructed using the specified request.
     *
     * @param request the servlet request processed
     * @return the application server URL constructed
     */
    public static Optional<String> constructApplicationServerURL(Request request) {
        if (request == null) {
            return Optional.empty();
        }
        StringBuilder appServerURL = new StringBuilder(request.getConnector().getProtocol() + "://");
        String requestHost = request.getHost().getName();
        int requestPort = request.getConnector().getPort();

        return Optional.of(appServerURL.append(requestHost).append(":").append(requestPort).toString());
    }


    /**
     * Returns the query parameters split out of the query parameter string.
     *
     * @param queryParameterString the query parameter {@link String}
     * @return the split query parameters
     */
    public static Map<String, String[]> getSplitQueryParameters(String queryParameterString) {
        Map<String, String[]> queryParameters = new HashMap<>();

        if (!StringUtils.isBlank(queryParameterString)) {
            Map<String, List<String>> queryParameterMap = new HashMap<>();
            Stream.of(queryParameterString.split("&"))
                    .map(queryParameter -> queryParameter.split("="))
                    .forEach(splitParameters -> {
                        if (splitParameters.length == 2) {
                            if (queryParameterMap.get(splitParameters[0]) != null) {
                                queryParameterMap.get(splitParameters[0]).add(splitParameters[1]);
                            } else {
                                List<String> newList = new ArrayList<>();
                                newList.add(splitParameters[1]);
                                queryParameterMap.put(splitParameters[0], newList);
                            }
                        }
                        queryParameterMap.entrySet()
                                .stream()
                                .forEach(entry -> {
                                    String[] values = entry.getValue().toArray(new String[entry.getValue().size()]);
                                    queryParameters.put(entry.getKey(), values);
                                });
                    });
        }

        return queryParameters;
    }

    /**
     * Returns a unique id value for the SAML 2.0 service provider application based on its context path.
     * <p>
     * An optional id is returned based on the context path provided.
     *
     * @param contextPath the context path of the service provider application
     * @param hostAppBase the name of the Tomcat host's web application base
     * @return a unique id value for the SAML 2.0 service provider application based on its context path
     */
    public static Optional<String> generateIssuerID(String contextPath, String hostAppBase) {
        if (contextPath != null) {
            String issuerId = contextPath.replaceFirst("/" + hostAppBase, "").replace("/", "_");
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
     * @param contextPath the context path of the service provider application
     * @param acsBase     the base URL of the Assertion Consumer Service
     * @param acsPostFix  the postfix which identifies a SAML 2.0 Assertion Consumer URL
     * @return a SAML 2.0 Assertion Consumer URL based on service provider application context path
     */
    public static Optional<String> generateConsumerURL(String contextPath, String acsBase, String acsPostFix) {
        if ((contextPath != null) && (acsBase != null) && (acsPostFix != null)) {
            return Optional.of(acsBase + contextPath + "/" + acsPostFix);
        } else {
            return Optional.empty();
        }
    }


    /**
     * Returns a {@code KeyStore} based on keystore properties specified.
     *
     * @param configuration the keystore properties
     * @return the {@link KeyStore} instance generated
     * @throws SSOException if an error occurs while generating the {@link KeyStore} instance
     */
    public static Optional generateKeyStore(AppServerSecurity configuration) throws SSOException {
        if ((configuration == null) || (configuration.getKeystore() == null)) {
            return Optional.empty();
        }

        AppServerSecurity.Keystore keystoreConfiguration = configuration.getKeystore();

        String keystorePathString = keystoreConfiguration.getLocation();
        String keystorePasswordString = keystoreConfiguration.getPassword();
        if ((keystorePasswordString == null) || (keystorePathString == null)) {
            return Optional.empty();
        }

        Path keyStorePath = Paths.get(URI.create(keystorePathString).getPath());
        if (Files.exists(keyStorePath)) {
            try (InputStream keystoreInputStream = Files.newInputStream(keyStorePath)) {
                KeyStore keyStore = KeyStore.getInstance(keystoreConfiguration.getType());
                keyStore.load(keystoreInputStream, keystorePasswordString.toCharArray());
                return Optional.of(keyStore);
            } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
                throw new SSOException("Error while loading the key store", e);
            }
        } else {
            throw new SSOException("File path specified for the keystore does not exist");
        }
    }

    /**
     * Utility functions of the flow of single-sign-on and single-logout.
     */

    /**
     * Generates a {@code RelayState} based on the {@code Request}.
     *
     * @param request the {@link Request} instance
     * @return the created {@link RelayState} instance
     */
    public static RelayState generateRelayState(Request request) {
        RelayState relayState = new RelayState();

        relayState.setRequestedURL(request.getRequestURI());
        relayState.setRequestQueryString(request.getQueryString());
        relayState.setRequestParameters(request.getParameterMap());

        return relayState;
    }

    /**
     * Sends character data specified by the {@code htmlPayload} in the servlet response body.
     *
     * @param response    the servlet response body in which character data are to be sent
     * @param htmlPayload the character data to be sent in the servlet body
     * @throws SSOException if an error occurs while writing character data to the servlet
     *                      response body
     */
    public static void sendCharacterData(HttpServletResponse response, String htmlPayload) throws SSOException {
        try {
            Writer writer = response.getWriter();
            writer.write(htmlPayload);
            response.flushBuffer();
            //  not closing the Writer instance, as its creator is the HttpServletResponse
        } catch (IOException e) {
            throw new SSOException("Error occurred while writing to HttpServletResponse", e);
        }
    }

    /**
     * OpenSAML utility functions.
     */

    /**
     * Initializes the OpenSAML library modules, if not initialized yet.
     *
     * @throws SSOException if an error occurs when bootstrapping the OpenSAML2 library
     */
    public static void doBootstrap() throws SSOException {
        try {
            if (!isBootstrapped) {
                InitializationService.initialize();
                isBootstrapped = true;
            }
        } catch (InitializationException e) {
            throw new SSOException("Error in bootstrapping the OpenSAML library", e);
        }
    }

    /**
     * Applies the XML Digital Signature to the SAML 2.0 based Request.
     *
     * @param request            the SAML 2.0 based Request
     * @param signatureAlgorithm the algorithm used to compute the signature
     * @param credential         the signature signing credential
     * @return the SAML 2.0 based Request with XML Digital Signature set
     * @throws SSOException if an error occurs while signing the SAML 2.0 based Request message
     */
    public static RequestAbstractType setSignature(RequestAbstractType request, String signatureAlgorithm,
                                                   X509Credential credential) throws SSOException {
        try {
            Signature signature = setSignatureRaw(signatureAlgorithm, credential);
            request.setSignature(signature);

            List<Signature> signatureList = new ArrayList<>();
            signatureList.add(signature);

            //  marshall and sign
            Marshaller marshaller = new MarshallerFactory().getMarshaller(request);
            if (marshaller != null) {
                marshaller.marshall(request);
            }

            //  initializes and configures the library
            Init.init();
            //  signer is responsible for creating the digital signatures for the given XML Objects,
            //  signs the XML Objects based on the given order of the Signature list
            Signer.signObjects(signatureList);
            return request;
        } catch (MarshallingException | SignatureException e) {
            throw new SSOException("Error while signing the SAML 2.0 Request message", e);
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
        XMLObjectBuilder builder = new XMLObjectBuilderFactory().getBuilder(objectQualifiedName);
        if (builder == null) {
            throw new SSOException("Unable to retrieve builder for object QName " + objectQualifiedName);
        }
        return builder.buildObject(objectQualifiedName.getNamespaceURI(), objectQualifiedName.getLocalPart(),
                objectQualifiedName.getPrefix());
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
    public static String encodeRequestMessage(RequestAbstractType requestMessage, String binding) throws SSOException {
        Element authDOM = requestMessage.getDOM();
        StringWriter writer = new StringWriter();
        //  writes the node out to the writer using the DOM
        Optional.ofNullable(authDOM)
                .ifPresent(dom -> XMLHelper.writeNode(dom, writer));

        if (SAMLConstants.SAML2_REDIRECT_BINDING_URI.equals(binding)) {
            //  compresses the message using default DEFLATE encoding, Base 64 encode and URL encode
            Deflater deflater = new Deflater(Deflater.DEFLATED, true);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream,
                    deflater)) {
                deflaterOutputStream.write(writer.toString().getBytes(Charset.forName(Constants.UTF8_ENC)));
            } catch (IOException e) {
                throw new SSOException("Error occurred while deflate encoding SAML 2.0 request", e);
            }

            String encodedRequestMessage = Base64.
                    encodeBytes(byteArrayOutputStream.toByteArray(), Base64.DONT_BREAK_LINES);
            try {
                return URLEncoder.encode(encodedRequestMessage, Constants.UTF8_ENC).trim();
            } catch (UnsupportedEncodingException e) {
                throw new SSOException("Error occurred while encoding SAML 2.0 request", e);
            }
        } else {
            //  if the binding type encountered is HTTP-POST binding or an unsupported binding type
            return Base64.encodeBytes(writer.toString().getBytes(Charset.forName(Constants.UTF8_ENC)),
                    Base64.DONT_BREAK_LINES);
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
            Marshaller marshaller = new MarshallerFactory().getMarshaller(xmlObject);
            Element element = marshaller.marshall(xmlObject);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS implementation = (DOMImplementationLS) registry.getDOMImplementation("LS");
            LSSerializer writer = implementation.createLSSerializer();
            LSOutput output = implementation.createLSOutput();
            output.setByteStream(byteArrayOutputStream);
            writer.write(element, output);
            return new String(byteArrayOutputStream.toByteArray(), Charset.forName(Constants.UTF8_ENC));
        } catch (ClassNotFoundException | InstantiationException | MarshallingException | IllegalAccessException e) {
            throw new SSOException("Error in marshalling SAML Assertion", e);
        }
    }

    /**
     * Returns a SAML 2.0 based XML content representation from the string value representing the XML syntax.
     *
     * @param xmlString the {@link String} representation of the XML content
     * @return an XML object from the {@link String} value representing the XML syntax
     * @throws SSOException if an error occurs when unmarshalling the XML string representation
     */
    public static XMLObject unmarshall(String xmlString) throws SSOException {
        try {
            DocumentBuilder docBuilder = SSOUtils.getDocumentBuilder(false, true, new XMLEntityResolver());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(
                    xmlString.getBytes(Charset.forName(Constants.UTF8_ENC)));
            Document document = docBuilder.parse(inputStream);
            Element element = document.getDocumentElement();
            Unmarshaller unmarshaller = new UnmarshallerFactory().getUnmarshaller(element);
            return unmarshaller.unmarshall(element);
        } catch (UnmarshallingException | SAXException | IOException e) {
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
    public static Assertion decryptAssertion(SSOX509Credential ssoAgentX509Credential,
                                             EncryptedAssertion encryptedAssertion) throws SSOException {
        try {
            KeyInfoCredentialResolver keyResolver = new StaticKeyInfoCredentialResolver(
                    new X509CredentialImplementation(ssoAgentX509Credential));

            Optional<EncryptedKey> key = encryptedAssertion.getEncryptedData().getKeyInfo().getEncryptedKeys()
                    .stream()
                    .findFirst();
            EncryptedKey encryptedKey = null;
            if (key.isPresent()) {
                encryptedKey = key.get();
            }
            Decrypter decrypter = new Decrypter(null, keyResolver, null);
            SecretKey decrypterKey;
            if (encryptedKey != null) {
                decrypterKey = (SecretKey) decrypter.decryptKey(encryptedKey, encryptedAssertion.getEncryptedData().
                        getEncryptionMethod().getAlgorithm());
                Credential shared = SSOUtils.getSimpleCredential(decrypterKey);
                decrypter = new Decrypter(new StaticKeyInfoCredentialResolver(shared), null, null);
                decrypter.setRootInNewDocument(true);
            }
            return decrypter.decrypt(encryptedAssertion);
        } catch (DecryptionException e) {
            throw new SSOException("Decrypted assertion error", e);
        }
    }

    /**
     * Get a simple, minimal credential containing a secret (symmetric) key.
     *
     * @param secretKey the symmetric key to wrap
     * @return a credential containing the secret key specified
     */
    private static BasicCredential getSimpleCredential(SecretKey secretKey) {
        if (secretKey == null) {
            throw new IllegalArgumentException("A secret key is required");
        }
        return new BasicCredential(secretKey);
    }

    /**
     * Returns the SAML 2.0 Assertion Attribute Statement content.
     *
     * @param assertion the SAML Assertion whose content is to be returned
     * @return the SAML 2.0 Assertion Attribute Statement content of the SAML 2.0 Assertion specified
     */
    public static Map<String, String> getAssertionStatements(Assertion assertion) {
        Map<String, String> results = new HashMap<>();
        if ((assertion != null) && (assertion.getAttributeStatements() != null)) {
            Stream<AttributeStatement> attributeStatements = assertion.getAttributeStatements().stream();
            attributeStatements.
                    forEach(attributeStatement -> attributeStatement.getAttributes()
                            .stream()
                            .forEach(attribute -> {
                                Optional<XMLObject> value = attribute.getAttributeValues()
                                        .stream()
                                        .findFirst();
                                if (value.isPresent()) {
                                    String attributeValue = value.get().getDOM().getTextContent();
                                    results.put(attribute.getName(), attributeValue);
                                }
                            }));
        }
        return results;
    }

    /**
     * Applies the XML Digital Signature to the HTTP query string specified.
     *
     * @param httpQueryString the primary HTTP query string which is to be digitally signed
     * @param credential      an entity credential associated with X.509 Public Key Infrastructure
     * @throws SSOException if an error occurs while applying the SAML 2.0 Redirect binding signature
     */
    public static void addDeflateSignatureToHTTPQueryString(StringBuilder httpQueryString, X509Credential credential)
            throws SSOException {
        try {
            httpQueryString.append("&SigAlg=").
                    append(URLEncoder.encode(XMLSignature.ALGO_ID_SIGNATURE_RSA, Constants.UTF8_ENC).trim());

            java.security.Signature signature = java.security.Signature.getInstance("SHA1withRSA");
            signature.initSign(credential.getPrivateKey());
            signature.update(httpQueryString.toString().getBytes(Charset.forName(Constants.UTF8_ENC)));
            byte[] signatureByteArray = signature.sign();

            String signatureBase64EncodedString = Base64.encodeBytes(signatureByteArray, Base64.DONT_BREAK_LINES);
            httpQueryString.append("&Signature=").
                    append(URLEncoder.encode(signatureBase64EncodedString, Constants.UTF8_ENC).trim());
        } catch (NoSuchAlgorithmException | InvalidKeyException |
                java.security.SignatureException | UnsupportedEncodingException e) {
            throw new SSOException("Error applying SAML 2.0 Redirect Binding signature", e);
        }
    }

    /**
     * Generates a {@code javax.xml.parsers.DocumentBuilder} instance based on the specified configurations.
     *
     * @param expandEntityReferences true if the parser is to expand entity reference nodes, else false
     * @param namespaceAware         true if the parser provides support for XML namespaces, else false
     * @param entityResolver         the {@link EntityResolver} to be used within the parser, if {@code entityResolver}
     *                               is set to null default implementation is used
     * @return the generated {@link DocumentBuilder} instance
     * @throws SSOException if an error occurs when generating the new DocumentBuilder
     */
    private static DocumentBuilder getDocumentBuilder(boolean expandEntityReferences, boolean namespaceAware,
                                                      EntityResolver entityResolver) throws SSOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        if (!expandEntityReferences) {
            documentBuilderFactory.setExpandEntityReferences(false);
        }
        if (namespaceAware) {
            documentBuilderFactory.setNamespaceAware(true);
        }

        DocumentBuilder docBuilder;
        try {
            docBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new SSOException("Error when generating the new DocumentBuilder", e);
        }
        Optional.ofNullable(entityResolver).ifPresent(docBuilder::setEntityResolver);

        return docBuilder;
    }
}
