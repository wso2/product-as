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
package org.superbiz.servlet;

import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JPA example
 */
public class JpaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @PersistenceUnit(name = "jpa-example")
    private transient EntityManagerFactory emf;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        response.setContentType("text/plain");
        ServletOutputStream out = response.getOutputStream();

        out.println("@PersistenceUnit=" + emf);

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        JpaBean jpaBean = new JpaBean();
        jpaBean.setName("JpaBean");
        em.persist(jpaBean);

        transaction.commit();
        transaction.begin();

        Query query = em.createQuery("SELECT j FROM JpaBean j WHERE j.name='JpaBean'");
        jpaBean = (JpaBean) query.getSingleResult();
        out.println("Loaded " + jpaBean);

        em.remove(jpaBean);

        transaction.commit();
        transaction.begin();

        query = em.createQuery("SELECT count(j) FROM JpaBean j WHERE j.name='JpaBean'");
        int count = ((Number) query.getSingleResult()).intValue();
        if (count == 0) {
            out.println("Removed " + jpaBean);
        } else {
            out.println("ERROR: unable to remove" + jpaBean);
        }

        transaction.commit();
    }
}
