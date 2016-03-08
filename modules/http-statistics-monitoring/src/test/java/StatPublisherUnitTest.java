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
 */


import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.wso2.appserver.monitoring.exceptions.EventBuilderException;
import org.wso2.appserver.monitoring.utils.EventBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import org.wso2.carbon.databridge.commons.Event;
import ua_parser.CachingParser;
import ua_parser.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * This sample test class contains unit tests performed for the methods used within the http-statistics-monitoring
 * module.
 */

public class StatPublisherUnitTest {

    private Request request;
    private Response response;
    EventBuilder eventBuilder = new EventBuilder();
    Parser uaParser;


    @Before
    public void setUp() throws Exception {

        uaParser = new CachingParser();

        request = mock(Request.class);
        response = mock(Response.class);

        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("HTTP_CLIENT_IP")).thenReturn(null);
        when(request.getHeader("HTTP_X_FORWARDED_FOR")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        when(request.getRequestURI()).thenReturn("/");
        when(request.getContext()).thenReturn(null);

        when(request.getUserPrincipal()).thenReturn(null);
        when(request.getPathInfo()).thenReturn("/");
        when(request.getHeader("user-agent")).thenReturn(null);
        when(request.getLocale()).thenReturn(null);
        when(request.getSession(false)).thenReturn(null);
        when(request.getSession(false)).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getContentType()).thenReturn(null);
        when(response.getContentType()).thenReturn("/");
        when(response.getStatus()).thenReturn(200);
        when(response.getHeader("Referer")).thenReturn(null);
        when(request.getRemoteUser()).thenReturn(null);
        when(request.getAuthType()).thenReturn(null);
        when(request.getContentLength()).thenReturn(-1);
        when(response.getContentLength()).thenReturn(-1);
        when(request.getHeaderNames()).thenReturn(null);
        when(response.getHeaderNames()).thenReturn(null);

        when(request.getServerName()).thenReturn("localhost");
        when(request.getLocalName()).thenReturn("localhost");


    }

    @Test
    public void getClientIpAddressTest() {


        String ip = eventBuilder.getClientIpAddress(request);

        Assert.assertEquals("Client Ip Address", "127.0.0.1", ip);

    }

    @Test
    public void buildEventTest() {

        Long startTime = System.currentTimeMillis();

        List<Object> payload = new ArrayList<>();
        payload.add("/");
        payload.add(null);
        payload.add("anonymous.user");
        payload.add("/");
        payload.add(startTime);
        payload.add("/");
        payload.add(null);
        payload.add(null);
        payload.add(null);
        payload.add(null);
        payload.add(null);
        payload.add(null);
        payload.add("webapp");
        payload.add(null);
        payload.add("-");
        payload.add("GET");
        payload.add(null);
        payload.add("/");
        payload.add(200);
        payload.add("127.0.0.1");
        payload.add(null);
        payload.add(null);
        payload.add(null);
        payload.add(0);
        payload.add(-1);
        payload.add(-1);
        payload.add(null);
        payload.add(null);
        payload.add(null);

        Event testEvent = new Event("org.wso2.http.stats:1.0.0", System.currentTimeMillis(),
                new ArrayList<>(Arrays.asList("localhost", "localhost")).toArray() ,
                null, payload.toArray());
        Event event = null;
        try {
            event = eventBuilder.buildEvent("org.wso2.http.stats:1.0.0", request, response, startTime, 0, uaParser);
        } catch (EventBuilderException e) {
            System.out.println(event);;
        }
        Assert.assertEquals("Event created", testEvent, event );
    }

}
