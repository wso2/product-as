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

package org.wso2.appserver.sample.ee.cdi.produces;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;

@SessionScoped
public class GreeterFactory implements Serializable {

    private static final long serialVersionUID = 5269302440619391616L;

    private GreetingType greetingType;

    @Produces
    @Greetings(GreetingType.HI)
    public Greeter getGreeterOne() {
        return new GreeterImplOne();
    }

    @Produces
    @Greetings(GreetingType.BYE)
    public Greeter getGreeterTwo() {
        return new GreeterImplTwo();
    }
}
