/*
* Copyright 2004,2013 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.appserver.integration.tests.javaee;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;

public class JpaJaxRsTestCase extends WebappDeploymentTestCase {

    private static final Log log = LogFactory.getLog(CdiServletTestCase.class);
    private static final String webAppFileName = "jpa-student-register-1.0.war";
    private static final String webAppName = "jpa-student-register-1.0";
    private static final String webAppLocalURL ="/jpa-student-register-1.0";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init();
        setWebAppFileName(webAppFileName);
        setWebAppName(webAppName);
        setWebAppURL(getWebAppURL() + webAppLocalURL);
    }

    /**
     * ex. getall response in xml
     *
     * <Students><students><index>100</index><name>John</name></students></Students>
     *
     */
    @Test(groups = "wso2.as", description = "test jpa and jax-rs", dependsOnMethods = "webApplicationDeploymentTest")
    public void testJpaRsGet() throws Exception {

        String getAll = "/student/getall";
        String jndiUrl = getWebAppURL() + getAll;
        OMElement result = runAndGetResultAsOM(jndiUrl);

        log.info("Response - " + result.toString());
        OMElement students = result.getFirstElement();
        assertEquals(students.getLocalName(), "students", "response is invalid for " + jndiUrl);
        assertEquals(students.getFirstChildWithName(new QName("index")).toString(), "<index>100</index>",
                     "response is invalid for " + jndiUrl);
        assertEquals(students.getFirstChildWithName(new QName("name")).toString(), "<name>John</name>",
                     "response is invalid for " + jndiUrl);
    }
}
