/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package ms.integration.tests.mashupadmin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.test.ASIntegrationTest;
import org.wso2.carbon.mashup.jsservices.stub.CarbonExceptionException;
import org.wso2.carbon.mashup.jsservices.stub.MashupServiceAdminStub;

import java.rmi.RemoteException;

import static org.testng.Assert.assertTrue;

public class MashupAdminTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(MashupAdminTestCase.class);
    private MashupServiceAdminStub msServiceStub;
    private String serviceName;
    private String type;
    private String contents;
    private String[] serviceNames;

    @BeforeClass(alwaysRun = true)
    public void initialize() throws Exception {
        super.init();
    }

    @AfterClass(alwaysRun = true)
    public void serviceDelete() throws Exception {
        if (isServiceDeployed("admin/kll")) {
            deleteService("admin/kll");   // deleting scrapperTest from the services list
            log.info("admin/kll service deleted");
        }
    }

    @BeforeMethod(groups = {"wso2.as"})
    public void init() throws Exception {
        log.debug("MashupAdminTestCase Initialised");

        log.debug("Running SuccessCase");
        msServiceStub = MashupTestUtils.getMashupServiceAdminStub(asServer);

        serviceName = "admin/hello";
        type = "js";
        contents = "LyoKKiBMaWNlbnNlZCB1bmRlciB0aGUgQXBhY2hlIExpY2Vuc2Us" +
                "IFZlcnNpb24gMi4wICh0aGUgIkxpY2Vuc2UiKTsKKiB5b3UgbWF5" +
                "IG5vdCB1c2UgdGhpcyBmaWxlIGV4Y2VwdCBpbiBjb21wbGlhbmNl" +
                "IHdpdGggdGhlIExpY2Vuc2UuCiogWW91IG1heSBvYnRhaW4gYSBj" +
                "b3B5IG9mIHRoZSBMaWNlbnNlIGF0CioKKiBodHRwOi8vd3d3LmFw" +
                "YWNoZS5vcmcvbGljZW5zZXMvTElDRU5TRS0yLjAKKgoqIFVubGVz" +
                "cyByZXF1aXJlZCBieSBhcHBsaWNhYmxlIGxhdyBvciBhZ3JlZWQg" +
                "dG8gaW4gd3JpdGluZywgc29mdHdhcmUKKiBkaXN0cmlidXRlZCB1" +
                "bmRlciB0aGUgTGljZW5zZSBpcyBkaXN0cmlidXRlZCBvbiBhbiAi" +
                "QVMgSVMiIEJBU0lTLAoqIFdJVEhPVVQgV0FSUkFOVElFUyBPUiBD" +
                "T05ESVRJT05TIE9GIEFOWSBLSU5ELCBlaXRoZXIgZXhwcmVzcyBv" +
                "ciBpbXBsaWVkLgoqIFNlZSB0aGUgTGljZW5zZSBmb3IgdGhlIHNw" +
                "ZWNpZmljIGxhbmd1YWdlIGdvdmVybmluZyBwZXJtaXNzaW9ucyBh" +
                "bmQKKiBsaW1pdGF0aW9ucyB1bmRlciB0aGUgTGljZW5zZS4KKi8K" +
                "dGhpcy5zZXJ2aWNlTmFtZSA9ICJrbGwiOwp0aGlzLmRvY3VtZW50" +
                "YXRpb24gPSAiVE9ETzogQWRkIHNlcnZpY2UgbGV2ZWwgZG9jdW1l" +
                "bnRhdGlvbiBoZXJlIiA7Cgp0b1N0cmluZy5kb2N1bWVudGF0aW9u" +
                "ID0gIlRPRE86IEFkZCBvcGVyYXRpb24gbGV2ZWwgZG9jdW1lbnRh" +
                "dGlvbiBoZXJlIiA7CnRvU3RyaW5nLmlucHV0VHlwZXMgPSB7IC8q" +
                "IFRPRE86IEFkZCBpbnB1dCB0eXBlcyBvZiB0aGlzIG9wZXJhdGlv" +
                "biAqLyB9Owp0b1N0cmluZy5vdXRwdXRUeXBlID0gIlN0cmluZyI7" +
                "IC8qIFRPRE86IEFkZCBvdXRwdXQgdHlwZSBoZXJlICovIApmdW5j" +
                "dGlvbiB0b1N0cmluZygpCnsKICAgLy9UT0RPOiBBZGQgZnVuY3Rp" +
                "b24gY29kZSBoZXJlCiAgIHJldHVybiAiSGksIG15IG5hbWUgaXMg" +
                "a2xsIjsKfQoKICAgICAgICAgICAgICAgICAgICAgICAg";

        serviceNames = new String[]{"admin/digit2image", "admin/hello"};
    }

    // Save mashup service
    @Test(groups = {"wso2.as"}, description = "Save mashup service")
    public void testSaveMashupServiceSource() throws RemoteException, CarbonExceptionException {
        boolean save = msServiceStub.saveMashupServiceSource(serviceName, type, contents);
        assertTrue(save, "Failed to save a mashup service from saveMashupServiceSource test");
        log.info("Successfully executed saveMashupServiceSource test");
    }

    // Retrieve mashup service content
    @Test(groups = {"wso2.as"}, description = "Retrieve mashup service content",
            dependsOnMethods = "testSaveMashupServiceSource"
    )
    public void testGetMashupServiceContentAsString() throws RemoteException {
        String[] mashupContent = msServiceStub.getMashupServiceContentAsString(serviceName);
        if (mashupContent != null) {
            for (String content : mashupContent) {
                log.info("Successfully executed getMashupServiceContentAsString Test");
            }
        } else {
            log.error("Failed to execute getMashupServiceContentAsString Test");
        }
    }

    // Check a service exists/not
    @Test(groups = {"wso2.as"}, description = "Check a service exists/not",
            dependsOnMethods = "testSaveMashupServiceSource")
    public void testDoesServiceExists() throws RemoteException, CarbonExceptionException {
        boolean exist = msServiceStub.doesServiceExists(serviceName);
        assertTrue(exist, "Service called" + serviceName + " doesn't exist in doesServiceExists");
        log.info("Successfully executed doesServiceExists Test");
    }

    // Check a service group exists/not
    @Test(groups = {"wso2.as"}, description = "Check a service group exists/not",
            dependsOnMethods = "testSaveMashupServiceSource")
    public void testDoesServicesExist() throws RemoteException, CarbonExceptionException {
        String[] existServices = msServiceStub.doesServicesExists(serviceNames);
        if (existServices != null) {
            for (String serviceName : existServices) {
                log.info("Service : " + serviceName + " -exists.");
            }
        } else {
            log.error("Not any service exists in doesServicesExist");
        }
        log.info("Successfully executed doesServicesExists test");
    }

    // Get backend http port
    @Test(groups = {"wso2.as"}, description = "Check a service group exists/not"
            , dependsOnMethods = "testDoesServicesExist")
    public void getBackendHttpPort() throws RemoteException, CarbonExceptionException {
        String portName = msServiceStub.getBackendHttpPort();
        if (portName == null) {
            log.error("Port name retrieved as null from getBackendHttpPort test");
        } else {
            log.info("Successfully executed getBackendHttpPort");
        }
    }
}
