package org.wso2.appserver.test.integration.apieverywhere;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.appserver.test.integration.TestBase;
import org.wso2.appserver.test.integration.TestConstants;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;

/**
 * This test case checks the creating api in API Publisher.
 *
 * @since 6.0.0
 */
public class ApiEverywhereTestCase extends TestBase {

    private static final Logger log = LoggerFactory.getLogger(ApiEverywhereTestCase.class);

    @Test(description = "test if the api creation working.......................")
    public void testAPICreation() throws IOException {
        File appserverHome = new File(System.getProperty(TestConstants.APPSERVER_HOME));
        FileUtils.copyDirectory(
                Paths.get(appserverHome.toString(), "samples", "jaxrs_basic", "target", "jaxrs_basic.war").
                        toFile(), Paths.get(appserverHome.toString(), "webapps").toFile());

        for (int i = 0; i < 100; i++) {
            log.info("wating : " + i);
        }
        URL requestUrlGet = new URL(getBaseUrl() + "jaxrs_basic/services/customerservice/customers/123");
        HttpURLConnection connectionGet = (HttpURLConnection) requestUrlGet.openConnection();
        connectionGet.setRequestMethod(TestConstants.HTTP_GET_METHOD);

        int responseCodeGet = connectionGet.getResponseCode();
        Assert.assertEquals(responseCodeGet, 200, "Server Response Code");
    }

}
