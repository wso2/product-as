/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.appserver.sample.chad.data;

/**
 * POJO representing a particular IP address from which a particular poll has received a vote
 */
public class VotedIPAddress extends ChadData{
    private String ip;
    private ChadPoll poll; // many-one relationship between VotedIPAddress and ChadPolls

    public VotedIPAddress() {
    }

    public VotedIPAddress(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public ChadPoll getPoll() {
        return poll;
    }

    public void setPoll(ChadPoll poll) {
        this.poll = poll;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return ip.equals(((VotedIPAddress) o).ip);
    }

    public int hashCode() {
        return ip.hashCode();
    }
}
