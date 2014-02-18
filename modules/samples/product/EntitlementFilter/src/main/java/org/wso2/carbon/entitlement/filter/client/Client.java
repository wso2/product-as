package org.wso2.carbon.entitlement.filter.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Encoder;


public class Client{

    public static void main(String[] args) throws IOException {
        URL url=null;
        HttpURLConnection urlConnection = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BASE64Encoder encoder = new BASE64Encoder();

        try {
            System.out.println();
            System.out.println("***********Starting the Entitlement Servlet Filter Sample************");
            System.out.println();

            System.out.println("Sending Request For a Web Page Which Requires Authorization");
            String webPage = args[0]+"Entitlement_Sample_WebApp/protected.jsp";
            url = new URL(webPage);
            urlConnection = (HttpURLConnection)url.openConnection();
            String authorizationString = "Basic " + new String(encoder.encode("admin:admin".getBytes()));
            urlConnection.addRequestProperty("Authorization",authorizationString);
            urlConnection.setRequestMethod("GET");

            is = urlConnection.getInputStream();
            isr = new InputStreamReader(is);

            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuffer sb = new StringBuffer();
            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
            String result = sb.toString();

            System.out.println("Subject : admin");
            System.out.println("Resource : /Entitlement_Sample_WebApp/protected.jsp");
            System.out.println("Action : GET");
            System.out.println("Environment : Not Specified");
            System.out.print("***Response BEGIN ***");
            System.out.println(result);
            System.out.println("***Response END ***");

            System.out.println();
            System.out.println("Sending Request For a Web Page Which Not Requires Authorization");
            webPage = args[0]+"Entitlement_Sample_WebApp/index.jsp";
            url = new URL(webPage);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.addRequestProperty("Authorization",authorizationString);
            urlConnection.setRequestMethod("GET");

            is = urlConnection.getInputStream();
            isr = new InputStreamReader(is);

            charArray = new char[1024];
            sb = new StringBuffer();
            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
           result = sb.toString();

            System.out.println("Subject : admin");
            System.out.println("Resource : /Entitlement_Sample_WebApp/index.jsp");
            System.out.println("Action : GET");
            System.out.println("Environment : Not Specified");
            System.out.print("***Response BEGIN ***");
            System.out.println(result);
            System.out.println("***Response END ***");

            System.out.println();
            System.out.println("Sending Request For a Web Page Which Requires Authorization with False Subject NAME");
            webPage = args[0]+"Entitlement_Sample_WebApp/protected.jsp";
            url = new URL(webPage);
            urlConnection = (HttpURLConnection)url.openConnection();
            authorizationString = "Basic " + new String(encoder.encode("andunslg:admin".getBytes()));
            urlConnection.addRequestProperty("Authorization",authorizationString);
            urlConnection.setRequestMethod("GET");

            is = urlConnection.getInputStream();
            isr = new InputStreamReader(is);

            charArray = new char[1024];
            sb = new StringBuffer();
            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
            result = sb.toString();


            System.out.println("Subject : andunslg");
            System.out.println("Resource : /Entitlement_Sample_WebApp/protected.jsp");
            System.out.println("Action : GET");
            System.out.println("Environment : Not Specified");
            System.out.print("***Response BEGIN ***");
            System.out.println(result);
            System.out.println("***Response END ***");

            System.out.println();
            System.out.println("Sending Request For a Web Page Which Requires Authorization with False Action");
            webPage = args[0]+"Entitlement_Sample_WebApp/protected.jsp";
            url = new URL(webPage);
            urlConnection = (HttpURLConnection)url.openConnection();
            authorizationString = "Basic " + new String(encoder.encode("admin:admin".getBytes()));
            urlConnection.addRequestProperty("Authorization",authorizationString);
            urlConnection.setRequestMethod("POST");


            is = urlConnection.getInputStream();
            isr = new InputStreamReader(is);


            charArray = new char[1024];
            sb = new StringBuffer();
            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
            result = sb.toString();


            System.out.println("Subject : admin");
            System.out.println("Resource : /Entitlement_Sample_WebApp/protected.jsp");
            System.out.println("Action : POST");
            System.out.println("Environment : Not Specified");
            System.out.print("***Response BEGIN ***");
            System.out.println(result);
            System.out.println("***Response END ***");

            System.out.println();
            System.out.println("Sending Request For a Web Page Which Requires Authorization But Policy is not defined");
            webPage = args[0]+"Entitlement_Sample_WebApp/other.jsp";
            url = new URL(webPage);
            urlConnection = (HttpURLConnection)url.openConnection();
            authorizationString = "Basic " + new String(encoder.encode("admin:admin".getBytes()));
            urlConnection.addRequestProperty("Authorization",authorizationString);
            urlConnection.setRequestMethod("GET");


            is = urlConnection.getInputStream();
            isr = new InputStreamReader(is);


            charArray = new char[1024];
            sb = new StringBuffer();
            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
            result = sb.toString();


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
        finally {
            if(is!=null){
                is.close();
            }
            if(isr!=null){
                isr.close();
            }
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
        }
    }

}
