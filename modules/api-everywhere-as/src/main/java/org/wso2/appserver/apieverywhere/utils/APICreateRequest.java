package org.wso2.appserver.apieverywhere.utils;

import org.json.JSONObject;
import org.wso2.appserver.configuration.listeners.ServerConfigurationLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *Utility class to create an API from the web app
 *
 * @since 6.0.0
 */
@SuppressWarnings("URF_UNREAD_FIELD")
public class APICreateRequest {


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
            pathsJSON.put(apiPath.getUrl(), propsJSON);
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


        String applicationServerUrl = ServerConfigurationLoader.
                getServerConfiguration().getApiEverywhereConfiguration().getApplicationServerUrl();
        JSONObject endPointConfigJSON = new JSONObject();
        JSONObject productionJSON = new JSONObject();
        productionJSON.put("url", applicationServerUrl);
        productionJSON.put("config", "null");

        JSONObject sandboxJSON = new JSONObject();
        sandboxJSON.put("url", applicationServerUrl);
        sandboxJSON.put("config", "null");

        endPointConfigJSON.put("production_endpoints", productionJSON);
        endPointConfigJSON.put("sandbox_endpoints", sandboxJSON);
        endPointConfigJSON.put("endpoint_type", "http");


        endpointConfig = endPointConfigJSON.toString();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getContext() {
        return context;
    }

    public String getVersion() {
        return version;
    }

    public String getApiDefinition() {
        return apiDefinition;
    }

    public String getStatus() {
        return status;
    }

    public String getResponseCaching() {
        return responseCaching;
    }

    public Integer getCacheTimeout() {
        return cacheTimeout;
    }

    public Boolean getDestinationStatsEnabled() {
        return destinationStatsEnabled;
    }

    public Boolean getDefaultVersion() {
        return isDefaultVersion;
    }

    public List<String> getTransport() {
        return transport;
    }

    public List<String> getTiers() {
        return tiers;
    }

    public String getVisibility() {
        return visibility;
    }

    public List<Object> getVisibleRoles() {
        return visibleRoles;
    }

    public List<Object> getVisibleTenants() {
        return visibleTenants;
    }

    public String getEndpointConfig() {
        return endpointConfig;
    }
}
