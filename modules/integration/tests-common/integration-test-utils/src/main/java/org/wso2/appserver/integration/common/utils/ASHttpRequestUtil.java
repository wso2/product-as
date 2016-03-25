/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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

package org.wso2.appserver.integration.common.utils;

import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ASHttpRequestUtil {
    public ASHttpRequestUtil() {
    }

    public static HttpResponse sendGetRequest(String endpoint, String requestParameters) throws IOException {
        HttpURLConnection conn = null;

        try {
            String urlStr = endpoint;
            if(requestParameters != null && requestParameters.length() > 0) {
                urlStr = endpoint + "?" + requestParameters;
            }

            URL url = new URL(urlStr);
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setReadTimeout(600000);
            conn.connect();
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;

            try {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.defaultCharset()));

                String itr;
                while((itr = rd.readLine()) != null) {
                    sb.append(itr);
                }
            } catch (FileNotFoundException var17) {
                ;
            } finally {
                if(rd != null) {
                    rd.close();
                }

            }

            Iterator itr1 = conn.getHeaderFields().keySet().iterator();
            HashMap headers = new HashMap();

            while(itr1.hasNext()) {
                String key = (String)itr1.next();
                if(key != null) {
                    headers.put(key, conn.getHeaderField(key));
                }
            }

            HttpResponse key1 = new HttpResponse(sb.toString(), conn.getResponseCode(), headers);
            return key1;
        } finally {
            if(conn != null) {
                conn.disconnect();
            }

        }
    }

    public static void sendPostRequest(Reader data, URL endpoint, Writer output) throws AutomationFrameworkException {
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection)endpoint.openConnection();

            try {
                urlConnection.setRequestMethod("POST");
            } catch (ProtocolException var32) {
                throw new AutomationFrameworkException("Shouldn\'t happen: HttpURLConnection doesn\'t support POST?? " + var32.getMessage(), var32);
            }

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setRequestProperty("Content-type", "text/xml; charset=UTF-8");
            OutputStream e = urlConnection.getOutputStream();

            try {
                OutputStreamWriter in = new OutputStreamWriter(e, "UTF-8");
                pipe(data, in);
                in.close();
            } catch (IOException var31) {
                throw new AutomationFrameworkException("IOException while posting data " + var31.getMessage(), var31);
            } finally {
                if(e != null) {
                    e.close();
                }

            }

            InputStream in1 = urlConnection.getInputStream();

            try {
                InputStreamReader e1 = new InputStreamReader(in1, "UTF-8");
                pipe(e1, output);
                e1.close();
            } catch (IOException var30) {
                throw new AutomationFrameworkException("IOException while reading response " + var30.getMessage(), var30);
            } finally {
                if(in1 != null) {
                    in1.close();
                }

            }
        } catch (IOException var35) {
            throw new AutomationFrameworkException("Connection error (is server running at " + endpoint + " ?): " + var35.getMessage(), var35);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }

        }

    }

    public static HttpResponse doPost(URL endpoint, String body) throws AutomationFrameworkException {
        HttpURLConnection urlConnection = null;

        HttpResponse key1;
        try {
            urlConnection = (HttpURLConnection)endpoint.openConnection();

            try {
                urlConnection.setRequestMethod("POST");
            } catch (ProtocolException var32) {
                throw new AutomationFrameworkException("Shouldn\'t happen: HttpURLConnection doesn\'t support POST?? " + var32.getMessage(), var32);
            }

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            OutputStream e = urlConnection.getOutputStream();

            try {
                OutputStreamWriter sb = new OutputStreamWriter(e, "UTF-8");
                sb.write(body);
                sb.close();
            } catch (IOException var31) {
                throw new AutomationFrameworkException("IOException while posting data " + var31.getMessage(), var31);
            } finally {
                if(e != null) {
                    e.close();
                }

            }

            StringBuilder sb1 = new StringBuilder();
            BufferedReader rd = null;

            try {
                rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), Charset.defaultCharset()));

                String itr;
                while((itr = rd.readLine()) != null) {
                    sb1.append(itr);
                }
            } catch (FileNotFoundException var34) {
                ;
            } finally {
                if(rd != null) {
                    rd.close();
                }

            }

            Iterator itr1 = urlConnection.getHeaderFields().keySet().iterator();
            HashMap headers = new HashMap();

            while(itr1.hasNext()) {
                String key = (String)itr1.next();
                if(key != null) {
                    headers.put(key, urlConnection.getHeaderField(key));
                }
            }

            key1 = new HttpResponse(sb1.toString(), urlConnection.getResponseCode(), headers);
        } catch (IOException var36) {
            throw new AutomationFrameworkException("Connection error (is server running at " + endpoint + " ?): " + var36.getMessage(), var36);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }

        }

        return key1;
    }

    public static HttpResponse doPost(URL endpoint, String postBody, Map<String, String> headers) throws AutomationFrameworkException {
        HttpURLConnection urlConnection = null;

        HttpResponse key1;
        try {
            urlConnection = (HttpURLConnection)endpoint.openConnection();

            try {
                urlConnection.setRequestMethod("POST");
            } catch (ProtocolException var33) {
                throw new AutomationFrameworkException("Shouldn\'t happen: HttpURLConnection doesn\'t support POST?? " + var33.getMessage(), var33);
            }

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            Iterator e = headers.entrySet().iterator();

            while(e.hasNext()) {
                Map.Entry sb = (Map.Entry)e.next();
                urlConnection.setRequestProperty((String)sb.getKey(), (String)sb.getValue());
            }

            OutputStream e1 = urlConnection.getOutputStream();

            try {
                OutputStreamWriter sb1 = new OutputStreamWriter(e1, "UTF-8");
                sb1.write(postBody);
                sb1.close();
            } catch (IOException var32) {
                throw new AutomationFrameworkException("IOException while posting data " + var32.getMessage(), var32);
            } finally {
                if(e1 != null) {
                    e1.close();
                }

            }

            StringBuilder sb2 = new StringBuilder();
            BufferedReader rd = null;

            try {
                rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), Charset.defaultCharset()));

                String itr;
                while((itr = rd.readLine()) != null) {
                    sb2.append(itr);
                }
            } catch (FileNotFoundException var35) {
                ;
            } finally {
                if(rd != null) {
                    rd.close();
                }

            }

            Iterator itr1 = urlConnection.getHeaderFields().keySet().iterator();
            HashMap responseHeaders = new HashMap();

            while(itr1.hasNext()) {
                String key = (String)itr1.next();
                if(key != null) {
                    responseHeaders.put(key, urlConnection.getHeaderField(key));
                }
            }

            key1 = new HttpResponse(sb2.toString(), urlConnection.getResponseCode(), responseHeaders);
        } catch (IOException var37) {
            throw new AutomationFrameworkException("Connection error (is server running at " + endpoint + " ?): " + var37.getMessage(), var37);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }

        }

        return key1;
    }

    public static HttpResponse doGet(String endpoint, Map<String, String> headers) throws IOException {
        HttpURLConnection conn = null;

        HttpResponse ignored1;
        try {
            URL url = new URL(endpoint);
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setReadTimeout(30000);
            Iterator sb = headers.entrySet().iterator();

            while(sb.hasNext()) {
                Map.Entry rd = (Map.Entry)sb.next();
                conn.setRequestProperty((String)rd.getKey(), (String)rd.getValue());
            }

            conn.connect();
            StringBuilder sb1 = new StringBuilder();
            BufferedReader rd1 = null;

            HttpResponse httpResponse;
            try {
                rd1 = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.defaultCharset()));

                String ignored;
                while((ignored = rd1.readLine()) != null) {
                    sb1.append(ignored);
                }

                httpResponse = new HttpResponse(sb1.toString(), conn.getResponseCode());
                httpResponse.setResponseMessage(conn.getResponseMessage());
            } catch (IOException var17) {
                rd1 = new BufferedReader(new InputStreamReader(conn.getErrorStream(), Charset.defaultCharset()));

                String line;
                while((line = rd1.readLine()) != null) {
                    sb1.append(line);
                }

                httpResponse = new HttpResponse(sb1.toString(), conn.getResponseCode());
                httpResponse.setResponseMessage(conn.getResponseMessage());
            } finally {
                if(rd1 != null) {
                    rd1.close();
                }

            }

            ignored1 = httpResponse;
        } finally {
            if(conn != null) {
                conn.disconnect();
            }

        }

        return ignored1;
    }

    public static void sendPostRequest(Reader data, URL endpoint, Writer output, String contentType) throws AutomationFrameworkException {
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection)endpoint.openConnection();

            try {
                urlConnection.setRequestMethod("POST");
            } catch (ProtocolException var33) {
                throw new AutomationFrameworkException("Shouldn\'t happen: HttpURLConnection doesn\'t support POST?? " + var33.getMessage(), var33);
            }

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setRequestProperty("Content-type", contentType);
            OutputStream e = urlConnection.getOutputStream();

            try {
                OutputStreamWriter in = new OutputStreamWriter(e, "UTF-8");
                pipe(data, in);
                in.close();
            } catch (IOException var32) {
                throw new AutomationFrameworkException("IOException while posting data " + var32.getMessage(), var32);
            } finally {
                if(e != null) {
                    e.close();
                }

            }

            InputStream in1 = urlConnection.getInputStream();

            try {
                InputStreamReader e1 = new InputStreamReader(in1, Charset.defaultCharset());
                pipe(e1, output);
                e1.close();
            } catch (IOException var31) {
                throw new AutomationFrameworkException("IOException while reading response " + var31.getMessage(), var31);
            } finally {
                if(in1 != null) {
                    in1.close();
                }

            }
        } catch (IOException var36) {
            throw new AutomationFrameworkException("Connection error (is server running at " + endpoint + " ?): " + var36.getMessage(), var36);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }

        }

    }

    public static void sendPutRequest(Reader data, URL endpoint, Writer output, String contentType) throws AutomationFrameworkException {
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection)endpoint.openConnection();

            try {
                urlConnection.setRequestMethod("PUT");
            } catch (ProtocolException var33) {
                throw new AutomationFrameworkException("Shouldn\'t happen: HttpURLConnection doesn\'t support PUT?? " + var33.getMessage(), var33);
            }

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setRequestProperty("Content-type", contentType);
            OutputStream e = urlConnection.getOutputStream();

            try {
                OutputStreamWriter in = new OutputStreamWriter(e, "UTF-8");
                pipe(data, in);
                in.close();
            } catch (IOException var32) {
                throw new AutomationFrameworkException("IOException while posting data " + var32.getMessage(), var32);
            } finally {
                if(e != null) {
                    e.close();
                }

            }

            InputStream in1 = urlConnection.getInputStream();

            try {
                InputStreamReader e1 = new InputStreamReader(in1, Charset.defaultCharset());
                pipe(e1, output);
                e1.close();
            } catch (IOException var31) {
                throw new AutomationFrameworkException("IOException while reading response " + var31.getMessage(), var31);
            } finally {
                if(in1 != null) {
                    in1.close();
                }

            }
        } catch (IOException var36) {
            throw new AutomationFrameworkException("Connection error (is server running at " + endpoint + " ?): " + var36.getMessage(), var36);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }

        }

    }

    public static int sendDeleteRequest(URL endpoint, String contentType) throws AutomationFrameworkException {
        HttpURLConnection urlConnection = null;

        int responseCode;
        try {
            urlConnection = (HttpURLConnection)endpoint.openConnection();

            try {
                urlConnection.setRequestMethod("DELETE");
            } catch (ProtocolException var9) {
                throw new AutomationFrameworkException("Shouldn\'t happen: HttpURLConnection doesn\'t support DELETE?? " + var9.getMessage(), var9);
            }

            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-type", contentType);
            responseCode = urlConnection.getResponseCode();
        } catch (IOException var10) {
            throw new AutomationFrameworkException("Connection error (is server running at " + endpoint + " ?): " + var10.getMessage(), var10);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }

        }

        return responseCode;
    }

    private static void pipe(Reader reader, Writer writer) throws IOException {
        char[] buf = new char[1024];

        int read;
        while((read = reader.read(buf)) >= 0) {
            writer.write(buf, 0, read);
        }

        writer.flush();
    }
}
