/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.appserver.apieverywhere.exceptions;

/**
 * Custom run time exception class for the api-everywhere-as module.
 *
 * @since 6.0.0
 */
public class APIEverywhereException extends RuntimeException {

    /**
     * Exception to be thrown when an error occurs in the api-everywhere-as module.
     *
     * @param message the detail message
     * @param cause   the cause of exception
     */
    public APIEverywhereException(String message, Throwable cause) {
        super(message, cause);
    }
}
