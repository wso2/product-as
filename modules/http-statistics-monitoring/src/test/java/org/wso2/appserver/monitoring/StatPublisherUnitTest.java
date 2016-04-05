package org.wso2.appserver.monitoring;
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
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.mapper.MappingData;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.monitoring.exceptions.StatPublisherException;
import org.wso2.appserver.monitoring.utils.EventBuilder;
import org.wso2.carbon.databridge.commons.Event;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This sample test class contains unit tests performed for the methods used within the http-statistics-monitoring
 * module.
 *
 * @since 6.0.0
 */
public class StatPublisherUnitTest {
    private Request request;
    private Response response;

    /**
     * This method mocks Request and Response objects and sets up all necessary information.
     *
     * @throws Exception
     */
    @BeforeClass
    public void setUp() throws Exception {
        request = mock(Request.class);
        response = mock(Response.class);

        // populating the context
        StandardContext standardContext = new StandardContext();
        standardContext.setDisplayName("Test Display Name");
        standardContext.setEffectiveMajorVersion(3);
        standardContext.setEffectiveMinorVersion(1);
        MappingData mappingData = new MappingData();
        mappingData.context = standardContext;

        // setting request locale
        Locale locale = new Locale("en", "SL");

        // populating request headers
        List<String> requestHeaders = new ArrayList<>();
        requestHeaders.add("host");
        requestHeaders.add("custom-header");
        Enumeration<String> headerNames = Collections.enumeration(requestHeaders);

        // populating response headers
        List<String> responseHeaders = new ArrayList<>();
        responseHeaders.add("Content-Type");
        responseHeaders.add("Content-Length");

        when(request.getHeader("host")).thenReturn("localhost:8080");
        when(request.getHeader("custom-header")).thenReturn("dummyvalue");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/");
        when(request.getContext()).thenReturn(standardContext);
        when(request.getUserPrincipal()).thenReturn(null);
        when(request.getPathInfo()).thenReturn("/");
        when(request.getLocale()).thenReturn(locale);
        when(request.getSession(false)).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getContentType()).thenReturn("application/json");
        when(request.getHeader("Referer")).thenReturn("http://localhost:8080/examples/servlets/");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0 (X11; Linux x86_64; rv:46.0) " +
                "Gecko/20100101 Firefox/46.0");
        when(request.getHeader("Host")).thenReturn("localhost:8080");
        when(request.getRemoteUser()).thenReturn(null);
        when(request.getAuthType()).thenReturn(null);
        when(request.getContentLength()).thenReturn(-1);
        when(request.getHeaderNames()).thenReturn(headerNames);
        when(request.getServerName()).thenReturn("localhost");
        when(request.getLocalName()).thenReturn("localhost.localdomain");

        when(response.getHeader("Content-Type")).thenReturn("text/html;charset=utf-8");
        when(response.getHeader("Content-Length")).thenReturn("1046");
        when(response.getContentType()).thenReturn("text/html;charset=utf-8");
        when(response.getStatus()).thenReturn(200);
        when(response.getHeaderNames()).thenReturn(responseHeaders);
        when(response.getContentLength()).thenReturn(-1);
    }

    @Test(description = "Checks if Event object is created properly")
    public void buildEventTest() {
        Long startTime = System.currentTimeMillis();

        //  creating the payload list
        List<Object> payload = new ArrayList<>();
        payload.add("/");
        payload.add("3.1");
        payload.add("anonymous.user");
        payload.add("/");
        payload.add(startTime);
        payload.add("/");
        payload.add("webapp");
        payload.add("Test Display Name");
        payload.add("-");
        payload.add("GET");
        payload.add("application/json");
        payload.add("text/html;charset=utf-8");
        payload.add(200L);
        payload.add("127.0.0.1");
        payload.add("http://localhost:8080/examples/servlets/");
        payload.add("Mozilla/5.0 (X11; Linux x86_64; rv:46.0) Gecko/20100101 Firefox/46.0");
        payload.add("localhost:8080");
        payload.add(null);
        payload.add(null);
        payload.add(0L);
        payload.add(-1L);
        payload.add(-1L);
        payload.add("host:(localhost:8080);custom-header:(dummyvalue)");
        payload.add("Content-Type:(text/html;charset=utf-8),Content-Length:(1046)");
        payload.add("en");

        Event testEvent = new Event("org.wso2.http.stats:1.0.0", startTime,
                new ArrayList<>(Arrays.asList("localhost", "localhost.localdomain")).toArray(),
                null, payload.toArray(), new HashMap<String, String>());
        Event event = null;
        try {
            event = EventBuilder.buildEvent("org.wso2.http.stats:1.0.0", request, response, startTime, 0);
        } catch (StatPublisherException e) {
            Assert.fail("Building event failed.");
        }

        Assert.assertEquals(testEvent, event, "Event created");
    }
}
