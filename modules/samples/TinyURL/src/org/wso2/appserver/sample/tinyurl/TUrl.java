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

package org.wso2.appserver.sample.tinyurl;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.MessageContext;

import java.security.SecureRandom;
import java.util.Hashtable;

/**
 * The TinyURL store + help methods.
 */
public class TUrl {

    /**
     * Key used to store the url table in the configuration context
     */
    public final static String URL_TABLE = "local_urlTable";

    /**
     * Key used to store the id table in the configuration context
     */
    public final static String ID_TABLE = "local_idTable";

    public static final String HTTP_TRANSPORT = "http";

    public final static int ERR_INVALID_REQ = 0;

    public final static int ERR_NO_URL = 1;

    public final static int ERR_INTERNAL = 2;

    public static String addUrl(String url, MessageContext msgCtx) throws AxisFault {
        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }

        Object urlTableObj =
                msgCtx.getConfigurationContext().getProperty(URL_TABLE);
        Hashtable urlTable;
        Hashtable idTable;

        if (urlTableObj == null) {
            urlTable = new Hashtable();
            idTable = new Hashtable();

            msgCtx.getConfigurationContext().setProperty(URL_TABLE, urlTable);
            msgCtx.getConfigurationContext().setProperty(ID_TABLE, idTable);
        } else {
            urlTable = (Hashtable) urlTableObj;
            idTable = (Hashtable) msgCtx.getConfigurationContext().getProperty(
                    ID_TABLE);
        }

        // Check whether this url is already stored
        String id = (String) urlTable.get(url);

        if (id == null) {
            // Create a new id
            id = createId();
            urlTable.put(url, id);
            idTable.put(id, url);
        }

        EndpointReference myEPR = msgCtx.getServiceContext().getMyEPR(TUrl.HTTP_TRANSPORT);
/*        if (myEPR == null) {
            myEPR = msgCtx.getServiceContext().getMyEPR();
        }*/
        return getUrlPage(id, myEPR.getAddress());
    }

    public static String go(String id, MessageContext msgCtx) {

        Object idTableObj = msgCtx.getConfigurationContext().getProperty(
                ID_TABLE);

        if (idTableObj == null) {
            // Return the error message
            return getErrorMessage(ERR_NO_URL);
        } else {
            // We have a some urls with us
            // Now lets see whether this id is assigned to any of them
            Hashtable idTable = (Hashtable) idTableObj;
            String url = (String) idTable.get(id);
            if (url == null) {
                return getErrorMessage(ERR_NO_URL);
            } else {
                return getPage(url.replaceAll("&", "&amp;"));
            }

        }
    }

    private static String getPage(String url) {
        return "<html><head><script language=\"javascript\">location.href='" + url +
               "'</script></head></html>";
    }

    private static String getUrlPage(String id, String hostUrl) {
        String url = hostUrl + "go?id=" + id;
        return "<html><h2>The short URL is : </h2><a href=\"" + url + "\">" + url + "</a></html>";
    }

    protected static String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case ERR_NO_URL:
                return "<html><h1>URL not found!</h1></html>";
            case ERR_INVALID_REQ:
                return "<html><h1>Invalid request!</h1></html>";
            case ERR_INTERNAL:
                return "Service error!";
            default:
                return "<html><h2>Invalid request</h2></html>";
        }
    }

    private static String createId() {
        SecureRandom rnd = new SecureRandom();
        return Integer.toString(Math.abs(rnd.nextInt()));
    }

}
