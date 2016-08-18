/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.appserver.sample.ee.jpa.jaxrs;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

/**
 * StudentManagerImpl class.
 */
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
