WSDL First Demo
===============

This demo shows how to build and call a webservice using a given WSDL (also called Contract First).
As writing a WSDL by hand is not so easy the following How to may also be a useful read:

http://cxf.apache.org/docs/defining-contract-first-webservices-with-wsdl-generation-from-java.html

This demo mainly addresses SOAP over HTTP in Document/Literal or Document/Literal wrapped style.
For other transports or styles the configuration may look different.

The Demo consist of three parts:

- Creating the server and client code stubs from the WSDL
- Service implementation
- Client implementation

Code generation
---------------
When using maven the code generation is done using the maven cxf-codegen-plugin
(see http://cxf.apache.org/docs/maven-cxf-codegen-plugin-wsdl-to-java.html).

The code generation is tuned using a binding.xml file. In this case the file configures that 
normal java Date is used for xsd:date and xsd:DateTime. If this is not present then XMLGregorianCalendar
will be used.

One other common use of the binding file is to also generate asynchronous stubs. The line
jaxws:enableAsyncMapping has to be uncommented to use this.

More info about the binding file can be found here:
http://jax-ws.java.net/jax-ws-20-fcs/docs/customizations.html

Service implementation
---------------------

The service is implemented in the class CustomerServiceImpl. The class simply implements the previously
generated service interface. The method getCustomersByName demonstrates how a query function could look like.
The idea is to search and return all customers with the given name. If the searched name is none then the method
returns an exception to indicate that no matching customer was found. (In a real implementation probably a list with
zero objects would be used. This is mainly to show how custom exceptions can be used).
For any other name the method will return a list of two Customer objects. The number of  objects can be increased to
test how fast CXF works for larger data.


Client implementation
---------------------

The main client code lives in the class CustomerServiceTester. This class needs a proxy to the service and then 
demonstrates some calls and their expected outcome using junit assertions.

The first call is a request getCustomersByName for all customers with name "Smith". The result is then checked.
Then the same method is called with the invalid name "None". In this case a NoSuchCustomerException is expected.
The third call shows that the one way method updateCustomer will return instantly even if the service needs some
time to process the request.

The classes CustomerServiceClient and CustomerServiceSpringClient show how to get a service proxy using JAX-WS
and how to wire it to your business class (in this case CustomerServiceTester).

Building and running the demo using Maven
-----------------------------------------

From the base directory of this sample (i.e., where this README file is
located), the pom.xml file is used to build and run the demo. 

Using either UNIX or Windows:

  * mvn clean install (builds the demo and creates a WAR file)
  * Start the server (run bin/wso2server.sh/.bat)
  * mvn -Pdeploy (deploys the generated WAR file on WSO2 AS with related logs on the console)
  * mvn -Pclient (runs the client)

Note : If you want to change the endpoint URL of the service, you can edit the WSDL and change it.

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

NOTE :
  * If you have a EPR location other than the http://localhost:9763/java_first_jaxws/services/hello_world please edit the run-client script.
  * Prior to running the client (mvn -Pclient or using run-client script) good to confirm the generated WSDL
    can be seen from a web browser at: http://{ip}:{port}/java_first_jaxws/services/hello_world?wsdl