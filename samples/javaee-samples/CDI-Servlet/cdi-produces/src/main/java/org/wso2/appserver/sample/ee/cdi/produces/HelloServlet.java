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

package org.wso2.appserver.sample.ee.cdi.produces;

import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;

/**
 * Generate greeting responses by two different greeters and send to the requester.
 */
@WebServlet(name = "HelloServlet", urlPatterns = "/")
public class HelloServlet extends javax.servlet.http.HttpServlet {

    private static final long serialVersionUID = -2427002074500672880L;
    @Inject
    @Greetings(GreetingType.HI)
    private transient Greeter greeter1;

    @Inject
    @Greetings(GreetingType.BYE)
    private transient Greeter greeter2;

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
            throws javax.servlet.ServletException, IOException {
        PrintWriter writer = response.getWriter();
        writer.println(greeter1.greet());
        writer.println(greeter2.greet());
        writer.close();
    }
}
