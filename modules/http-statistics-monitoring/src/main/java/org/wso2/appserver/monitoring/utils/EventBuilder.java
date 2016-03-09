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
import org.apache.commons.lang.StringUtils;
import org.wso2.appserver.monitoring.EventPublisherConstants;
import org.wso2.appserver.monitoring.exceptions.EventBuilderException;
import org.wso2.carbon.databridge.commons.Event;
import ua_parser.Client;
import ua_parser.Parser;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpSession;

/**
 * Utility class to create an Event to published by the DataPublisher.
 */
public class EventBuilder {

    /**
     *
     * @param streamId
     * @param request
     * @param response
     * @param startTime
     * @param responseTime
     * @param uaParser
     * @return
     * @throws EventBuilderException
     */
    public static Event buildEvent(String streamId, Request request, Response response, long startTime,
                                   long responseTime, Parser uaParser) throws EventBuilderException {


        List<Object> payload = buildPayloadData(request, response, startTime, responseTime, uaParser);

        return new Event(streamId, startTime,
                new ArrayList<>(Arrays.asList(request.getServerName(), request.getLocalName())).toArray() ,
                null, payload.toArray());

    }

    /**
     *
     * @param request
     * @param response
     * @param startTime
     * @param responseTime
     * @param uaParser
     * @return
     */
    private static List<Object> buildPayloadData(Request request, Response response, long startTime,
                                                 long responseTime, Parser uaParser)  {

        List<Object> payload = new ArrayList<>();
        final String forwardSlash = "/";

        String requestedURI = request.getRequestURI();

        if (requestedURI != null) {
            requestedURI = requestedURI.trim();
            String[] requestedUriParts = requestedURI.split(forwardSlash);

            if (!forwardSlash.equals(requestedURI)) {
                payload.add((requestedUriParts[1]));
            } else {
                payload.add((forwardSlash));
            }
        }

        String webappServletVersion = request.getContext().getEffectiveMajorVersion() + "."
                        + request.getContext().getEffectiveMinorVersion();
        payload.add((webappServletVersion));
        String consumerName = extractUsername(request);
        payload.add((consumerName));
        payload.add((request.getRequestURI()));
        payload.add((startTime));
        payload.add((request.getPathInfo()));
        parserUserAgent(request, uaParser, payload);
        payload.add((request.getLocale().getCountry()));
        payload.add((EventPublisherConstants.APP_TYPE));
        payload.add((request.getContext().getDisplayName()));
        payload.add((extractSessionId(request)));
        payload.add((request.getMethod()));
        payload.add((request.getContentType()));
        payload.add((response.getContentType()));
        payload.add(((long) response.getStatus()));
        payload.add((getClientIpAddress(request)));
        payload.add((request.getHeader(EventPublisherConstants.REFERRER)));
        payload.add((request.getRemoteUser()));
        payload.add((request.getAuthType()));
        payload.add((responseTime));
        payload.add(((long) request.getContentLength()));
        payload.add(((long) response.getContentLength()));
        payload.add((getRequestHeaders(request)));
        payload.add((getResponseHeaders(response)));
        payload.add((request.getLocale().getLanguage()));


        return payload;
    }

    //get request headers

    /**
     *
     * @param request
     * @return
     */
    private static String getRequestHeaders(Request request) {
        List<String> requestHeaders = new ArrayList<>();
        Collections.list(request.getHeaderNames()).forEach(header -> {
            List<String> values = new ArrayList<>();
            values.add(request.getHeader(header));
            String tmpString = "(" + StringUtils.join(values, ",") + ")";
            requestHeaders.add(header + ":" + tmpString);
        });
        return StringUtils.join(requestHeaders, ",");
    }

    //get response headers

    /**
     *
     * @param response
     * @return
     */
    private static String getResponseHeaders(Response response) {
        List<String> responseHeaders = new ArrayList<>();
        response.getHeaderNames().forEach(header -> {
                    List<String> values = new ArrayList<>();
                    values.add(response.getHeader(header));
                    String tmpString = "(" + StringUtils.join(values, ",") + ")";
                    responseHeaders.add(header + ":" + tmpString);
        });
        return StringUtils.join(responseHeaders, ",");
    }

    //extracts the Id of the current session associated with the request

    /**
     *
     * @param request
     * @return
     */
    private static String extractSessionId(Request request) {
        HttpSession session = request.getSession(false);
        // CXF web services does not have a session id, because they are stateless
        return (session != null && session.getId() != null) ? session.getId() : "-";

    }

    //extracts the name of the current authenticated user for the request

    /**
     *
     * @param request
     * @return
     */
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

    /**
     *
     * @param request
     * @param uaParser
     * @param payload
     */
    private static void parserUserAgent(Request request, Parser uaParser, List<Object> payload) {

        String userAgent = request.getHeader(EventPublisherConstants.USER_AGENT);
        if (uaParser != null) {
            Client readableUserAgent = uaParser.parse(userAgent);

            payload.add((readableUserAgent.userAgent.family));
            payload.add((readableUserAgent.userAgent.major));
            payload.add((readableUserAgent.os.family));
            payload.add((readableUserAgent.os.major));
            payload.add((readableUserAgent.device.family));
        }

    }

    /*
    * Checks the remote address of the request. Server could be hiding behind a proxy or load balancer.
    * if we get only request.getRemoteAddr() will give only the proxy pr load balancer address.
    * For that we are checking the request forwarded address in the header of the request.
    */

    /**
     *
     * @param request
     * @return
     */
    private static String getClientIpAddress(Request request) {

        List<String> headers = Arrays.asList(EventPublisherConstants.X_FORWARDED_FOR,
                EventPublisherConstants.PROXY_CLIENT_IP,
                EventPublisherConstants.WL_PROXY_CLIENT_IP,
                EventPublisherConstants.HTTP_CLIENT_IP,
                EventPublisherConstants.HTTP_X_FORWARDED_FOR
                );

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !EventPublisherConstants.UNKNOWN.equalsIgnoreCase(ip)) {
                return ip;
            } else {
               return request.getRemoteAddr();
            }

        }

        return request.getRemoteAddr();
    }

}
