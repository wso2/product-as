/*
 * Copyright 2005,2014 WSO2, Inc. http://www.wso2.org
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
package org.wso2.appserver.integration.common.clients;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.logging.view.stub.LogViewerException;
import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.stub.LogViewerStub;
import org.wso2.carbon.logging.view.stub.types.carbon.PaginatedLogEvent;
import org.wso2.carbon.logging.view.stub.types.carbon.PaginatedLogFileInfo;

import java.rmi.RemoteException;

/**
 * This class can be used as the client to get log information from log viewer feature
 */
public class LogViewerClient {
    private static final Log log = LogFactory.getLog(LogViewerClient.class);
    String serviceName = "LogViewer";
    private LogViewerStub logViewerStub;

    public LogViewerClient(String backEndUrl, String sessionCookie)
            throws AxisFault {
        String endpoint = backEndUrl + serviceName;
        logViewerStub = new LogViewerStub(endpoint);
        logViewerStub._getServiceClient().getOptions().setTimeOutInMilliSeconds(300000);
        AuthenticateStubUtil.authenticateStub(sessionCookie, logViewerStub);
    }

    public LogViewerClient(String backEndURL, String userName, String password)
            throws AxisFault {
        String endpoint = backEndURL + serviceName;
        logViewerStub = new LogViewerStub(endpoint);
        logViewerStub._getServiceClient().getOptions().setTimeOutInMilliSeconds(300000);
        AuthenticateStubUtil.authenticateStub(userName, password, logViewerStub);
    }

    /**
     * Return log events of the given page (as a collection of loginfo)
     *
     * @param pageNumber
     *         - page number
     * @param type
     *         - type of the log
     * @param keyword
     *         - keyword to search
     * @param tenantDomain
     *         - tenant domain
     * @param serverKey
     *         - server key
     * @return - a paginated log event of the requested page
     * @throws RemoteException
     * @throws LogViewerLogViewerException
     */
    public PaginatedLogEvent getPaginatedLogEvents(int pageNumber, String type, String keyword,
                                                   String tenantDomain, String serverKey)
            throws RemoteException, LogViewerLogViewerException {
        String errorMsg = "Error occurred while getting paginated log events. Backend service may" +
                          " be unavailable";
        try {
            return logViewerStub
                    .getPaginatedLogEvents(pageNumber, type, keyword, tenantDomain, serverKey);
        } catch (RemoteException e) {
            log.error(errorMsg, e);
            throw e;
        } catch (LogViewerLogViewerException e) {
            log.error(errorMsg, e);
            throw e;
        }
    }

    /**
     * Return a paginated collection of local log file information
     *
     * @param pageNumber
     *         - page number
     * @param tenantDomain
     *         - tenant domain
     * @param serverKey
     *         - server key
     * @return - a paginated log file information
     * @throws RemoteException
     * @throws LogViewerLogViewerException
     */
    public PaginatedLogFileInfo getLocalLogFiles(int pageNumber, String tenantDomain,
                                                 String serverKey)
            throws RemoteException, LogViewerLogViewerException {

        String errorMsg = "Error occurred while getting local log files. Backend service may be " +
                          "unavailable";
        try {
            return logViewerStub.getLocalLogFiles(pageNumber, tenantDomain, serverKey);
        } catch (RemoteException e) {
            log.error(errorMsg, e);
            throw e;
        } catch (LogViewerLogViewerException e) {
            log.error(errorMsg, e);
            throw e;
        }

    }

    /**
     * Get a paginated log info collection per application
     *
     * @param pageNumber
     *         - page number
     * @param type
     *         - type of the log
     * @param keyword
     *         - keyword to search
     * @param appName
     *         - application name
     * @param tenantDomain
     *         - tenant domain
     * @param serverKey
     *         - server key
     * @return - a paginated log info collection
     * @throws RemoteException
     * @throws LogViewerException
     */
    public PaginatedLogEvent getPaginatedApplicationLogEvents(int pageNumber, String type,
                                                              String keyword, String appName,
                                                              String tenantDomain, String serverKey)
            throws RemoteException, LogViewerException {
        String errorMsg = "Error occurred while getting paginated application log events. Backend" +
                          "service may be unavailable";
        try {
            return logViewerStub.getPaginatedApplicationLogEvents(pageNumber, type, keyword,
                                                                  appName, tenantDomain, serverKey);
        } catch (RemoteException e) {
            log.error(errorMsg, e);
            throw e;
        } catch (LogViewerException e) {
            log.error(errorMsg, e);
            throw e;
        }
    }

    /**
     * Reads and returns the specified log lines from the given log file.
     *
     * @param logFile log file path relative to carbon logs
     * @param maxLogs maximum number of lines of the log
     * @param start   starting line of the log
     * @param end     ending line of the log
     * @return requested log lines from the given log file
     * @throws RemoteException
     * @throws LogViewerLogViewerException
     */
    public String[] getLogLinesFromFile(String logFile, int maxLogs, int start, int end)
            throws RemoteException, LogViewerLogViewerException {
        try {
            return logViewerStub.getLogLinesFromFile(logFile, maxLogs, start, end);
        } catch (RemoteException e) {
            throw e;
        } catch (LogViewerLogViewerException e) {
            throw e;
        }
    }
}
