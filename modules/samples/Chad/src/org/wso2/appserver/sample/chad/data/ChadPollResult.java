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
 * POJO representing a poll result
 */
public class ChadPollResult {
    private int totalNumberOfVotes;
    private String pollId;
    private String pollTitle;
    private String pollDescription;
    private boolean isPollStopped;
    private boolean isSingleVote;
    private ChadChoice[] orderedChoices;

    public ChadPollResult() {
    }

    public ChadChoice[] getOrderedChoices() {
        return orderedChoices;
    }

    public void setOrderedChoices(ChadChoice[] orderedChoices) {
        this.orderedChoices = orderedChoices;
    }

    public String getPollDescription() {
        return pollDescription;
    }

    public void setPollDescription(String pollDescription) {
        this.pollDescription = pollDescription;
    }

    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }

    public String getPollTitle() {
        return pollTitle;
    }

    public void setPollTitle(String pollTitle) {
        this.pollTitle = pollTitle;
    }

    public int getTotalNumberOfVotes() {
        return totalNumberOfVotes;
    }

    public void setTotalNumberOfVotes(int totalNumberOfVotes) {
        this.totalNumberOfVotes = totalNumberOfVotes;
    }

    public boolean isPollStopped() {
        return isPollStopped;
    }

    public void setPollStopped(boolean pollStopped) {
        isPollStopped = pollStopped;
    }

    public boolean isSingleVote() {
        return isSingleVote;
    }

    public void setSingleVote(boolean singleVote) {
        isSingleVote = singleVote;
    }
}
