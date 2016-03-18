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
import org.wso2.appserver.monitoring.EventPublisherConstants;
import org.wso2.appserver.monitoring.exceptions.StatPublisherException;
import org.wso2.appserver.monitoring.utils.EventBuilder;
import org.wso2.carbon.databridge.commons.Event;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This sample test class contains unit tests performed for the methods used within the http-statistics-monitoring
 * module.
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

        //setting request context
        StandardContext standardContext = new StandardContext();
        standardContext.setDisplayName("Test Display Name");
        standardContext.setEffectiveMajorVersion(3);
        standardContext.setEffectiveMinorVersion(1);
        MappingData mappingData = new MappingData();
        mappingData.context = standardContext;

        //setting request locale
        Locale locale = new Locale("en", "SL");

        List<String> requestHeaders = Arrays.asList(EventPublisherConstants.X_FORWARDED_FOR,
                EventPublisherConstants.PROXY_CLIENT_IP,
                EventPublisherConstants.WL_PROXY_CLIENT_IP,
                EventPublisherConstants.HTTP_CLIENT_IP,
                EventPublisherConstants.HTTP_X_FORWARDED_FOR
        );
        Enumeration<String> headerNames = Collections.enumeration(requestHeaders);
        List<String> responseHeaders = new ArrayList<>();

        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL_Proxy_Client_IP")).thenReturn(null);
        when(request.getHeader("HTTP_CLIENT_IP")).thenReturn(null);
        when(request.getHeader("HTTP_X_FORWARDED_FOR")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/");
        when(request.getContext()).thenReturn(standardContext);
        when(request.getUserPrincipal()).thenReturn(null);
        when(request.getPathInfo()).thenReturn("/");
        when(request.getLocale()).thenReturn(locale);
        when(request.getSession(false)).thenReturn(null);
        when(request.getSession(false)).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getContentType()).thenReturn(null);
        when(response.getContentType()).thenReturn("/");
        when(response.getStatus()).thenReturn(200);
        when(response.getHeader("Referer")).thenReturn(null);
        when(response.getHeader("User-Agent")).thenReturn(null);
        when(response.getHeader("Host")).thenReturn(null);
        when(request.getRemoteUser()).thenReturn(null);
        when(request.getAuthType()).thenReturn(null);
        when(request.getContentLength()).thenReturn(-1);
        when(response.getContentLength()).thenReturn(-1);
        when(request.getHeaderNames()).thenReturn(headerNames);
        when(response.getHeaderNames()).thenReturn(responseHeaders);
        when(request.getServerName()).thenReturn("localhost");
        when(request.getLocalName()).thenReturn("localhost");

    }

    /**
     * This test case checks if Event object is created properly.
     */
    @Test
    public void buildEventTest() {

        Long startTime = System.currentTimeMillis();

        //creating the payload list
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
        payload.add(null);
        payload.add("/");
        payload.add(200L);
        payload.add("127.0.0.1");
        payload.add(null);
        payload.add(null);
        payload.add(null);
        payload.add(null);
        payload.add(null);
        payload.add(0L);
        payload.add(-1L);
        payload.add(-1L);
        payload.add("X-Forwarded-For:();Proxy-Client-IP:();WL-Proxy-Client-IP:();HTTP_CLIENT_IP:();" +
                "HTTP_X_FORWARDED_FOR:()");
        payload.add("");
        payload.add("en");

        Event testEvent = new Event("org.wso2.http.stats:1.0.0", startTime,
                new ArrayList<>(Arrays.asList("localhost", "localhost")).toArray() ,
                null, payload.toArray());
        Event event = null;
        try {
            event = EventBuilder.buildEvent("org.wso2.http.stats:1.0.0", request, response, startTime, 0);
        } catch (StatPublisherException e) {
            Assert.fail("Building event failed.");
        }

        Assert.assertEquals(testEvent, event, "Event created");
    }

}
