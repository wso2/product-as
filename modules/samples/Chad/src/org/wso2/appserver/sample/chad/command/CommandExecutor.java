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

package org.wso2.appserver.sample.chad.command;

/**
 * This class executes any {@link ChadCommand}
 * provided to its {@link CommandExecutor#execute} method, after switching to the Chad.aar
 * classloader and, finally switches back to the original classloader.
 */
public class CommandExecutor {

    /**
     * Execute any ChadCommand provided to this method, after switching to the Chad.aar classloader
     * and, finally switches back to the original classloader.
     *
     * @param command
     * @throws CommmandExecutionException If an error occurs during command execution
     */
    public void execute(ChadCommand command) throws CommmandExecutionException {
        ClassLoader prevClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            // Set the new classloader
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

            // Execute the command
            command.process();
        } finally {
            // Restore the previous classloader
            Thread.currentThread().setContextClassLoader(prevClassLoader);
        }
    }
}
