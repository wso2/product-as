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
 * POJO representing a particular choice in a particular poll
 */
public class ChadChoice extends ChadData{
    private String choiceName;
    private int numberOfVotes;
    private float votePercentage; // non-persistent field
    private ChadPoll poll;

    public String getChoiceName() {
        return choiceName;
    }

    public void setChoiceName(String choiceName) {
        this.choiceName = choiceName;
    }

    public void setNumberOfVotes(int numberOfVotes) {
        this.numberOfVotes = numberOfVotes;
    }

    public int getNumberOfVotes() {
        return numberOfVotes;
    }

    public void addMark(int mark) {
        this.numberOfVotes += mark;
    }

    public String toString() {
        return choiceName + " : " + numberOfVotes;
    }

    public float getVotePercentage() {
        return votePercentage;
    }

    public void setVotePercentage(float votePercentage) {
        this.votePercentage = votePercentage;
    }

    ChadPoll getPoll() {
        return poll;
    }

    void setPoll(ChadPoll poll) {
        this.poll = poll;
    }
}
