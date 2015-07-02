package org.wso2.appserver.sample.ee.jpa.jaxrs;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

@Stateful
public class StudentManagerImpl implements StudentManager {

    @PersistenceContext(unitName = "rest-jpa", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    @Override
    public Student getStudent(int index) {
        Student student = entityManager.find(Student.class, index);
        return student;
    }

    @Override
    public Student addStudent(Student student) {

        Student stu = getStudent(student.getIndex());
        if (stu == null) {
            entityManager.persist(student);
            return student;
        }

        return null;
    }

    @Override
    public Student removeStudent(int index) {
        Student toBeRemovedStudent = getStudent(index);
        if (toBeRemovedStudent != null) {
            entityManager.remove(toBeRemovedStudent);
        }
        return toBeRemovedStudent;
    }

    @Override
    public List<Student> getAllStudents() {
        Query query = entityManager.createQuery("SELECT student FROM Student student");
        return query.getResultList();
    }

    @PostConstruct
    private void addDefaultStudent() {
        Student defaultStudent = new Student(100, "John");
        addStudent(defaultStudent);
    }

}
