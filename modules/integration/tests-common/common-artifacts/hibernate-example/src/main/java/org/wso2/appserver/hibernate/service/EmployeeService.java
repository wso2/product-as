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

import org.wso2.appserver.hibernate.Employee;
import org.wso2.appserver.hibernate.EmployeeManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import java.io.IOException;
import java.util.List;

@Path("/EmployeeService")
public class EmployeeService {
    EmployeeManager employeeManager = new EmployeeManager();

    @GET
    @Produces({"application/xml", "application/json"})
    @Path("/get")
    public Response getEmployees() {
        List<Employee> employees = employeeManager.getEmployees();
        return Response.ok().entity(new EmployeeBean(employees)).build();
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Path("/add")
    public Response addEmployee(Employee employee) {
        String firstName = employee.getFirstName();
        String lastName = employee.getLastName();
        Integer salary = employee.getSalary();

        Integer id = employeeManager.addEmployee(firstName, lastName, salary);
        return Response.ok().entity(id).build();
    }

    @PUT
    @Path("/update")
    public void updateEmployee(@QueryParam("id") Integer id,
                               @QueryParam("salary") Integer salary) {
        employeeManager.updateEmployee(id, salary);
    }

    @DELETE
    @Path("/delete")
    public void deleteEmployee(Integer id) {
        employeeManager.deleteEmployee(id);
    }

}
