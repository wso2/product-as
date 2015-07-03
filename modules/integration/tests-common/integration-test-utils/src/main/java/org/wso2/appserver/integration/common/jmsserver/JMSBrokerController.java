/*
*Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.appserver.integration.common.jmsserver;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JMSBrokerController {
	private static final Log log = LogFactory.getLog(JMSBrokerController.class);
	private String serverName;
	private List<TransportConnector> transportConnectors;
	private BrokerService broker;
	private static boolean isBrokerStarted = false;

	public JMSBrokerController(String serverName) {
		this.serverName = serverName;
		this.transportConnectors = new ArrayList();
		TransportConnector connector = new TransportConnector();
		connector.setName("tcp");

		try {
			connector.setUri(new URI("tcp://localhost:61616"));
		} catch (URISyntaxException var5) {
			log.error("Invalid URI", var5);
		}

		this.transportConnectors.add(connector);
	}

	public JMSBrokerController(String serverName, List<TransportConnector> transportConnectors) {
		this.serverName = serverName;
		this.transportConnectors = transportConnectors;
	}

	public String getServerName() {
		return this.serverName;
	}

	public boolean start(BrokerService broker) {
		try {
			log.info("JMSServerController: Preparing to start JMS Broker: " + this.serverName);
			this.broker = broker;
			this.broker.setBrokerName(this.serverName);
			log.info(this.broker.getBrokerDataDirectory());
			this.broker.setDataDirectory(System.getProperty("carbon.home") + File.separator + this.broker.getBrokerDataDirectory());
			this.broker.setTransportConnectors(this.transportConnectors);
			this.broker.setPersistent(true);
			this.broker.start();
			setBrokerStatus(true);
			log.info("JMSServerController: Broker is Successfully started. continuing tests");
			return true;
		} catch (Exception var2) {
			log.error("JMSServerController: There was an error starting JMS broker: " + this.serverName, var2);
			return false;
		}
	}

	public boolean stop() {
		try {
			log.info(" ************* Stopping **************");
			if(this.broker.isStarted()) {
				this.broker.stop();
				Iterator e = this.transportConnectors.iterator();

				while(e.hasNext()) {
					TransportConnector transportConnector = (TransportConnector)e.next();
					transportConnector.stop();
				}

				setBrokerStatus(false);
			}

			return true;
		} catch (Exception var3) {
			log.error("Error while shutting down the broker", var3);
			return false;
		}
	}

	public static boolean isBrokerStarted() {
		return isBrokerStarted;
	}

	private static void setBrokerStatus(boolean status) {
		isBrokerStarted = status;
	}
}
