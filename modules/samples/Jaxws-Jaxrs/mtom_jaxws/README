MTOM Demo for SWA & XOP
=======================

This demo illustrates the use of a SOAP message 
with an attachment and XML-binary Optimized Packaging.

Please review the README in the samples directory before
continuing.


Building and running the demo using Maven
---------------------------------------
From the base directory of this sample (i.e., where this README file is
located), the maven pom.xml file can be used to build and run the demo. 

Using either UNIX or Windows:

  * mvn clean install (builds the demo and creates a WAR file)
  * Start the server (run bin/wso2server.sh/.bat)
  * mvn -Pdeploy (deploys the generated WAR file on WSO2 AS with related logs on the console)
  * mvn -Pclient (runs the client)
    
To remove the code generated from the WSDL file and the .class
files, run mvn clean".



Building and running the demo using ant
---------------------------------------

1. Run "ant" on AS_HOME/samples/Jaxws-Jaxrs/mtom_jaxws directory. This will deploy the mtom_jaxws
   service in WSO2 AS.
2. Start the server and access the Management Console at https://localhost:9443/carbon. Go to
   the service listing page. You will see the deployed mtom_jaxws service.
3. Execute "sh run-client.sh" to run the client.
4. Try the sample with different QoS options. Run "sh run-client.sh -help" for different options.

Please download the Documentation Distribution and refer to the mtom_jaxws sample document
for detailed instructions on how to run the mtom_jaxws sample.


