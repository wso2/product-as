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


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;

@Named("LiftOperator")
public class LiftOperator implements Greeter {

    private int meetings;

    public LiftOperator() {
        meetings = 0;
    }

    @Override
    public String greet() {
        String greeting;

        if (meetings == 0)
            greeting = "Lift Operator: Hi, this is the first time I meet you";
        else
            greeting = "Lift Operator: Hi, I met you for " + meetings + " time(s)";

        meetings++;
        return greeting;
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("Post construct of LiftOperator");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("Pre destroy of LifeOperator");
    }

}
