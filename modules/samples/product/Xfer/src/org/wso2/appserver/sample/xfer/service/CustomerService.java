/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.appserver.sample.xfer.service;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.addressing.EndpointReference;
import org.wso2.appserver.sample.util.CustomerUtil;
import org.wso2.appserver.sample.xfer.Customer;
import org.wso2.xfer.WSTransferAdapter;
import org.wso2.xfer.WSTransferException;

import java.util.Hashtable;


public class CustomerService extends WSTransferAdapter {

    public static final QName Q_ELEM_CUSTOMER_ID = new QName(Customer.NS_URI, Customer.CUSTOMER_ID, "xxx");

    private Hashtable storage = new Hashtable();
    
    public EndpointReference create(OMElement resource) throws WSTransferException {
        
        Customer customer = CustomerUtil.fromOM(resource);
        Integer index = Integer.valueOf(customer.getId());
        storage.put(index, customer);

        EndpointReference targetEPR = new EndpointReference("http://127.0.0.1:8080/axis2/services/WSTransferSampleService");

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement refProperty = fac.createOMElement(Q_ELEM_CUSTOMER_ID, null);
        refProperty.setText(index.toString());
        targetEPR.addExtensibleElement(refProperty);

        return targetEPR;
    }

    public OMElement delete(OMElement headers) throws WSTransferException {
        OMElement identifierHeader = headers.getFirstChildWithName(Q_ELEM_CUSTOMER_ID);

        Object removed = storage.remove(identifierHeader.getText());

        if (removed == null) {
            throw new WSTransferException(null, null, null, null);
        }
        return identifierHeader;
    }

    public OMElement get(OMElement headers) throws WSTransferException {
        OMElement identifierHeader = headers.getFirstChildWithName(Customer.Q_ELEM_CUSTOMER_ID);
        Integer index = new Integer(identifierHeader.getText());
        Customer customer = (Customer) storage.get(index);

        if (customer == null){
            throw new WSTransferException(null, null, null, null);
        }

        return CustomerUtil.toOM(customer);
    }

    public OMElement put(OMElement headers, OMElement resource) throws WSTransferException {
        OMElement customerIdHeader = headers.getFirstChildWithName(Q_ELEM_CUSTOMER_ID);

        String customerId = customerIdHeader.getText();
        Customer customer = CustomerUtil.fromOM(resource);

        storage.put(customerId, customer);

        return null;
    }


}
