package org.wso2.appserver.integration.tests.helloworldsampleservice;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.AARServiceUploaderClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.integration.common.utils.clients.SecureAxisServiceClient;
import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/*
  This class can be used to invoke a deployed service with pre defined security  policies
 */

public class HelloWorldSampleTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(HelloWorldSampleTestCase.class);
    private TestUserMode userMode;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(userMode);
    }

    @Factory(dataProvider = "userModeProvider")
    public HelloWorldSampleTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    private static TestUserMode[][] userModeProvider() {
        return new TestUserMode[][]{
                new TestUserMode[]{TestUserMode.SUPER_TENANT_ADMIN},
        };
    }

    @Test(groups = "wso2.as", description = "Upload aar service and verify deployment")
    public void testHelloServiceUpload() throws Exception {
        AARServiceUploaderClient aarServiceUploaderClient
                = new AARServiceUploaderClient(backendURL, sessionCookie);
        aarServiceUploaderClient.uploadAARFile("HelloWorld.aar",
                FrameworkPathUtil.getSystemResourceLocation() + "artifacts" +
                        File.separator + "AS" + File.separator + "aar" + File.separator +
                        "HelloWorld.aar", "");
        String axis2Service = "HelloService";
        isServiceDeployed(axis2Service);
        log.info("HelloWorld.aar service uploaded successfully");
    }

    @Test(groups = "wso2.as", description = "invoke HelloWorld service without security",
            dependsOnMethods = "testHelloServiceUpload")
    public void InvokeSerWithoutSec() throws Exception {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        String endpoint = asServer.getContextUrls().getServiceUrl() + "/HelloService";
        OMElement response = axisServiceClient.sendReceive(createPayLoad(), endpoint, "greet");
        log.info("Response for Invoke Service Without Security : " + response);
        assertTrue(response.toString().contains("<ns:greetResponse xmlns:ns=" +
                "\"http://www.wso2.org/types\"><return>Hello World, Hello Without Security !!!" +
                "</return></ns:greetResponse>"));
    }

    //FIXME: WSAS-1747
    @Test(groups = "wso2.as", description = "invoke HelloWorld service with security",
            dependsOnMethods = "InvokeSerWithoutSec", enabled = false)
    public void InvokeSerWithSec() throws Exception {
        SecureAxisServiceClient secAxisSerClient = new SecureAxisServiceClient();
        for (int x = 1; x <= 15; x++) {
            applySecurity(Integer.toString(x), "HelloService", FrameworkConstants.ADMIN_ROLE);
            OMElement result = secAxisSerClient.sendReceive(asServer.getSuperTenant().getTenantAdmin().getUserName(),
                    asServer.getSuperTenant().getTenantAdmin().getPassword(), backendURL + "HelloService", "greet",
                    createPayLoadSec(), x);
            log.info("Response for Invoke Service With Security : " + result.getFirstElement().getText());
            assertEquals("<ns:greetResponse xmlns:ns=\"http://www.wso2.org/types\"><return>Hello World, " +
                    "Hello With Security !!!</return></ns:greetResponse>",
                    result.toString().trim());
        }
    }

    public static OMElement createPayLoad() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://www.wso2.org/types", "ns");
        OMElement getOmeOne = fac.createOMElement("greet", omNs);
        OMElement getOmeTwo = fac.createOMElement("name", omNs);
        getOmeTwo.setText("Hello Without Security");
        getOmeOne.addChild(getOmeTwo);
        return getOmeOne;
    }

    public static OMElement createPayLoadSec() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://www.wso2.org/types", "ns");
        OMElement getOmeOne = fac.createOMElement("greet", omNs);
        OMElement getOmeTwo = fac.createOMElement("name", omNs);
        getOmeTwo.setText("Hello With Security");
        getOmeOne.addChild(getOmeTwo);
        return getOmeOne;
    }

}





