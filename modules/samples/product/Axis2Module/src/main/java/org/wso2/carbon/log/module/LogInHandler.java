package org.wso2.carbon.log.module;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogInHandler extends AbstractHandler {

    private static final Log log = LogFactory.getLog(LogInHandler.class);

    public InvocationResponse invoke(MessageContext messageContext) throws AxisFault {
        // Ignore filtered services and services to which this module is not engaged..
        if (LogModuleUtils.isFilteredOutService(messageContext.getAxisServiceGroup()) ||
                !messageContext.getAxisOperation().isEngaged("logmodule-1.0.0")) {
            return InvocationResponse.CONTINUE;
        }
        // Log the message
        if (log.isDebugEnabled()) {
            log.debug("Incoming Message : " + messageContext.getEnvelope());
        }
        return InvocationResponse.CONTINUE;
    }
}
