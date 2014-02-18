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

package org.wso2.appserver.sample.commodityquote.client;
/*
 * This class will be used in getting private keypassword for the samples
 */

import org.apache.ws.security.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class PWCallback implements CallbackHandler {


    /**
     * Field key
     */

    private static final byte[] key = {

            (byte) 0x31, (byte) 0xfd, (byte) 0xcb, (byte) 0xda, (byte) 0xfb,

            (byte) 0xcd, (byte) 0x6b, (byte) 0xa8, (byte) 0xe6, (byte) 0x19,

            (byte) 0xa7, (byte) 0xbf, (byte) 0x51, (byte) 0xf7, (byte) 0xc7,

            (byte) 0x3e, (byte) 0x80, (byte) 0xae, (byte) 0x98, (byte) 0x51,

            (byte) 0xc8, (byte) 0x51, (byte) 0x34, (byte) 0x04,

    };

    private static Map<String, String> userPassMap = new HashMap<String, String>();

    /**
     * Method handle
     *
     * @param callbacks
     * @throws java.io.IOException
     * @throws javax.security.auth.callback.UnsupportedCallbackException
     *
     */

    public void handle(Callback[] callbacks)

            throws IOException, UnsupportedCallbackException {


        for (int i = 0; i < callbacks.length; i++) {

            if (callbacks[i] instanceof WSPasswordCallback) {

                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];

                /*

                * This usage type is used only in case we received a

                * username token with a password of type PasswordText or

                * an unknown password type.

                *

                * This case the WSPasswordCallback object contains the

                * identifier (aka username), the password we received, and

                * the password type string to identify the type.

                *

                * Here we perform only a very simple check.

                */

                if (pc.getUsage() == WSPasswordCallback.USERNAME_TOKEN_UNKNOWN) {

                    if (pc.getIdentifer().equals("Ron") && pc.getPassword().equals("noR")) {

                        return;

                    }

                    if (pc.getIdentifer().equals("joe") && pc.getPassword().equals("eoj")) {

                        return;

                    }

                    if (pc.getPassword().equals("sirhC")) {

                        return;

                    }

                    throw new UnsupportedCallbackException(callbacks[i],

                            "check failed");

                }

                /*

                 * here call a function/method to lookup the password for

                 * the given identifier (e.g. a user name or keystore alias)

                 * e.g.: pc.setPassword(passStore.getPassword(pc.getIdentfifier))

                 * for Testing we supply a fixed name here.

                 */

                if (pc.getUsage() == WSPasswordCallback.KEY_NAME) {

                    pc.setKey(key);

                } else if (pc.getIdentifer().equals("alice")) {

                    pc.setPassword("password");

                } else if (pc.getIdentifer().equals("bob")) {

                    pc.setPassword("password");

                } else if (pc.getIdentifer().equals("Ron")) {

                    pc.setPassword("noR");

                } else if (pc.getIdentifer().equals("joe")) {

                    pc.setPassword("eoj");

                } else if (pc.getIdentifer().equals("ip")) {

                    pc.setPassword("password");

                } else if (pc.getIdentifer().equals("client")) {

                    pc.setPassword("testing");

                } else if (userPassMap.get(pc.getIdentifier()) != null) {

                    pc.setPassword(userPassMap.get(pc.getIdentifier()));

                } else {
                    throw new UnsupportedCallbackException(callbacks[i],

                            "no password found for given identifier");
                }

            } else {

                throw new UnsupportedCallbackException(callbacks[i],

                        "Unrecognized Callback");

            }

        }

    }

    public static void addUser(String username, String password) {
        userPassMap.put(username, password);
    }

}
