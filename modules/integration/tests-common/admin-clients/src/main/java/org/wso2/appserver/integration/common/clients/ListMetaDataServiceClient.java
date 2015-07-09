package org.wso2.appserver.integration.common.clients;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.governance.list.stub.ListMetadataServiceRegistryExceptionException;
import org.wso2.carbon.governance.list.stub.ListMetadataServiceStub;
import org.wso2.carbon.governance.list.stub.beans.xsd.PolicyBean;
import org.wso2.carbon.governance.list.stub.beans.xsd.SchemaBean;
import org.wso2.carbon.governance.list.stub.beans.xsd.ServiceBean;
import org.wso2.carbon.governance.list.stub.beans.xsd.WSDLBean;

import java.rmi.RemoteException;

public class ListMetaDataServiceClient {

    private static final Log log = LogFactory.getLog(ListMetaDataServiceClient.class);

    private final String serviceName = "ListMetadataService";
    private ListMetadataServiceStub listMetadataServiceStub;
    private String endPoint;

    public ListMetaDataServiceClient(String backEndUrl, String sessionCookie)
            throws RemoteException {
        this.endPoint = backEndUrl + serviceName;
        listMetadataServiceStub = new ListMetadataServiceStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, listMetadataServiceStub);
    }

    public ListMetaDataServiceClient(String backEndUrl, String userName, String password)
            throws RemoteException {
        this.endPoint = backEndUrl + serviceName;
        listMetadataServiceStub = new ListMetadataServiceStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, listMetadataServiceStub);
    }

    public ServiceBean listServices(String criteria)
            throws ListMetadataServiceRegistryExceptionException, RemoteException {
        return listMetadataServiceStub.listservices(criteria);
    }


    public WSDLBean listWSDLs()
            throws RemoteException, ListMetadataServiceRegistryExceptionException {
        return listMetadataServiceStub.listwsdls();
    }


    public PolicyBean listPolicies()
            throws RemoteException, ListMetadataServiceRegistryExceptionException {
        return listMetadataServiceStub.listpolicies();
    }

    public SchemaBean listSchemas()
            throws RemoteException, ListMetadataServiceRegistryExceptionException {
        return listMetadataServiceStub.listschema();

    }
}
