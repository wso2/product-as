package org.wso2.appserver.monitoring.collector.http;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.wso2.appserver.monitoring.config.ConfigurationException;
import org.wso2.appserver.monitoring.config.InvalidXMLConfiguration;
import org.wso2.appserver.monitoring.config.StreamConfig;
import org.wso2.appserver.monitoring.publisher.MonitoringPublisherException;
import org.wso2.appserver.monitoring.publisher.http.EventBuilder;
import org.wso2.appserver.monitoring.publisher.http.WebappMonitoringEvent;
import org.wso2.carbon.databridge.agent.AgentHolder;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;
import ua_parser.Client;
import ua_parser.Parser;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;


/**
 * Custom Tomcat valve to Publish server statistics data to Data Analytics Server.
 */
public class HttpStatValve extends ValveBase {

    public static final String BACKSLASH = "/";
    private Parser uaParser = null;
//    public static final String WEBAPP = "webapp";
    private static final Log LOG = LogFactory.getLog(HttpStatValve.class);

    protected String username = "admin";
    protected String password = "admin";

    protected String host = "127.0.0.1";
    protected String configFileFolder = "conf/Security";

    private volatile EventBuilder eventbuilder;

    public DataPublisher dataPublisher = null;

    //Event stream name
    public String httpStream;
    //Event stream version
    public String streamVersion;
    String streamId;

    public String parentDir;

    private static final int defaultThriftPort = 7611;
    private static final int defaultBinaryPort = 9611;



    public HttpStatValve() throws InvalidXMLConfiguration {

        LOG.debug("The HttpStatValve initialized.");

        File userDir = new File(System.getProperty("catalina.home"));
        parentDir = userDir.getAbsolutePath();

        //retrieving stream name and version
        StreamConfig streamconfig = new StreamConfig();

        httpStream = streamconfig.getStreamName();
        streamVersion = streamconfig.getStreamVersion();

        //generate streamID
        streamId = DataBridgeCommonsUtils.generateStreamId(httpStream, streamVersion);


        /*the resources folder is in tomcat*/
        System.setProperty("javax.net.ssl.trustStore", parentDir + File.separator + getConfigFileFolder()
                + "/client-truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

        //create dataPublisher to publish data
        try {
            if (dataPublisher == null) {
                dataPublisher = getDataPublisher();
            }
        } catch (ConfigurationException e) {
            LOG.error("Failed at configurations: " + e);
        }


    }

    @Override
    public void invoke(Request request, Response response)  {

        Long startTime = System.currentTimeMillis();

        try {
            getNext().invoke(request, response);
        } catch (IOException e) {
            LOG.error("Invoke failed:" + e);
        } catch (ServletException e) {
            LOG.error("Invoke failed:" + e);
        }

        long responseTime = System.currentTimeMillis() - startTime;


        //Extracting the data from request and response and setting them to bean class
        WebappMonitoringEvent webappMonitoringEvent = prepareWebappMonitoringEventData(request, response, responseTime);

        //Time stamp of request initiated in the class
        webappMonitoringEvent.setTimestamp(startTime);


        if (LOG.isDebugEnabled()) {
            LOG.debug("publishing the HTTP Stat : " + webappMonitoringEvent);
        }

        eventbuilder = new EventBuilder();
        
        try {

            //Event created
            Event event = eventbuilder.prepareEvent(streamId, webappMonitoringEvent);

            if (Optional.ofNullable(dataPublisher).isPresent()) {
                //Event published
                dataPublisher.publish(event);
            }


        } catch (MonitoringPublisherException e) {
            LOG.error("Publishing failed:" + e);
        }

    }

    public WebappMonitoringEvent prepareWebappMonitoringEventData(Request request,
                                                                  Response response,
                                                                  long responseTime) {

        WebappMonitoringEvent webappMonitoringEvent = new WebappMonitoringEvent();

        String requestedURI = request.getRequestURI();

        /*
        * Checks requested url null
        */
        if (Optional.ofNullable(requestedURI).isPresent()) {

            requestedURI = requestedURI.trim();
            String[] requestedUriParts = requestedURI.split(BACKSLASH);

           /*
            * If url start with /t/, the request comes to a tenant web app
            */
            if (requestedURI.startsWith("/t/")) {
                if (requestedUriParts.length >= 4) {
                    webappMonitoringEvent.setWebappName(requestedUriParts[4]);
                    webappMonitoringEvent.setWebappOwnerTenant(requestedUriParts[2]);
                }
            } else {
                webappMonitoringEvent.setWebappOwnerTenant(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
                if (!BACKSLASH.equals(requestedURI)) {
                    webappMonitoringEvent.setWebappName(requestedUriParts[1]);
                } else {
                    webappMonitoringEvent.setWebappName(BACKSLASH);
                }
            }

            String webappServletVersion = request.getContext().getEffectiveMajorVersion() + "."
                    + request.getContext().getEffectiveMinorVersion();
            webappMonitoringEvent.setWebappVersion(webappServletVersion);

            String consumerName = extractUsername(request);
            webappMonitoringEvent.setUserId(consumerName);
            webappMonitoringEvent.setResourcePath(request.getPathInfo());
            webappMonitoringEvent.setRequestUri(request.getRequestURI());
            webappMonitoringEvent.setHttpMethod(request.getMethod());
            webappMonitoringEvent.setContentType(request.getContentType());
            webappMonitoringEvent.setResponseContentType(response.getContentType());
            webappMonitoringEvent.setResponseHttpStatusCode(response.getStatus());
            webappMonitoringEvent.setRemoteAddress(getClientIpAddress(request));
            webappMonitoringEvent.setReferrer(request.getHeader(WebappMonitoringPublisherConstants.REFERRER));
            webappMonitoringEvent.setRemoteUser(request.getRemoteUser());
            webappMonitoringEvent.setAuthType(request.getAuthType());
            webappMonitoringEvent.setCountry("-");
            webappMonitoringEvent.setResponseTime(responseTime);
            webappMonitoringEvent.setLanguage(request.getLocale().getLanguage());
            webappMonitoringEvent.setCountry(request.getLocale().getCountry());
            webappMonitoringEvent.setSessionId(extractSessionId(request));
            webappMonitoringEvent.setWebappDisplayName(request.getContext().getDisplayName());
            webappMonitoringEvent.setWebappContext(requestedURI);
            webappMonitoringEvent.setWebappType("webapp");
            webappMonitoringEvent.setServerAddress(request.getServerName());
            webappMonitoringEvent.setServerName(request.getLocalName());
            webappMonitoringEvent.setRequestSizeBytes(request.getContentLength());
            webappMonitoringEvent.setResponseSizeBytes(response.getContentLength());
            parserUserAgent(request, webappMonitoringEvent);

        }
        return webappMonitoringEvent;
    }

    private String extractSessionId(Request request) {
        final HttpSession session = request.getSession(false);

        // CXF web services does not have a session id, because they are stateless
        return (session != null && session.getId() != null) ? session.getId() : "-";

    }

    private String extractUsername(Request request) {
        String consumerName;
        Principal principal = request.getUserPrincipal();

        if (Optional.ofNullable(principal).isPresent()) {
            consumerName = principal.getName();
        } else {
            consumerName = WebappMonitoringPublisherConstants.ANONYMOUS_USER;
        }
        return consumerName;
    }

    private void parserUserAgent(Request request, WebappMonitoringEvent webappMonitoringEvent) {
        String userAgent = request.getHeader(WebappMonitoringPublisherConstants.USER_AGENT);

        if (Optional.ofNullable(uaParser).isPresent()) {

            Client readableUserAgent = uaParser.parse(userAgent);

            webappMonitoringEvent.setUserAgentFamily(readableUserAgent.userAgent.family);
            webappMonitoringEvent.setUserAgentVersion(readableUserAgent.userAgent.major);
            webappMonitoringEvent.setOperatingSystem(readableUserAgent.os.family);
            webappMonitoringEvent.setOperatingSystemVersion(readableUserAgent.os.major);
            webappMonitoringEvent.setDeviceCategory(readableUserAgent.device.family);
        }
    }

    /*
    * Checks the remote address of the request. Server could be hiding behind a proxy or load balancer.
    * if we get only request.getRemoteAddr() will give only the proxy pr load balancer address.
    * For that we are checking the request forwarded address in the header of the request.
    */
    private String getClientIpAddress(Request request) {
        String ip = request.getHeader(WebappMonitoringPublisherConstants.X_FORWARDED_FOR);
        ip = tryNextHeaderIfIpNull(request, ip, WebappMonitoringPublisherConstants.PROXY_CLIENT_IP);
        ip = tryNextHeaderIfIpNull(request, ip, WebappMonitoringPublisherConstants.WL_PROXY_CLIENT_IP);
        ip = tryNextHeaderIfIpNull(request, ip, WebappMonitoringPublisherConstants.HTTP_CLIENT_IP);
        ip = tryNextHeaderIfIpNull(request, ip, WebappMonitoringPublisherConstants.HTTP_X_FORWARDED_FOR);

        if (!Optional.ofNullable(ip).isPresent() || ip.length() == 0 ||
                WebappMonitoringPublisherConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            // Failed. remoteAddr is the only option
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    // If the input param ip is invalid, it will return the value of the next header
    // as the output
    private String tryNextHeaderIfIpNull(Request request, String ip, String nextHeader) {
        if (!Optional.ofNullable(ip).isPresent() || ip.length() == 0 ||
                WebappMonitoringPublisherConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            return request.getHeader(nextHeader);
        }
        return null;
    }

    public  String getDataAgentConfigPath() {
        File filePath = new File(parentDir + File.separator + getConfigFileFolder());

        return filePath.getAbsolutePath() + File.separator + "data-agent-conf.xml";

    }


    private static String  getProperty(String name, String def) {
        String result = System.getProperty(name);
        if (!Optional.ofNullable(result).isPresent() || result.length() == 0 || result == "") {
            result = def;
        }
        return result;
    }


    private DataPublisher getDataPublisher() throws ConfigurationException {

        //configuring the data agent
        AgentHolder.setConfigPath(getDataAgentConfigPath());

        String type = getProperty("type", "Thrift");

        int receiverPort = defaultThriftPort;
        if (type.equals("Binary")) {
            receiverPort = defaultBinaryPort;
        }

        String url = getProperty("url", "tcp://" + getHost() + ":" + receiverPort);

        //instantiating the datapublisher
        DataPublisher dataPublisher = null;
        try {
            dataPublisher = new DataPublisher(url, getUsername(), getPassword());
        } catch (DataEndpointAgentConfigurationException | DataEndpointException | DataEndpointConfigurationException |
                DataEndpointAuthenticationException | TransportException e) {
            throw new ConfigurationException("Configuring Data Endpoint Agent failed", e);
        }

        return dataPublisher;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setConfigFileFolder(String configFileFolder) {
        this.configFileFolder = configFileFolder;
    }

    public String getConfigFileFolder() {
        return configFileFolder;
    }

}
