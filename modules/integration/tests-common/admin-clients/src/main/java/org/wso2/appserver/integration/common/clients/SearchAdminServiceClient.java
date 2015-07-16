package org.wso2.appserver.integration.common.clients;


import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.registry.search.stub.SearchAdminServiceRegistryExceptionException;
import org.wso2.carbon.registry.search.stub.SearchAdminServiceStub;
import org.wso2.carbon.registry.search.stub.beans.xsd.AdvancedSearchResultsBean;
import org.wso2.carbon.registry.search.stub.beans.xsd.CustomSearchParameterBean;
import org.wso2.carbon.registry.search.stub.beans.xsd.MediaTypeValueList;
import org.wso2.carbon.registry.search.stub.beans.xsd.SearchResultsBean;

import java.rmi.RemoteException;

public class SearchAdminServiceClient {
    private static final Log log = LogFactory.getLog(SearchAdminServiceClient.class);

    private final String serviceName = "SearchAdminService";
    private SearchAdminServiceStub searchAdminServiceStub;

    public SearchAdminServiceClient(String backEndUrl, String sessionCookie) throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        searchAdminServiceStub = new SearchAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, searchAdminServiceStub);
    }

    public SearchAdminServiceClient(String backEndUrl, String username, String password)
            throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        searchAdminServiceStub = new SearchAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(username, password, searchAdminServiceStub);
    }

    public void deleteFilter(String filterName)
            throws SearchAdminServiceRegistryExceptionException, RemoteException {

        searchAdminServiceStub.deleteFilter(filterName);
    }

    public CustomSearchParameterBean getAdvancedSearchFilter(String filterName)
            throws SearchAdminServiceRegistryExceptionException, RemoteException {
        return searchAdminServiceStub.getAdvancedSearchFilter(filterName);
    }

    public MediaTypeValueList getMediaTypeSearch(String mediaType)
            throws SearchAdminServiceRegistryExceptionException, RemoteException {
        return searchAdminServiceStub.getMediaTypeSearch(mediaType);
    }

    public AdvancedSearchResultsBean getAdvancedSearchResults(
            CustomSearchParameterBean searchParams)
            throws SearchAdminServiceRegistryExceptionException, RemoteException {
        return searchAdminServiceStub.getAdvancedSearchResults(searchParams);
    }

    public String[] getSavedFilters()
            throws SearchAdminServiceRegistryExceptionException, RemoteException {
        return searchAdminServiceStub.getSavedFilters();
    }

    public SearchResultsBean getSearchResults(String searchType, String criteria)
            throws SearchAdminServiceRegistryExceptionException, RemoteException {
        return searchAdminServiceStub.getSearchResults(searchType, criteria);

    }

    public void saveAdvancedSearchFilter(CustomSearchParameterBean queryBean, String filterName)
            throws SearchAdminServiceRegistryExceptionException, RemoteException {
        searchAdminServiceStub.saveAdvancedSearchFilter(queryBean, filterName);

    }
}
