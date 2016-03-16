/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.wso2.appserver.monitoring;

/**
 * The Constants used within this package in setting the statistics.
 */
public class EventPublisherConstants {

    //standard header field name for identifying the originating IP address of a client connecting to a Web server
    // through an HTTP proxy or load balancer
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";

    //header field name that identifies the browser client IP instead of web server IP
    public static final String PROXY_CLIENT_IP = "Proxy-Client-IP";

    //header field name that identifies the browser client IP instead of web server IP that acts as a proxy
    // for a WebLogic Server
    public static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";

    //header field name for header that identify the ip-address of the host that is making the HTTP request
    // through the proxy
    public static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";

    //header field name for header containing a complete track of the forwarding chain through proxies
    public static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";

    //header field name for header that identify the address of the previous web page from which a link to the
    // currently requested page was followed
    public static final String REFERRER = "Referer";

    public static final String USER_AGENT = "User-Agent";
    public static final String HOST = "Host";

    //a probable value for IP address set in headers
    public static final String UNKNOWN = "unknown";

    //constant to represent a user when an authenticated user for the current session is not present
    public static final String ANONYMOUS_USER = "anonymous.user";

    public static final String APP_TYPE = "webapp";

    //File containing the configurations and properties that define the data agent in the JVM.
    // Resides in product-as/distribution/contents/conf/wso2
    public static final String DATA_AGENT_CONF = "data-agent-conf.xml";

    //Trust store file that refers to all trusted certificates.
    // Resides in product-as/distribution/contents/conf/wso2/Webapp_Statistics_Monitoring
    public static final String CLIENT_TRUSTSTORE = "client-truststore.jks";

    /**
     * instantiating is not needed for this class. private constructor to block that.
     */
    private EventPublisherConstants() {

    }

}
