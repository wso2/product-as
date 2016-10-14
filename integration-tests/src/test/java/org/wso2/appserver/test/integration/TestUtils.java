/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.appserver.test.integration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the utility methods to be used by the integration tests.
 */
public class TestUtils {

    /**
     * Checks if the specified {@code port} number is available or closed.
     *
     * @param port the port number
     * @return true if the specified {@code port} is available or closed else false
     */
    public static boolean isPortAvailable(final int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            return true;
        } catch (final IOException ignored) {
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ignored) {
                }
            }
        }
        return false;
    }

    /**
     * Returns a {@link List} of available ports within a given port range.
     *
     * @param portMin       lower bound of the port range
     * @param portMax       upper bound of the port range
     * @param numberOfPorts number of available ports required
     * @return list of available ports
     */
    public static List<Integer> getAvailablePortsFromRange(int portMin, int portMax, int numberOfPorts) {
        List<Integer> availablePorts = new ArrayList<>();

        int port = portMin;
        int portCount = 0;

        while ((portCount < numberOfPorts) && (port <= portMax)) {
            if (isPortAvailable(port)) {
                availablePorts.add(port);
                portCount++;
            }
            port++;
        }

        return availablePorts;
    }

    /**
     * Checks if the server is listening using the {@code host} name and the {@code port} number specified.
     *
     * @param host the host name
     * @param port the port number
     * @return true if the server is listening else false
     */
    public static boolean isServerListening(String host, int port) {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception ignored) {
                }
            }
        }
    }


}
