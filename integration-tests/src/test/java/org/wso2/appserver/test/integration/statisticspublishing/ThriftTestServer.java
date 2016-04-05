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

package org.wso2.appserver.test.integration.statisticspublishing;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.databridge.commons.Credentials;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.DifferentStreamDefinitionAlreadyDefinedException;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.carbon.databridge.commons.utils.EventDefinitionConverterUtils;
import org.wso2.carbon.databridge.core.AgentCallback;
import org.wso2.carbon.databridge.core.DataBridge;
import org.wso2.carbon.databridge.core.definitionstore.InMemoryStreamDefinitionStore;
import org.wso2.carbon.databridge.core.exception.DataBridgeException;
import org.wso2.carbon.databridge.core.exception.StreamDefinitionStoreException;
import org.wso2.carbon.databridge.core.internal.authentication.AuthenticationHandler;
import org.wso2.carbon.databridge.receiver.thrift.internal.ThriftDataReceiver;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thrift test server to be used in the http statistics publishing test case.
 *
 * @since 6.0.0
 */
public class ThriftTestServer {

    private static Log log = LogFactory.getLog(ThriftTestServer.class);
    private int thriftPort;

    private InMemoryStreamDefinitionStore inMemoryStreamDefinitionStore;
    private ThriftDataReceiver thriftDataReceiver;
    private AtomicInteger numberOfEventsReceived;


    public ThriftTestServer(int thriftPort) {
        this.thriftPort = thriftPort;
        inMemoryStreamDefinitionStore = new InMemoryStreamDefinitionStore();
        numberOfEventsReceived = new AtomicInteger(0);
    }

    /**
     * Starts the server
     *
     * @throws DataBridgeException
     */
    public void start() throws DataBridgeException {
        DataPublisherTestUtil.setKeyStoreParams();
        DataBridge dataBridge = new DataBridge(new AuthenticationHandler() {
            @Override
            public boolean authenticate(String s, String s1) {
                return true;
            }

            @Override
            public String getTenantDomain(String s) {
                return "admin";
            }
        }, inMemoryStreamDefinitionStore);

        thriftDataReceiver = new ThriftDataReceiver(thriftPort, dataBridge);

        dataBridge.subscribe(new AgentCallback() {
            @Override
            public void definedStream(StreamDefinition streamDefinition, Credentials credentials) {
                log.info("Stream definition added: " + streamDefinition);
            }

            @Override
            public void removeStream(StreamDefinition streamDefinition, Credentials credentials) {
                log.info("Stream removed: " + streamDefinition);
            }

            @Override
            public void receive(List<Event> list, Credentials credentials) {
                log.info("Event received.");
                numberOfEventsReceived.addAndGet(list.size());
            }
        });

        log.info("Starting server...");
        thriftDataReceiver.start(Constants.HOST);
        log.info("Test Server Started");
    }

    /**
     * Stops the server.
     */
    public void stop() {
        thriftDataReceiver.stop();
    }

    /**
     * Returns the number of events received.
     *
     * @return number of events received.
     */
    public int getNumberOfEventsReceived() {
        return numberOfEventsReceived.get();
    }

    /**
     * Adds a stream definition to in memory stream definition store.
     *
     * @param streamDefinition string representation of the stream definition.
     * @throws MalformedStreamDefinitionException
     * @throws DifferentStreamDefinitionAlreadyDefinedException
     * @throws StreamDefinitionStoreException
     */
    public void addStreamDefinition(String streamDefinition) throws MalformedStreamDefinitionException,
            DifferentStreamDefinitionAlreadyDefinedException, StreamDefinitionStoreException {
        inMemoryStreamDefinitionStore.saveStreamDefinition(new Credentials("admin", "admin", "admin"),
                EventDefinitionConverterUtils.convertFromJson(streamDefinition));
    }
}
