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

package org.wso2.appserver.sample.chad;

import org.apache.axiom.om.util.UUIDGenerator;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appserver.sample.chad.command.*;
import org.wso2.appserver.sample.chad.data.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Contains all the logic related to Chad
 */
public class Chad {
    private static Log log = LogFactory.getLog(Chad.class);

    private ChadDataProvider dataProvider;
    private static final String LOGIN_MSG = "Access denied. " +
                                            "Only Administrators are authorized to carry out this operation.<br/><br/>" +
                                            "If you are an administrator, please login and retry.";
    public static final String ADMIN_LOGGED_IN = "wso2appserver.admin.logged.in";


    public Chad() {
        try {
            dataProvider = new DatabaseChadDataProvider();
        } catch (CommmandExecutionException e) {
            log.error("Error while trying to instantiate DatabaseChadDataProvider", e);
        }
    }

    /**
     * Create a new poll. Only an admin can add votes
     *
     * @param title
     * @param description
     * @param isSingleVote
     * @param choices
     * @return The result of adding a poll
     * @throws ChadAuthenticationException If the invoking user is not authenticated
     * @throws DuplicatePollException      If a poll with title <code>title</code> already exists
     * @throws ChadChoiceException         If <code>choices</code> contains repeating items or
     *                                     If the number of choices are 1 or less
     * @throws IllegalArgumentException    If the title is null or empty and/or
     */
    public synchronized String createPoll(String title,
                                          String description,
                                          boolean isSingleVote,
                                          String[] choices) throws ChadAuthenticationException,
                                                                   DuplicatePollException,
                                                                   ChadChoiceException {
        checkAuthentication();
        if (title == null || title.trim().length() == 0) {
            throw new IllegalArgumentException("Poll title cannot be null or empty.");
        }

        // The Poll Title should be unique
        if (containsPoll(listPolls(), title)) {
            throw new DuplicatePollException("Poll with title \"" + title + "\" already exists.");
        }

        ChadPoll chadPoll = new ChadPoll();
        String uuid = UUIDGenerator.getUUID();
        chadPoll.setPollId(uuid);
        chadPoll.setIsStopped(false);
        chadPoll.setIsSingleVote(isSingleVote);
        chadPoll.setTitle(title);
        chadPoll.setDescription(description);

        // Some choices may be null or empty. Eliminate those choices.
        Collection validChoices = new ArrayList();
        for (int i = 0; i < choices.length; i++) {
            String choice = choices[i];
            if (choice != null && choice.trim().length() != 0) {
                validChoices.add(choice);
            }
        }
        if (validChoices.size() <= 1) {
            throw new ChadChoiceException("A valid poll should contain at least two choices.");
        }
        for (Iterator iter = validChoices.iterator(); iter.hasNext();) {
            String option = (String) iter.next();
            if (option == null || option.trim().length() == 0) {
                continue;
            }
            if (containsChoice(chadPoll.getChoices(), option)) {
                throw new ChadChoiceException("Choice \"" + option + "\" is duplicated. " +
                                              "A valid poll cannot contain duplicate choices.");
            }
            ChadChoice choice = new ChadChoice();
            choice.setChoiceName(option);
            chadPoll.addChoice(choice);
        }
        dataProvider.addChadPoll(chadPoll);
        return uuid;
    }

    private boolean containsChoice(ChadChoice[] choices, String choiceStr) {
        for (int i = 0; i < choices.length; i++) {
            ChadChoice choice = choices[i];
            if (choice == null) {
                return false;
            }
            if (choice.getChoiceName().toLowerCase().equals(choiceStr.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean containsPoll(ChadPoll[] polls, String pollTitle) {
        for (int i = 0; i < polls.length; i++) {
            ChadPoll poll = polls[i];
            if (poll == null) {
                return false;
            }
            if (poll.getTitle().toLowerCase().equals(pollTitle.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Start a poll. Only an admin can do this.
     *
     * @param pollId
     */
    public synchronized void startPoll(String pollId) throws ChadAuthenticationException {
        checkAuthentication();
        ChadPoll chadPoll = dataProvider.getChadPoll(pollId);
        if (chadPoll != null) {
            chadPoll.setIsStopped(false);
            dataProvider.updateChadPoll(chadPoll);
        }
    }

    /**
     * Stop a poll. Only an admin can do this.
     *
     * @param pollId
     */
    public synchronized void stopPoll(String pollId) throws ChadAuthenticationException {
        checkAuthentication();
        ChadPoll chadPoll = dataProvider.getChadPoll(pollId);
        if (chadPoll != null) {
            chadPoll.setIsStopped(true);
            dataProvider.updateChadPoll(chadPoll);
        }
    }

    public synchronized boolean login(String username, String password) {
        try {
            GetAdminUserCommand getAdminCmd = new GetAdminUserCommand(username);
            CommandExecutor cmdExec = new CommandExecutor();
            cmdExec.execute(getAdminCmd);
            AdminUser adminUser = getAdminCmd.getAdminUser();
            if (adminUser != null && adminUser.getPassword().equals(password)) {
                MessageContext.getCurrentMessageContext().getServiceGroupContext()
                        .setProperty(Chad.ADMIN_LOGGED_IN, "true");
                return true;
            } else {
                MessageContext.getCurrentMessageContext().getServiceGroupContext()
                        .setProperty(Chad.ADMIN_LOGGED_IN, "false");
            }
        } catch (CommmandExecutionException e) {
            log.error("Exception occurred while trying to login", e);
        }
        return false;
    }

    public synchronized void addAdminUser(String username,
                                          String password) throws Exception {
        checkAuthentication();
        CommandExecutor commandExecutor = new CommandExecutor();
        try {
            GetAdminUserCommand getAdminCmd = new GetAdminUserCommand(username);
            commandExecutor.execute(getAdminCmd);
            if (getAdminCmd.getAdminUser() != null) {
                throw new Exception("An Administrator with name \'" + username.trim() +
                                    "\' already exists!");
            }
            commandExecutor.execute(new AddAdminUserCommand(username, password));
        } catch (CommmandExecutionException e) {
            throw new Exception(e);
        }
    }

    public synchronized void deleteAdminUser(String username) throws Exception {
        checkAuthentication();
        try {
            new CommandExecutor().execute(new DeleteAdminUserCommand(username));
        } catch (CommmandExecutionException e) {
            throw new Exception(e);
        }
    }

    public synchronized void changePassword(String username,
                                            String oldPassword,
                                            String newPassword) throws Exception {
        checkAuthentication();
        try {
            new CommandExecutor().execute(new ChangeAdminUserPasswordCommand(username,
                                                                             oldPassword,
                                                                             newPassword));
        } catch (CommmandExecutionException e) {
            if (e.getCause() instanceof ChadAuthenticationException) {
                throw new Exception(e.getCause().getMessage());
            } else {
                throw new Exception(e);
            }
        }
    }

    public synchronized String[] listAdminUsers() throws Exception {
        checkAuthentication();
        ListAdminUsersCommand command = new ListAdminUsersCommand();
        try {
            new CommandExecutor().execute(command);
        } catch (CommmandExecutionException e) {
            throw new Exception(e);
        }
        return command.getUsers();
    }

    public synchronized void logout() {
        MessageContext.getCurrentMessageContext().getServiceGroupContext()
                .setProperty(Chad.ADMIN_LOGGED_IN, "false");
    }

    private void checkAuthentication() throws ChadAuthenticationException {
        if (!isAuthenticated()) {
            throw new ChadAuthenticationException(LOGIN_MSG);
        }
    }

    private boolean isAuthenticated() {
        return "true".equals(MessageContext.getCurrentMessageContext()
                .getServiceGroupContext().getProperty(Chad.ADMIN_LOGGED_IN));
    }

    /**
     * @return The currently active polls
     */
    public synchronized ChadPoll[] getStartedPolls() {
        ChadPoll[] allChadPolls = dataProvider.getAllChadPolls();
        Collection startedPolls = new ArrayList();
        for (int i = 0; i < allChadPolls.length; i++) {
            ChadPoll poll = allChadPolls[i];
            if (!poll.getIsStopped()) {
                startedPolls.add(poll);
            }
        }
        return (ChadPoll[]) startedPolls.toArray(new ChadPoll[startedPolls.size()]);
    }

    /**
     * @return The currently inactive polls
     */
    public synchronized ChadPoll[] getStoppedPolls() {
        ChadPoll[] allChadPolls = dataProvider.getAllChadPolls();
        Collection stoppedPolls = new ArrayList();
        for (int i = 0; i < allChadPolls.length; i++) {
            ChadPoll poll = allChadPolls[i];
            if (poll.getIsStopped()) {
                stoppedPolls.add(poll);
            }
        }
        return (ChadPoll[]) stoppedPolls.toArray(new ChadPoll[stoppedPolls.size()]);
    }

    /**
     * @return All polls
     */
    public synchronized ChadPoll[] listPolls() {
        ChadPoll[] chadPolls = dataProvider.getAllChadPolls();
        for (int i = 0; i < chadPolls.length; i++) {
            getResult(chadPolls[i].getPollId());
        }
        return chadPolls;
    }

    /**
     * isEligibleForVoting method will check if the User is eligible for voting.
     * If the candidtae could vote retured ture otehrwise false
     *
     * @param pollId
     * @return boolean
     * @throws AxisFault
     */
    public synchronized boolean isEligibleForVoting(String pollId) throws AxisFault {
        ChadPoll chadPoll = dataProvider.getChadPoll(pollId);
        String votersIP =
                (String) MessageContext.getCurrentMessageContext()
                        .getProperty(MessageContext.REMOTE_ADDR);
        if (chadPoll != null) {
            if (chadPoll.hasVoted(votersIP)) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Vote
     *
     * @param pollId
     * @param choices
     * @return The result of the vote
     */
    public synchronized String vote(String pollId,
                                    String[] choices) throws AxisFault {
        //TODO: Need to handle the logic for user to indicate level of choice
        ChadPoll chadPoll = dataProvider.getChadPoll(pollId);
        String votersIP =
                (String) MessageContext.getCurrentMessageContext().
                        getProperty(MessageContext.REMOTE_ADDR);
        if (chadPoll != null) {
            if (chadPoll.hasVoted(votersIP)) {
                return "You have already voted for poll \"" + chadPoll.getTitle() + "\"";
            }
            if (chadPoll.getIsStopped()) {
                return "This poll has been stopped.";
            }
            if (chadPoll.getIsSingleVote() && (choices.length > 1)) {
                return "Only one choice is available for this poll.";
            }
            for (int i = 0; i < choices.length; i++) {
                ChadChoice chadChoice = chadPoll.getChadChoice(choices[i]);
                if (chadChoice != null) {
                    chadChoice.addMark(1);
                    chadPoll.addVotedIP(new VotedIPAddress(votersIP));
                    dataProvider.updateChadPoll(chadPoll);
                } else {
                    return "Choice " + choices[i] + " not found!";
                }
            }
            return "Vote successful";
        } else {
            return "Chad Poll with ID " + pollId + " not found";
        }
    }

    /**
     * @param pollId
     * @return The poll result
     */
    public synchronized ChadPollResult getResult(String pollId) {
        return dataProvider.getPollResult(pollId);
    }
}
