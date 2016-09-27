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
import org.wso2.appserver.apieverywhere.utils.Constants;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

    // TODO: 9/26/16 refactor this class

    private static final Log log = LogFactory.getLog(APIScanner.class);

    /**
     * Scan the deployed web apps
     * It will scan the web.xml and cxf-servlet.xml of the web app and find out the service classes and it's base path
     * Then it will scan those classes for the annotation and get the list of the methods.
     * Then scan those methods, method params annotation and return type
     * Then initiate a new Thread to create API in the API Publisher
     *
     * @param servletContext     the deployed web apps' servlet context
     *
     */
    Optional<APICreateRequest> scan(ServletContext servletContext) throws APIEverywhereException {


        HashMap<String, StringBuilder> serverParams = scanConfigs(servletContext);

        if (!serverParams.isEmpty()) {
            APICreateRequest apiCreateRequest = new APICreateRequest();
            List<APIPath> generatedApiPaths = new ArrayList<>();
            apiCreateRequest.setContext(servletContext.getContextPath());
            String realPath = servletContext.getRealPath("/");
            apiCreateRequest.setName(realPath.substring(realPath.lastIndexOf("/")));

            for (Map.Entry<String, StringBuilder> entry : serverParams.entrySet()) {
                //append address in beans
                StringBuilder url = entry.getValue();

                //scanning annotations
                Reflections reflections = new Reflections(entry.getKey(),
                        new MethodAnnotationsScanner(), new TypeAnnotationsScanner(),
                        new SubTypesScanner());


                Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Path.class);
                generatedApiPaths = scanMethodAnnotation(url, reflections, classes, generatedApiPaths);

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
                        generatedApiPaths = scanMethodAnnotation(url, tempReflection, classes, generatedApiPaths);
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
            return Optional.of(apiCreateRequest);
        }
        return Optional.empty();
    }


    /**
     * Scan for beans in config xml files in cxf servlet
     *
     * @param servletContext     the deployed web apps' servlet context
     * @return HashMap of <Bean class name, Bean url>
     */
    private HashMap<String, StringBuilder> scanConfigs(ServletContext servletContext) throws APIEverywhereException {

        String baseUrl = servletContext.getContextPath();

        //Map of <className, UrlPattern> that stores the class name and the address from beans
        HashMap<String, StringBuilder> beanParams = new HashMap<>();

        String webXmlPath = servletContext.getRealPath("/") + Constants.WEB_XML_LOCATION;
        //default servlet xml path;
        String servletXmlPath = servletContext.getRealPath("/") +  Constants.CXF_SERVLET_XML_LOCATION;

        try {
            //reading from web.xml
            File webXmlFile = new File(webXmlPath);
            if (!webXmlFile.exists()) {
                if (log.isDebugEnabled()) {
                    log.debug("It is not a CXF Servlet : " + servletContext.getContextPath());
                }
                return beanParams;
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

            Document webXmlDoc = dbFactory.newDocumentBuilder().parse(webXmlPath);
            webXmlDoc.getDocumentElement().normalize();

            NodeList servletList = webXmlDoc.getElementsByTagName(Constants.SERVLET);
            NodeList servletMappingList = webXmlDoc.getElementsByTagName(Constants.SERVLET_MAPPING);

            if (servletList != null && servletMappingList != null) {

                // check for cxf-servlet config from context param.
                NodeList contextParams = webXmlDoc.getElementsByTagName(Constants.CONTEXT_PARAM);
                for (int i = 0; i < contextParams.getLength(); i++) {
                    Element contextParam = (Element) contextParams.item(i);
                    String paramName = contextParam.getElementsByTagName(Constants.PARAM_NAME)
                                                                                .item(0).getTextContent().trim();
                    if (Objects.equals(paramName, Constants.CONTEXT_CONFIG_LOC)) {
                        servletXmlPath = servletContext.getRealPath("/") + contextParam.
                                getElementsByTagName(Constants.PARAM_VALUE).item(0).getTextContent().trim();
                    }
                }

                HashMap<String, String> servletMappingParams = new HashMap<>(); // <servletName, UrlPattern>
                for (int i = 0; i < servletMappingList.getLength(); i++) {
                    Element servletMapping = (Element) servletMappingList.item(i);
                    String urlPattern = servletMapping.getElementsByTagName(Constants.URL_PATTERN)
                            .item(0).getTextContent();
                    if (urlPattern.endsWith("*")){
                        urlPattern = urlPattern.substring(0, urlPattern.length() - 2);
                    } else {
                        if (urlPattern.endsWith("/")) {
                            urlPattern = urlPattern.substring(0, urlPattern.length() - 1);
                        }
                    }
                    String servletName = servletMapping.getElementsByTagName(Constants.SERVLET_NAME).item(0).
                            getTextContent().trim();
                    servletMappingParams.put(servletName, urlPattern);
                }

                for (int i = 0; i < servletList.getLength(); i++) {
                    Element servlet = (Element) servletList.item(i);
                    String servletName = servlet.getElementsByTagName(Constants.SERVLET_NAME)
                                                                            .item(0).getTextContent().trim();
                    if (servletMappingParams.containsKey(servletName)) {
                        StringBuilder urlBuilder = new StringBuilder(baseUrl);
                        String urlPatternString = servletMappingParams.get(servletName);
                        urlBuilder.append(urlPatternString);
                        String servletClassName = servlet.getElementsByTagName(Constants.SERVLET_CLASS)
                                .item(0).getTextContent().trim();

                        NodeList initParams = servlet.getElementsByTagName(Constants.INIT_PARAM);
                        switch (servletClassName) {
                            case Constants.CXF_NON_SPRING_JAXRS_SERVLET:
                                //getting bean from init-param
                                for (int j = 0; j < initParams.getLength(); j++) {
                                    Element initParam = (Element) initParams.item(j);
                                    String paramName = initParam.getElementsByTagName(Constants.PARAM_NAME)
                                            .item(0).getTextContent().trim();
                                    if (Objects.equals(paramName, Constants.JAXRS_SERVICE_CLASSES)) {
                                        String[] classNames = initParam.getElementsByTagName(Constants.PARAM_VALUE)
                                                .item(0).getTextContent().trim().split(",");
                                        Arrays.stream(classNames).forEach(className ->
                                                beanParams.put(className, urlBuilder));
                                    }
                                }
                                break;
                            case Constants.CXF_SERVLET:
                                for (int j = 0; j < initParams.getLength(); j++) {
                                    Element initParam = (Element) initParams.item(j);
                                    String paramName = initParam.getElementsByTagName(Constants.PARAM_NAME)
                                            .item(0).getTextContent().trim();
                                    if (Objects.equals(paramName, Constants.CONFIG_LOC)) {
                                        String xmlPath = initParam.getElementsByTagName(Constants.PARAM_VALUE)
                                                .item(0).getTextContent().trim();
                                        servletXmlPath = servletContext.getRealPath("/") + xmlPath;
                                    }
                                }


                                //getting beans from servletXml file
                                Document servletDoc = dbFactory.newDocumentBuilder().parse(servletXmlPath);
                                servletDoc.getDocumentElement().normalize();

                                HashMap<String, String> serverParams = new HashMap<>(); // <beanId, address>
                                NodeList jaxrsServerList = servletDoc.getElementsByTagName(Constants.JAXRS_SERVER);
                                for (int j = 0; j < jaxrsServerList.getLength(); j++) {
                                    Element jaxrsServer = (Element) jaxrsServerList.item(j);
                                    String address = jaxrsServer.getAttribute(Constants.ADDRESS);
                                    NodeList jaxrsServerBeans = jaxrsServer
                                                                .getElementsByTagName(Constants.JAXRS_SERVICE_BEANS);
                                    for (int k = 0; k < jaxrsServerBeans.getLength(); k++) {
                                        Element jaxrsServerBean = (Element) jaxrsServerBeans.item(k);
                                        Element ref = (Element) jaxrsServerBean
                                                                        .getElementsByTagName(Constants.REF).item(0);
                                        String beanId = ref.getAttribute(Constants.BEAN).trim();
                                        serverParams.put(beanId, address);

                                    }
                                }

                                if (!serverParams.isEmpty()) {
                                    NodeList beans = servletDoc.getElementsByTagName(Constants.BEAN);
                                    for (int j = 0; j < beans.getLength(); j++) {
                                        Element bean = (Element) beans.item(j);
                                        String beanId = bean.getAttribute(Constants.ID);
                                        if (serverParams.containsKey(beanId)) {
                                            String address = serverParams.get(beanId);
                                            String className = bean.getAttribute(Constants.CLASS);
                                            urlBuilder.append(address);
                                            beanParams.put(className, urlBuilder);
                                        }
                                    }
                                }
                                break;
                            default:
                                //other servlet config
                                break;
                        }
                    }
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
        return beanParams;
    }


    /**
     * Scan the classes to get the annotated methods and generate APIPath
     * Add to the gerneratedApiPath arraylist
     * @param baseUrl     the url generated from the config
     * @param reflections      the Reflection object which scanned the current classes
     * @param classes     the classes for scanning
     * @param generatedApiPaths     the List of APIPath generated
     */
    private List<APIPath> scanMethodAnnotation(StringBuilder baseUrl, Reflections reflections,
                                               Set<Class<?>> classes, List<APIPath> generatedApiPaths) {
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
                //search for the same API Path in the array list and  and attach it to the list
                List<APIPath> sameAPI = generatedApiPaths.stream()
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
        return generatedApiPaths;
    }
}
