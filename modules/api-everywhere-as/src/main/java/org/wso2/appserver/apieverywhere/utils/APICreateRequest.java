package org.wso2.appserver.apieverywhere.utils;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void produceSample(List<APIPath> apiPaths) {
//        name = "PizzaShackAPI";
        description = "automatically published to API publisher.\r\n";
//        context = "/pizzashack";
        version = "1.0.0";
        provider = "admin";

        StringBuilder tempApiDef = new StringBuilder();
        tempApiDef.append("{\"paths\":{");
        for (int i = 0; i < apiPaths.size(); i++) {
            APIPath apiPath = apiPaths.get(i);
            if (i != 0) {
                tempApiDef.append(",");
            }
            tempApiDef.append("\"" + apiPath.getUrl() + "\":{");
            tempApiDef.append("\"" + apiPath.getType() + "\":{");
            ArrayList<APIPath.Param> params = apiPath.getParams();
            tempApiDef.append("\"parameters\":[");
            for (int j = 0; j < params.size(); j++) {
                APIPath.Param param = params.get(j);
                if (j != 0) {
                    tempApiDef.append(",");
                }
                tempApiDef.append("{\"name\":\"" + param.getParamName() + "\"," +
                                    "\"in\":\"" + param.getParamType() + "\"," +
                                    "\"type\":\"" + param.getDataType() + "\"}");

            }
            tempApiDef.append("],");
            tempApiDef.append("\"responses\":{\"200\":{}},");
            tempApiDef.append("\"produces\":" + Arrays.toString(apiPath.getProduces()) + ",");
            tempApiDef.append("\"consumes\":" + Arrays.toString(apiPath.getConsumes()));
            tempApiDef.append("}}");
        }
        tempApiDef.append("},\"schemes\":[\"https\"],\"swagger\":\"2.0\"" + "}");
        apiDefinition = tempApiDef.toString();
        log.info("............. API def" + apiDefinition);
        status = "PUBLISHED";
        responseCaching = "Disabled";
        cacheTimeout = 300;
        destinationStatsEnabled = false;
        isDefaultVersion = true;
        transport = new ArrayList<>(Arrays.asList("http", "https"));
        tiers = new ArrayList<>(Arrays.asList("Unlimited"));
        visibility = "PUBLIC";
        visibleRoles = new ArrayList<>(Arrays.asList());
        ;
        visibleTenants = new ArrayList<>(Arrays.asList());
        ;
        endpointConfig = "{\"production_endpoints\":{\"url\":\"https://localhost:9443/am/sample/pizzashack/v1/api/\"," +
                "\"config\":null}," +
                "\"sandbox_endpoints\":{\"url\":" +
                "\"https://localhost:9443/am/sample/pizzashack/v1/api/\",\"config\":null},\"endpoint_type\":\"http\"}";
    }
}
