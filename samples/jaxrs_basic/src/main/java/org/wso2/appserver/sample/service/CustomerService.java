/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.wso2.appserver.sample.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * A web service class.
 *
 * @since 6.0.0
 */
@Service
@Path("/customerservice")
public class CustomerService {
    long currentId = 123;
    Map<Long, Customer> customers = new HashMap<>();
    Map<Long, Order> orders = new HashMap<>();

    public CustomerService() {
        init();
    }

    @GET
    @Path("/customers/{id}/")
    @Produces("application/xml")
    public Customer getCustomer(@PathParam("id") String id) {
        long idNumber = Long.parseLong(id);
        return customers.get(idNumber);
    }

    @PUT
    @Path("/customers/")
    @Consumes("text/xml")
    public Response updateCustomer(Customer customer) {
        Customer c = customers.get(customer.getId());
        Response r;
        if (c != null) {
            customers.put(customer.getId(), customer);
            r = Response.ok(customer).build();
        } else {
            r = Response.notModified().build();
        }
        return r;
    }

    @POST
    @Path("/customers/")
    @Consumes("text/xml")
    public Response addCustomer(Customer customer) {
        customer.setId(++currentId);

        customers.put(customer.getId(), customer);

        return Response.ok(customer).build();
    }

    // Adding a new method to demonstrate Consuming and Producing text/plain

    @POST
    @Path("/customers/name/")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String getCustomerName(String id) {
        return "Isuru Suriarachchi";
    }

    @DELETE
    @Path("/customers/{id}/")
    public Response deleteCustomer(@PathParam("id") String id) {
        long idNumber = Long.parseLong(id);
        Customer c = customers.get(idNumber);

        Response r;
        if (c != null) {
            r = Response.ok().build();
            customers.remove(idNumber);
        } else {
            r = Response.notModified().build();
        }

        return r;
    }

    @Path("/orders/{orderId}/")
    public Order getOrder(@PathParam("orderId") String orderId) {
        long idNumber = Long.parseLong(orderId);
        return orders.get(idNumber);
    }

    final void init() {
        Customer c = new Customer();
        c.setName("John");
        c.setId(123);
        customers.put(c.getId(), c);

        Order o = new Order();
        o.setDescription("order 223");
        o.setId(223);
        orders.put(o.getId(), o);
    }
}
