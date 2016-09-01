package org.wso2.appserver.apieverywhere;


import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.Method;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.Path;



/**
 *
 */
public class WebAppDeploymentListener implements ServletContextListener {
    private static final Log log = LogFactory.getLog(WebAppDeploymentListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //APIs of web application have to be extracted
        ServletContext servletContext = servletContextEvent.getServletContext();
        StringBuffer baseUrl = new StringBuffer(servletContext.getContextPath());

        // TO DO have to read the web.xml file and get the base url, class names.

        baseUrl.append("/HAD_TO_GET_FROM_WEB.XML");

        //scanning annotations
        Reflections reflections = new Reflections("org.wso2.appserver.sample.service.CustomerService" ,
                new MethodAnnotationsScanner(), new TypeAnnotationsScanner(), new SubTypesScanner());


        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Path.class);

        for (Class an :
                typesAnnotatedWith) {
            Path path = (Path) an.getAnnotation(Path.class);
            baseUrl.append(path.value());
        }

        Set<Method> methods =
                reflections.getMethodsAnnotatedWith(Path.class);
        for (Method me :
                methods) {
            API api = new API(baseUrl.toString(), me);
            log.info(api.toString());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //no channge when an web app destroyed.
    }
}
