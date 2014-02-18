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

package org.wso2.carbon.integration.test.ui.webapp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.api.selenium.webapp.list.WebAppListPage;
import org.wso2.carbon.automation.core.BrowserManager;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.integration.test.ui.ASIntegrationUiTestCase;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class JSPSamplesTestCase extends ASIntegrationUiTestCase {
    private WebDriver driver;
    private static final Log log = LogFactory.getLog(JSPSamplesTestCase.class);

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        super.init();
        driver = BrowserManager.getWebDriver();
        driver.get(getLoginURL(ProductConstant.APP_SERVER_NAME));
        LoginPage test = new LoginPage(driver, isRunningOnCloud());
        test.loginAs(userInfo.getUserName(), userInfo.getPassword());
        new WebAppListPage(driver);
    }

    @Test(groups = "wso2.as", description = "Verify example:Expression Language")
    public void testExpressionLanguageExample() throws Exception {
        driver.get(asServer.getWebAppURL() + "/example/jsp/");
        assertEquals(driver.getTitle(), "JSP Examples");
        driver.findElement(By.xpath("/html/body/p[4]/table/tbody/tr[2]/td[2]/a[2]")).click();
        assertEquals(driver.getCurrentUrl(), asServer.getWebAppURL() +
                                             "/example/jsp/jsp2/el/basic-arithmetic.jsp");
        WebElement table_element = driver.findElement(By.xpath("/html/body/blockquote/code/table"));

        Map<String, List<String>> inputTextMap = new HashMap<String, List<String>>();
        List<String> arrList1 = new ArrayList<String>();
        arrList1.add("12001.4");
        inputTextMap.put("${1.2E4 + 1.4}", arrList1);
        List<String> arrList2 = new ArrayList<String>();
        arrList2.add("Infinity");
        inputTextMap.put("${3/0}", arrList2);

        assertTrue(checkThroughTable(table_element, inputTextMap));

    }

    @Test(groups = "wso2.as", description = "Verify example: Expression Language - Composite Expressions")
    public void testCompositeExpressionExample() throws Exception {
        driver.get(asServer.getWebAppURL() + "/example/jsp/");
        assertEquals(driver.getTitle(), "JSP Examples");
        driver.findElement(By.xpath("/html/body/p[4]/table/tbody/tr[6]/td[2]/a[2]")).click();
        assertEquals(driver.getCurrentUrl(), asServer.getWebAppURL() +
                                             "/example/jsp/jsp2/el/composite.jsp");
        WebElement table_element = driver.findElement(By.xpath("/html/body/blockquote/code/table"));

        Map<String, List<String>> inputTextMap = new HashMap<String, List<String>>();
        List<String> arrList1 = new ArrayList<String>();
        arrList1.add("String");
        arrList1.add("hello world");
        inputTextMap.put("${'hello'} wo${'rld'}", arrList1);
        List<String> arrList2 = new ArrayList<String>();
        arrList2.add("String");
        arrList2.add("hello world");
        inputTextMap.put("${undefinedFoo}hello world${undefinedBar}", arrList2);

        assertTrue(checkThroughTable(table_element, inputTextMap));

    }

    @Test(groups = "wso2.as", description = "Verify example: SimpleTag Handlers and JSP Fragments - Book example")
    public void simpleTagHandlerExample() throws Exception {
        driver.get(asServer.getWebAppURL() + "/example/jsp/");
        assertEquals(driver.getTitle(), "JSP Examples");
        driver.findElement(By.xpath("/html/body/p[4]/table/tbody/tr[10]/td[2]/a[2]")).click();
        assertEquals(driver.getCurrentUrl(), asServer.getWebAppURL() +
                                             "/example/jsp/jsp2/simpletag/book.jsp");
        WebElement table_element = driver.findElement(By.xpath("/html/body/table"));

        Map<String, List<String>> inputTextMap = new HashMap<String, List<String>>();
        List<String> arrList1 = new ArrayList<String>();
        arrList1.add("J. R. R. Tolkein");
        arrList1.add("J. R. R. TOLKEIN");
        inputTextMap.put("Author", arrList1);
        List<String> arrList2 = new ArrayList<String>();
        arrList2.add("0618002251");
        arrList2.add("0618002251");
        inputTextMap.put("ISBN", arrList2);

        assertTrue(checkThroughTable(table_element, inputTextMap));

    }

    @Test(groups = "wso2.as", description = "Verify example: Tag Files - Display Products Example")
    public void tagFilesExample() throws Exception {
        driver.get(asServer.getWebAppURL() + "/example/jsp/");
        assertEquals(driver.getTitle(), "JSP Examples");
        driver.findElement(By.xpath("/html/body/p[4]/table/tbody/tr[14]/td[2]/a[2]")).click();
        assertEquals(driver.getCurrentUrl(), asServer.getWebAppURL() +
                                             "/example/jsp/jsp2/tagfiles/products.jsp");

        assertTrue(driver.getPageSource().contains("Item: Hand-held Color PDA"));

    }

    @Test(groups = "wso2.as", description = "Verify example: New JSP XML Syntax (.jspx) - XHTML Basic Example")
    public void xhtmlBasicExample() throws Exception {
        driver.get(asServer.getWebAppURL() + "/example/jsp/");
        assertEquals(driver.getTitle(), "JSP Examples");
        driver.findElement(By.xpath("/html/body/p[4]/table/tbody/tr[16]/td[2]/a[2]")).click();
        assertEquals(driver.getCurrentUrl(), asServer.getWebAppURL() +
                                             "/example/jsp/jsp2/jspx/basic.jspx");

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("MMMM d, yyyy, H:mm");

        log.info("DATE TEST : " + ft.format(dNow));

        assertTrue(driver.getPageSource().contains(ft.format(dNow)));

    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        driver.quit();
    }

    private boolean checkThroughTable(WebElement table_element,
                                      Map<String, List<String>> inputTextMap) {

        List<WebElement> tr_collection = table_element.findElements(By.tagName("tr"));

        log.info("NUMBER OF ROWS IN THIS TABLE = " + tr_collection.size());
        int row_num, col_num, matches;
        matches = 0;
        row_num = 1;

        log.info("TEST = " + inputTextMap);

        for (WebElement trElement : tr_collection) {
            List<WebElement> td_collection = trElement.findElements(By.xpath("td"));
            col_num = 1;

            List<String> tdStringList;

            if ((tdStringList = inputTextMap.get(td_collection.get(0).getText())) != null) {
                int tdNum = 0;
                for (WebElement tdElement : td_collection.subList(1, td_collection.size())) {
                    log.info("row # " + row_num + ", col # " + col_num + " text=" + tdElement.getText());

                    //for(String s:tdStringList){
                    if (tdElement.getText().equals(tdStringList.get(tdNum))) {
                        log.info("Match : row # " + row_num + ", col # " + col_num);
                    } else {
                        log.info("Missmatch : row # " + row_num + ", col # " + col_num);
                        return false;
                    }
                    //}
                    tdNum++;
                    col_num++;
                }
                matches++;
            }

            row_num++;
        }

        if (matches >= inputTextMap.size()) {
            return true;
        } else {
            return false;
        }
    }

}
