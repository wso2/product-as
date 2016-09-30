package org.wso2.appserver.configuration.server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Java class which models a holder for server level api-everywhere configurations
 *
 * @since 6.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class AppServerApiEverywhere {
    @XmlElement(name = "ClientId")
    private String clientId;
    @XmlElement(name = "ClientSecret")
    private String clientSecret;
    @XmlElement(name = "ApiPublisherUrl")
    private String apiPublisherUrl;
    @XmlElement(name = "ApplicationServerUrl")
    private String applicationServerUrl;
    @XmlElement(name = "AuthenticationUrl")
    private String authenticationUrl;

    public String getKeys() {
        return clientId + ":" + clientSecret;
    }


    public String getApiPublisherUrl() {
        return apiPublisherUrl;
    }

    public void setApiPublisherUrl(String apiPublisherUrl) {
        this.apiPublisherUrl = apiPublisherUrl;
    }

    public String getApplicationServerUrl() {
        return applicationServerUrl;
    }

    public void setApplicationServerUrl(String applicationServerUrl) {
        this.applicationServerUrl = applicationServerUrl;
    }

    public String getApiAuthenticationUrl() {
        return authenticationUrl;
    }

    public void setApiAuthenticationUrl(String authenticationUrl) {
        this.authenticationUrl = authenticationUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
