package org.wso2.carbon.integration.test.helloworldsampleservice;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.automation.utils.axis2client.SecureAxisServiceClient;
import org.wso2.carbon.integration.test.ASIntegrationTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/*
  This class can be used to invoke a deployed service with pre defined security  policies
 */

public class HelloWorldSampleTestCase extends ASIntegrationTest {

    private static final Log log = LogFactory.getLog(HelloWorldSampleTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.as", description = "invoke HelloWorld service without security")
    public void InvokeSerWithoutSec() throws Exception {
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        String endpoint = asServer.getServiceUrl() + "/HelloService";
        OMElement response = axisServiceClient.sendReceive(createPayLoad(), endpoint, "greet");
        log.info("Response for Invoke Service Without Security : " + response);
        assertTrue(response.toString().contains("<ns:greetResponse xmlns:ns=" +
                "\"http://www.wso2.org/types\"><return>Hello World, Hello Without Security !!!" +
                "</return></ns:greetResponse>"));
    }

    @Test(groups = "wso2.as", description = "invoke HelloWorld service with security",
            dependsOnMethods = "InvokeSerWithoutSec")
    public void InvokeSerWithSec() throws Exception {
        SecureAxisServiceClient secAxisSerClient = new SecureAxisServiceClient();
        for (int x = 1; x <= 15; x++) {
            applySecurity(Integer.toString(x), "HelloService", ProductConstant.DEFAULT_PRODUCT_ROLE);
            OMElement result = secAxisSerClient.sendReceive(userInfo.getUserName(),
                    userInfo.getPassword(), asServer.getBackEndUrl() + "HelloService", "greet",
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





