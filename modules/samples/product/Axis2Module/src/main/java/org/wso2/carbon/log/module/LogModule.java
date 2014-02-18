package org.wso2.carbon.log.module;

import org.apache.axis2.modules.Module;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.neethi.Assertion;
import org.apache.neethi.Policy;

public class LogModule implements Module {

    private static final Log log = LogFactory.getLog(LogModule.class);

    public void init(ConfigurationContext configurationContext, AxisModule axisModule)
            throws AxisFault {
        log.info("Initializing Log Module");
    }

    public void engageNotify(AxisDescription axisDescription) throws AxisFault {

    }

    public boolean canSupportAssertion(Assertion assertion) {
        return false;
    }

    public void applyPolicy(Policy policy, AxisDescription axisDescription) throws AxisFault {
        log.info("Applying a policy to Log Module");
    }

    public void shutdown(ConfigurationContext configurationContext) throws AxisFault {
        log.info("Shutting down Log Module");
    }
}
