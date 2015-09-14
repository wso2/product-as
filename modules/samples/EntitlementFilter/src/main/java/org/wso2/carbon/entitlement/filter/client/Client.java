package org.wso2.carbon.entitlement.filter.client;

import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Client {

    public static void main(String[] args) throws IOException {

        BASE64Encoder encoder = new BASE64Encoder();
        try {
            System.out.println();
            System.out.println("***********Starting the Entitlement Servlet Filter Sample************");
            System.out.println();

            System.out.println("Sending Request For a Web Page Which Requires Authorization");
            String webPage = args[0] + "Entitlement_Sample_WebApp/protected.jsp";
            String authorizationString = "Basic " + encoder.encode("admin:admin".getBytes());

            String result = readInputStream(webPage, authorizationString);

            System.out.println("Subject : admin");
            System.out.println("Resource : /Entitlement_Sample_WebApp/protected.jsp");
            System.out.println("Action : GET");
            System.out.println("Environment : Not Specified");
            System.out.print("***Response BEGIN ***");
            System.out.println(result);
            System.out.println("***Response END ***");

            System.out.println();
            System.out.println("Sending Request For a Web Page Which Not Requires Authorization");
            webPage = args[0] + "Entitlement_Sample_WebApp/index.jsp";

            result = readInputStream(webPage, authorizationString);

            System.out.println("Subject : admin");
            System.out.println("Resource : /Entitlement_Sample_WebApp/index.jsp");
            System.out.println("Action : GET");
            System.out.println("Environment : Not Specified");
            System.out.print("***Response BEGIN ***");
            System.out.println(result);
            System.out.println("***Response END ***");

            System.out.println();
            System.out.println("Sending Request For a Web Page Which Requires Authorization with False Subject NAME");
            webPage = args[0] + "Entitlement_Sample_WebApp/protected.jsp";
            authorizationString = "Basic " + encoder.encode("andunslg:admin".getBytes());

            result = readInputStream(webPage, authorizationString);

            System.out.println("Subject : andunslg");
            System.out.println("Resource : /Entitlement_Sample_WebApp/protected.jsp");
            System.out.println("Action : GET");
            System.out.println("Environment : Not Specified");
            System.out.print("***Response BEGIN ***");
            System.out.println(result);
            System.out.println("***Response END ***");

            System.out.println();
            System.out.println("Sending Request For a Web Page Which Requires Authorization with False Action");
            webPage = args[0] + "Entitlement_Sample_WebApp/protected.jsp";
            authorizationString = "Basic " + encoder.encode("admin:admin".getBytes());

            result = readInputStream(webPage, authorizationString);

            System.out.println("Subject : admin");
            System.out.println("Resource : /Entitlement_Sample_WebApp/protected.jsp");
            System.out.println("Action : POST");
            System.out.println("Environment : Not Specified");
            System.out.print("***Response BEGIN ***");
            System.out.println(result);
            System.out.println("***Response END ***");

            System.out.println();
            System.out.println("Sending Request For a Web Page Which Requires Authorization But Policy is not defined");
            webPage = args[0] + "Entitlement_Sample_WebApp/other.jsp";
            authorizationString = "Basic " + encoder.encode("admin:admin".getBytes());

            result = readInputStream(webPage, authorizationString);

            System.out.println("Subject : admin");
            System.out.println("Resource : /Entitlement_Sample_WebApp/other.jsp");
            System.out.println("Action : GET");
            System.out.println("Environment : Not Specified");
            System.out.print("***Response BEGIN ***");
            System.out.println(result);
            System.out.println("***Response END ***");

            System.out.println();
            System.out.println("***********Ending the Entitlement Servlet Filter Sample************");
            System.out.println();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readInputStream(String webPage, String authorizationString) throws IOException {
        int numCharsRead;
        char[] charArray = new char[1024];
        StringBuilder sb = new StringBuilder();
        InputStream is = null;
        InputStreamReader isr = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(webPage);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.addRequestProperty("Authorization", authorizationString);
            urlConnection.setRequestMethod("GET");

            is = urlConnection.getInputStream();
            isr = new InputStreamReader(is);

            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
        } catch (IOException e) {
            sb.append("\n").append(e.getMessage());
        } finally {
            if (is != null) {
                is.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sb.toString();
    }
}
