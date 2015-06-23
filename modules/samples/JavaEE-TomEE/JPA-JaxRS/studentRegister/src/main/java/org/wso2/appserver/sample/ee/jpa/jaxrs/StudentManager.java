package org.wso2.appserver.sample.ee.jpa.jaxrs;

import java.util.List;

public interface StudentManager {

    public Student getStudent(int index);

    public Student addStudent(Student student);

    public Student removeStudent(int index);

    public List<Student> getAllStudents();

}
