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
package org.wso2.appserver.apieverywhere;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.json.JSONObject;
import org.wso2.appserver.apieverywhere.exceptions.APIEverywhereException;
import org.wso2.appserver.apieverywhere.utils.Constants;
import org.wso2.appserver.configuration.listeners.ServerConfigurationLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;

/**
 *  The thread class which create the produced APIs into the API Publisher
 *
 * @since 6.0.0
 */
class APICreator implements Runnable {

    private static final Log log = LogFactory.getLog(APICreator.class);
    private String apiCreateRequest;

    void addAPIRequest(String apiCreateRequest) {
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

            if (!authenticationUrl.startsWith("https://")) {
                authenticationUrl = "https://" + authenticationUrl;
            }
            if (authenticationUrl.endsWith("/")) {
                authenticationUrl = authenticationUrl.substring(0, authenticationUrl.lastIndexOf("/"));
            }

            if (!apiPublisherUrl.startsWith("https://")) {
                apiPublisherUrl = "https://" + apiPublisherUrl;
            }
            if (apiPublisherUrl.endsWith("/")) {
                apiPublisherUrl = apiPublisherUrl.substring(0, apiPublisherUrl.lastIndexOf("/"));
            }

            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes("utf-8"));

            String accessToken = httpCall(encodedKey, authenticationUrl);
                createAPI(accessToken, apiCreateRequest, apiPublisherUrl);
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
        try {
            //Create connection
            URL url = new URL(requestAccessTokenUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(Constants.HTTP_POST_METHOD);
            connection.setRequestProperty("Authorization", "Basic " + encodedKey);
            connection.setDoOutput(true);
            connection.setDoInput(true);

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
     *
     *  @param accessToken     access token for the request
     * @param apiJson   APICreateRequest object as string which to publish
     * @param apiPublisherUrl the base usl of API Publisher
     */
    private void createAPI(String accessToken, String apiJson, String apiPublisherUrl) throws APIEverywhereException {
        String publishApiUrl = apiPublisherUrl + "/api/am/publisher/" + Constants.API_PUBLISHER_API_VERSION + "/apis";
        try {
            //Create connection
            URL url = new URL(publishApiUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(Constants.HTTP_POST_METHOD);
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);

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
            log.info("API created successfully: API id- " + apiCreateResponse.get("id") + " API name- "
                    + apiCreateResponse.get("name"));
        } catch (IOException e) {
            log.error("Error in establishing connection with API Publisher: " + e);
            throw new APIEverywhereException("Error in establishing connection with API Publisher ", e);

        }
    }
}
