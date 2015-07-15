/*
 * Copyright 2015 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.appserver.hibernate.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appserver.hibernate.Employee;
import org.wso2.appserver.hibernate.EmployeeManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/EmployeeService")
public class EmployeeService {
    EmployeeManager employeeManager = new EmployeeManager();

    private static final Log log = LogFactory.getLog(EmployeeService.class);

    @GET
    @Produces({"application/xml", "application/json"})
    @Path("/get")
    public Response getEmployees() {
        log.info("Get employees.");
        List<Employee> employees = employeeManager.getEmployees();
        return Response.ok().entity(new Employees(employees)).build();
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Path("/add")
    public Response addEmployee(Employees employees) {
        Employee employee = employees.getEmployees().get(0);
        log.info("Received employee: " + employee.getFirstName());
        String firstName = employee.getFirstName();
        String lastName = employee.getLastName();
        Integer salary = employee.getSalary();

        Integer id = employeeManager.addEmployee(firstName, lastName, salary);
        return Response.ok().entity(id).build();
    }

    @PUT
    @Path("/update")
    public void updateEmployee(@FormParam("id") Integer id,
                               @FormParam("salary") Integer salary) {
        log.info("Received id: " + id + ", salary: " + salary);
        employeeManager.updateEmployee(id, salary);
    }

    @DELETE
    @Path("/delete")
    public void deleteEmployee(@QueryParam("id" )Integer id) {
        log.info("Received id: " + id);
        employeeManager.deleteEmployee(id);
    }

}
