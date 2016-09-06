package org.wso2.appserver.apieverywhere;

import com.google.gson.Gson;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.json.JSONObject;
import org.wso2.appserver.apieverywhere.utils.API;
import org.wso2.appserver.apieverywhere.utils.APICreateRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 *
 */
public class APIPublisher extends Thread {

    private static final Log log = LogFactory.getLog(APIPublisher.class);
    private final APICreateRequest apiCreateRequest;
    private final List<API> generatedAPIs;


    public APIPublisher(APICreateRequest apiCreateRequest, List<API> generatedAPIs) {
        this.apiCreateRequest = apiCreateRequest;
        this.generatedAPIs = generatedAPIs;
    }

    @Override
    public void run() {
//        String clientId = "fzr7f4asH5az3Ef4b7qrVJITYTka";
//        String clientSecret = "dYBYDKcfYRtOc6jVAIvAz0JCD2sa";
//        String key = clientId + ":" + clientSecret;
//        byte[]   bytesEncoded = Base64.encodeBase64(key.getBytes());
        String encodedKey = "ZnpyN2Y0YXNINWF6M0VmNGI3cXJWSklUWVRrYTpkWUJZREtjZllSdE9jNmpWQUl2QXowSkNEMnNh";

        //ToDo: get the required info from wso2-as.xml
        String accessTokenUrl = "https://127.0.0.1:8243/token";
        //ToDo: replace curl with http method
        String[] accessTokenCommand = {"curl", "-k", "-d",
                "grant_type=password&username=admin&password=admin&scope=apim:api_create", "-H",
                "Authorization : Basic " + encodedKey, accessTokenUrl};
        String accessToken;
        try {
            JSONObject accessTokenResponse = curlCommand(accessTokenCommand);
            accessToken = (String) accessTokenResponse.get("access_token");
            String publishApiUrl = "https://127.0.0.1:9443/api/am/publisher/v0.10/apis";
            apiCreateRequest.produceSample(generatedAPIs);
            Gson gson = new Gson();
            String apiJson = gson.toJson(apiCreateRequest);
            String[] publishApiCommand = {"curl", "-k", "-H", "Authorization: Bearer " + accessToken,
                    "-H", "Content-Type: application/json", "-X", "POST", "-d", apiJson, publishApiUrl};
            JSONObject publishAPIResponse = curlCommand(publishApiCommand);
            log.info("Response of API publish JSON....................");
            log.info(publishAPIResponse);
        } catch (IOException e) {
            log.error(e);
        }
    }

    private JSONObject curlCommand(String[] command) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        InputStreamReader inputStreamReader = null;
        InputStream inputStream = null;
        Process p = null;
        try {
            ProcessBuilder process = new ProcessBuilder(command);
            p = process.start();
            inputStream = p.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            log.error(e);
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (p != null) {
                p.destroy();
            }
        }
        return new JSONObject(builder.toString());
    }
}
