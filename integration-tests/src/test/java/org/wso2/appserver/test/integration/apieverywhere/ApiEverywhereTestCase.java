package org.wso2.appserver.test.integration.apieverywhere;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.appserver.test.integration.TestBase;
import org.wso2.appserver.test.integration.TestConstants;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This test case checks the creating api in API Publisher.
 *
 * @since 6.0.0
 */
public class ApiEverywhereTestCase extends TestBase {

    @Test(description = "Test for jaxrs_basic-6.0.0-SNAPSHOT web app is running")
    public void testAPICreation() throws IOException {

        URL requestUrlGet = new URL(getBaseUrl() +
                "/jaxrs_basic-6.0.0-SNAPSHOT/services/customerservice/customers/123");
        HttpURLConnection connectionGet = (HttpURLConnection) requestUrlGet.openConnection();
        connectionGet.setRequestMethod(TestConstants.HTTP_GET_METHOD);

        int responseCodeGet = connectionGet.getResponseCode();
        Assert.assertEquals(responseCodeGet, 200, "Error in accessing the jaxrs_basic-6.0.0-SNAPSHOT web app");
    }

}
