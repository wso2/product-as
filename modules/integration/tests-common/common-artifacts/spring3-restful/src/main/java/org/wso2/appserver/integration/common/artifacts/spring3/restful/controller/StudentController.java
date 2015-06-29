/*
 *   Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.wso2.appserver.integration.common.artifacts.spring3.restful.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.wso2.appserver.integration.common.artifacts.spring3.restful.model.Status;
import org.wso2.appserver.integration.common.artifacts.spring3.restful.model.Student;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student")
public class StudentController {

    private static final Log log = LogFactory.getLog(StudentController.class);

    @Autowired
    @Qualifier("dbStudent")
    private DataSource dataSource;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Student> getallStudent() {
        log.info("Retrieve all Student info");
        List<Student> students = new ArrayList<>();
        String query = "SELECT id, first_name, last_name, age from student";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        List<Map<String, Object>> studentRows = jdbcTemplate.queryForList(query);
        for(Map<String, Object> studentRow : studentRows) {
            Student student = new Student();
            student.setId(Integer.parseInt(String.valueOf(studentRow.get("id"))));
            student.setFirstName(String.valueOf(studentRow.get("first_name")));
            student.setLastName(String.valueOf(studentRow.get("last_name")));
            student.setAge(Integer.parseInt(String.valueOf(studentRow.get("age"))));
            students.add(student);
        }
        return students;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Student getStudent(@PathVariable("id") int id) {
        log.info("Retrieve Student Info");
        Student student = new Student();
        String query = "SELECT id, first_name, last_name, age FROM student WHERE id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                student.setId(resultSet.getInt("id"));
                student.setFirstName(resultSet.getString("first_name"));
                student.setLastName(resultSet.getString("last_name"));
                student.setAge(resultSet.getInt("age"));
            }
        } catch (SQLException e) {
            log.error("Error retrieving Student Info");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("Closing Connection");
            }
        }
        return student;
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public Status insertStudent(@RequestBody Student student) {
        log.info("Insert Student Info");
        Status status = new Status();
        String query = "INSERT INTO student (id, first_name, last_name, age) VALUES (?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, student.getId());
            statement.setString(2, student.getFirstName());
            statement.setString(3, student.getLastName());
            statement.setInt(4, student.getAge());
            statement.execute();
            status.setStatus(true);
        } catch (SQLException e) {
            log.error("Error in inserting Student Info");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("Closing Connection");
            }
        }
        return status;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Status updateStudent(@RequestBody Student student) {
        log.info("Update Student Info");
        Status status = new Status();
        String query = "UPDATE student SET first_name = ?, last_name = ?, age = ? WHERE id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, student.getFirstName());
            statement.setString(2, student.getLastName());
            statement.setInt(3, student.getAge());
            statement.setInt(4, student.getId());
            statement.executeUpdate();
            status.setStatus(true);
        } catch (SQLException e) {
            log.error("Error in updating Student Info");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("Closing Connection");
            }
        }
        return status;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Status deleteStudent(@PathVariable("id") int id) {
        log.info("Deleting Student Info");
        Status status = new Status();
        String query = "DELETE FROM student WHERE id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
            status.setStatus(true);
        } catch (SQLException e) {
            log.error("Error in deleting student");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("Closing Connection");
            }
        }
        return status;
    }
}
