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
package org.wso2.appserver.integration.common.artifacts.java.util.logging;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Path("/")
public class JavaUtilLogGenerator {

	private static final Logger logger = Logger.getLogger("jul");
	FileHandler fileHandler;

	@GET
	@Path("/logging")
	public String printLogs() throws IOException {

		fileHandler =
				new FileHandler(System.getProperty("carbon.home") + "/repository/logs/web_app_java_util_logging.log");
		logger.addHandler(fileHandler);
		SimpleFormatter formatter = new SimpleFormatter();
		fileHandler.setFormatter(formatter);

		logger.info("INFO LOG");
		logger.warning("WARNING LOG");
		logger.severe("SEVERE LOG");

		return "hello";
	}

}
