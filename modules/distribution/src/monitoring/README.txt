---------------------------------------------------------------------------------
	Introduction
---------------------------------------------------------------------------------

This document describes how to setup WSO2 Business Activity Monitor (BAM) to collect
and analyze HTTP statistics for webapps hosted in WSO2 Application Server. Thrift protocol is used
to publish data from the Application Server to BAM. Information processed at BAM are stored
in a database from which the HTTP Statistics Monitoring Dashboard can retrieve them and display.

---------------------------------------------------------------------------------
	Requirements
---------------------------------------------------------------------------------

1. WSO2 AS 5.3.0 or above
2. WSO2 BAM 2.4.1 or above

NOTE:
You can download a latest snapshot of WSO2 BAM from http://wso2.com/products/business-activity-monitor/

---------------------------------------------------------------------------------
    Configuring BAM
---------------------------------------------------------------------------------

1. Extract the BAM binary distribution to your local file system.
2. Change port offset to 1 by editing the repository/conf/carbon.xml.

Open <BAM_HOME>/repository/conf/datasources/bam-datasources.xml and edit the port in the
url in WSO2BAM_CASSANDRA_DATASOURCE datasource as follows

    <datasource>
        <name>WSO2BAM_CASSANDRA_DATASOURCE</name>
        <description>The datasource used for Cassandra data</description>
        <definition type="RDBMS">
            <configuration>
                <url>jdbc:cassandra://localhost:9161/EVENT_KS</url>
                <username>admin</username>
                <password>admin</password>
            </configuration>
        </definition>
    </datasource>

Open <BAM_HOME>/repository/conf/etc/hector-config.xml and edit the port of the cassandra cluster as follows,

    <Nodes>localhost:9161</Nodes>

In both of the above modifications, the increment to the port is determined by the offset we set in carbon.xml. For
example, if we set the offset value to be 5, the port of the cassandra datasource and the node would be 9165 (default
 value is 9160).

3. Copy AS_Monitoring.tbox to repository/deployment/server/bam-toolbox
   (Create the bam-toolbox directory if it doesn't already exist)
4. Add the following to <BAM_HOME>/conf/datasources/master-datasources.xml file. Here we have used the embedded H2 database, you can use the database of your choice (MySQL for example).

    <datasource>
        <name>WSO2AS_MONITORING_DB</name>
        <description>The datasource used for WSO2 Application Server HTTP Monitoring</description>
        <jndiConfig>
            <name>jdbc/WSO2AS_MONITORING_DB</name>
        </jndiConfig>
        <definition type="RDBMS">
            <configuration>
                <url>jdbc:h2:repository/database/WSO2AS_MONITORING_DB;AUTO_SERVER=TRUE</url>
                <username>wso2carbon</username>
                <password>wso2carbon</password>
                <driverClassName>org.h2.Driver</driverClassName>
                <maxActive>50</maxActive>
                <maxWait>60000</maxWait>
                <testOnBorrow>true</testOnBorrow>
                <validationQuery>SELECT 1</validationQuery>
                <validationInterval>30000</validationInterval>
            </configuration>
        </definition>
    </datasource>

5. Start WSO2 BAM server

---------------------------------------------------------------------------------
    Configuring AS
---------------------------------------------------------------------------------

1. Open <AS_HOME>/repository/conf/etc/bam-publisher.xml and set <enable>true</enable> in the section <stream
id="monitoring.webapp.calls">. You also need to change the port of the receiverUrl (Thrift SSL port). By default it will
 be 7711, increase it by the value you increase the Offset in BAM. For example, if we set the offset value to be 5,
 the port of the receiverUrl would be 7716.

2. Add the following to <AS_HOME>/conf/datasources/master-datasources.xml file. This needs to be the same datasource we configured in BAM.

       <datasource>
           <name>WSO2AS_MONITORING_DB</name>
           <description>The datasource used for WSO2 Application Server HTTP Monitoring</description>
           <jndiConfig>
               <name>jdbc/WSO2AS_MONITORING_DB</name>
           </jndiConfig>
           <definition type="RDBMS">
               <configuration>
                   <url>jdbc:h2:<BAM_HOME>/repository/database/WSO2AS_MONITORING_DB;AUTO_SERVER=TRUE</url>
                   <username>wso2carbon</username>
                   <password>wso2carbon</password>
                   <driverClassName>org.h2.Driver</driverClassName>
                   <maxActive>50</maxActive>
                   <maxWait>60000</maxWait>
                   <testOnBorrow>true</testOnBorrow>
                   <validationQuery>SELECT 1</validationQuery>
                   <validationInterval>30000</validationInterval>
               </configuration>
           </definition>
       </datasource>

3. Start AS Server.

NOTE: Replace <BAM_HOME> with the absolute path to the installation directory of BAM and <AS_HOME> with the
absolute path to the installation directory of AS.

---------------------------------------------------------------------------------
    Access the monitoring dashboard
---------------------------------------------------------------------------------

Log into the management console of AS, go to Main > Manage > Applications > List.
There you will find the "monitoring" jaggery app.

---------------------------------------------------------------------------------
    Changing the Statistics Database
---------------------------------------------------------------------------------

It is possible to use a different database than the default H2 database for statistics publishing.
When doing this you need to change the properties of the datasource element, and additionally delete
some meta data tables created by the previous executions of the hive script.

Drop the meta data table by executing the following in BAM script editor.
You can go to the script editor in BAM by accessing, Main > Analytics > Add in BAM Management Console.

drop table AsHttpStat;
drop table RequestsPerMinute;
drop table HttpStatus;
drop table Language;
drop table UserAgentFamily;
drop table OperatingSystem;
drop table DeviceType;
drop table Referrer;
drop table Country;
drop table WebappContext;
