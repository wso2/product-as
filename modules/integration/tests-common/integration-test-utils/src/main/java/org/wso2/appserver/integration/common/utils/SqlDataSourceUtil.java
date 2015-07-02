/*
 *   Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

/**
 * Created by Niranjan Karunanandham on 6/25/15.
 */
package org.wso2.appserver.integration.common.utils;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.extensions.XPathConstants;
import org.wso2.carbon.automation.test.utils.common.FileManager;
import org.wso2.carbon.automation.test.utils.dbutils.DatabaseFactory;
import org.wso2.carbon.automation.test.utils.dbutils.DatabaseManager;
import org.wso2.carbon.automation.test.utils.dbutils.H2DataBaseManager;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SqlDataSourceUtil {

    private static final Log log = LogFactory.getLog(SqlDataSourceUtil.class);

    private String jdbcUrl = null;
    private String jdbcDriver = null;
    private String databaseName;
    private String databaseUser;
    private String databasePassword;
    private AutomationContext automationContext;
    private String aSBackEndUrl;
    private String sessionCookie;
    private final String userPrivilegeGroupName = "automation";

    public SqlDataSourceUtil(String sessionCookie, String backEndUrl) {
        this.sessionCookie = sessionCookie;
        this.aSBackEndUrl = backEndUrl;
    }

    protected void init() throws XPathExpressionException {
        automationContext = new AutomationContext();
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public String getDatabaseUser() {
        return this.databaseUser;
    }

    public String getDatabasePassword() {
        return this.databasePassword;
    }

    public int getDatabaseUserId() {
        return -1;
    }

    public String getJdbcUrl() {
        return this.jdbcUrl;
    }

    public String getDriver() {
        return this.jdbcDriver;
    }

    public DataHandler createArtifact(String dbsFilePath)
            throws XMLStreamException, IOException, XPathExpressionException {

        if (automationContext == null) {
            init();
        }

        Assert.assertNotNull(jdbcUrl, "Initialize jdbcUrl");
        try {
            OMElement dbsFile = AXIOMUtil.stringToOM(FileManager.readFile(dbsFilePath));
            OMElement dbsConfig = dbsFile.getFirstChildWithName(new QName("config"));
            Iterator configElement1 = dbsConfig.getChildElements();
            while (configElement1.hasNext()) {
                OMElement property = (OMElement) configElement1.next();
                String value = property.getAttributeValue(new QName("name"));
                if ("org.wso2.ws.dataservice.protocol".equals(value)) {
                    property.setText(jdbcUrl);
                } else if ("org.wso2.ws.dataservice.driver".equals(value)) {
                    property.setText(jdbcDriver);
                } else if ("org.wso2.ws.dataservice.user".equals(value)) {
                    property.setText(databaseUser);
                } else if ("org.wso2.ws.dataservice.password".equals(value)) {
                    property.setText(databasePassword);
                }
            }
            log.debug(dbsFile);
            ByteArrayDataSource dbs = new ByteArrayDataSource(dbsFile.toString().getBytes());
            return new DataHandler(dbs);
        } catch (XMLStreamException e) {
            log.error("XMLStreamException when Reading Service File", e);
            throw new XMLStreamException("XMLStreamException when Reading Service File", e);
        } catch (IOException e) {
            log.error("IOException when Reading Service File", e);
            throw new IOException("IOException  when Reading Service File", e);
        }
    }

    private void createDataBase(String driver, String jdbc, String user, String password)
            throws ClassNotFoundException, SQLException, XPathExpressionException {

        if (automationContext == null) {
            init();
        }

        try {
            DatabaseManager dbm = DatabaseFactory.getDatabaseConnector(driver, jdbc, user, password);
            dbm.executeUpdate("DROP DATABASE IF EXISTS " + databaseName);
            dbm.executeUpdate("CREATE DATABASE " + databaseName);
            jdbcUrl = jdbc + "/" + databaseName;

            dbm.disconnect();
        } catch (ClassNotFoundException e) {
            log.error("Class Not Found. Check MySql-jdbc Driver in classpath: ", e);
            throw new ClassNotFoundException("Class Not Found. Check MySql-jdbc Driver in classpath: ", e);
        } catch (SQLException e) {
            log.error("SQLException When executing SQL: ", e);
            throw new SQLException("SQLException When executing SQL: ", e);
        }
    }

    public void createDataSource(List<File> sqlFileList) throws Exception {

        if (automationContext == null) {
            init();
        }
        databaseName = "testdb";
        databasePassword = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_PASSWORD);
        jdbcUrl = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_URL);
        jdbcDriver = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DRIVER_CLASS_NAME);
        databaseUser = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_USER_NAME);

        if (jdbcUrl.contains("h2") && jdbcDriver.contains("h2")) {
            //Random number appends to a database name to create new database for H2*//*
            databaseName = System.getProperty("basedir") + File.separator + "target" + File.separator + databaseName +
                           new Random().nextInt();
            jdbcUrl = jdbcUrl + databaseName;
            //create database on in-memory
            H2DataBaseManager h2 = null;
            try {
                h2 = new H2DataBaseManager(jdbcUrl, databaseUser, databasePassword);
                h2.executeUpdate("DROP ALL OBJECTS");
            } finally {
                if (h2 != null) {
                    h2.disconnect();
                }
            }
        } else {
            createDataBase(jdbcDriver, jdbcUrl, databaseUser, databasePassword);
        }
        executeUpdate(sqlFileList);
    }

    public void createDataSource(String dbName, List<File> sqlFileList) throws Exception {
        if (automationContext == null) {
            init();
        }
        databaseName = dbName;
        databasePassword = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_PASSWORD);
        jdbcUrl = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_URL);
        jdbcDriver = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DRIVER_CLASS_NAME);
        databaseUser = automationContext.getConfigurationValue(XPathConstants.DATA_SOURCE_DB_USER_NAME);

        if (jdbcUrl.contains("h2") && jdbcDriver.contains("h2")) {
            //Random number appends to a database name to create new database for H2*//*
            databaseName = System.getProperty("basedir") + File.separator + "target" + File.separator + databaseName +
                           new Random().nextInt();
            jdbcUrl = jdbcUrl + databaseName;
            //create database on in-memory
            H2DataBaseManager h2 = null;
            try {
                h2 = new H2DataBaseManager(jdbcUrl, databaseUser, databasePassword);
                h2.executeUpdate("DROP ALL OBJECTS");
            } finally {
                if (h2 != null) {
                    h2.disconnect();
                }
            }
        } else {
            createDataBase(jdbcDriver, jdbcUrl, databaseUser, databasePassword);
        }
        executeUpdate(sqlFileList);
    }

    private void executeUpdate(List<File> sqlFileList)
            throws IOException, ClassNotFoundException, SQLException, XPathExpressionException {

        DatabaseManager dbm = null;
        try {
            dbm = DatabaseFactory.getDatabaseConnector(jdbcDriver, jdbcUrl, databaseUser, databasePassword);
            for (File sql : sqlFileList) {
                dbm.executeUpdate(sql);
            }
        } catch (IOException e) {
            log.error("IOException When reading SQL files: ", e);
            throw new IOException("IOException When reading SQL files: ", e);
        } catch (ClassNotFoundException e) {
            log.error("Class Not Found. Check MySql-jdbc Driver in classpath: " + e);
            throw new ClassNotFoundException("Class Not Found. Check MySql-jdbc Driver in classpath: ", e);
        } catch (SQLException e) {
            log.error("SQLException When executing SQL: " + e);
            throw new SQLException("SQLException When executing SQL: ", e);
        } finally {
            if (dbm != null) {
                dbm.disconnect();
            }
        }
    }
}
