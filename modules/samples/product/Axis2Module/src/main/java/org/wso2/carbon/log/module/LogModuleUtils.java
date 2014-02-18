package org.wso2.carbon.log.module;

import org.apache.axis2.description.AxisServiceGroup;

public class LogModuleUtils {

    public static final String ADMIN_SERVICE_PARAM_NAME = "adminService";
    public static final String HIDDEN_SERVICE_PARAM_NAME = "hiddenService";

    public static boolean isFilteredOutService(AxisServiceGroup axisServiceGroup) {
        String adminParamValue =
                (String) axisServiceGroup.getParameterValue(ADMIN_SERVICE_PARAM_NAME);
        String hiddenParamValue =
                (String) axisServiceGroup.getParameterValue(HIDDEN_SERVICE_PARAM_NAME);
        if (adminParamValue != null && adminParamValue.length() != 0) {
            if (Boolean.parseBoolean(adminParamValue.trim())) {
                return true;
            }
        } else if (hiddenParamValue != null && hiddenParamValue.length() != 0) {
            if (Boolean.parseBoolean(hiddenParamValue.trim())) {
                return true;
            }
        }
        return false;
    }
}
