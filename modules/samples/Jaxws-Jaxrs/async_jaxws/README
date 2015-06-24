JAX-WS Asynchronous Demo using Document/Literal Style
=====================================================

This demo illustrates the use of the JAX-WS asynchronous 
invocation model. Please refer to the JAX-WS 2.0 specification
(http://jcp.org/aboutJava/communityprocess/pfd/jsr224/index.html)
for background.

The asynchronous model allows the client thread to continue after 
making a two-way invocation without being blocked while awaiting a 
response from the server. Once the response is available, it is
delivered to the client application asynchronously using one
of two alternative approaches:

- Callback: the client application implements the 
javax.xml.ws.AsyncHandler interface to accept notification
of the response availability

- Polling: the client application periodically polls a
javax.xml.ws.Response instance to check if the response
is available

This demo illustrates both approaches.

Additional methods are generated on the Service Endpoint
Interface (SEI) to provide this asynchrony, named by 
convention with the suffix "Async".

As many applications will not require this functionality,
the asynchronous variants of the SEI methods are omitted
by default to avoid polluting the SEI with unnecessary 
baggage. In order to enable generation of these methods,
a bindings file (wsdl/async_bindings.xml) is passed
to the wsdl2java generator. 

Please review the README in the samples directory before
continuing.

Building and running the demo using Maven
---------------------------------------

From the base directory of this sample (i.e., where this README file is
located), the pom.xml file is used to build and run the demo. 

Using either UNIX or Windows:

  * mvn clean install (builds the demo and creates a WAR file)
  * Start the server (run bin/wso2server.sh/.bat)
  * mvn -Pdeploy (deploys the generated WAR file on WSO2 AS with related logs on the console)
  * mvn -Pclient (runs the client)

To remove the code generated from the WSDL file and the .class
files, run "mvn clean".



Building and running the demo using ant
---------------------------------------

1. Run "ant" on AS_HOME/samples/Jaxws-Jaxrs/async_jaxws directory. This will deploy the async_jaxws
   service in WSO2 AS.
2. Start the server and access the Management Console at https://localhost:9443/carbon. Go to
   the service listing page. You will see the deployed async_jaxws service.
3. Execute "sh run-client.sh" to run the client.
4. Try the sample with different QoS options. Run "sh run-client.sh -help" for different options.

Please download the Documentation Distribution and refer to the async_jaxws sample document
for detailed instructions on how to run the async_jaxws sample.
