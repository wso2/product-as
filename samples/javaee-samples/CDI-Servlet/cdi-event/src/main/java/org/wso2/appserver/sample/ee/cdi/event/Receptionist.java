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

package org.wso2.appserver.sample.ee.cdi.event;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Generate custom greeting messages.
 */
@Named("Receptionist")
@RequestScoped
public class Receptionist implements Greeter {

    private static final Log log = LogFactory.getLog(Receptionist.class);

    private int meetings;

    public Receptionist() {
        meetings = 0;
    }

    @Override
    public String greet() {
        String greeting;

        if (meetings == 0) {
            greeting = "Receptionist: Hi, this is the first time I meet you";
        } else {
            greeting = "Receptionist: Hi, I met you for " + meetings + " time(s)";
        }
        meetings++;
        return greeting;
    }

    @PostConstruct
    public void postConstruct() {
        log.info("Post construct of Receptionist");
    }

    @PreDestroy
    public void preDestroy() {
        log.info("Pre destroy of Receptionist");
    }
}
