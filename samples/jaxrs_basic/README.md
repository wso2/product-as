Samples: JaxRS-Basic sample
===========================

Introduction
------------

This is a simple JAX-RS (JSR-311) sample. The REST server provides the following services: 

A RESTful customer service is provided on URL `http://<host>:<port>/jaxrs_basic/services/customerservice`.
Users may access this URI to perform the following operations on customer(s).

- A HTTP GET request to URL `${serviceURL}/customers/123` returns a customer instance whose id is 123. 
The XML document returned:
```
<Customer>
  <id>123</id>
  <name>John</name>
</Customer>
```

- A HTTP GET request to URL `${serviceURL}/orders/223/products/323` returns product 323 that belongs to order 223. 
The XML document returned:
```
<Product>
  <description>product 323</description> 
  <id>323</id> 
</Product>
```

- A HTTP POST request to URL `${serviceURL}/customers` with the following payload adds a customer whose name is Jack.
```
<Customer>
  <name>Jack</name>
</Customer>
```
 
- A HTTP PUT request to URL `${serviceURL}/customers` with the following payload updates the customer instance whose id is 123.
with the data:
```
<Customer>
  <id>123</id>
  <name>John</name>
</Customer>
```

Requirements
-------------

1. JDK 1.8 or higher
2. Apache Maven 3.0.4 or higher

Building the sample
-------------------

1. Open a command line, and navigate to the <AS_HOME>/samples/jaxrs-basic directory.
2. Build the sample with `mvn clean install`.
3. Deploy the sample with `mvn -Pdeploy`.
4. Start WSO2 Application Server.
