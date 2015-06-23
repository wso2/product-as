/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.appserver.integration.tests.webapp.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.tyrus.client.ClientManager;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EchoWebSocketTestCase extends ASIntegrationTest {

    private String baseWsUrl;
    private String baseWssUrl;

    private static CountDownLatch messageLatch;
    private static final String SENT_MESSAGE = "Hello World";
    private Log log = LogFactory.getLog(EchoWebSocketTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        //parse webapp url to get hostname and port
        String host = asServer.getInstance().getHosts().get("default");
        String httpPort = asServer.getInstance().getPorts().get("http");
        String httpsPort = asServer.getInstance().getPorts().get("https");

        baseWsUrl = "ws://" + host + ":" + httpPort;
        baseWssUrl = "wss://" + host + ":" + httpsPort; //secure
        // + "/example/websocket/echoProgrammatic";
    }

    @Test(groups = "wso2.as", description = "Testing websocket")
    public void testInvokeEchoSample()  {
        String echoProgrammaticEndpoint = baseWsUrl + "/example/websocket/echoProgrammatic";;

        try {
            messageLatch = new CountDownLatch(1);

            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client = ClientManager.createClient();
            Session session = client.connectToServer(new clientEndpoint(),
                                                     cec, new URI(echoProgrammaticEndpoint));

            session.getBasicRemote().sendText(SENT_MESSAGE);
            messageLatch.await(100, TimeUnit.SECONDS);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Assert.fail("Websocket initialization failed due to an exception - " +
                        e.getMessage(), e);
        }
    }

    private class clientEndpoint extends Endpoint {

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            log.info("Websocket session id: " + session.getId());
            session.addMessageHandler(new MessageHandler.Whole<String>() {

                @Override
                public void onMessage(String message) {
                    log.info("Received message: " + message);
                    Assert.assertEquals(message, SENT_MESSAGE,
                                        "Websocket Echo response is incorrect.");
                    messageLatch.countDown();
                }
            });
        }
    }

}
