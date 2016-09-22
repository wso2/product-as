package org.wso2.appserver.apieverywhere;

import com.google.gson.Gson;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.json.JSONObject;
import org.wso2.appserver.apieverywhere.exceptions.APIEverywhereException;
import org.wso2.appserver.apieverywhere.utils.APICreateRequest;
import org.wso2.appserver.apieverywhere.utils.Constants;
import org.wso2.appserver.configuration.listeners.ServerConfigurationLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 *  The thread class which create the produced APIs into the API Publisher
 *
 * @since 6.0.0
 */
class APICreator extends Thread {

    private static final Log log = LogFactory.getLog(APICreator.class);
    private final APICreateRequest apiCreateRequest;

    APICreator(APICreateRequest apiCreateRequest) {
        this.apiCreateRequest = apiCreateRequest;
    }


    @Override
    public void run() throws APIEverywhereException {
        try {
            String key  = ServerConfigurationLoader.
                    getServerConfiguration().getApiEverywhereConfiguration().getKeys();
            String apiPublisherUrl = ServerConfigurationLoader.
                    getServerConfiguration().getApiEverywhereConfiguration().getApiPublisherUrl();
            String authenticationUrl = ServerConfigurationLoader.
                    getServerConfiguration().getApiEverywhereConfiguration().getApiAuthenticationUrl();

            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes("utf-8"));

            String accessToken = httpCall(encodedKey, authenticationUrl);
            Gson gson = new Gson();
            String apiJson = gson.toJson(apiCreateRequest);
            createAPI(accessToken, apiJson, apiPublisherUrl);
        } catch (UnsupportedEncodingException e) {
            log.error("Failed to generate encoded key: " + e);
            throw new APIEverywhereException("Failed to generate encoded key", e);
        }
    }

    /**
     * Https call for access token from authentication end point
     *
     * @param encodedKey     the encoded key from clentId:clientSecret
     * @param authenticationUrl the authentication end point of the WSO2 API manager
     * @return JSONObject of the response
     */
    private String httpCall(String encodedKey, String authenticationUrl) throws APIEverywhereException {
        String requestAccessTokenUrl = authenticationUrl + "/token";
        SSLSocketFactory sslSocketFactory = generateSSL();
        try {
            //Create connection
            URL url = new URL(requestAccessTokenUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(Constants.HTTP_POST_METHOD);
            connection.setRequestProperty("Authorization", "Basic " + encodedKey);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setSSLSocketFactory(sslSocketFactory);
            // for development purpose only
            connection.setHostnameVerifier((hostname, sslSession) -> hostname.equals("127.0.0.1"));

            try (OutputStreamWriter os = new OutputStreamWriter(connection.getOutputStream(),
                    "utf-8")) {
                os.write("grant_type=client_credentials&scope=apim:api_create");
            }

            int responseCode = connection.getResponseCode();

            StringBuilder responseBuilder = new StringBuilder();
            if (responseCode != Constants.REQUEST_ACCESS_TOKEN_SUCCESS_CODE) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(
                        connection.getErrorStream(), "utf-8");
                     BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                }
                connection.disconnect();
                JSONObject errorObject = new JSONObject(responseBuilder.toString());
                log.error("Authentication failed: " + errorObject.get("error"));
                throw new APIEverywhereException("Authentication failed ", null);
            }

            try (InputStreamReader is = new InputStreamReader(connection.getInputStream(), "utf-8");
                 BufferedReader bufferedReader = new BufferedReader(is)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
            connection.disconnect();
            JSONObject accessTokenResponse = new JSONObject(responseBuilder.toString());
            String accessToken = (String) accessTokenResponse.get("access_token");
            log.info("Access token received");
            return accessToken;
        } catch (IOException e) {
            log.error("Error in establishing connection : " + e);
            throw new APIEverywhereException("Error in establishing connection ", e);
        }
    }


    /**
     * Https call to create API in API Publisher.
     *  @param accessToken     access token for the request
     * @param apiJson   APICreateRequest object as string which to publish
     * @param apiPublisherUrl the base usl of API Publisher
     */
    private void createAPI(String accessToken, String apiJson, String apiPublisherUrl) throws APIEverywhereException {
        String publishApiUrl = apiPublisherUrl + "/api/am/publisher/" + Constants.API_PUBLISHER_API_VERSION + "/apis";
        SSLSocketFactory sslSocketFactory = generateSSL();
        try {
            //Create connection
            URL url = new URL(publishApiUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(Constants.HTTP_POST_METHOD);
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setSSLSocketFactory(sslSocketFactory);
            // for development purpose only
            connection.setHostnameVerifier((hostname, sslSession) -> hostname.equals("127.0.0.1"));

            try (OutputStreamWriter os = new OutputStreamWriter(connection.getOutputStream(), "utf-8")) {
                os.write(apiJson);
            }
            int responseCode = connection.getResponseCode();

            StringBuilder responseBuilder = new StringBuilder();
            if (responseCode != Constants.CREATE_API_SUCCESS_CODE) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(
                        connection.getErrorStream(), "utf-8");
                     BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                }
                connection.disconnect();
                JSONObject errorObject = new JSONObject(responseBuilder.toString());
                log.error("Error in creating API: " + errorObject.get("description"));
                throw new APIEverywhereException("Error in ceating API ", null);
            }

            try (InputStreamReader inputStreamReader = new InputStreamReader(
                    connection.getInputStream(), "utf-8");
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
            connection.disconnect();
            JSONObject apiCreateResponse = new JSONObject(responseBuilder.toString());
            log.info("API created successfully: API id - " + apiCreateResponse.get("id"));
        } catch (IOException e) {
            log.error("Error in establishing connection with API Publisher: " + e);
            throw new APIEverywhereException("Error in establishing connection with API Publisher ", e);

        }
    }

    /**
     * Produce SSL certificate
     *
     * @return SSLSocketFactory
     */
    private SSLSocketFactory generateSSL() throws APIEverywhereException {
        String keystorePathString = System.getProperty(
                org.wso2.appserver.Constants.JAVA_KEYSTORE_LOCATION);
        String keystorePasswordString = System.getProperty(
                org.wso2.appserver.Constants.JAVA_KEYSTORE_PASSWORD);

        Path keyStorePath = Paths.get(URI.create(keystorePathString).getPath());
        try (InputStream keystoreInputStream = Files.newInputStream(keyStorePath)) {
            KeyStore keyStore = KeyStore.getInstance(System.getProperty(
                    org.wso2.appserver.Constants.JAVA_KEYSTORE_TYPE));
            keyStore.load(keystoreInputStream, keystorePasswordString.toCharArray());

            TrustManagerFactory tmf =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);


            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tmf.getTrustManagers(), null);

            return ctx.getSocketFactory();
        } catch (IOException e) {
            log.error("Provided keystore file does not exist: " + e);
            throw new APIEverywhereException("File path specified for the keystore does not exist "
                    , e);
        } catch (CertificateException e) {
            log.error("Failed to create SSL certificate: " + e);
            throw new APIEverywhereException("Failed to create SSL certificate ", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("Wrong algorithm applied for certificate creation: " + e);
            throw new APIEverywhereException("Wrong algorithm applied for certificate creation ", e);
        } catch (KeyStoreException e) {
            log.error("Failed to load to provided keystore: " + e);
            throw new APIEverywhereException("Failed to load to provided keystore ", e);
        } catch (KeyManagementException e) {
            log.error("Failed to load KeyManagement: " + e);
            throw new APIEverywhereException("Failed to load KeyManagement ", e);
        }
    }
}
