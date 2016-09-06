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

    //ToDo: add default to api version
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

    public void produceSample(List<API> apis) {
//        name = "PizzaShackAPI";
        description = "automatically published to API publisher.\r\n";
//        context = "/pizzashack";
        version = "1.0.0";
        provider = "admin";

        StringBuilder tempApiDef = new StringBuilder();
        tempApiDef.append("{\"paths\":{");
        for (int i = 0; i < apis.size(); i++) {
            API api = apis.get(i);
            if (api.getType() == null) {
                continue;
            }
            if (i != 0) {
                tempApiDef.append(",");
            }
            tempApiDef.append("\"" + api.getUrl() + "\":{");
            tempApiDef.append("\"" + api.getType() + "\":{");
            tempApiDef.append("\"x-auth-type\":\"Application & Application User\",\"x-throttling-tier\":" +
                    "\"Unlimited\"}}");
        }
        tempApiDef.append("},\"schemes\":[\"https\"],\"produces\":[\"application/json\"],\"swagger\":\"2.0\"," +
                "\"consumes\":[\"application/json\"]}");
        apiDefinition = tempApiDef.toString();
        log.info("............. API def" + apiDefinition);
        status = "PUBLISHED";
        responseCaching = "Disabled";
        cacheTimeout = 300;
        destinationStatsEnabled = false;
        isDefaultVersion = false;
        transport = new ArrayList<>(Arrays.asList("http", "https"));
        tiers = new ArrayList<>(Arrays.asList("Unlimited"));
        visibility = "PUBLIC";
        visibleRoles = new ArrayList<>(Arrays.asList());
        ;
        visibleTenants = new ArrayList<>(Arrays.asList());
        ;
        endpointConfig = "{\"production_endpoints\":{\"url\":\"https://localhost:9443/am/sample/pizzashack/v1/api/\"," +
                "\"config\":null},\"sandbox_endpoints\":{\"url\":" +
                "\"https://localhost:9443/am/sample/pizzashack/v1/api/\",\"config\":null},\"endpoint_type\":\"http\"}";
    }
}
