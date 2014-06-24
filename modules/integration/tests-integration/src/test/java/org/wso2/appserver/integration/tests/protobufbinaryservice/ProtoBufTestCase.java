package org.wso2.appserver.integration.tests.protobufbinaryservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.common.clients.WebAppAdminClient;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.appserver.integration.common.utils.WebAppDeploymentUtil;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;
import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ProtoBufTestCase extends ASIntegrationTest{
    private static final Log log = LogFactory.getLog(ProtoBufTestCase.class);
    private ServerConfigurationManager serverManager = null;
    private WebAppAdminClient webAppAdminClient;
    private final String webAppFileName = "simplestockquote.war";
    private final String webAppName = "simplestockquote";

    private final String PROTOBUF_CONFIG ="pbs.xml";



    @BeforeClass(alwaysRun = true)
    public void init() throws Exception, IOException, LoginAuthenticationExceptionException, SAXException, URISyntaxException, XPathExpressionException {
        super.init();

        serverManager = new ServerConfigurationManager(asServer);


        serverManager.applyConfigurationWithoutRestart(new File(TestConfigurationProvider.getResourceLocation() +
                File.separator + "artifacts" + File.separator + "AS" + File.separator + "protodependencies" +
                File.separator + "conf" + File.separator + PROTOBUF_CONFIG), new File(System.getProperty(ServerConstants.CARBON_HOME) +
                File.separator + "repository" + File.separator + "conf" + File.separator + "etc" + File.separator + PROTOBUF_CONFIG), true);

        serverManager.restartForcefully();
        super.init();
        webAppAdminClient = new WebAppAdminClient(backendURL,sessionCookie);
    }

    @Test(groups = "wso2.as", description = "Deploying protobuf service")
    public void testProtoBufServiceDeployment() throws Exception {
        webAppAdminClient.warFileUplaoder(FrameworkPathUtil.getSystemResourceLocation() +
                "artifacts" + File.separator + "AS" + File.separator + "war"
                + File.separator + "protobufservice" + File.separator + webAppFileName);


        assertTrue(WebAppDeploymentUtil.isWebApplicationDeployed(
                backendURL, sessionCookie, webAppName)
                , "Web Application Deployment failed");

    }


   @Test(groups = "wso2.as", description = "Invoke protobuf service",
            dependsOnMethods = "testProtoBufServiceDeployment")
    public void testInvokeProtoBufService() throws Exception {
        StockQuoteClient stockQuoteClient = new StockQuoteClient();
        stockQuoteClient.startClient();

       StockQuoteService.GetQuoteResponse quoteResponse =stockQuoteClient.getQuote();
       assertEquals(quoteResponse.getSymbol(),"IBM","Incorrect Response");
       assertEquals(quoteResponse.getName(),"IBM Company","Incorrect Response");

       StockQuoteService.GetFullQuoteResponse fullQuoteResponse=stockQuoteClient.getFullQuoteResponse();
       assertEquals(fullQuoteResponse.getTradeHistoryCount(),1000,"Incorrect Response");

       StockQuoteService.GetMarketActivityResponse marketActivityResponse=stockQuoteClient.getMarketActivityResponse();
       assertEquals(marketActivityResponse.getQuotesCount(),2,"Incorrect Response");
       assertEquals(marketActivityResponse.getQuotes(0).getSymbol(),"IBM","Incorrect Response");
       assertEquals(marketActivityResponse.getQuotes(1).getSymbol(),"SUN","Incorrect Response");

    }





    @AfterClass(alwaysRun = true)
    public void stop() throws Exception, IOException, URISyntaxException {
        Thread.sleep(10000); //let server to clear the artifact undeployment
        if (serverManager != null) {
            webAppAdminClient.deleteWebAppFile(webAppFileName);
            assertTrue(WebAppDeploymentUtil.isWebApplicationUnDeployed(
                    backendURL, sessionCookie, webAppName),
                    "Web Application unDeployment failed");

            serverManager.restoreToLastConfiguration();
        }
    }

}
