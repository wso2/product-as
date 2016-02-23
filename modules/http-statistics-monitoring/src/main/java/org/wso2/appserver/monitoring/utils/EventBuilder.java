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

package org.wso2.appserver.monitoring.utils;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.wso2.appserver.monitoring.EventPublisherConstants;
import org.wso2.appserver.monitoring.exceptions.EventBuilderException;
import org.wso2.carbon.databridge.commons.Event;
import ua_parser.CachingParser;
import ua_parser.Client;
import ua_parser.Parser;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;

/**
 * Utility class to create an Event to published by the DataPublisher.
 */
public class EventBuilder {
    private static Parser uaParser = null;
    private static String userAgentVersion;
    private static String userAgentFamily;
    private static String osVersion;
    private static String osFamily;
    private static String deviceCategory;

    public static Event buildEvent(String streamId, Request request, Response response, long startTime,
                                   long responseTime) throws EventBuilderException {
        try {
            uaParser = new CachingParser();
        } catch (IOException e) {
            throw new EventBuilderException("Creating Parser object failed: ", e);
        }

        List<Object> payload = buildPayloadData(request, response, startTime, responseTime);
        List<Object> metaData = buildMetaData(request);

        Event event = new Event(streamId, System.currentTimeMillis(), metaData.toArray(), null, payload.toArray());
        return event;

    }

    private static List<Object> buildMetaData(Request request) {

        List<Object> metaData = new ArrayList<Object>();

        metaData.add(mapNull(request.getServerName()));
        metaData.add(mapNull(request.getLocalName()));

        return metaData;
    }

    private static List<Object> buildPayloadData(Request request, Response response, long startTime,
                                                 long responseTime) {

        List<Object> payload = new ArrayList<Object>();
        final String backslash = "/";
        String requestedURI = request.getRequestURI();

        if (requestedURI != null) {
            requestedURI = requestedURI.trim();
            String[] requestedUriParts = requestedURI.split(backslash);

            if (!backslash.equals(requestedURI)) {
                payload.add(mapNull(requestedUriParts[1])); //webappname

            } else {
                payload.add(mapNull(backslash)); //webappname
            }
        }

        String webappServletVersion = request.getContext().getEffectiveMajorVersion() + "."
                + request.getContext().getEffectiveMinorVersion();
        payload.add(mapNull(webappServletVersion));
        String consumerName = extractUsername(request);
        payload.add(mapNull(consumerName));
        payload.add(mapNull(request.getRequestURI()));
        payload.add(mapNull(startTime));
        payload.add(mapNull(request.getPathInfo()));
        parserUserAgent(request);
        payload.add(mapNull(userAgentVersion));
        payload.add(mapNull(osFamily));
        payload.add(mapNull(osVersion));
        payload.add(mapNull(request.getLocale().getCountry()));
        payload.add(mapNull("webapp"));
        payload.add(mapNull(request.getContext().getDisplayName()));
        payload.add(mapNull(requestedURI));
        payload.add(mapNull(extractSessionId(request)));
        payload.add(mapNull(request.getMethod()));
        payload.add(mapNull(request.getContentType()));
        payload.add(mapNull(response.getContentType()));
        payload.add(mapNull((long) response.getStatus()));
        payload.add(mapNull(getClientIpAddress(request)));
        payload.add(mapNull(request.getHeader(EventPublisherConstants.REFERRER)));
        payload.add(mapNull(request.getRemoteUser()));
        payload.add(mapNull(request.getAuthType()));
        payload.add(mapNull(userAgentFamily));
        payload.add(mapNull(responseTime));
        payload.add(mapNull((long) request.getContentLength())); //request size bytes
        payload.add(mapNull((long) response.getContentLength())); //response size bytes
        payload.add(mapNull("-")); //get request header
        payload.add(mapNull("-")); //get response header
        payload.add(mapNull("-"));
        payload.add(mapNull("-"));
        payload.add(mapNull(request.getLocale().getLanguage()));
        payload.add(mapNull(deviceCategory));

        return payload;
    }

    //extracts the Id of the current session associated with the request
    private static String extractSessionId(Request request) {
        final HttpSession session = request.getSession(false);
        // CXF web services does not have a session id, because they are stateless
        return (session != null && session.getId() != null) ? session.getId() : "-";

    }

    //extracts the name of the current authenticated user for the request
    private static String extractUsername(Request request) {
        String consumerName;
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            consumerName = principal.getName();
        } else {
            consumerName = EventPublisherConstants.ANONYMOUS_USER;
        }
        return consumerName;
    }

    //parse statistics of User Agent and set them in the WebappMonitoringEvent
    private static void parserUserAgent(Request request) {
        String userAgent = request.getHeader(EventPublisherConstants.USER_AGENT);
        if (uaParser != null) {
            Client readableUserAgent = uaParser.parse(userAgent);

            userAgentFamily = readableUserAgent.userAgent.family;
            userAgentVersion = readableUserAgent.userAgent.major;
            osFamily = readableUserAgent.os.family;
            osVersion = readableUserAgent.os.major;
            deviceCategory = readableUserAgent.device.family;
        }
    }

    /*
    * Checks the remote address of the request. Server could be hiding behind a proxy or load balancer.
    * if we get only request.getRemoteAddr() will give only the proxy pr load balancer address.
    * For that we are checking the request forwarded address in the header of the request.
    */
    private static String getClientIpAddress(Request request) {
        String ip = request.getHeader(EventPublisherConstants.X_FORWARDED_FOR);
        ip = tryNextHeaderIfIpNull(request, ip, EventPublisherConstants.PROXY_CLIENT_IP);
        ip = tryNextHeaderIfIpNull(request, ip, EventPublisherConstants.WL_PROXY_CLIENT_IP);
        ip = tryNextHeaderIfIpNull(request, ip, EventPublisherConstants.HTTP_CLIENT_IP);
        ip = tryNextHeaderIfIpNull(request, ip, EventPublisherConstants.HTTP_X_FORWARDED_FOR);

        if (ip == null || ip.length() == 0 || EventPublisherConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            // Failed. remoteAddr is the only option
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    // If the input param ip is invalid, it will return the value of the next header as the output
    private static String tryNextHeaderIfIpNull(Request request, String ip, String nextHeader) {
        if (ip == null || ip.length() == 0 ||  EventPublisherConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            return request.getHeader(nextHeader);
        }
        return null;
    }
    /**
     * Maps null Integers to zero.
     *
     * @param value
     * @return
     */
    protected Integer mapNull(Integer value) {
        Integer zero = Integer.valueOf(0);
        return (value == null) ? zero : value;
    }

    /**
     * Maps null Long values to zero.
     *
     * @param value @param value The value that should be mapped.
     * @return the value if not null, otherwise 0
     */
    protected static Long mapNull(Long value) {
        Long zero = Long.valueOf(0);
        return (value == null) ? zero : value;
    }

    /**
     * Map null String to -.
     *
     * @param value The value that should be mapped.
     * @return the value if not null, otherwise "-"
     */
    protected static String mapNull(String value) {
        return (value == null) ? "-" : value;
    }

}
