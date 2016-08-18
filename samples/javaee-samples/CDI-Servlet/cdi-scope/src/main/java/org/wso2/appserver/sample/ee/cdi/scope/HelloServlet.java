/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// Scope @PostConstructor @PreDestroy

package org.wso2.appserver.sample.ee.cdi.scope;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.annotation.WebServlet;

/**
 * Generate greetings.
 */
@WebServlet(name = "HelloServlet", urlPatterns = "/")
public class HelloServlet extends javax.servlet.http.HttpServlet {
    private static final Log log = LogFactory.getLog(HelloServlet.class);

    private static final long serialVersionUID = -2515820013618888138L;

    @Inject
    @Named("Receptionist")
    private transient Greeter receptionist;

    @Inject
    @Named("LiftOperator")
    private transient Greeter lifeOperator;

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
            throws javax.servlet.ServletException, IOException {
        PrintWriter writer = response.getWriter();
        String receptionistGreeting = receptionist.greet();
        String lifeOperatorGreeting = lifeOperator.greet();

        writer.println(receptionistGreeting);
        log.info(receptionistGreeting);

        writer.println(lifeOperatorGreeting);
        log.info(lifeOperatorGreeting);
        writer.close();
    }
}
