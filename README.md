# Welcome to WSO2 Application Server

Welcome to WSO2 Application Server, the successor of WSO2 Carbon based Application Server. WSO2 Application Server 6.0.0 is a complete revamp and is based on vanilla Apache Tomcat. WSO2 provides a number of features by means of extensions to Tomcat
to add/enhance the functionality. It provides first class support for generic web applications, JAX-RS/JAX-WS web
applications and jaggery applications. The performance of the server and individual application can be monitored by integrating WSO2 Application
Server with WSO2 Data Analytics Server.

## Features

* HTTP statistics monitoring for webapplications
* SAML based Single Sign On (SSO) for webapplications
* Jaggerry applications deployment
* Webapp classloading runtimes

## Building the product

### Prerequisites
1. JDK 1.8 or higher
2. Apache Maven 3.0.x or higher
3. Git

### How to build the product
1. Clone the repository using `git clone https://github.com/wso2/product-as.git`
2. `cd` into `product-as` directory
2. Run `mvn clean install`
3. Extract *wso2as-{version}.zip* located at `distribution/target` directory

## Running WSO2 Application Server

### Prerequisites
1. JDK 1.8 or higher
2. Apache Maven 3.0.x or higher

### How to run WSO2 Application Server
1. Goto `<AS_HOME>/bin/` directory
2. Run `./catalina.sh run` (in MAC OS and Linux based operating systems) or `catalina.bat run` (in Windows)

## How to Contribute
* Please report issues at [WSO2 Application Server JIRA] (https://wso2.org/jira/browse/WSAS).
* Send your bug fixes pull requests to [master branch] (https://github.com/wso2/product-as/tree/master)

## Contact us
WSO2 Application Server developers can be contacted via the mailing lists:

* WSO2 Developers List : dev@wso2.org
* WSO2 Architecture List : architecture@wso2.org
