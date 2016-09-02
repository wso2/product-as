package org.wso2.appserver.apieverywhere;


import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wso2.appserver.apieverywhere.utils.API;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.Path;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 *
 */
public class WebAppDeploymentListener implements ServletContextListener {
    private static final Log log = LogFactory.getLog(WebAppDeploymentListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //APIs of web application have to be extracted
        ServletContext servletContext = servletContextEvent.getServletContext();
        StringBuilder baseUrl = new StringBuilder(servletContext.getContextPath());


        Boolean isJaxRs = true;
        Map<String, String> serverParams = new HashMap<String, String>();
        String webXmlPath = servletContext.getRealPath("/") + "/WEB-INF/web.xml";
        String servletXmlPath = servletContext.getRealPath("/") + "/WEB-INF/cxf-servlet.xml";

        try {
            //reading from web.xml
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            Document webXmlDoc = dbFactory.newDocumentBuilder().parse(webXmlPath);
            webXmlDoc.getDocumentElement().normalize();

            Element servlet = (Element) webXmlDoc.getElementsByTagName("servlet").item(0);
            Element servletMapping = (Element) webXmlDoc.getElementsByTagName("servlet-mapping").item(0);

            String urlPattern = servletMapping.getElementsByTagName("url-pattern").item(0).getTextContent();
            baseUrl.append(urlPattern.substring(0, urlPattern.length() - 2));


            NodeList initParams = servlet.getElementsByTagName("init-param");
            for (int i = 0; i < initParams.getLength(); i++) {
                Element initParam = (Element) initParams.item(i);
                String paramName = initParam.getElementsByTagName("param-name").item(0).getTextContent();
                if (Objects.equals(paramName, "jaxrs.serviceClasses")) {
                    String tempClass = initParam.getElementsByTagName("param-value").item(0).getTextContent();
                    serverParams.put(tempClass, "");
//                    log.info("adding class name in web.xml: " + tempClass);
                    servletXmlPath = null;
                }
            }

            //reading form cxt-servlet.xml
            if (servletXmlPath != null) {
//                log.info("Scanning servlet.xml ");
                Document servletDoc = dbFactory.newDocumentBuilder().parse(servletXmlPath);


                Element jaxrsServer = (Element) servletDoc.getElementsByTagName("jaxrs:server").item(0);

                String address = jaxrsServer.getAttribute("address");

//            Element serviceBeans = (Element) jaxrsServer.getElementsByTagName("jaxrs:serviceBeans").item(0);
//            String beanName = ((Element) serviceBeans.getElementsByTagName("ref").item(0)).getAttribute("bean");

                NodeList beans = servletDoc.getElementsByTagName("bean");
                for (int i = 0; i < beans.getLength(); i++) {
                    Element bean = (Element) beans.item(i);
                    String tempClass = bean.getAttribute("class");
                    serverParams.put(tempClass, address);
//                    log.info("adding class name in servlet.xml: " + tempClass);

                }
            }

        } catch (ParserConfigurationException e) {
            log.error(e);
        } catch (SAXException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        } catch (NullPointerException e) {
            isJaxRs = false;
        }

        if (isJaxRs && !serverParams.isEmpty()) {

            for (Map.Entry<String, String> entry : serverParams.entrySet()) {

//                log.info(" class name :" + entry.getKey());
                baseUrl.append(entry.getValue());

                //scanning annotations
                Reflections reflections = new Reflections(entry.getKey(),
                        new MethodAnnotationsScanner(), new TypeAnnotationsScanner(), new SubTypesScanner());


                Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Path.class);
                methodAnnotated(baseUrl, reflections, classes);

                ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                try {
                    //adding interfaces of the class
                    Class<?> aClass = contextClassLoader.loadClass(entry.getKey());
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (Class cl : interfaces) {
                        Reflections tempReflection = new Reflections(cl.getName(), new TypeAnnotationsScanner(),
                                new SubTypesScanner(), new MethodAnnotationsScanner());
                        classes = tempReflection.getTypesAnnotatedWith(Path.class);
                        methodAnnotated(baseUrl, tempReflection, classes);
                    }
                } catch (ClassNotFoundException e) {
                    log.error(e);
                }

            }
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //no channge when an web app destroyed.
    }

    private void methodAnnotated(StringBuilder baseUrl, Reflections reflections, Set<Class<?>> classes) {
        for (Class cl : classes) {
            Path path = (Path) cl.getAnnotation(Path.class);
            if (path == null) {
                continue;
            }
//            log.info("class : " + cl.getName() + " path : " + path);
            baseUrl.append(path.value());

            Set<Method> methods = reflections.getMethodsAnnotatedWith(Path.class);
            for (Method me : methods) {
                API api = new API(baseUrl.toString(), me);
                log.info(api.toString());
            }
        }
    }
}
