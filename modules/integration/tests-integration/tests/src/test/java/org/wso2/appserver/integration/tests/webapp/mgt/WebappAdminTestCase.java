/*
 * Copyright 2015 WSO2, Inc. (http://wso2.com)
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
package org.wso2.appserver.integration.tests.webapp.mgt;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.AuthenticateStubUtil;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.servers.utils.ArchiveExtractor;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.webapp.mgt.stub.WebappAdminStub;
import org.wso2.carbon.webapp.mgt.stub.types.carbon.SessionMetadata;
import org.wso2.carbon.webapp.mgt.stub.types.carbon.SessionsWrapper;
import org.wso2.carbon.webapp.mgt.stub.types.carbon.WebappMetadata;

import javax.activation.DataHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests the WebappAdmin service
 * 
 * <p/>
 * WebappMetadata#getContext - "/HelloWorldWebapp"
 * WebappMetadata#getHostName - "localhost"
 * WebappMetadata#getState - "Started" Or "Stopped"
 * WebappMetadata#getWebappFile - "HelloWorldWebapp.war"
 * WebappMetadata#getWebappType - "webapp"
 * <p/>
 * SessionsWrapper#getSessions is null if no sessions are available
 */
public class WebappAdminTestCase extends ASIntegrationTest {

    private final Log log = LogFactory.getLog(WebappAdminTestCase.class);
    private WebappAdminStub webAppAdminStub;
    private WebAppAdminClient webAppAdminClient;

    private final String webAppName = "HelloWorldWebapp";
    private final String webAppFileName = webAppName + ".war";
    private final String hostName = "localhost";

    String webAppDownloadDirectory;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        String serviceName = "WebappAdmin";
        String endPoint = backendURL + serviceName;
        webAppAdminStub = new WebappAdminStub(endPoint);
        AuthenticateStubUtil.authenticateStub(sessionCookie, webAppAdminStub);

        webAppAdminClient = new WebAppAdminClient(backendURL, sessionCookie);
        webAppDownloadDirectory = (System.getProperty("basedir", ".")) + File.separator + "target" + File.separator;
    }

    @Test(groups = "wso2.as", description = "Deploying web application")
    public void testWebApplicationDeployment() throws Exception {
        webAppAdminClient.uploadWarFile(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war"
                + File.separator + webAppName + ".war");

        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");
    }

    @Test(groups = "wso2.as", description = "", dependsOnMethods = "testWebApplicationDeployment")
    public void testStartStopWebapp() throws RemoteException {
        String webAppStartedState = "Started";
        String webAppStoppedState = "Stopped";

        webAppAdminStub.stopWebapps(new String[]{webAppFileName});
        WebappMetadata webappMetadata = webAppAdminStub.getStoppedWebapp(webAppFileName, hostName);
        assertNotNull(webappMetadata, "Stop webapp operation failed for - " + webAppFileName);
        assertEquals(webappMetadata.getWebappFile(), webAppFileName);
        assertEquals(webappMetadata.getHostName(), hostName);
        assertEquals(webappMetadata.getState(), webAppStoppedState);

        webAppAdminStub.startWebapps(new String[]{webAppFileName});
        webappMetadata = webAppAdminStub.getStartedWebapp(webAppFileName, hostName);
        assertNotNull(webappMetadata, "Start webapp operation failed for - " + webAppFileName);
        assertEquals(webappMetadata.getWebappFile(), webAppFileName);
        assertEquals(webappMetadata.getHostName(), hostName);
        assertEquals(webappMetadata.getState(), webAppStartedState);

        //Stop webapp using webappKey as well. webappKey = <hostname>:<webappfilename>
        String webappKey = hostName + ":" + webAppFileName;
        webAppAdminStub.stopWebapps(new String[]{webappKey});
        webappMetadata = webAppAdminStub.getStoppedWebapp(webAppFileName, hostName);
        assertNotNull(webappMetadata, "Stop webapp operation failed for - " + webappKey);
        assertEquals(webappMetadata.getWebappFile(), webAppFileName);
        assertEquals(webappMetadata.getHostName(), hostName);
        assertEquals(webappMetadata.getState(), webAppStoppedState);

        webAppAdminStub.startWebapps(new String[]{webAppFileName});
        webappMetadata = webAppAdminStub.getStartedWebapp(webAppFileName, hostName);
        assertNotNull(webappMetadata, "Start webapp operation failed for - " + webAppFileName);
        assertEquals(webappMetadata.getWebappFile(), webAppFileName);
        assertEquals(webappMetadata.getHostName(), hostName);
        assertEquals(webappMetadata.getState(), webAppStartedState);
    }

    @Test(groups = "wso2.as", description = "", dependsOnMethods = "testStartStopWebapp")
    public void testReloadWebapp() throws RemoteException {
        String webappFileName = webAppName + ".war";
        webAppAdminStub.startWebapps(new String[]{webappFileName});

        WebappMetadata webappMetadata = webAppAdminStub.getStartedWebapp(webappFileName, hostName);
        assertNotNull(webappMetadata);

        webAppAdminStub.reloadWebapps(new String[]{webappFileName});
        webappMetadata = webAppAdminStub.getStartedWebapp(webappFileName, hostName);
        assertNotNull(webappMetadata);
        assertEquals(webappMetadata.getWebappFile(), webAppFileName);
    }

    /**
     * This tests webapp session expiration.
     * It first sends a request, gets the current session count
     * Expire all the sessions, and make sure the session count is zero
     * Send a request again.
     * Make sure the session count is 1.
     * Then, it sends another request using the same session,
     * and verifies whether the session last accessed time is updated.
     *
     * @throws IOException
     */
    @Test(groups = "wso2.as", description = "Tests webapp session information", dependsOnMethods = "testStartStopWebapp")
    public void testWebAppSessionExpiration() throws IOException {
        String endpoint = webAppURL + "/" + webAppName;
        HttpResponse response = HttpRequestUtil.sendGetRequest(endpoint, null);
        assertEquals(response.getResponseCode(), HttpStatus.SC_OK);

        SessionsWrapper sessions = webAppAdminStub.getActiveSessions(webAppFileName, 0, hostName);
        int sessionsCount = sessions.getNumberOfActiveSessions();
        assertTrue(sessionsCount > 0); //there should be at least one session

        webAppAdminStub.expireAllSessions(webAppFileName);
        sessions = webAppAdminStub.getActiveSessions(webAppFileName, 0, hostName);
        int sessionsCountAfterExpiration = sessions.getNumberOfActiveSessions();
        assertEquals(sessionsCountAfterExpiration, 0);

        response = sendGetRequest(endpoint, null);
        assertEquals(response.getResponseCode(), HttpStatus.SC_OK);
        String cookie = response.getHeaders().get("Set-Cookie");
        String jsessionId = getJSessionId(cookie);

        sessions = webAppAdminStub.getActiveSessions(webAppFileName, 0, hostName);
        int newSessionsCount = sessions.getNumberOfActiveSessions();
        assertEquals(newSessionsCount, 1);

        SessionMetadata[] sessionMetadataArray = sessions.getSessions();
        assertNotNull(sessionMetadataArray);
        assertEquals(sessionMetadataArray.length, 1);

        SessionMetadata sessionMetadata = sessionMetadataArray[0];
        String serverSideSessionId = sessionMetadata.getSessionId();
        assertEquals(jsessionId, serverSideSessionId);
        //make sure last accessed time gets updated
        long lastAccessedTime = sessionMetadata.getLastAccessedTime();

        Map<String, String> cookieHeader = new HashMap<>(1);
        cookieHeader.put("Cookie", cookie);
        response = HttpRequestUtil.doGet(endpoint, cookieHeader);
        assertEquals(response.getResponseCode(), HttpStatus.SC_OK);
        assertTrue(response.getData().contains("Hello 2!"), "Expected value: Hello 2!, actual value: " +
                response.getData());

        sessions = webAppAdminStub.getActiveSessions(webAppFileName, 0, hostName);
        sessionMetadataArray = sessions.getSessions();
        assertNotNull(sessionMetadataArray);
        assertEquals(sessionMetadataArray.length, 1);

        sessionMetadata = sessionMetadataArray[0];
        //make sure last accessed time gets updated
        long lastAccessedTime2 = sessionMetadata.getLastAccessedTime();
        assertNotEquals(lastAccessedTime, lastAccessedTime2);

        //expire sessions in all webapps
        webAppAdminStub.expireSessionsInAllWebapps();
        sessions = webAppAdminStub.getActiveSessions(webAppFileName, 0, hostName);
        sessionMetadataArray = sessions.getSessions();
        assertNull(sessionMetadataArray);
    }

    private String getJSessionId(String cookie) {
        return cookie.substring(0, cookie.indexOf(';')).replace("JSESSIONID=", "");
    }

    @Test(groups = "wso2.as", description = "expire sessions that are older than x minutes",
            dependsOnMethods = "testWebAppSessionExpiration")
    public void testSessionExpirationByAge() throws IOException {

        webAppAdminStub.expireAllSessions(webAppFileName);

        String endpoint = webAppURL + "/" + webAppName;
        HttpResponse response = HttpRequestUtil.sendGetRequest(endpoint, null);
        assertEquals(response.getResponseCode(), HttpStatus.SC_OK);

        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException ignore) {
        }

        webAppAdminStub.expireSessionsInWebapp(webAppFileName, 5000l); // five seconds
        SessionsWrapper sessions = webAppAdminStub.getActiveSessions(webAppFileName, 0, hostName);
        assertEquals(sessions.getNumberOfActiveSessions(), 0);
    }

    @Test(groups = "wso2.as", description = "download the war file through admin api",
            dependsOnMethods = "testWebApplicationDeployment")
    public void testWebAppDownload() throws IOException {
        String destination = webAppDownloadDirectory + webAppFileName;

        FileUtils.deleteQuietly(new File(destination));
        DataHandler dataHandler = webAppAdminStub.downloadWarFileHandler(webAppFileName, hostName, "webapp");
        InputStream in = null;
        OutputStream outputStream = null;

        try {
            in = dataHandler.getDataSource().getInputStream();
            outputStream = new FileOutputStream(new File(destination));

            int read;
            byte[] bytes = new byte[1024];

            while ((read = in.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } finally {
            assertNotNull(in);
            assertNotNull(outputStream);

            in.close();
            outputStream.close();
        }

        String extractedDir = extractZip(webAppDownloadDirectory + webAppFileName);
        log.info("Unpacked webapp in " + extractedDir);
        assertTrue(new File(extractedDir + File.separator + "index.jsp").exists(),
                "Download web app extraction was not successful.");

    }

    private String extractZip(String zipFile) throws IOException {

        String fileSeparator = (File.separator.equals("\\")) ? "\\" : "/";
        if (fileSeparator.equals("\\")) {
            zipFile = zipFile.replace("/", "\\");
        }
        String extractedDir = webAppDownloadDirectory + webAppName + System.currentTimeMillis();
        new ArchiveExtractor().extractFile(zipFile, extractedDir);

        return extractedDir;
    }

    /**
     * This sends a get request, and returns a HttpResponse object populated with
     * content, response code, and header fields.
     * <p/>
     * HttpRequestUtil.sendGetRequest does not include the header fields - TA-990
     */
    private HttpResponse sendGetRequest(String endpoint, String requestParameters) throws IOException {
        if (requestParameters != null && requestParameters.length() > 0) {
            endpoint = endpoint + "?" + requestParameters;
        }

        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpGet httpget = new HttpGet(endpoint);
        log.info(httpget.getRequestLine());

        org.apache.http.HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();

        String content = EntityUtils.toString(entity);

        Header headers[] = response.getAllHeaders();
        Map<String, String> headerMap = new HashMap<>(headers.length);
        for (Header header : headers) {
            headerMap.put(header.getName(), header.getValue());
        }

        return new HttpResponse(content, response.getStatusLine().getStatusCode(), headerMap);
    }

}
