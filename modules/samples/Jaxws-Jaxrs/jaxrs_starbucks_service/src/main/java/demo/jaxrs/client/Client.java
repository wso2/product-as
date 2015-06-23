/*
 * Copyright 2011-2012 WSO2, Inc. (http://wso2.com)
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

package demo.jaxrs.client;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.resource.URIResolver;
import org.codehaus.jettison.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Client {

    private Client() {
    }
    
    private String getJSONStringWithId(File file, String jsonRootElement, String orderId) throws Exception{
        String content = new Scanner(file).useDelimiter("\\Z").next(); //get the file content
        JSONObject obj = new JSONObject(content);   //parse it
        ((JSONObject)obj.get(jsonRootElement)).put("orderId", orderId); //add orderId under Order element

        return obj.toString();
    }

    public static void main(String args[]) throws Exception {
        // First set the URL of the service
        // Default is : http://localhost:9763/jaxrs_starbucks_service/services/Starbucks_Outlet_Service
        String serviceURL = "http://localhost:9763/jaxrs_starbucks_service/services/Starbucks_Outlet_Service";
        if (args[0] != null) {
            serviceURL = args[0];
        }

        String currentOrderId = null;

        // Sent HTTP GET request to query customer info
        System.out.println("Sent HTTP GET request to query order info of " + 123);
        URL url = new URL(serviceURL + "/orders/123");
        InputStream in = url.openStream();
        System.out.println(getStringFromInputStream(in));

        // Sent HTTP POST request to add customer
        System.out.println("\n");
        System.out.println("Sent HTTP POST request to add an order");
        Client client = new Client();
        String inputFile = client.getClass().getResource("add_order.xml").getFile();
        URIResolver resolver = new URIResolver(inputFile);
        File input = new File(resolver.getURI());
        PostMethod post = new PostMethod(serviceURL + "/orders");
        RequestEntity entity = new FileRequestEntity(input, "text/xml");
        post.setRequestEntity(entity);
        HttpClient httpclient = new HttpClient();

        try {
            int result = httpclient.executeMethod(post);
            System.out.println("Response status code: " + result);
            System.out.println("Response body: ");
            String response = post.getResponseBodyAsString();
            System.out.println(response);

            currentOrderId = getOrderId(response) ;
        } finally {
            // Release current connection to the connection pool once you are
            // done
            post.releaseConnection();
        }

        if (currentOrderId == null) {
            System.out.println("The retrieved order id is null. Either the POST operation was failed or some other error.");
            return;
        }

        // Sent HTTP GET request to query customer info
        System.out.println("\n");
        System.out.println("Sent HTTP GET request to query order info of " + currentOrderId);
        url = new URL(serviceURL + "/orders/"+currentOrderId);
        in = url.openStream();
        System.out.println(getStringFromInputStream(in));

        // Sent HTTP PUT request to update order
        System.out.println("\n");
        System.out.println("Sent HTTP PUT request to update order " + currentOrderId);
        inputFile = client.getClass().getResource("update_order.json").getFile();
        resolver = new URIResolver(inputFile);
        input = new File(resolver.getURI());
        String jsonString = client.getJSONStringWithId(input, "Order", currentOrderId);
        PutMethod put = new PutMethod(serviceURL + "/orders");
        entity = new StringRequestEntity(jsonString, "application/json", "utf-8");
        put.setRequestEntity(entity);
        httpclient = new HttpClient();

        try {
            int result = httpclient.executeMethod(put);
            System.out.println("Response status code: " + result);
            System.out.println("Response body: ");
            System.out.println(put.getResponseBodyAsStream());
        } finally {
            // Release current connection to the connection pool once you are
            // done
            put.releaseConnection();
        }

        // Sent HTTP PUT request to lock
        System.out.println("\n");
        System.out.println("Sent HTTP PUT request to lock the order " + currentOrderId);
        put = new PutMethod(serviceURL + "/orders/lock/" + currentOrderId);
        httpclient = new HttpClient();

        try {
            int result = httpclient.executeMethod(put);
            System.out.println("Response status code: " + result);
            System.out.println("Response body: ");
            System.out.println(put.getResponseBodyAsStream());
        } finally {
            // Release current connection to the connection pool once you are
            // done
            put.releaseConnection();
        }

        // Sent HTTP POST request to do payment
        System.out.println("\n");
        System.out.println("Sent HTTP POST request to do payment for order " + currentOrderId);
        inputFile = client.getClass().getResource("payment.json").getFile();
        resolver = new URIResolver(inputFile);
        input = new File(resolver.getURI());
        jsonString = client.getJSONStringWithId(input, "Payment", currentOrderId);
        post = new PostMethod(serviceURL + "/payment/"+currentOrderId);
        post.addRequestHeader("Accept" , "text/html");
        entity = new StringRequestEntity(jsonString, "application/json", "utf-8");
        post.setRequestEntity(entity);
        httpclient = new HttpClient();

        try {
            int result = httpclient.executeMethod(post);
            System.out.println("Response status code: " + result);
            System.out.println("Response body: ");
            System.out.println(post.getResponseBodyAsStream());
        } finally {
            // Release current connection to the connection pool once you are
            // done
            post.releaseConnection();
        }

        // Sent HTTP GET request to query payment
        System.out.println("\n");
        System.out.println("Sent HTTP GET request to query payment info for order " + currentOrderId);
        url = new URL(serviceURL + "/payment/"+currentOrderId);
        in = url.openStream();
        System.out.println(getStringFromInputStream(in));

        //HTTP DELETE request
        System.out.println("\n");
        System.out.println("Sent HTTP DELETE request to remove the order " + currentOrderId);
        DeleteMethod delete = new DeleteMethod(serviceURL + "/orders/"+currentOrderId);

        httpclient = new HttpClient();

        try {
            int result = httpclient.executeMethod(delete);
            System.out.println("Response status code: " + result);
            System.out.println("Response body: ");
            System.out.println(delete.getResponseBodyAsStream());
        } finally {
            // Release current connection to the connection pool once you are
            // done
            put.releaseConnection();
        }

        System.out.println("\n");
        System.exit(0);
    }

    /**
     * Retrieves the orderId from a json string.
     * Only lighweight string manipulations are performed since this is a client.
     *
     *     String response = "{\"Order\":{\"additions\":\"Caramel\",\"drinkName\":\"Mocha Flavored Coffee\"," +
     "\"locked\":false,\"orderId\" : \"4b650b9e-86c0-4561-92b2-050f8c61de05\"}}\n";
     * @param response json string
     * @return the orderId
     */
    private static String getOrderId(String response) {
        String regexString = "(?<=\"orderId\")\\s*:\\s*\"([a-z\\-0-9]*)";    // "orderId"\s*:\s*"([a-z\-0-9]*)"
        String match = null;                                              //(?<="orderId":")([a-z\-0-9]*)

        Pattern regex = Pattern.compile(regexString);
        Matcher m = regex.matcher(response);
        if (m.find()) {
            match = m.group();
            match = match.trim().replaceAll("\\s*:\\s*\"", "");
        }

        return match;
    }

    private static String getStringFromInputStream(InputStream in) throws Exception {
        CachedOutputStream bos = new CachedOutputStream();
        IOUtils.copy(in, bos);
        in.close();
        bos.close();
        return bos.getOut().toString();
    }

}
