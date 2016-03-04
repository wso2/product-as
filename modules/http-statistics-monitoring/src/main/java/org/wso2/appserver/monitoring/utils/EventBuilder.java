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
import ua_parser.Client;
import ua_parser.Parser;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Utility class to create an Event to published by the DataPublisher.
 */
public class EventBuilder {

    public static Event buildEvent(String streamId, Request request, Response response, long startTime,
                                   long responseTime, Parser uaParser) throws EventBuilderException {


        List<Object> payload = buildPayloadData(request, response, startTime, responseTime, uaParser);

        return new Event(streamId, System.currentTimeMillis(),
                new ArrayList<>(Arrays.asList(request.getServerName(), request.getLocalName())).toArray() ,
                null, payload.toArray());

    }

    private static List<Object> buildPayloadData(Request request, Response response, long startTime,
                                                 long responseTime, Parser uaParser) {

        List<Object> payload = new ArrayList<>();
        final String forwardSlash = "/";

        String requestedURI = request.getRequestURI();

        if (requestedURI != null) {
            requestedURI = requestedURI.trim();
            String[] requestedUriParts = requestedURI.split(forwardSlash);

            if (!forwardSlash.equals(requestedURI)) {
                payload.add(mapNull(requestedUriParts[1]));

            } else {
                payload.add(mapNull(forwardSlash));
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
        parserUserAgent(request, uaParser, payload);

        payload.add(mapNull(request.getLocale().getCountry()));
        payload.add(mapNull(EventPublisherConstants.APP_TYPE));
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

        payload.add(mapNull(responseTime));
        payload.add(mapNull((long) request.getContentLength()));
        payload.add(mapNull((long) response.getContentLength()));
        payload.add(mapNull(getRequestHeader(request)));
        payload.add(mapNull(getResponseHeaders(response)));
        payload.add(mapNull("-")); //request payload
        payload.add(mapNull("-")); //response payload
        payload.add(mapNull(request.getLocale().getLanguage()));

        return payload;
    }

    //get request headers
    private static String getRequestHeader(Request request) {
        HttpServletRequest httpServletRequest = request.getRequest();
        List<String> headers = new ArrayList<>();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            headers.add(header);
        }
        return headers.toString();
    }

    //get response headers
    private static String getResponseHeaders(Response response) {
        HttpServletResponse httpServletResponse = response.getResponse();
        Collection<String> headerNames = httpServletResponse.getHeaderNames();
        return headerNames.toString();
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

    //parse information about User Agent
    private static void parserUserAgent(Request request, Parser uaParser, List<Object> payload) {

        String userAgent = request.getHeader(EventPublisherConstants.USER_AGENT);
        if (uaParser != null) {
            Client readableUserAgent = uaParser.parse(userAgent);

            payload.add(mapNull(readableUserAgent.userAgent.family));
            payload.add(mapNull(readableUserAgent.userAgent.major));
            payload.add(mapNull(readableUserAgent.os.family));
            payload.add(mapNull(readableUserAgent.os.major));
            payload.add(mapNull(readableUserAgent.device.family));
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
