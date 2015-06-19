WS-Security Demo  (UsernameToken and Timestamp)
===============================================

This demo shows how WS-Security support in JAX-WS services may be enabled.

WS-Security can be configured to the Client and Server endpoints by adding WSS4JInterceptors.
Both Server and Client can be configured for outgoing and incoming interceptors. Various Actions like,
Timestamp, UsernameToken, Signature, Encryption, etc., can be applied to the interceptors by passing
appropriate configuration properties.

The logging feature is used to log the inbound and outbound
SOAP messages and display these to the console.

In all other respects this demo is based on the basic hello_world sample.


Prerequisite
------------

The samples in this directory use STRONG encryption.  The default encryption algorithms
included in a JRE is not adequate for these samples.   The Java Cryptography Extension
(JCE) Unlimited Strength Jurisdiction Policy Files available on Oracle's JDK download
page[3] *must* be installed for the examples to work.   If you get errors about invalid
key lengths, the Unlimited Strength files are not installed.

[3] http://www.oracle.com/technetwork/java/javase/downloads/index.html


Building and running the demo using Maven
-----------------------------------------

From the base directory of this sample (i.e., where this README file is
located), the maven pom.xml file can be used to build and run the demo.


Using either UNIX or Windows:

  * mvn clean install (builds the demo and creates a WAR file)
  * Start the server (run bin/wso2server.sh/.bat)
  * mvn -Pdeploy (deploys the generated WAR file on WSO2 AS with related logs on the console)
  * mvn -Pclient (runs the client)

On startup, the client makes a sequence of 4 two-way invocations.

NOTE :
  * To remove the code generated from the WSDL file and the .class files, run "mvn clean".


Building and running the demo using Ant
---------------------------------------
The base directory of this sample (i.e., where this README file is located) has the build.xml file
which is used to build the necessary webapp and deploy it in WSO2 App Server.

To build and deploy the sample,type :
  * ant

To run the sample demo :
  * Start the App Server and access the Management Console at https://localhost:9443/carbon. Go to the Jaxws-Jaxrs service listing
    page. You will see the deployed service.
  * You have to run the run-client.sh or run-client.bat script. It has all the arguments and classpaths configured to run the sample.

