package org.wso2.appserver.samples;

import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.user.api.UserRealmService;
import org.wso2.carbon.user.api.UserStoreException;

/**
 * Helper Class to turn domain name into the tenant id
 * 
 *
 */
public class TenantUtils {
	
	public static int getTID(String tenantDomain) throws UserStoreException {
	    UserRealmService realmService = (UserRealmService) PrivilegedCarbonContext.getThreadLocalCarbonContext().getOSGiService(UserRealmService.class);
		org.wso2.carbon.user.api.TenantManager tenantManager = realmService.getTenantManager();		
	   	return tenantManager.getTenantId(tenantDomain);
	}

}
