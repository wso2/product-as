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
package org.wso2.appserver.webapp.security.sso.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Response;
import org.wso2.appserver.webapp.security.sso.utils.SSOUtils;
import org.wso2.appserver.webapp.security.sso.utils.exception.SSOException;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * A Java bean class which represents a user logged-in session.
 *
 * @since 6.0.0
 */
public class LoggedInSession implements Serializable {
    private static final long serialVersionUID = 1639369078633501892L;
    private static final String emptyString = "";

    private SAML2SSO saml2SSO;

    public SAML2SSO getSAML2SSO() {
        return saml2SSO;
    }

    public void setSAML2SSO(SAML2SSO saml2SSO) {
        this.saml2SSO = saml2SSO;
    }

    /**
     * A nested static class which represents an access token.
     */
    public static class AccessTokenResponseBean implements Serializable {
        private static final long serialVersionUID = -3976452423669184620L;

        @XmlAttribute(name = "access_token")
        @SerializedName("access_token")
        private String accessToken;

        @XmlAttribute(name = "refresh_token")
        @SerializedName("refresh_token")
        private String refreshToken;

        @XmlAttribute(name = "token_type")
        @SerializedName("token_type")
        private String tokenType;

        @XmlAttribute(name = "expires_in")
        @SerializedName("expires_in")
        private String expiresIn;

        /**
         * Serializes this {@code AccessTokenResponseBean} object to its JSON representation.
         *
         * @return the serialized {@link String} form of the JSON representation of this object
         */
        public String serialize() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }

        /**
         * Deserialize the {@code accessTokenResponseBeanString} JSON representation to its
         * {@code AccessTokenResponseBean} form.
         *
         * @param accessTokenResponseBeanString the {@link String} JSON representation to be deserialized
         * @return the deserialized object
         */
        public AccessTokenResponseBean deSerialize(String accessTokenResponseBeanString) {
            Gson gson = new Gson();
            return gson.fromJson(accessTokenResponseBeanString, AccessTokenResponseBean.class);
        }
    }

    /**
     * A static nested class which represents the SAML 2.0 specific single-sign-on (SSO) details to be held
     * in a user logged-in session.
     */
    public static class SAML2SSO implements Serializable {
        private static final long serialVersionUID = -2832436047480647011L;

        private String subjectId;
        private Response response;
        private String responseString;
        private Assertion assertion;
        private String assertionString;
        private AccessTokenResponseBean accessTokenResponseBean;
        private String sessionIndex;
        private Map<String, String> subjectAttributes;

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public void setSubjectAttributes(Map<String, String> samlSSOAttributes) {
            this.subjectAttributes = samlSSOAttributes;
        }

        public String getSessionIndex() {
            return sessionIndex;
        }

        public void setSessionIndex(String sessionIndex) {
            this.sessionIndex = sessionIndex;
        }

        public void setSAMLResponse(Response samlResponse) {
            this.response = samlResponse;
        }

        public void setResponseString(String responseString) {
            this.responseString = responseString;
        }

        public void setAssertion(Assertion samlAssertion) {
            this.assertion = samlAssertion;
        }

        public void setAssertionString(String samlAssertionString) {
            this.assertionString = samlAssertionString;
        }

        /*
            These are the two default methods which would be executed during the serialization and deserialization
            process of a LoggedInSession instance.
        */

        /**
         * Writes this {@code LoggedInSession} instance to the specified {@code ObjectOutputStream}.
         * <p>
         * This is the default {@code writeObject} method executed during the serialization process of this instance.
         *
         * @param stream the {@link java.io.ObjectOutputStream} to which this LoggedInSession instance is to be
         *               written
         * @throws IOException if there are I/O errors while writing to the underlying stream
         */
        private void writeObject(java.io.ObjectOutputStream stream) throws IOException {
            stream.writeObject(subjectId);
            stream.writeObject(responseString);
            stream.writeObject(assertionString);
            stream.writeObject(sessionIndex);
            if (accessTokenResponseBean != null) {
                stream.writeObject(accessTokenResponseBean.serialize());
            } else {
                stream.writeObject(emptyString);
            }
            stream.writeObject(subjectAttributes);
        }

        /**
         * Reads this {@code LoggedInSession} instance to the specified {@code ObjectInputStream}.
         * <p>
         * This is the default {@code readObject} method executed during the deSerialization process of this instance.
         *
         * @param stream the serialized {@link java.io.ObjectInputStream} from which the LoggedInSession instance is
         *               to be read
         * @throws IOException            if I/O errors occurred while reading from the underlying stream
         * @throws ClassNotFoundException if class definition of a serialized object is not found
         * @throws SSOException           if an error occurs during unmarshalling
         */
        @SuppressWarnings("unchecked")
        private void readObject(java.io.ObjectInputStream stream)
                throws IOException, ClassNotFoundException, SSOException {
            subjectId = (String) stream.readObject();

            responseString = (String) stream.readObject();
            if ((responseString != null) && (!emptyString.equals(responseString))) {
                response = (Response) SSOUtils.unmarshall(responseString);
            }

            setAssertionString((String) stream.readObject());
            if ((responseString != null) && (!emptyString.equals(assertionString))) {
                assertion = (Assertion) SSOUtils.unmarshall(assertionString);
            }

            sessionIndex = (String) stream.readObject();
            String accessTokenResponseBeanString = (String) stream.readObject();
            if (!emptyString.equals(accessTokenResponseBeanString)) {
                accessTokenResponseBean = accessTokenResponseBean.deSerialize(accessTokenResponseBeanString);
            } else {
                accessTokenResponseBean = null;
            }
            subjectAttributes = (Map<String, String>) stream.readObject();
        }
    }
}
