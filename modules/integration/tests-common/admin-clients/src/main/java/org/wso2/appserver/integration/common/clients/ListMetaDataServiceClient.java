package org.wso2.appserver.integration.common.clients;


import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.governance.list.stub.ListMetadataServiceRegistryExceptionException;
import org.wso2.carbon.governance.list.stub.ListMetadataServiceStub;
import org.wso2.carbon.governance.list.stub.beans.xsd.PolicyBean;
import org.wso2.carbon.governance.list.stub.beans.xsd.SchemaBean;
import org.wso2.carbon.governance.list.stub.beans.xsd.ServiceBean;
import org.wso2.carbon.governance.list.stub.beans.xsd.WSDLBean;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;

import java.rmi.RemoteException;

public class ListMetaDataServiceClient {

    private static final Log log = LogFactory.getLog(ListMetaDataServiceClient.class);

    private final String serviceName = "ListMetadataService";
    private ListMetadataServiceStub listMetadataServiceStub;
    private String endPoint;

    public ListMetaDataServiceClient(String backEndUrl, String sessionCookie)
            throws RemoteException {
        this.endPoint = backEndUrl + serviceName;
        try {
            listMetadataServiceStub = new ListMetadataServiceStub(endPoint);
        } catch (AxisFault axisFault) {
            log.error("Error on initializing listMetadataServiceStub : " + axisFault.getMessage());
            throw new RemoteException("Error on initializing listMetadataServiceStub : ", axisFault);
        }
        AuthenticateStub.authenticateStub(sessionCookie, listMetadataServiceStub);
    }

    public ListMetaDataServiceClient(String backEndUrl, String userName, String password)
            throws RemoteException {
        this.endPoint = backEndUrl + serviceName;
        try {
            listMetadataServiceStub = new ListMetadataServiceStub(endPoint);
        } catch (AxisFault axisFault) {
            log.error("Error on initializing listMetadataServiceStub : " + axisFault.getMessage());
            throw new RemoteException("Error on initializing listMetadataServiceStub : ", axisFault);
        }
        AuthenticateStub.authenticateStub(userName, password, listMetadataServiceStub);
    }

    public ServiceBean listServices(String criteria)
            throws RemoteException, ResourceAdminServiceExceptionException {
        ServiceBean serviceBean = null;
        try {
            serviceBean = listMetadataServiceStub.listservices(criteria);

        } catch (RemoteException e) {
            log.error("Cannot list services : " + e.getMessage());
            throw new RemoteException("Service listing error : ", e);
        } catch (ListMetadataServiceRegistryExceptionException e) {
            log.error("Service listing error : " + e.getMessage());
            throw new ResourceAdminServiceExceptionException("Service listing error : ", e);

        }
        return serviceBean;
    }


    public WSDLBean listWSDLs() throws RemoteException, ResourceAdminServiceExceptionException {
        WSDLBean wsdlBean = null;
        try {
            wsdlBean = listMetadataServiceStub.listwsdls();
        } catch (RemoteException e) {
            log.error("Cannot list wsdls : " + e.getMessage());
            throw new RemoteException("WSDLs listing error : ", e);
        } catch (ListMetadataServiceRegistryExceptionException e) {
            log.error("Cannot list wsdls : " + e.getMessage());
            throw new ResourceAdminServiceExceptionException("WSDLs listing error : ", e);
        }
        return wsdlBean;
    }


    public PolicyBean listPolicies()
            throws RemoteException, ResourceAdminServiceExceptionException {
        PolicyBean policyBean = null;
        try {
            policyBean = listMetadataServiceStub.listpolicies();

        } catch (RemoteException e) {
            log.error("Cannot list policies : " + e.getMessage());
            throw new RemoteException("Policy listing error : ", e);
        } catch (ListMetadataServiceRegistryExceptionException e) {
            log.error("Cannot list policies : " + e.getMessage());
            throw new ResourceAdminServiceExceptionException("Policy listing error : ", e);
        }
        return policyBean;
    }

    public SchemaBean listSchemas() throws RemoteException, ResourceAdminServiceExceptionException {
        SchemaBean schemaBean = null;
        try {
            schemaBean = listMetadataServiceStub.listschema();
        } catch (RemoteException e) {
            log.error("Cannot list schemas : " + e.getMessage());
            throw new RemoteException("Schema listing error : ", e);
        } catch (ListMetadataServiceRegistryExceptionException e) {
            log.error("Cannot list schemas : " + e.getMessage());
            throw new ResourceAdminServiceExceptionException("Schema listing error : ", e);

        }
        return schemaBean;
    }
}
