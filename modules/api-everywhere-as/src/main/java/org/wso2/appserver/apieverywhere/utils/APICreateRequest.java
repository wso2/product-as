package org.wso2.appserver.apieverywhere.utils;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *Utility class to create an API from the web app
 *
 * @since 6.0.0
 */
public class APICreateRequest {

    private static final Log log = LogFactory.getLog(APICreateRequest.class);

    private String name;
    private String description;
    private String context;
    private String version;
    private String apiDefinition;
    private String status;
    private String responseCaching;
    private Integer cacheTimeout;
    private Boolean destinationStatsEnabled;
    private Boolean isDefaultVersion;
    private List<String> transport;
    private List<String> tiers;
    private String visibility;
    private List<Object> visibleRoles = new ArrayList<>();
    private List<Object> visibleTenants = new ArrayList<>();
    private String endpointConfig;

    public void setName(String name) {
        this.name = name;
    }

    public void setContext(String context) {
        this.context = context;
    }


    /**
     * set-up the API object for publishing
     *
     * @param apiPaths     the API paths scanned by API scanner
     *
     */
    public void buildAPI(List<APIPath> apiPaths) {
        description = "automatically created to API publisher.\r\n";
        version = "1.0.0";

        JSONObject apiDefBuilder = new JSONObject();
        JSONObject pathsJSON = new JSONObject();
        for (APIPath apiPath : apiPaths) {
            JSONObject propsJSON = new JSONObject();
            for (APIPath.APIProp apiProp : apiPath.getApiProps()) {
                ArrayList<JSONObject> paramsJSONList = new ArrayList<>();
                for (APIPath.Param param : apiProp.getParams()) {
                    JSONObject apiParamJSON = new JSONObject();
                    apiParamJSON.put("name", param.getParamName());
                    apiParamJSON.put("in", param.getParamType());
                    apiParamJSON.put("type", param.getDataType());
                    paramsJSONList.add(apiParamJSON);
                }
                JSONObject apiPropJSON = new JSONObject();
                apiPropJSON.put("parameters", paramsJSONList);
                apiPropJSON.put("responses", new JSONObject().put("default", ""));
                apiPropJSON.put("produces", apiProp.getProduces());
                apiPropJSON.put("consumes", apiProp.getConsumes());
                propsJSON.put(apiProp.getType(), apiPropJSON);
            }
            pathsJSON.put(apiPath.getUrl(), propsJSON);;
        }
        apiDefBuilder.put("paths", pathsJSON);
        apiDefBuilder.put("schemes", new ArrayList<>(Arrays.asList("http")));
        apiDefBuilder.put("swagger", "2.0");
        apiDefinition = apiDefBuilder.toString();
        status = "CREATED";
        responseCaching = "Disabled";
        cacheTimeout = 0;
        destinationStatsEnabled = false;
        isDefaultVersion = true;
        transport = new ArrayList<>(Arrays.asList("http", "https"));
        tiers = new ArrayList<>(Arrays.asList("Unlimited"));
        visibility = "PUBLIC";
        visibleRoles = new ArrayList<>(Arrays.asList());
        visibleTenants = new ArrayList<>(Arrays.asList());


        JSONObject endPointConfigJSON = new JSONObject();
        JSONObject productionJSON = new JSONObject();
        productionJSON.put("url", "https://localhost:9443/am/sample/pizzashack/v1/api/");
        productionJSON.put("config", "null");

        JSONObject sandboxJSON = new JSONObject();
        sandboxJSON.put("url", "https://localhost:9443/am/sample/pizzashack/v1/api/");
        sandboxJSON.put("config", "null");

        endPointConfigJSON.put("production_endpoints", productionJSON);
        endPointConfigJSON.put("sandbox_endpoints", sandboxJSON);
        endPointConfigJSON.put("endpoint_type", "http");


        endpointConfig = endPointConfigJSON.toString();
    }
}
