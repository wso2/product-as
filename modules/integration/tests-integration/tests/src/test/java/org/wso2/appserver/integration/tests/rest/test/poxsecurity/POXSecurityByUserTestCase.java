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

package org.wso2.appserver.integration.tests.rest.test.poxsecurity;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appserver.integration.tests.ASTestConstants;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.appserver.integration.common.utils.ASIntegrationTest;
import org.wso2.carbon.automation.test.utils.http.client.HttpsResponse;
import org.wso2.carbon.automation.test.utils.http.client.HttpsURLConnectionClient;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import static org.testng.Assert.assertTrue;

/*
check all GET, PUT, DELETE and POST HTTPS methods with pox security by admin user.
*/
public class POXSecurityByUserTestCase extends ASIntegrationTest {
    private static String USER_GROUP = "everyone";
    private static final String SERVICE_NAME = "StudentService";
    private static final String studentName = "automationStudent";

    @BeforeClass(alwaysRun = true, enabled = false)
    public void init() throws Exception {
        super.init();
        applySecurity("1", SERVICE_NAME, ASTestConstants.POX_ROLE_NAME);
    }


    @Test(groups = {"wso2.as"}, description = "POST request by valid user", enabled = false)
    public void testAddNewStudent() throws IOException,
            LoginAuthenticationExceptionException,
            XMLStreamException, XPathExpressionException {

        String addStudentData = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" +
                                "   <p:addStudent xmlns:p=\"http://axis2.apache.org\">\n" +
                                "      <!--0 to 1 occurrence-->\n" +
                                "      <ns:student xmlns:ns=\"http://axis2.apache.org\">\n" +
                                "         <!--0 to 1 occurrence-->\n" +
                                "         <xs:age xmlns:xs=\"http://axis2.apache.org\">100</xs:age>\n" +
                                "         <!--0 to 1 occurrence-->\n" +
                                "         <xs:name xmlns:xs=\"http://axis2.apache.org\">" + studentName + "</xs:name>\n" +
                                "         <!--0 or more occurrences-->\n" +
                                "         <xs:subjects xmlns:xs=\"http://axis2.apache.org\">testAutomation</xs:subjects>\n" +
                                "      </ns:student>\n" +
                                "   </p:addStudent>";


        String securedRestURL = getSecuredServiceEndpoint(SERVICE_NAME) + "/students";
        HttpsResponse response = HttpsURLConnectionClient.postWithBasicAuth(securedRestURL, addStudentData,
                "application/xml", ASTestConstants.POX_USER, ASTestConstants.POX_USER_PASSWORD);
        assertTrue(response.getData().contains(studentName)
                , "response doesn't contain the expected output");

        //check whether the student is added.
        String studentGetUri = getSecuredServiceEndpoint(SERVICE_NAME) + "/student/" + studentName;
        HttpsResponse getResponse =
                HttpsURLConnectionClient.getWithBasicAuth(studentGetUri, null, ASTestConstants.POX_USER,
                        ASTestConstants.POX_USER_PASSWORD);
        assertTrue(getResponse.getData().contains("<ns:getStudentResponse xmlns:ns=\"http://axis2.apache.org\"><ns:return>" +
                                                  "<ns:age>100</ns:age>" +
                                                  "<ns:name>" + studentName + "</ns:name>" +
                                                  "<ns:subjects>testAutomation</ns:subjects>" +
                                                  "</ns:return></ns:getStudentResponse>"));

    }

    @Test(groups = {"wso2.as"}, description = "PUT request by valid user", dependsOnMethods = "testAddNewStudent", enabled = false)
    public void testUpdateStudent() throws IOException,
            LoginAuthenticationExceptionException,
            XMLStreamException, XPathExpressionException {

        String updateStudentData = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" +
                                   "<p:updateStudent xmlns:p=\"http://axis2.apache.org\">\n" +
                                   "      <!--0 to 1 occurrence-->\n" +
                                   "      <ns:student xmlns:ns=\"http://axis2.apache.org\">\n" +
                                   "         <!--0 to 1 occurrence-->\n" +
                                   "         <xs:age xmlns:xs=\"http://axis2.apache.org\">999</xs:age>\n" +
                                   "         <!--0 to 1 occurrence-->\n" +
                                   "         <xs:name xmlns:xs=\"http://axis2.apache.org\">" + studentName + "</xs:name>\n" +
                                   "         <!--0 or more occurrences-->\n" +
                                   "         <xs:subjects xmlns:xs=\"http://axis2.apache.org\">testAutomationUpdated</xs:subjects>\n" +
                                   "      </ns:student>\n" +
                                   "</p:updateStudent>";

        String securedRestURL = getSecuredServiceEndpoint(SERVICE_NAME) + "/student/" + studentName;
        HttpsResponse response = HttpsURLConnectionClient.putWithBasicAuth(securedRestURL, updateStudentData,
                                                                           "application/xml", ASTestConstants.POX_USER,
                ASTestConstants.POX_USER_PASSWORD);

        assertTrue(response.getData().contains(studentName)
                , "response doesn't contain the expected output");

        //check whether the student is added.
        String studentGetUri = getSecuredServiceEndpoint(SERVICE_NAME) + "/student/" + studentName;
        HttpsResponse getResponse =
                HttpsURLConnectionClient.getWithBasicAuth(studentGetUri, null, ASTestConstants.POX_USER,
                        ASTestConstants.POX_USER_PASSWORD);
        assertTrue(getResponse.getData().contains("<ns:getStudentResponse xmlns:ns=\"http://axis2.apache.org\"><ns:return>" +
                                                  "<ns:age>999</ns:age>" +
                                                  "<ns:name>" + studentName + "</ns:name>" +
                                                  "<ns:subjects>testAutomationUpdated</ns:subjects>" +
                                                  "</ns:return></ns:getStudentResponse>"));
    }

    @Test(groups = {"wso2.as"}, description = "DELETE request by valid user", dependsOnMethods = "testUpdateStudent", enabled = false)
    public void testDeleteStudent() throws IOException,
            LoginAuthenticationExceptionException,
            XMLStreamException, XPathExpressionException {


        String deleteStudentData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                   "<p:deleteStudent xmlns:p=\"http://axis2.apache.org\">\n" +
                                   "      <!--0 to 1 occurrence-->\n" +
                                   "      <xs:name xmlns:xs=\"http://axis2.apache.org\">" + studentName + "</xs:name>\n" +
                                   "</p:deleteStudent>";

        String securedRestURL = getSecuredServiceEndpoint(SERVICE_NAME) + "/student/" + studentName;
        HttpsResponse response =
                HttpsURLConnectionClient.deleteWithBasicAuth(securedRestURL, null, ASTestConstants.POX_USER,
        ASTestConstants.POX_USER_PASSWORD);
        assertTrue(!response.getData().contains(studentName)
                , "response doesn't contain the expected output");
    }

    @Test(groups = {"wso2.as"}, description = "GET resource after delete by valid user",
          dependsOnMethods = "testDeleteStudent", expectedExceptions = IOException.class, enabled = false)
    public void testGetResourceAfterDelete()
            throws IOException,
            LoginAuthenticationExceptionException,
            XMLStreamException, XPathExpressionException {

        //check whether the student is deleted
        String studentGetUri = getSecuredServiceEndpoint(SERVICE_NAME) + "/student/" + studentName;
        HttpsResponse getResponse =
                HttpsURLConnectionClient.getWithBasicAuth(studentGetUri, null, ASTestConstants.POX_USER,
                        ASTestConstants.POX_USER_PASSWORD);
        assertTrue(getResponse.getData().equals(""), "student was not deleted");
    }

    @AfterClass(alwaysRun = true, enabled = false)
    public void destroy() throws Exception {
        securityAdminServiceClient.disableSecurity(SERVICE_NAME);
        super.cleanup();
    }
}
