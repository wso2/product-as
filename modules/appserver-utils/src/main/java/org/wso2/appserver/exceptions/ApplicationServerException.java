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
package org.wso2.appserver.exceptions;

/**
 * This class defines a custom exception type specified for WSO2 Application Server and thrown when the
 * application server encountered an error.
 *
 * @since 6.0.0
 */
public class ApplicationServerException extends Exception {
    private static final long serialVersionUID = -2311546694551512249L;

    public ApplicationServerException(String message) {
        super(message);
    }

    public ApplicationServerException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
