/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.appserver.integration.common.clients;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.module.mgt.stub.ModuleAdminServiceModuleMgtExceptionException;
import org.wso2.carbon.module.mgt.stub.ModuleAdminServiceStub;
import org.wso2.carbon.module.mgt.stub.types.ModuleMetaData;
import org.wso2.carbon.module.mgt.stub.types.ModuleUploadData;

import javax.activation.DataHandler;
import java.rmi.RemoteException;

public class ModuleAdminServiceClient {
    private static final Log log = LogFactory.getLog(ModuleAdminServiceClient.class);
    private final String serviceName = "ModuleAdminService";
    private ModuleAdminServiceStub moduleAdminServiceStub;

    public ModuleAdminServiceClient(String backEndUrl, String sessionCookie) throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        moduleAdminServiceStub = new ModuleAdminServiceStub(endPoint);
        AuthenticateStubUtil.authenticateStub(sessionCookie, moduleAdminServiceStub);
    }

    public ModuleAdminServiceClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        moduleAdminServiceStub = new ModuleAdminServiceStub(endPoint);
        AuthenticateStubUtil.authenticateStub(userName, password, moduleAdminServiceStub);
    }

    /**
     * Retrieve names and version of all modules
     *
     * @throws RemoteException - if module list can not be retrived
     */
    public ModuleMetaData[] listModules() throws RemoteException {
        try {
            return moduleAdminServiceStub.listModules();
        } catch (RemoteException e) {
            throw new RemoteException("Cannot retrieve module list ", e);
        }
    }

    /**
     * Retrieve modules engaged with a service.
     *
     * @param serviceName - Name of the service where list of modules to be retrieved.
     * @return ModuleMetaData[]
     * @throws ModuleAdminServiceModuleMgtExceptionException
     *          - if module list can not retrieved for a particular service
     */
    public ModuleMetaData[] listModulesForService(String serviceName)
            throws ModuleAdminServiceModuleMgtExceptionException, RemoteException {
        try {
            return moduleAdminServiceStub.listModulesForService(serviceName);
        } catch (ModuleAdminServiceModuleMgtExceptionException e) {
            throw new ModuleAdminServiceModuleMgtExceptionException("Cannot list modules engaged " +
                    "with a service", e);
        } catch (RemoteException e) {
            throw new RemoteException("Cannot list modules engaged with a service", e);
        }
    }

    public void uploadModule(DataHandler dh) throws RemoteException {
        ModuleUploadData moduleUploadData = new ModuleUploadData();
        moduleUploadData.setFileName(dh.getName().substring(dh.getName().lastIndexOf('/') + 1));
        moduleUploadData.setDataHandler(dh);
        log.info((moduleAdminServiceStub.uploadModule(new ModuleUploadData[]{moduleUploadData})));
    }

    public boolean engageModule(String moduleId, String ServiceName) throws
            ModuleAdminServiceModuleMgtExceptionException, RemoteException {
        return moduleAdminServiceStub.engageModuleForService(moduleId, ServiceName);
    }

    public boolean disengageModule(String moduleId, String ServiceName) throws
            ModuleAdminServiceModuleMgtExceptionException, RemoteException {
        return moduleAdminServiceStub.disengageModuleForService(moduleId, ServiceName);
    }

    public ServiceClient getServiceClient() {
        ServiceClient serverClient;
        serverClient = moduleAdminServiceStub._getServiceClient();
        return serverClient;
    }

    public ModuleMetaData[] getModuleList() throws RemoteException {
        ModuleMetaData[] moduleMetaData = moduleAdminServiceStub.listModules();
        return moduleMetaData;
    }

    public void deleteModule(String moduleId)
            throws ModuleAdminServiceModuleMgtExceptionException, RemoteException {
        log.info(moduleAdminServiceStub.removeModule(moduleId));
    }
}
