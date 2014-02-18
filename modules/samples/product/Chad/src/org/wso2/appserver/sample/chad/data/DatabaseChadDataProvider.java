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

import org.wso2.appserver.sample.chad.ChadDataProvider;
import org.wso2.appserver.sample.chad.command.*;

/**
 * Manages all the Chad data in a database
 */
public class DatabaseChadDataProvider implements ChadDataProvider {
    private CommandExecutor cmdExecutor;

    public DatabaseChadDataProvider() throws CommmandExecutionException {
        cmdExecutor = new CommandExecutor();
        GetAdminUserCommand getAdminUserCmd = new GetAdminUserCommand("admin");
        cmdExecutor.execute(getAdminUserCmd);

        if (getAdminUserCmd.getAdminUser() == null) {
            AddAdminUserCommand addAdminCmd = new AddAdminUserCommand("admin", "admin");
            cmdExecutor.execute(addAdminCmd);
        }
    }

    //to get the chad object for a givenID
    public ChadPoll getChadPoll(String pollId) {
        GetPollCommand command = new GetPollCommand(pollId);
        try {
            cmdExecutor.execute(command);
        } catch (CommmandExecutionException e) {
            throw new RuntimeException(e);
        }
        return command.getPoll();
    }

    public void updateChadPoll(ChadPoll chadPoll) {
        try {
            cmdExecutor.execute(new UpdatePollCommand(chadPoll));
        } catch (CommmandExecutionException e) {
            throw  new RuntimeException(e);
        }
    }

    public ChadPollResult getPollResult(String pollId) {
        GetPollResultCommand command = new GetPollResultCommand(pollId);
        try {
            cmdExecutor.execute(command);
        } catch (CommmandExecutionException e) {
            throw  new RuntimeException(e);
        }
        return command.getResult();
    }

    //to add a new chad poll
    public void addChadPoll(ChadPoll chadPoll) {
        try {
            cmdExecutor.execute(new AddPollCommand(chadPoll));
        } catch (CommmandExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public AdminUser getAdminUser(String username) {
        GetAdminUserCommand getAdminUserCmd = new GetAdminUserCommand(username);
        try {
            cmdExecutor.execute(getAdminUserCmd);
        } catch (CommmandExecutionException e) {
            throw new RuntimeException(e);
        }
        return getAdminUserCmd.getAdminUser();
    }

    public ChadPoll[] getAllChadPolls() {
        GetAllPollsCommand command = new GetAllPollsCommand();
        try {
            cmdExecutor.execute(command);
        } catch (CommmandExecutionException e) {
            throw new RuntimeException(e);
        }
        return command.getPolls();
    }
}
