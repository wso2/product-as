package org.wso2.appserver.sample.ee.jpa.jaxrs;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


@Path("/student")
public class StudentRegistryService {

    @EJB
    private StudentManager studentManager;

    @POST
    @Path("add")
    @Consumes({"application/json", "application/xml", "text/json", "text/xml"})
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
    @Produces({"application/json", "application/xml", "text/json", "text/xml"})
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
    @Produces({"application/json", "application/xml", "text/json", "text/xml"})
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
