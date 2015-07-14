package org.wso2.appserver.samples;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

public class SaaSManagerServlet extends HttpServlet {
	
	static final long serialVersionUID = 1;
	
	@Override
	public void init() throws ServletException {

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		String fq_username = request.getRemoteUser();
		int tenantID = 0;

		try {
				// convert tenant domain into tenant id
			tenantID = TenantUtils.getTID(MultitenantUtils.getTenantDomain(fq_username));
			PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername(fq_username);		
		} catch (UserStoreException e) {
			e.printStackTrace();
		}
			// forward the tenant identifier
		request.setAttribute("tenantID", tenantID);
	}
}
