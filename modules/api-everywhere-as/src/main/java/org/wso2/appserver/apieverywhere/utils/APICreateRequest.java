package org.wso2.appserver.apieverywhere.utils;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

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
    private String provider;
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
        provider = "admin";

        StringBuilder apiDefBuilder = new StringBuilder();
        apiDefBuilder.append("{\"paths\":{");
        for (int i = 0; i < apiPaths.size(); i++) {
            APIPath apiPath = apiPaths.get(i);
            if (i != 0) {
                apiDefBuilder.append(",");
            }
            apiDefBuilder.append("\"" + apiPath.getUrl() + "\":{");
            ArrayList<APIPath.APIProp> apiProps = apiPath.getApiProps();
            for (int k = 0; k < apiProps.size(); k++) {
                APIPath.APIProp apiProp = apiProps.get(k);
                if (k != 0) {
                    apiDefBuilder.append(",");
                }
                apiDefBuilder.append("\"" + apiProp.getType() + "\":{");
                ArrayList<APIPath.Param> params = apiProp.getParams();
                apiDefBuilder.append("\"parameters\":[");
                for (int j = 0; j < params.size(); j++) {
                    APIPath.Param param = params.get(j);
                    if (j != 0) {
                        apiDefBuilder.append(",");
                    }
                    apiDefBuilder.append("{\"name\":\"" + param.getParamName() + "\"," +
                            "\"in\":\"" + param.getParamType() + "\"," +
                            "\"type\":\"" + param.getDataType() + "\"}");

                }
                apiDefBuilder.append("],");
                apiDefBuilder.append("\"responses\":{\"200\":{}},");
                apiDefBuilder.append("\"produces\":" + Arrays.toString(apiProp.getProduces()) +
                                                                                ",");
                apiDefBuilder.append("\"consumes\":" + Arrays.toString(apiProp.getConsumes()));
                apiDefBuilder.append("}");
            }
            apiDefBuilder.append("}");
        }
        apiDefBuilder.append("},\"schemes\":[\"https\"],\"swagger\":\"2.0\"" + "}");
        apiDefinition = apiDefBuilder.toString();
        log.info("............. API def" + apiDefinition);
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
        endpointConfig = "{\"production_endpoints\":" +
                "{\"url\":\"https://localhost:9443/am/sample/pizzashack/v1/api/\"," +
                "\"config\":null}," +
                "\"sandbox_endpoints\":" +
                "{\"url\":\"https://localhost:9443/am/sample/pizzashack/v1/api/\"," +
                "\"config\":null}," +
                "\"endpoint_type\":\"http\"}";
    }
}
