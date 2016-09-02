package org.wso2.appserver.apieverywhere;


import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.appserver.apieverywhere.utils.API;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.reflect.Method;
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

        String webXmlPath = servletContext.getRealPath("/") + "/WEB-INF/web.xml";

        // TO DO have to read the web.xml file and get the base url, class names.
        String className = null;
        Boolean isJaxRs = true;

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            Document webXmlDoc = dbFactory.newDocumentBuilder().parse(webXmlPath);
            webXmlDoc.getDocumentElement().normalize();

            Element servlet = (Element) webXmlDoc.getElementsByTagName("servlet").item(0);
            Element servletMapping = (Element) webXmlDoc.getElementsByTagName("servlet-mapping").item(0);

            String urlPattern = servletMapping.getElementsByTagName("url-pattern").item(0).getTextContent();
            baseUrl.append(urlPattern.substring(0, urlPattern.length() - 2));


            Element initParam = (Element) servlet.getElementsByTagName("init-param").item(0);
            String paramName = initParam.getElementsByTagName("param-name").item(0).getTextContent();
            if (Objects.equals(paramName, "jaxrs.serviceClasses")) {
                className = initParam.getElementsByTagName("param-value").item(0).getTextContent();
            } else {
                log.info("reading cxf-servlet.xml ");

                String servletXmlPath = servletContext.getRealPath("/") + "/WEB-INF/cxf-servlet.xml";
                Document servletDoc = dbFactory.newDocumentBuilder().parse(servletXmlPath);

                Element jaxrsServer = (Element) servletDoc.getElementsByTagName("jaxrs:server").item(0);
                String address = jaxrsServer.getAttribute("address");
                baseUrl.append(address);

                Element serviceBeans = (Element) jaxrsServer.getElementsByTagName("jaxrs:serviceBeans").item(0);
                String beanName = ((Element) serviceBeans.getElementsByTagName("ref").item(0)).getAttribute("bean");


                Element bean = (Element) servletDoc.getElementsByTagName("bean").item(0);
                if (Objects.equals(bean.getAttribute("id"), beanName)) {
                    className = bean.getAttribute("class");
                }
            }

            log.info("param-value :" + className);

        } catch (ParserConfigurationException e) {
            log.error(e);
        } catch (SAXException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        } catch (NullPointerException e) {
            isJaxRs = false;
        }


        if (isJaxRs && className != null) {
            //scanning annotations
            Reflections reflections = new Reflections(className,
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

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //no channge when an web app destroyed.
    }
}
