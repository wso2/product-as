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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.appserver.samples.httpanalytics;

import org.wso2.carbon.databridge.commons.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is used to generate an Event with random set of HTTP data.
 */
public class EventGenerator {
    // This map holds the application name and the list of Request URIs for that application
    private static Map<String, List<String>> applications = new HashMap<>();
    private static final String[] HTTP_RESPONSE_CODES = {"200", "200", "405", "200", "404", "200", "200", "403",
            "200", "200", "500", "200", "408", "200", "200", "200"};

    // This array holds the IP address range assigned for ISPs
    private static final String[] CLIENT_ADDRESSES = {
            "112.134.0.0-112.135.255.255", // Sri Lanka
            "40.144.0.0-40.159.255.255", // United States
            "5.8.0.0-5.8.63.255", // Russian Federation
            "41.85.0.0-41.85.127.255", // South Africa
            "42.241.0.0-42.241.255.255", // Australia
            "5.53.64.0-5.53.95.255", // United Kingdom
            "42.123.0.0-42.123.31.255" // China
    };

    private static final String[] REFERERS = {
            "https://google.com",
            "https://facebook.com",
            "https://twitter.com",
            "https://medium.com/@geekwriter/your-first-webapp",
            "https://github.com/tutor/webapp-training",
            "https://search.yahoo.com",
            "https://medium.com/@geekwriter/jaggery-webapps",
            "https://dzone.com",
            "blogger.com",
            "https://github.com/tutor/sample-webapp",
            "https://github.com/tutor/sample-jaxrs",
    };

    private static final String[] USERAGENTS = {
            "Mozilla/5.0 (Linux; Android 5.1.1; SM-G928X Build/LMY47X) AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/47.0.2526.83 Mobile Safari/537.36",
            "Mozilla/5.0 (Windows Phone 10.0; Android 4.2.1; Microsoft; Lumia 950) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/46.0.2486.0 Mobile Safari/537.36 Edge/13.10586",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:15.0) Gecko/20100101 Firefox/15.0.1",
            "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 6P Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/47.0.2526.83 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 4.4.3; KFTHWI Build/KTU84M) AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Silk/47.1.79 like Chrome/47.0.2526.80 Safari/537.36",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:15.0) Gecko/20100101 Firefox/15.0.1",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 " +
                    "Safari/537.36",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:15.0) Gecko/20100101 Firefox/15.0.1",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/601.3.9 (KHTML, like Gecko) Version/9.0.2 " +
                    "Safari/601.3.9",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:15.0) Gecko/20100101 Firefox/15.0.1",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:15.0) Gecko/20100101 Firefox/15.0.1",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 " +
                    "Safari/537.36"
    };

    static final String[] LANGUAGES = {
            "en", "si", "fr", "en", "en", "ja", "en", "en", "ru", "fr", "ar"
    };

    static {
        populateApplications();
    }

    /**
     * Generate a random IP within the given IP range.
     *
     * @param startIP start of the IP range
     * @param endIP   end of the IP range
     * @return random IP within the given range
     */
    private static String generateRandomIPFromRange(String startIP, String endIP) {
        String[] start = startIP.split("\\.");
        String[] end = endIP.split("\\.");
        String[] newIp = new String[4];
        Random random = new Random();
        boolean sectionChanged = false;
        int diff;
        for (int i = 0; i < 4; i++) {
            if (sectionChanged) {
                newIp[i] = String.valueOf(random.nextInt(256));
                continue;
            }

            diff = Integer.parseInt(end[i]) - Integer.parseInt(start[i]);
            if (diff <= 0) {
                newIp[i] = String.valueOf(Integer.parseInt(start[i]));
                continue;
            }

            newIp[i] = String.valueOf((random.nextInt(diff) + Integer.parseInt(start[i])));
            sectionChanged = true;
        }

        return String.join(".", newIp);
    }

    /**
     * Returns a random value from a given array.
     *
     * @param array input array
     * @return random value from the array
     */
    private static String getRandomValueFromArray(String[] array) {
        return array[new Random().nextInt(array.length)];
    }

    /**
     * Generates and Event to be published.
     *
     * @param streamId  stream id
     * @param timestamp timestamp
     * @return event populated with random data
     */
    public static Event generateEvent(String streamId, long timestamp) {
        return new Event(streamId, timestamp, new Object[]{Quickstart.hostname, Quickstart.hostname}, null,
                getPayloadData(timestamp));
    }

    /**
     * Returns the payload for an event.
     *
     * @param timestamp timestamp
     * @return payload object
     */
    private static Object[] getPayloadData(long timestamp) {
        List<Object> payload = new ArrayList<>();

        String applicationName = getRandomValueFromArray(applications.keySet().toArray(
                new String[applications.size()]));

        payload.add(applicationName);
        payload.add("1.0.0");
        payload.add("admin");

        payload.add(getRandomValueFromArray(applications.get(applicationName).toArray(
                new String[applications.size()])));
        payload.add(timestamp);
        payload.add("");
        payload.add("webapp");
        payload.add(applicationName);
        payload.add("-");
        payload.add("GET");
        payload.add("");
        payload.add("text/html;charset=UTF-8");
        payload.add(Long.parseLong(getRandomValueFromArray(HTTP_RESPONSE_CODES)));

        String[] ipRange = getRandomValueFromArray(CLIENT_ADDRESSES).split("-");
        payload.add(generateRandomIPFromRange(ipRange[0], ipRange[1]));

        payload.add(getRandomValueFromArray(REFERERS));
        payload.add(getRandomValueFromArray(USERAGENTS));
        payload.add(Quickstart.hostname + ":8080");
        payload.add("");
        payload.add("");
        payload.add((long) (new Random().nextInt(300)));
        payload.add((long) -1);
        payload.add((long) -1);
        payload.add("");
        payload.add("");
        payload.add(getRandomValueFromArray(LANGUAGES));

        return payload.toArray();
    }

    /**
     * Populates the applications map with application name and it's request URIs.
     */
    private static void populateApplications() {
        applications.put("examples", Arrays.asList(
                "servlets/servlet/HelloWorldExample",
                "servlets/servlet/RequestInfoExample",
                "servlets/servlet/RequestHeaderExample",
                "servlets/servlet/RequestParamExample",
                "servlets/servlet/CookieExample",
                "servlets/servlet/SessionExample",
                "jsp/jsp2/el/basic-arithmetic.jsp"
        ));
        applications.put("musicstore-app", Arrays.asList(
                "artists",
                "albums",
                "albums/2016"
        ));
        applications.put("examples", Arrays.asList(
                "servlets/servlet/HelloWorldExample",
                "servlets/servlet/RequestInfoExample",
                "servlets/servlet/RequestHeaderExample"
        ));
        applications.put("bookstore-app", Arrays.asList(
                "authors",
                "top/2016",
                "top/2015"
        ));
        applications.put("examples", Arrays.asList(
                "servlets/servlet/HelloWorldExample"
        ));
        applications.put("bookstore-app", Arrays.asList(
                "authors",
                "top/2016",
                "top/2015"
        ));
        applications.put("utility-bill", Arrays.asList(
                "home",
                "pending/electricity",
                "pending/telephone",
                "history/electricity",
                "history/telephone",
                "remindme"
        ));
        applications.put("todo-app", Arrays.asList(
                "addNote",
                "deleteNote",
                "Achieve",
                "ShareNote",
                "Pending/lastweek",
                "Pending/lastmonth"
        ));
    }
}
