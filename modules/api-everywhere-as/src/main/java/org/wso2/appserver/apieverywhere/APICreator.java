package org.wso2.appserver.apieverywhere;

import com.google.gson.Gson;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.json.JSONObject;
import org.wso2.appserver.apieverywhere.utils.APICreateRequest;
import org.wso2.appserver.apieverywhere.utils.APIPath;
import org.wso2.appserver.configuration.listeners.ServerConfigurationLoader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 *  The class which publish the produced APIs into the API Publisher
 *
 * @since 6.0.0
 */
class APICreator extends Thread {

    private static final Log log = LogFactory.getLog(APICreator.class);
    private final APICreateRequest apiCreateRequest;
    private final List<APIPath> generatedApiPaths;


    APICreator(APICreateRequest apiCreateRequest, List<APIPath> generatedApiPaths) {
        this.apiCreateRequest = apiCreateRequest;
        this.generatedApiPaths = generatedApiPaths;
    }


    @Override
    public void run() {
        // TODO: have to get the keys from web-as.xml
        String clientId = "fzr7f4asH5az3Ef4b7qrVJITYTka";
        String clientSecret = "dYBYDKcfYRtOc6jVAIvAz0JCD2sa";


        String key = clientId + ":" + clientSecret;
        String encodedKey = null;
        try {
            encodedKey = Base64.getEncoder().encodeToString(key.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        }

        JSONObject accessTokenResponse = httpCall(encodedKey);
        if (accessTokenResponse != null) {
            String accessToken = (String) accessTokenResponse.get("access_token");
            log.info("access token : " + accessTokenResponse);
            apiCreateRequest.buildAPI(generatedApiPaths);
            Gson gson = new Gson();
            String apiJson = gson.toJson(apiCreateRequest);
            JSONObject jsonObject = createAPI(accessToken, apiJson);
            log.info("response from APIM : " + jsonObject);
        } else {
            log.error("Failed to connect to APIM");
        }
    }

    private JSONObject httpCall(String encodedKey) {
        String requestAccessTokenUrl = "https://127.0.0.1:8243/token";
        try {
            FileInputStream fis = null;
            OutputStreamWriter os = null;
            BufferedReader bufferedReader = null;
            InputStreamReader inputStreamReader = null;
            HttpsURLConnection connection = null;
            try {
                String keystoreLocation =  ServerConfigurationLoader.getServerConfiguration().
                        getSecurityConfiguration().getKeystore().getLocation();
        /* Load the keyStore that includes self-signed cert as a "trusted" entry. */
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                fis = new FileInputStream(keystoreLocation);
                keyStore.load(fis, ServerConfigurationLoader.getServerConfiguration().
                        getSecurityConfiguration().getKeystore().getPassword().toCharArray());
                TrustManagerFactory tmf =
                        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keyStore);
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, tmf.getTrustManagers(), null);
                SSLSocketFactory sslFactory = ctx.getSocketFactory();


                //Create connection
                URL url = new URL(requestAccessTokenUrl);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Basic " + encodedKey);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setSSLSocketFactory(sslFactory);
                connection.setHostnameVerifier((hostname, sslSession) -> {
                    log.info("....................." + hostname);
                    return hostname.equals("127.0.0.1");
                });

                os = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
                os.write("grant_type=password&username=admin&password=admin&scope=apim:api_create");
                os.close();

                int responseCode = connection.getResponseCode();
                log.info("Status code " + responseCode);
                inputStreamReader = new InputStreamReader(connection.getInputStream(), "utf-8");
                bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                return new JSONObject(stringBuilder.toString());
            } finally {
                if (fis != null) {
                    fis.close();
                }
                if (os != null) {
                    os.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException |
                CertificateException e) {
            log.error(e);
        }
        return null;
    }


    private JSONObject createAPI(String accessToken, String apiJson) {
        String publishApiUrl = "https://127.0.0.1:9443/api/am/publisher/v0.10/apis";
        try {
            FileInputStream fis = null;
            OutputStreamWriter os = null;
            BufferedReader bufferedReader = null;
            InputStreamReader inputStreamReader = null;
            HttpsURLConnection connection = null;
            try {
                String keystoreLocation =  ServerConfigurationLoader.getServerConfiguration().
                        getSecurityConfiguration().getKeystore().getLocation();
        /* Load the keyStore that includes self-signed cert as a "trusted" entry. */
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                fis = new FileInputStream(keystoreLocation);
                keyStore.load(fis, "wso2carbon".toCharArray());
                TrustManagerFactory tmf =
                        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keyStore);
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, tmf.getTrustManagers(), null);
                SSLSocketFactory sslFactory = ctx.getSocketFactory();


                //Create connection
                URL url = new URL(publishApiUrl);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setSSLSocketFactory(sslFactory);
                connection.setHostnameVerifier((hostname, sslSession) -> {
                    log.info("....................." + hostname);
                    return hostname.equals("127.0.0.1");
                });

                os = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
                os.write(apiJson);
                os.close();

                int responseCode = connection.getResponseCode();
                log.info("Status code " + responseCode);
                inputStreamReader = new InputStreamReader(connection.getInputStream(), "utf-8");
                bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                return new JSONObject(stringBuilder.toString());
            } finally {
                if (fis != null) {
                    fis.close();
                }
                if (os != null) {
                    os.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException |
                CertificateException e) {
            log.error(e);
        }
        return null;
    }
}
