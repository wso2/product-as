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

package org.wso2.appserver.sample.storage;

import org.springframework.stereotype.Service;

import java.util.Hashtable;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *  Sample JAX-RS class for storing and retrieving key value pairs.
 */
@Service
@Path("/store")
public class SimpleStorageService {

    Map<String, String> storageMap = new Hashtable<>();

    public SimpleStorageService() {
        storageMap.put("defaultKey", "This is a simple REST storage service for storing key value pairs.");
    }

    @GET
    @Path("/get/{key}")
    @Produces("text/plain")
    public String getValue(@PathParam("key") String key) {
        if (storageMap.containsKey(key)) {
            return storageMap.get(key);
        }
        return "Key not found.";
    }

    @POST
    @Path("/put")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    public String putValue(@FormParam("key") String key, @FormParam("value") String value) {
        if (storageMap.containsKey(key)) {
            return "Key: " + key + " already exist.";
        }
        storageMap.put(key, value);
        return "Stored successfully";
    }
}
