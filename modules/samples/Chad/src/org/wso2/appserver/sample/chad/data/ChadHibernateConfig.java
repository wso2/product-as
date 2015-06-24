/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
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

package org.wso2.appserver.sample.chad.data;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Initializes & manages the Hibernate configuration 
 */
public class ChadHibernateConfig {
    public final SessionFactory SESSION_FACTORY;

    /**
     * Default Constructor. This should be package protected since we should only instantiate it
     * through it HibernateConfigFactory
     * <br/>
     * <p/>
     * This will pickup hibernate.cfg.xml from the classpath
     * and load the configuration from this file.
     */
    ChadHibernateConfig() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml, which has to be in the classpath
            SESSION_FACTORY =
                    new Configuration().
                            configure("chad.hibernate.cfg.xml").buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public Session currentSession() throws HibernateException {
        return SESSION_FACTORY.getCurrentSession();
    }
}

