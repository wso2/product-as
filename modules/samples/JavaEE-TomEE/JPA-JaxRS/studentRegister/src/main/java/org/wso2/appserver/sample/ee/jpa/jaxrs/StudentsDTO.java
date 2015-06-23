package org.wso2.appserver.sample.ee.jpa.jaxrs;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "Students")
public class StudentsDTO {
    List<StudentDTO> students;

    public StudentsDTO(List<StudentDTO> students) {
        this.students = students;
    }

    public StudentsDTO() {

    }

    public List<StudentDTO> getStudents() {
        return students;
    }

    public void setStudents(List<StudentDTO> students) {
        this.students = students;
    }
}
