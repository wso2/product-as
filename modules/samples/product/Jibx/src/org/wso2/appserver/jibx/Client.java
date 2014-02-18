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
package org.wso2.appserver.jibx;

import org.apache.axis2.AxisFault;
import org.wso2.carbon.utils.NetworkUtils;
import org.wso2.appserver.jibx.client.LibraryServiceStub;
import org.wso2.appserver.jibx.library.beans.Book;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Client for Jibx unwrapped sample
 */
public class Client {

    private static final String PARAM_ENDPOINT = "-e";
    private static final String PARAM_HELP = "-help";

    BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        try {
            String epr = "http://" + NetworkUtils.getLocalHostname() +
                    ":9763/services/LibraryService";

            if (args.length > 0) {
                if (PARAM_HELP.equals(args[0])) {
                    printUsage();
                    System.exit(0);
                } else if (PARAM_ENDPOINT.equals(args[0]) && args.length > 1) {
                    epr = args[1];
                }
            }
            LibraryServiceStub stub = new LibraryServiceStub(epr);
            Client client = new Client();
            client.execute(args, stub);
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    private void addBook(LibraryServiceStub stub, String type, String isbn, String[] author,
                         String title)
            throws RemoteException {
        boolean b = stub.addBook(type, isbn, author, title);
        if (b) {
            System.out.println("Book added successfully with isbn : " + isbn);
        } else {
            System.out.println("Book cannot be added as it already exist in the library : " + isbn);
        }
    }

    private void getBook(LibraryServiceStub stub, String isbn) throws RemoteException {
        Book book = stub.getBook(isbn);
        System.out.println("======= Report ===========");
        System.out.println("Type : " + book.getType());
        System.out.println("ISBN : " + book.getIsbn());
        System.out.println("Title : " + book.getTitle());
        System.out.println("Authors : ");
        for (int i = 0; i < book.getAuthors().length; i++) {
            System.out.println("Author : " + book.getAuthors()[i]);
        }
    }

    private void execute(String[] args, LibraryServiceStub stub) {
        System.out.println("=== Welcome to WSO2 JIBX Library ====");
        while (true) {
            System.out.println("1. Add Book");
            System.out.println("2. Get Book");
            System.out.println("3. Exit");
            int opt = readIntOption();
            switch (opt) {
                case 1:
                    System.out.println("Type : ");
                    String type = readOption();
                    System.out.println("ISBN : ");
                    String isbn = readOption();
                    System.out.println("Title : ");
                    String title = readOption();
                    System.out.println("Authors: ");
                    String authors = readOption();
                    String[] strings = authors.split(":");
                    List<String> authorList = new ArrayList<String>();
                    for (int k = 0; k < strings.length; k++) {
                        authorList.add(strings[k]);
                    }
                    strings = authorList.toArray(new String[authorList.size()]);
                    try {
                        addBook(stub, type, isbn, strings, title);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                    break;
                case 2:
                    System.out.println("ISBN : ");
                    isbn = readOption();
                    try {
                        getBook(stub, isbn);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                    break;
                case 3:
                    System.exit(0);
                    break;
            }


        }
    }


    private int readIntOption() {
        int option;

        while (true) {
            String s = readOption();

            try {
                option = Integer.parseInt(s);

                return option;
            } catch (NumberFormatException e) {
                System.out.println("Please enter an integer value.");
            }
        }
    }

    private String readOption() {
        try {
            String str = console.readLine();
            if ("".equals(str)) {
                return null;
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
}
