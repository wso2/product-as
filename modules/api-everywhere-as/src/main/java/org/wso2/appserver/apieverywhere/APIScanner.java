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
import org.wso2.appserver.apieverywhere.exceptions.APIEverywhereException;
import org.wso2.appserver.apieverywhere.utils.APICreateRequest;
import org.wso2.appserver.apieverywhere.utils.APIPath;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * The class which scan the deployed web app and extract the APIs.
 *
 * @since 6.0.0
 */
class APIScanner {

    private static final Log log = LogFactory.getLog(APIScanner.class);
    private APICreateRequest apiCreateRequest = new APICreateRequest();
    private List<APIPath> generatedApiPaths = new ArrayList<>();


    void scan(ServletContext servletContext) throws APIEverywhereException {
        apiCreateRequest.setContext(servletContext.getContextPath());
        HashMap<String, String> serverParams = scanConfigs(servletContext);

        if (!serverParams.isEmpty()) {

            for (Map.Entry<String, String> entry : serverParams.entrySet()) {
                //append address in beans
                StringBuilder url = new StringBuilder(entry.getValue());

                //scanning annotations
                Reflections reflections = new Reflections(entry.getKey(),
                        new MethodAnnotationsScanner(), new TypeAnnotationsScanner(),
                        new SubTypesScanner());


                Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Path.class);
                scanMethodAnnotation(url, reflections, classes);

                ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                try {
                    //adding interfaces of the class
                    Class<?> aClass = contextClassLoader.loadClass(entry.getKey());
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (Class cl : interfaces) {
                        Reflections tempReflection = new Reflections(cl.getName(),
                                new TypeAnnotationsScanner(), new SubTypesScanner(),
                                new MethodAnnotationsScanner());
                        classes = tempReflection.getTypesAnnotatedWith(Path.class);
                        scanMethodAnnotation(url, tempReflection, classes);
                    }
                } catch (ClassNotFoundException e) {
                    log.error("The class is not found in scanning: " + e);
                    throw new APIEverywhereException("The class is not found in scanning ", e);
                }
            }
            for (APIPath apiPath : generatedApiPaths) {
                log.info(apiPath.toString());
            }
            //Run Thread to publish generated apis into API Publisher
            apiCreateRequest.buildAPI(generatedApiPaths);
            APICreator apiCreator = new APICreator(apiCreateRequest);
            apiCreator.start();
        }
    }


    /**
     * Scan for beans in config xml files in cxf servlet
     *
     * @param servletContext     the deployed web apps' servlet context
     * @return Hash map of <Bean class name, Bean url>
     */
    private HashMap<String, String> scanConfigs(ServletContext servletContext) throws APIEverywhereException {
        //Map that stores the class name and the address from beans
        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(servletContext.getContextPath());
        HashMap<String, String> serverParams = new HashMap<>();
        String webXmlPath = servletContext.getRealPath("/") + "/WEB-INF/web.xml";
        String servletXmlPath = servletContext.getRealPath("/") + "/WEB-INF/cxf-servlet.xml";

        try {
            //reading from web.xml
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            Document webXmlDoc = dbFactory.newDocumentBuilder().parse(webXmlPath);
            webXmlDoc.getDocumentElement().normalize();

            Element servlet = (Element) webXmlDoc.getElementsByTagName("servlet").item(0);
            Element servletMapping = (Element) webXmlDoc.getElementsByTagName("servlet-mapping")
                                                                            .item(0);

            if (servlet != null && servletMapping != null) {
                // TODO: What to put in API name ? get the name to web app name
                apiCreateRequest.setName(servletContext.getContextPath().substring(1));
//                String servletName = servlet.getElementsByTagName("servlet-name").item(0).getTextContent().trim();
//                apiCreateRequest.setName(servletName);

                String urlPattern = servletMapping.getElementsByTagName("url-pattern")
                                                                .item(0).getTextContent();
                baseUrl.append(urlPattern.substring(0, urlPattern.length() - 2));


                String servletClassName = servlet.getElementsByTagName("servlet-class")
                                                            .item(0).getTextContent().trim();
                switch (servletClassName) {
                    case "org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet":
                        //getting bean from init-param
                        NodeList initParams = servlet.getElementsByTagName("init-param");
                        for (int i = 0; i < initParams.getLength(); i++) {
                            Element initParam = (Element) initParams.item(i);
                            String paramName = initParam.getElementsByTagName("param-name")
                                                                        .item(0).getTextContent();
                            if (Objects.equals(paramName, "jaxrs.serviceClasses")) {
                                String tempClass = initParam.getElementsByTagName("param-value")
                                        .item(0).getTextContent();
                                serverParams.put(tempClass, baseUrl.toString());
                            }
                        }
                        break;
                    case "org.apache.cxf.transport.servlet.CXFServlet":
                        //getting beans from cfx-servlet.xml
                        Document servletDoc = dbFactory.newDocumentBuilder().parse(servletXmlPath);
                        servletDoc.getDocumentElement().normalize();

                        Element jaxrsServer = (Element) servletDoc.getElementsByTagName
                                                                    ("jaxrs:server").item(0);

                        if (jaxrsServer != null) {
                            //jax rs configurations
                            String address = jaxrsServer.getAttribute("address");
                            baseUrl.append(address);
                            NodeList beans = servletDoc.getElementsByTagName("bean");
                            for (int i = 0; i < beans.getLength(); i++) {
                                Element bean = (Element) beans.item(i);
                                String tempClass = bean.getAttribute("class");
                                serverParams.put(tempClass, baseUrl.toString());

                            }
                        }
                        // TODO: reading other config
//                        else {
//                            //other server config
//                            Element jaxwsServer = (Element) servletDoc.getElementsByTagName
//                                                                  ("jaxws:server").item(0);
//                            //getting jaxws config
//                        }
                        break;
                    default:
                        //other servlet config
                        break;
                }
            }

        } catch (ParserConfigurationException e) {
            log.error("Failed to parse configuration: " + e);
            throw new APIEverywhereException("Failed to parse configuration ", e);
        } catch (SAXException e) {
            log.error("Failed to parse xml configuration: " + e);
            throw new APIEverywhereException("Failed to parse xml configuration ", e);
        } catch (IOException e) {
            log.error("The config file is not found: " + e);
            throw new APIEverywhereException("The config file is not found ", e);
        }
        return serverParams;
    }


    /**
     * produces APIs and add to the @generatedApiPaths array List
     *
     * @param baseUrl     the url generated from the config
     * @param reflections      the Reflection object which scanned the current classes
     * @param classes     the classes for scanning
     */
    private void scanMethodAnnotation(StringBuilder baseUrl, Reflections reflections,
                                      Set<Class<?>> classes) {
        for (Class cl : classes) {
            Path path = (Path) cl.getAnnotation(Path.class);
            if (path == null) {
                continue;
            }
            //append path in class annotation
            baseUrl.append(path.value());

            Set<Method> methods = reflections.getMethodsAnnotatedWith(Path.class);
            for (Method me : methods) {
                Path methodPath = me.getAnnotation(Path.class);
                String url = baseUrl + methodPath.value();
                // if the Path in class has only '/' then the url have '//'
                url = url.replace("//", "/");
                //remove path param
                int index = url.indexOf("{");
                if (index > 0) {
                    url = url.substring(0, index);
                }

                String finalUrl = url;
                List<APIPath> sameAPI = generatedApiPaths
                        .stream()
                        .filter(p -> p.getUrl().equals((finalUrl)))
                        .collect(Collectors.toList());
                if (sameAPI.size() > 0) {
                    sameAPI.get(0).addProp(me);
                } else {
                    APIPath apiPath = new APIPath(finalUrl);
                    apiPath.addProp(me);
                    generatedApiPaths.add(apiPath);
                }
            }
        }
    }
}
