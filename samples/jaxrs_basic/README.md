JAX-RS Basic Demo
=================

Introduction
------------

The demo shows a basic REST based Web Services using JAX-RS (JSR-311) in WSO2 Application Server .WSO2 Application Server.

Getting Started
---------------

### Starting the server

Before you run the sample, start the server as explained below.

1.Navigate to the <AS_HOME>/bin folder on command prompt.
2.Execute the relevant command given below to start the server.
  * On Windows: ```catalina.bat start```
  * On Linux/Solaris: ```./catalina.sh start```  

### Deploying the JAX-RS application 

Follow the steps given below to execute the web application.

#####Building and running the demo using maven

From the base directory of this sample (i.e., where this README file is
located), the maven pom.xml file can be used to build and run the demo. 

Using either UNIX or Windows:

  * ```mvn clean install``` (builds the demo and creates a WAR file)  
  * ```mvn -Pdeploy``` (deploys the generated WAR file on WSO2 AS with related logs on the console)  

To remove the target dir, run ```mvn clean```".

#####Building and running the demo using ant

Run ```ant``` on AS_HOME/samples/jaxrs_basic directory. This will deploy the jaxrs_basic service in WSO2 AS.




Running a JAX-RS Application
----------------------------

You can invoke the JAX-RS web application as shown below.

Get the customer instance for customer ID 123 by sending an HTTP GET request to http://localhost:8080/jaxrs_basic/services/customerservice/customers/123. The XML document returns the following:
```
<Customer>
   <id>123</id>
   <name>John</name>
</Customer>
```
Get the product 323 that belongs to order 223 by sending an HTTP GET request to http://localhost:8080/jaxrs_basic/services/customerservice/orders/223/products/323. The XML document returns the following:
```
<Product>
   <description>product 323</description>
   <id>323</id>
</Product>
```
Add the customer named Jack by sending an HTTP POST request to http://localhost:8080/jaxrs_basic/services/customerservice/customers as shown below.
```
<Customer>
   <name>Jack</name>
</Customer>
```
Update the customer with ID 123 as shown below by sending an HTTP PUT request to http://localhost:8080/jaxrs_basic/services/customerservice/customers/customers as shown below.
```
<Customer>
   <id>123</id>
   <name>John</name>
</Customer>
```