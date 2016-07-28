Samples : SSO samples
======================

Introduction
-------------

Here are two simple sample that demonstrates the single sign on functionality in WSO2 Web Service Application Server.
 Each of this application contains link to goto other web application to check the single sign on functionality.

1. bookstore-app
    * This is a simple book store web application.
2. musicstore-app
    * This is a simple music store web application.


Requirements
--------------

1. JDK 1.8 or higher
2. Apache ANT 1.7 or higher
3. Apache Maven 3.0.4 or higher
4. A JavaScript compatible web browser
5. An active Internet connection

Building the Samples
----------------------

1. Open a command line, and navigate to the <AS_HOME>/samples/sso-sample-apps/bookstore-app directory.
2. Run the relevant command to deploy the web app:
    * Using Maven
        * Create a WAR file for the sample using the following command:
            * mvn clean install
        * Deploy the generated WAR file on WSO2 AS with the related logs on the console:
            * mvn -Pdeploy
    * Using Ant
        * ant
3. Do this again for musicstore-app to deploy the music store web app.
3. This will deploy those webapps in <AS_HOME>/webapps directory. If you start AppServer, those web apps will be
available in the server.