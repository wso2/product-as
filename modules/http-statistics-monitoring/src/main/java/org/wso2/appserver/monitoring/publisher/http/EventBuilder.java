package org.wso2.appserver.monitoring.publisher.http;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.wso2.appserver.monitoring.collector.http.WebappMonitoringPublisherConstants;
import org.wso2.appserver.monitoring.publisher.MonitoringPublisherException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;
import ua_parser.Client;
import ua_parser.Parser;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by nathasha on 12/7/15.
 */

/**
 * Build an Event using WebappMonitoringEvent.
 */
public class EventBuilder  {



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

