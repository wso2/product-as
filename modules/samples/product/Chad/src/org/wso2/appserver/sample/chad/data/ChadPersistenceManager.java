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

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.exception.ConstraintViolationException;
import org.wso2.appserver.sample.chad.ChadAuthenticationException;
import org.wso2.appserver.sample.chad.ChadChoiceComparator;
import org.wso2.appserver.sample.chad.DuplicatePollException;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Manage all persistence related functionality
 */
public class ChadPersistenceManager {
    private ChadHibernateConfig hbConfig;
    private static Logger log = Logger.getLogger(ChadPersistenceManager.class);

    public ChadPersistenceManager(ChadHibernateConfig hbConfig) {
        this.hbConfig = hbConfig;
    }

    /**
     * Add a new Administrator
     *
     * @param username
     * @param password
     * @throws UserAlreadyExistsException If an administrator with the name <code>username</code>
     *                                    already exists
     */
    public void addAdminUser(String username, String password) throws UserAlreadyExistsException {
        Session session = hbConfig.currentSession();
        Transaction tx = session.beginTransaction();
        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(username.trim());
        adminUser.setPassword(password);
        adminUser.setLastUpdatedTime(new Date());
        try {
            session.persist(adminUser);
            tx.commit();
        } catch (ConstraintViolationException e) {
            String msg = "Administrator " + username.trim() + " already exists";
            log.warn(msg, e);
            throw new UserAlreadyExistsException(msg);
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException("Cannot create Administrator account", e);
        }
    }

    /**
     * Delete an administrator
     *
     * @param username
     */
    public void deleteAdminUser(String username) {
        Session session = hbConfig.currentSession();
        Transaction tx = session.beginTransaction();
        try {
            Criteria criteria = session.createCriteria(AdminUser.class);
            criteria.add(Expression.eq("username", username.trim()));

            Object obj = criteria.uniqueResult();
            if (obj != null) {
                session.delete(obj);
            }
            tx.commit();
        } catch (Throwable t) {
            tx.rollback();
        }
    }

    /**
     * Add a new Poll
     *
     * @param poll
     * @throws DuplicatePollException If a poll with the same Title already exists
     */
    public void addPoll(ChadPoll poll) throws DuplicatePollException {
        Session session = hbConfig.currentSession();
        Transaction tx = session.beginTransaction();
        poll.setLastUpdatedTime(new Date());
        try {
            session.persist(poll);
            session.flush();
            tx.commit();
        } catch (ConstraintViolationException e) {
            String msg = "Trying to create duplicate poll entity";
            log.warn(msg, e);
            throw new DuplicatePollException(msg, e);
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
            throw new RuntimeException("Cannot create entity", e);
        } 
    }

    /**
     * Get administrator
     *
     * @param username
     * @return Get administrator
     */
    public AdminUser getAdminUser(String username) {
        Session session = hbConfig.currentSession();
        Transaction tx = session.beginTransaction();
        AdminUser admin = null;
        try {
            Criteria criteria = session.createCriteria(AdminUser.class);
            criteria.add(Expression.eq("username", username.trim()));
            admin = (AdminUser) criteria.uniqueResult();
            session.evict(admin);
            tx.commit();
        } catch (Throwable e) {
            e.printStackTrace();
            tx.rollback();
        }
        return admin;
    }

    /**
     * Change the password of the administrator
     *
     * @param username
     * @param oldPassword
     * @param newPassword
     * @throws ChadAuthenticationException If the <code>oldPassword</code> is incorrect
     */
    public void changeAdminPassword(String username,
                                    String oldPassword,
                                    String newPassword) throws ChadAuthenticationException {
        Session session = hbConfig.currentSession();
        Transaction tx = session.beginTransaction();
        try {
            Criteria criteria = session.createCriteria(AdminUser.class);
            criteria.add(Expression.eq("username", username.trim()));
            AdminUser adminUser = (AdminUser) criteria.uniqueResult();
            if (!adminUser.getPassword().equals(oldPassword)) {
                throw new ChadAuthenticationException("Old password is incorrect!");
            }
            adminUser.setPassword(newPassword);
            session.update(adminUser);
            tx.commit();
        } catch (ChadAuthenticationException e) {
            throw e;
        } catch (Throwable t) {
            t.printStackTrace();
            tx.rollback();
        }
    }

    /**
     * Retrieve a chad poll
     *
     * @param pollId
     * @return The chad poll with uuid <code>pollId</code>
     */
    public ChadPoll getPoll(String pollId) {
        Session session = hbConfig.currentSession();
        Transaction tx = session.beginTransaction();
        ChadPoll poll = null;
        try {
            Criteria criteria = session.createCriteria(ChadPoll.class);
            criteria.add(Expression.eq("pollId", pollId.trim()));
            poll = (ChadPoll) criteria.uniqueResult();
            session.evict(poll);
            tx.commit();
        } catch (Throwable t) {
            t.printStackTrace();
            tx.rollback();
        }
        return poll;
    }

    /**
     * Get all the usernames of administrators
     *
     * @return usernames of administrators
     */
    public String[] getAdminUsernames() {
        Session session = hbConfig.currentSession();
        Transaction tx = session.beginTransaction();
        String[] users = new String[0];
        try {
            Criteria criteria = session.createCriteria(AdminUser.class);
            List list = criteria.list();
            users = new String[list.size()];
            int i = 0;
            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                users[i++] = ((AdminUser) iterator.next()).getUsername();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            tx.rollback();
        }
        return users;
    }

    /**
     * Get all the polls in the system
     *
     * @return All the polls in the system
     */
    public ChadPoll[] getAllPolls() {
        Session session = hbConfig.currentSession();
        Transaction tx = session.beginTransaction();
        ChadPoll[] polls = new ChadPoll[0];
        try {
            Criteria criteria = session.createCriteria(ChadPoll.class);
            List list = criteria.list();
            polls = (ChadPoll[]) list.toArray(new ChadPoll[list.size()]);
            tx.commit();
        } catch (Throwable t) {
            t.printStackTrace();
            tx.rollback();
        }
        return polls;
    }

    /**
     * Update a poll
     *
     * @param poll
     */
    public void updatePoll(ChadPoll poll) {
        Session session = hbConfig.currentSession();
        Transaction tx = session.beginTransaction();
        try {
            session.update(poll);
            tx.commit();
        } catch (Throwable t) {
            t.printStackTrace();
            tx.rollback();
        }
    }

    /**
     * Get the result of a poll
     *
     * @param pollId
     * @return The result of a poll
     */
    public ChadPollResult getPollResult(String pollId) {
        Session session = hbConfig.currentSession();
        Transaction tx = session.beginTransaction();
        ChadPollResult result = null;
        try {
            Criteria criteria = session.createCriteria(ChadPoll.class);
            criteria.add(Expression.eq("pollId", pollId.trim()));
            ChadPoll chadPoll = (ChadPoll) criteria.uniqueResult();
            if (chadPoll != null) {
                result = new ChadPollResult();
                result.setPollDescription(chadPoll.getDescription());
                result.setPollId(chadPoll.getPollId());
                result.setPollTitle(chadPoll.getTitle());
                result.setPollStopped(chadPoll.getIsStopped());
                result.setSingleVote(chadPoll.getIsSingleVote());
                int totalNumOfVotes = 0;

                // Sort the choices
                ChadChoice[] chadChoices = chadPoll.getChoices();
                Arrays.sort(chadChoices, new ChadChoiceComparator());
                for (int i = 0; i < chadChoices.length; i++) {
                    totalNumOfVotes += chadChoices[i].getNumberOfVotes();
                }
                for (int i = 0; i < chadChoices.length; i++) {
                    ChadChoice chadChoice = chadChoices[i];
                    int numberOfVotes = chadChoice.getNumberOfVotes();

                    if (numberOfVotes == 0) {
                        chadChoice.setVotePercentage(0);
                    } else {
                        float votePercentage = (((float) numberOfVotes) * 100) / totalNumOfVotes;
                        votePercentage = (float) Math.round(votePercentage * 100) / 100;
                        chadChoice.setVotePercentage(votePercentage);
                    }
                }

                result.setOrderedChoices(chadChoices);
                result.setTotalNumberOfVotes(totalNumOfVotes);
            } else {
                result = new ChadPollResult();
                result.setPollId("$INVALID POLL ID$");
                result.setPollDescription("Poll with ID " + pollId + " not found!");
                result.setPollTitle("$INVALID TITLE$");
                result.setPollStopped(true);
                result.setSingleVote(true);
                result.setTotalNumberOfVotes(-1);
                ChadChoice chadChoice = new ChadChoice();
                chadChoice.setChoiceName("$INVALID CHOICE$");
                chadChoice.setVotePercentage(-1);
                result.setOrderedChoices(new ChadChoice[]{chadChoice});
            }
            tx.commit();
        } catch (Throwable t) {
            t.printStackTrace();
            tx.rollback();
        }
        return result;
    }
}
