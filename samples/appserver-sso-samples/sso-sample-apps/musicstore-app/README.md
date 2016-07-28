Sample : Music Store
===================

Introduction
------------

This is a simple sample web application to demonstrate sso functionality in the WSO2 Application server. Before
starting this sample you have to set up sso configurations using Identity server and application server. Then you can
 log into this application using identity server credentials. In the book store web page it will contain a link to go
  to other webapp and log out button to log out from the web app.


Building the Samples
----------------------

1. Open a command line, and navigate to the <AS_HOME>/samples/sso-sample-apps/musicstore-app directory.
2. Run the relevant command to deploy the web app:
* Using Maven
** Create a WAR file for the sample using the following command:
*** mvn clean install
**Deploy the generated WAR file on WSO2 AS with the related logs on the console:
*** mvn -Pdeploy
*Using Ant
**ant

3. This will deploy those webapps in <AS_HOME>/webapps directory. If you start AppServer, those web apps will be
available in the server.

Requirements
--------------

1. JDK 1.7 or higher
2. Apache ANT 1.7 or higher
3. Apache Maven 3.0.4 or higher
4. A JavaScript compatible web browser
5. An active Internet connection