/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.appserver.sample.mtom.client;

import org.apache.axis2.Constants;
import org.apache.axis2.util.CommandLineOption;
import org.apache.axis2.util.CommandLineOptionParser;
import org.apache.axis2.util.OptionsValidator;
import org.w3.www._2005._05.xmlmime.Base64Binary;
import org.wso2.carbon.utils.NetworkUtils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.File;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;


public class Client {

    private static String OPTION_FILE_NAME = "fileName";
    private static String OPTION_FILE = "file";

    private static final String OPTION_ENDPOINT = "e";
    private static final String OPTION_HELP = "-help";

    public static void main(String[] args) throws Exception {

        for (String arg : args) {
            if (OPTION_HELP.equalsIgnoreCase(arg)) {
                printUsage();
                System.exit(0);
            }
        }

        String epr = "http://" + NetworkUtils.getLocalHostname() + ":9763/services/MTOMSample";

        CommandLineOptionParser optionsParser = new CommandLineOptionParser(args);
        List invalidOptionsList = optionsParser
                .getInvalidOptions(new OptionsValidator() {
                    public boolean isInvalid(CommandLineOption option) {
                        String optionType = option.getOptionType();
                        return !(OPTION_FILE_NAME.equalsIgnoreCase(optionType) ||
                                OPTION_FILE.equalsIgnoreCase(optionType) ||
                                OPTION_ENDPOINT.equalsIgnoreCase(optionType));
                    }
                });

        if ((invalidOptionsList.size() > 0)) {
            exitDueToInvalidArgs();
            System.exit(0);
        }

        Map optionsMap = optionsParser.getAllOptions();
        CommandLineOption fileOption = (CommandLineOption) optionsMap
                .get(OPTION_FILE_NAME);
        CommandLineOption destinationOption = (CommandLineOption) optionsMap
                .get(OPTION_FILE);
        CommandLineOption endpointOption = (CommandLineOption) optionsMap
                .get(OPTION_ENDPOINT);

        if (destinationOption == null) {
            exitDueToInvalidArgs();
        } else {
            String sourcePath = destinationOption.getOptionValue();
            File dstFile = new File(sourcePath);
            if (dstFile.exists() && dstFile.isFile()) {
                if (endpointOption != null) {
                    epr = endpointOption.getOptionValue();
                }
                String fileName = sourcePath.substring(sourcePath.lastIndexOf('/') + 1);
                if (fileOption != null) {
                    fileName = fileOption.getOptionValue();
                }
                transferFile(fileName, dstFile, epr);
            } else {
                System.out.println(
                        OPTION_FILE + " option contains an invalid file or file does not exist");
                System.exit(0);
            }
        }

    }

    public static void transferFile(String fileName, File file, String epr)
            throws RemoteException, SocketException {
        // uncomment the following if you need to capture the messages from
        // TCPMON. Please look at http://ws.apache.org/commons/tcpmon/tcpmontutorial.html
        // to learn how to setup tcpmon
        MTOMSampleStub stub = new MTOMSampleStub(epr);

        // Enable MTOM in the client side
        stub._getServiceClient().getOptions()
                .setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
        //Increase the time out when sending large attachments
        stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(10000);
        Base64Binary base64Binary = new Base64Binary();
        FileDataSource fileDataSource = new FileDataSource(file);
        DataHandler dataHandler = new DataHandler(fileDataSource);
        base64Binary.setBase64Binary(dataHandler);
        String staus = stub.attachment(fileName, base64Binary);
        System.out.println("====== Response Status =========");
        System.out.println(staus);
    }

    private static void printUsage() {
        System.out.println("\n============================= HELP =============================\n");
        System.out.println("Following parameters can be used when running the client\n");

        System.out.println("\t-" + OPTION_FILE + "\t\t: Full Path of the file that needs to be " +
                "sent to the service (mandatory)");
        System.out.println("\t-" + OPTION_FILE_NAME + "\t: Name of file to be created at the " +
                "server side (optional)");
        System.out.println("\t-" + OPTION_ENDPOINT + "\t\t: Endpoint URL of the service (optional)");
        System.out.println("\t" + OPTION_HELP + "\t\t: For Help");

        System.out.println("\n  Ex : sh run-client.sh -file /tmp/wwe.jpg -fileName " +
                "wrestling.jpg\n");
    }

    private static void exitDueToInvalidArgs() {
        System.out.println("\n\nInvalid parameters. Use \"" + OPTION_HELP +
                "\" option to get valid list of parameters..\n");
        System.exit(0);
    }

}
