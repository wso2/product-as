package org.wso2.appserver.integration.common.clients;


import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.wso2.carbon.registry.indexing.stub.generated.ContentSearchAdminServiceStub;
import org.wso2.carbon.registry.indexing.stub.generated.xsd.SearchResultsBean;

import java.rmi.RemoteException;

public class ContentSearchAdminClient {

    private static final Log log = LogFactory.getLog(ContentSearchAdminClient.class);

    private ContentSearchAdminServiceStub contentSearchAdminServiceStub;

    public ContentSearchAdminClient(String backEndUrl, String username, String password)
            throws AxisFault {
        String serviceName = "ContentSearchAdminService";
        String endPoint = backEndUrl + serviceName;
        contentSearchAdminServiceStub = new ContentSearchAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(username, password, contentSearchAdminServiceStub);

    }

    public ContentSearchAdminClient(String sessionCookie, String backEndUrl)
            throws AxisFault {
        String serviceName = "ContentSearchAdminService";
        String endPoint = backEndUrl + serviceName;
        contentSearchAdminServiceStub = new ContentSearchAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, contentSearchAdminServiceStub);

    }

    public SearchResultsBean getContentSearchResults(String searchQuery) throws RemoteException {

        SearchResultsBean bean;
        try {
            bean = contentSearchAdminServiceStub.getContentSearchResults(searchQuery);
        } catch (RemoteException e) {
            String msg = "Unable o search the contents";
            log.error(msg + e);
            throw new RemoteException(msg, e);
        }

        return bean;
    }
}
