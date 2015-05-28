package org.wso2.carbon.integration.test.patches.security;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.jmeter.JMeterTest;
import org.wso2.carbon.automation.extensions.jmeter.JMeterTestManager;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Date;

public class CARBON15255SecuringCookieParamsTest extends ASIntegrationTest {

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
    }

    @Test(groups = "wso2.as", description = "login and read session cookie")
    public void testCookieInLogin() throws Exception {
        File file = new File(FrameworkPathUtil.getSystemResourceLocation() + File.separator + "artifacts" +
                File.separator + "AS" + File.separator + "jmeter" + File.separator + "CARBON15255SecuringCookieParamsTest.jmx");

        JMeterTest script = new JMeterTest(file);
        JMeterTestManager manager = new JMeterTestManager();
        manager.runTest(script);
    }
}
