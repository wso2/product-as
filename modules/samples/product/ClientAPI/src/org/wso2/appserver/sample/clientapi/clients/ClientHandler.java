/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.appserver.sample.clientapi.clients;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ClientHandler {

    private static final String PARAM_ENDPOINT = "-e";
    private static final String PARAM_HELP = "-help";

    public static void main(String[] args) {
        // default EPR
        String epr = "http://localhost:9763/services/ClientAPIDemoService";

        if (args.length > 0) {
            if (PARAM_HELP.equals(args[0])) {
                printUsage();
                System.exit(0);
            } else if (PARAM_ENDPOINT.equals(args[0]) && args.length > 1) {
                epr = args[1];
            }
        }

        showOptions();
        // read user input
        int option = -1;
        while (option == -1) {
            String s = readOption();
            try {
                if (s != null) {
                    option = Integer.parseInt(s);
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter an integer value.");
            }
        }

        switch (option) {
            case 1:
                BlockingClient.run(epr);
                break;
            case 2:
                SingleChannelNonBlockingClient.run(epr);
                break;
            case 3:
                DualChannelNonBlockingClient.run(epr);
                break;
            case 4:
                DynamicBlockingClient.run(epr);
                break;
            case 5:
                RPCClient.run(epr);
                break;
            case 6:
                FireAndForgetClient.run(epr);
                break;
            case 7:
                SendRobustClient.run(epr);
                break;
            case 8:
                SampleOperationClient.run(epr);
                break;
            case 9:
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option..");
        }
    }

    private static String readOption() {
        try {
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            String str;
            while ((str = console.readLine()).equals("")) {
            }
            return str;
        } catch (Exception e) {
            return null;
        }
    }

    private static void printUsage() {
        System.out.println("\n============================= HELP =============================\n");
        System.out.println("Following optional parameters can be used" +
                " when running the client\n");
        System.out.println("\t" + PARAM_ENDPOINT + "\t: Endpoint URL of the service ");
        System.out.println("\t" + PARAM_HELP + "\t: For Help \n");
    }

    private static void showOptions() {
        System.out.println("\n\nPlease select the type of Client that you want to execute...");
        System.out.println("--------------------------------------------------------------");

        System.out.println("\n1. Blocking Client");
        System.out.println("2. Single Channel Non Blocking Client");
        System.out.println("3. Dual Channel Non Blocking Client");
        System.out.println("4. Dynamic Blocking Client");
        System.out.println("5. RPC Client");
        System.out.println("6. Fire and Forget Client");
        System.out.println("7. Send Robust Client");
        System.out.println("8. Operation Client");
        System.out.println("9. Exit");
    }

}
