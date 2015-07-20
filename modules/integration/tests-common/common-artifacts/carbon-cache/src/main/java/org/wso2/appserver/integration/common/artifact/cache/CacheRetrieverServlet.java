package org.wso2.appserver.integration.common.artifact.cache;
/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CacheRetrieverServlet extends HttpServlet {

    private static Log log = LogFactory.getLog(CacheRetrieverServlet.class);
    private CacheManager cacheManager = Caching.getCacheManagerFactory().getCacheManager("tsampleCacheManager");
    private Cache<String, String> cache = cacheManager.getCache("sampleCache");

    private static final String SET_CACHE = "setCache";
    private static final String GET_CACHE = "getCache";
    private static final String RANDOM_CACHE = "randomCache";
    private static final String TEMP_CACHE_VALUE = "tempCacheValue";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doProcess(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doProcess(request, response);
    }

    private void doProcess(HttpServletRequest request, HttpServletResponse response) {
        String action = request.getParameter("action");
        String key = request.getParameter("key");
        if (action != null && !action.isEmpty() && key != null) {
            switch (action) {
                case SET_CACHE:
                    setCache(key, request, response);
                    break;
                case GET_CACHE:
                    getCache(key, request, response);
                    break;
                case RANDOM_CACHE:
                    getRandomCacheManager(key, request, response);
                    break;
            }
        } else {
            log.info("action and key should be initialized");
        }
    }

    private void getRandomCacheManager(String key, HttpServletRequest request, HttpServletResponse response) {
        CacheManager defaultCacheMgr = Caching.getCacheManager();
        Cache<String, String> randomCache = defaultCacheMgr.getCache("randomCache");
        randomCache.put(key,"tmpCacheValue");
        response.addHeader("tmpCacheValue", randomCache.get(key));
    }

    private void setCache(String key, HttpServletRequest request, HttpServletResponse response) {
        String value = request.getParameter("value");
        cache.put(key, value);
        String setValue = cache.get(key);
        response.addHeader("added-cached-value", setValue);
    }

    private void getCache(String key, HttpServletRequest request, HttpServletResponse response) {
        String returnVal = cache.get(key);
        if (returnVal == null) {
            cache.put(key, TEMP_CACHE_VALUE);
            returnVal = cache.get(key);
        }
        response.addHeader("cached-value", returnVal);
    }
}