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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * POJO representing a poll
 */
public class ChadPoll extends ChadData {
    private String pollId;
    private String title;
    private String description;
    private boolean isSingleVote;
    private boolean isStopped;
    private Set votedIPs = new HashSet();  // one-many relationship between Polls and Voted IPs
    private Set choiceSet = new HashSet();  // one-many relationship between Polls and Choices

    public ChadPoll() {
    }

    public boolean getIsSingleVote() {
        return isSingleVote;
    }

    public void setIsSingleVote(boolean singleVote) {
        this.isSingleVote = singleVote;
    }

    public boolean hasVoted(String ip) {
        return votedIPs.contains(new VotedIPAddress(ip));
    }

    public void addVotedIP(VotedIPAddress ip) {
        votedIPs.add(ip);
        ip.setPoll(this);
    }

    Set getVotedIPs() {
        return votedIPs;
    }

    Set getChoiceSet() {
        return choiceSet;
    }

    public void addChoice(ChadChoice choice) {
        choiceSet.add(choice);
        choice.setPoll(this);
    }

    void setChoiceSet(Set choiceSet) {
        this.choiceSet = choiceSet;
    }

    void setVotedIPs(Set votedIPs) {
        this.votedIPs = votedIPs;
    }

     public ChadChoice getChadChoice(String choice) {
        for (Iterator iter = choiceSet.iterator(); iter.hasNext();) {
            ChadChoice chadChoice = (ChadChoice) iter.next();
            if (chadChoice.getChoiceName().equals(choice)) {
                return chadChoice;
            }
        }
        return null;
    }

    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }

    public boolean getIsStopped() {
        return isStopped;
    }

    public void setIsStopped(boolean stopped) {
        this.isStopped = stopped;
    }

    public ChadChoice[] getChoices() {
        return (ChadChoice[]) choiceSet.toArray(new ChadChoice[choiceSet.size()]);
    }

    public void setChoices(ChadChoice[] choices) {
        for (int i = 0; i < choices.length; i++) {
            addChoice(choices[i]);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ChadPoll chadPoll = (ChadPoll) o;

        if (!title.equals(chadPoll.title)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return title.hashCode();
    }
}
