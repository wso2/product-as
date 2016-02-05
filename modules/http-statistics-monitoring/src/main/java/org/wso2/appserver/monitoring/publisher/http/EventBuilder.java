package org.wso2.appserver.monitoring.publisher.http;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.wso2.appserver.monitoring.collector.http.WebappMonitoringPublisherConstants;
import org.wso2.appserver.monitoring.publisher.MonitoringPublisherException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;
import ua_parser.Client;
import ua_parser.Parser;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;


/**
 * Build an Event using WebappMonitoringEvent.
 */
public class EventBuilder  {

    private Parser uaParser = null;

    /**
     * Build an Event using a WebappMonitoringEvent.
     *
     * @param streamId Unique ID of the Event Stream
     * @param monitoringEvent A WebappMonitoringEvent that holds all server statistics data
     * @throws MonitoringPublisherException
     */
    public Event prepareEvent(String streamId, WebappMonitoringEvent monitoringEvent)
            throws MonitoringPublisherException {

        List<Object> payload = new ArrayList<Object>();

        payload.add(mapNull(monitoringEvent.getWebappName()));
        payload.add(mapNull(monitoringEvent.getWebappVersion()));
        payload.add(mapNull(monitoringEvent.getUserId()));
        payload.add(mapNull(monitoringEvent.getRequesturi()));
        payload.add(mapNull(monitoringEvent.getTimestamp()));
        payload.add(mapNull(monitoringEvent.getResourcePath()));
        payload.add(mapNull(monitoringEvent.getUserAgentVersion()));
        payload.add(mapNull(monitoringEvent.getOperatingSystem()));
        payload.add(mapNull(monitoringEvent.getOperatingSystemVersion()));
        payload.add(mapNull(monitoringEvent.getCountry()));
        payload.add(mapNull(monitoringEvent.getWebappType()));
        payload.add(mapNull(monitoringEvent.getWebappDisplayName()));
        payload.add(mapNull(monitoringEvent.getWebappContext()));
        payload.add(mapNull(monitoringEvent.getSessionId()));
        payload.add(mapNull(monitoringEvent.getHttpMethod()));
        payload.add(mapNull(monitoringEvent.getContentType()));
        payload.add(mapNull(monitoringEvent.getResponseContentType()));
        payload.add(mapNull(monitoringEvent.getResponseHttpStatusCode()));
        payload.add(mapNull(monitoringEvent.getRemoteAddress()));
        payload.add(mapNull(monitoringEvent.getReferrer()));
        payload.add(mapNull(monitoringEvent.getRemoteUser()));
        payload.add(mapNull(monitoringEvent.getAuthType()));
        payload.add(mapNull(monitoringEvent.getUserAgentFamily()));
        payload.add(mapNull(monitoringEvent.getResponseTime()));
        payload.add(mapNull(monitoringEvent.getRequestSizeBytes()));
        payload.add(mapNull(monitoringEvent.getResponseSizeBytes()));
        payload.add(mapNull(monitoringEvent.getRequestHeader()));
        payload.add(mapNull(monitoringEvent.getResponseHeader()));
        payload.add(mapNull(monitoringEvent.getRequestPayload()));
        payload.add(mapNull(monitoringEvent.getResponsePayload()));
        payload.add(mapNull(monitoringEvent.getLanguage()));
        payload.add(mapNull(monitoringEvent.getDeviceCategory()));

        List<Object> metaData = new ArrayList<Object>();
        metaData.add(mapNull(monitoringEvent.getServerAddress()));
        metaData.add(mapNull(monitoringEvent.getServerName()));
        metaData.add(mapNull(monitoringEvent.getClusterDomain()));
        metaData.add(mapNull(monitoringEvent.getClusterSubDomain()));

        Event event = new Event(streamId, System.currentTimeMillis(), metaData.toArray(), null, payload.toArray());
        return event;

    }

    public WebappMonitoringEvent setStatData(Request request, Response response, long responseTime) {

        String BACKSLASH = "/";
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

        if (ip == null || ip.length() == 0 || WebappMonitoringPublisherConstants.UNKNOWN.equalsIgnoreCase(ip)) {
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


    /**
     * Maps null Integers to zero.
     *
     * @param value
     * @return
     */
    protected Integer mapNull(Integer value) {
        return (value == null) ? 0 : value;
    }

    /**
     * Maps null Long values to zero.
     *
     * @param value @param value The value that should be mapped.
     * @return the value if not null, otherwise 0
     */
    protected Long mapNull(Long value) {
        return (value == null) ? 0L : value;
    }

    /**
     * Map null String to -.
     *
     * @param value The value that should be mapped.
     * @return the value if not null, otherwise "-"
     */
    protected String mapNull(String value) {
        return (value == null) ? "-" : value;
    }

}

