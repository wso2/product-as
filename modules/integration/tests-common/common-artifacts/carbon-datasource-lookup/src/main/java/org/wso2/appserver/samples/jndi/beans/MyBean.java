/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.appserver.samples.jndi.beans;

public class MyBean {
    private String foo = "My Bean";

    public String getFoo() {
        return (this.foo);
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    private int bar = 0;

    public int getBar() {
        return (this.bar);
    }

    public void setBar(int bar) {
        this.bar = bar;
    }

}

