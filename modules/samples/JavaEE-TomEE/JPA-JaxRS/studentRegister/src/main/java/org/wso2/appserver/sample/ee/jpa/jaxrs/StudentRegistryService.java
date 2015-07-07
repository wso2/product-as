/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.appserver.sample.ee.jpa.jaxrs;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


@Path("/student")
public class StudentRegistryService {

    @EJB
    private StudentManager studentManager;

    @POST
    @Path("add")
    @Consumes({"application/json"})
    public Response addStudent(StudentDTO stu) {
        Student student = new Student(stu.getIndex(), stu.getName());
        Student result = studentManager.addStudent(student);
        if (result != null) {
            return Response.ok().entity(result.getName() + " registered").build();
        }
        return Response.serverError().entity("A student with the same index is already registered").build();
    }

    @GET
    @Path("getall")
    @Produces({"application/json"})
    public Response getAllStudents() {
        List<Student> students = studentManager.getAllStudents();
        List<StudentDTO> studentsResponse = new ArrayList<StudentDTO>();

        for (Student student : students) {
            studentsResponse.add(new StudentDTO(student.getIndex(), student.getName()));
        }

        StudentsDTO studentsList = new StudentsDTO(studentsResponse);
        return Response.ok().entity(studentsList).build();
    }

    @GET
    @Path("get/{index}")
    @Produces({"application/json"})
    public Response getStudent(@PathParam("index") int index) {
        Student student = studentManager.getStudent(index);
        if (student != null) {
            StudentDTO studentResponse = new StudentDTO(student.getIndex(), student.getName());
            return Response.ok().entity(studentResponse).build();
        }
        return Response.serverError().entity("No student found for the index " + index).build();
    }

    @DELETE
    @Path("remove/{index}")
    public Response removeStudent(@PathParam("index") int index) {
        Student removedStudent = studentManager.removeStudent(index);
        if (removedStudent != null) {
            return Response.ok(removedStudent.getName() + " removed").build();
        }
        return Response.serverError().entity("No student found for the index " + removedStudent.getIndex()).build();
    }

}
