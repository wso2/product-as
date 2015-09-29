WSO2 Application Server
---------------

---

| Branch | Build Status |
| :------------ |:-------------
| master | [![Build Status](https://wso2.org/jenkins/job/product-as/badge/icon)](https://wso2.org/jenkins/job/product-as) |


---

Latest Released Version 5.3.0.

Welcome to the WSO2 Application Server.

 The WSO2 Application Server is an enterprise-ready cloud-enabled application server, powered by Apache Tomcat, and Apache TomEE, it integrates Apache CXF framework. It provides first class support for standard Web applications, JAX-WS/JAX-RS applications and Jaggery scripting applications. Coupled with features of WSO2 Carbon, users can now manage their applications including JAX-WS and JAX-RS to web applications in a unified manner within the management console itself.

 Application Server also provides a comprehensive Web services server platform using CXF as its Web services runtime, and provides many value additions on top of this runtime. It can expose services using both SOAP and REST models and supports a comprehensive set of WS-* specifications such as WS-Security, WS-Trust, WS-SecureConversation, WS-Addressing, WS-SecurityPolicy, etc. WSO2 Application Server also has inbuilt support for Jaggery. WSO2 Application Server can be installed on on-premise or any public/private cloud infrastructure and provide unified management console and lifecycle management features which are independent from underlying deployment option.

Features
------------
* JavaEE 6 Web Profile support via TomEE 1.7.2 integration
* Websocket 1.1 API Support as defined by the JSR-356 specification
* SAML2 Single-Sign-On support for web applications
* Tomcat Virtual Hosts support
* HTTP Session Persistence support
* WS-Discovery support for CXF JAX-WS and JAX-RS services
* OSGi ServiceLoader Mediator specification support via SPI-Fly
* Support for Servlet 3, JSP 2.2, EL 2.2, JSTL 1.2 specifications.
* Full JAX-WS 2.2 and JAX-RS 2.0 Specification support
* Integration of Jaggery - server side scripting framework
* Unified Application listing and management UI for WebApps, JAX-WS/RS, Jaggery
* Multi Tenant support for standalone deployment
* 100% Apache Tomcat compliance runtime
* Lazy loading for web applications and services
* Tooling - Application Server related artifacts can be easily generated using WSO2 Developer Studio
* Clustering support for High Availability and High Scalability
* Full support for WS-Security, WS-Trust, WS-Policy and WS-Secure Conversation
* JMX and Web interface based monitoring and management
* WS-* and REST support
* GUI, command line, and IDE based tools for Web service development
* Equinox P2 based provisioning support
* WSDL2Java/Java2WSDL/WSDL 1.1, and UI-based try it (invoke any remote Web service)

Installation & Running
----------------------
1. Download the WSO2 Application Server from http://wso2.com/products/application-server/
2. Extract the downloaded zip file
3. Run the wso2server.sh or wso2server.bat file in the bin directory
4. Once the server starts, point your Web browser to
   https://localhost:9443/carbon/

System Requirements
-------------------

1. Minimum memory - 1 GB
2. Processor      - Pentium 800MHz or equivalent at minimum
3. The Management Console requires full Javascript enablement of the Web browser

For more details see the Installation guide or,
http://docs.wso2.org/wiki/display/AS530/Installing+the+Product

Including External Dependencies
--------------------------------
For a complete guide on adding external dependencies to WSO2 Application Server & other carbon related products refer to the article:
http://wso2.com/library/knowledgebase/add-external-jar-libraries-wso2-carbon-based-products

Application Server Binary Distribution Directory Structure
--------------------------------------------

     CARBON_HOME
        |-- bin <directory>
        |-- dbscripts <directory>
        |-- lib <directory>
             `-- runtimes <directory>
		   |-- cxf <directory>
		   `-- ext <directory>
        |-- repository <directory>
        |   |-- carbonapps <directory>
        |       `-- work <directory>
        |   |-- components <directory>
        |   |-- conf <directory>
        |   |-- data <directory>
        |   |-- database <directory>
        |   |-- deployment <directory>
        |   |-- lib <directory>
        |   |-- logs <directory>
        |   |-- resources <directory>
        |   |   `-- security <directory>
        |   `-- tenants <directory>
        |-- tmp <directory>
        |-- LICENSE.txt <file>
        |-- README.txt <file>
        |-- INSTALL.txt <file>
        `-- release-notes.html <file>

    - bin
      Contains various scripts .sh & .bat scripts.

    - dbscripts
      Contains the database creation & seed data population SQL scripts for
      various supported databases.

    - lib
      Contains the basic set of libraries required to startup Application Server
      in standalone mode

    - repository
      The repository where Carbon artifacts & Axis2 services and
      modules deployed in WSO2 Carbon are stored.
      In addition to this other custom deployers such as
      dataservices and axis1services are also stored.

        - carbonapps/work
          Work directory for Carbon Applications. Carbon Application hot deployment directory is repository/deployment/server/carbonapps/

    	- components
          Contains all OSGi related libraries and configurations.

        - conf
          Contains server configuration files. Ex: axis2.xml, carbon.xml

        - data
          Contains internal LDAP related data.

        - database
          Contains the WSO2 Registry & User Manager database.

        - deployment
          Contains server side and client side Axis2 repositories.
	      All deployment artifacts should go into this directory.

        - logs
          Contains all log files created during execution.

        - resources
          Contains additional resources that may be required.

	- tenants
	  Directory will contain relevant tenant artifacts
	  in the case of a multitenant deployment.

    - tmp
      Used for storing temporary files, and is pointed to by the
      java.io.tmpdir System property.

    - LICENSE.txt
      Apache License 2.0 under which WSO2 Carbon is distributed.

    - README.txt
      This document.

    - INSTALL.txt
      This document contains information on installing WSO2 Application Server.

    - release-notes.html
      Release information for WSO2 Application Server ${appserver.version}


## How to Contribute
* Please report issues at [WSO2 Application Server JIRA] (https://wso2.org/jira/browse/WSAS).
* Send your bug fixes pull requests to [master branch] (https://github.com/wso2/product-as/tree/master) 

## Contact us
WSO2 Application Server developers can be contacted via the mailing lists:

* WSO2 Developers List : dev@wso2.org
* WSO2 Architecture List : architecture@wso2.org
