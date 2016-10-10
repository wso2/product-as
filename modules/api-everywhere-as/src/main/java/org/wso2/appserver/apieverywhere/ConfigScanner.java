package org.wso2.appserver.apieverywhere;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wso2.appserver.apieverywhere.exceptions.APIEverywhereException;
import org.wso2.appserver.apieverywhere.utils.Constants;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * The class which scan the deployed web app and extract the config information.
 *
 * @since 6.0.0
 */
class ConfigScanner {

    //default servlet xml path;
    private String cxfServletXmlLocation = "/WEB-INF/cxf-servlet.xml";

    private static final Log log = LogFactory.getLog(ConfigScanner.class);

    /**
     * Scan the deployed web apps
     * It will scan the web.xml and cxf-servlet.xml of the web app and find out the service classes and it's base path
     * Scan for beans in config xml files in cxf servlet
     *
     * @param servletContext the deployed web apps' servlet context
     * @return HashMap of <Bean class name, url pattern>
     */
    HashMap<String, StringBuilder> scanConfigs(ServletContext servletContext) throws APIEverywhereException {

        //Map of <Bean class name, UrlPattern> that stores the class name and the address from beans
        HashMap<String, StringBuilder> beanParams = new HashMap<>();

        String webXmlPath = servletContext.getRealPath("/") + Constants.WEB_XML_LOCATION;


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

                IntStream.range(0, contextParams.getLength()).mapToObj(i -> (Element) contextParams.item(i))
                        .filter(contextParam ->
                                Objects.equals(Constants.CONTEXT_CONFIG_LOC,
                                        contextParam.getElementsByTagName(Constants.PARAM_NAME).item(0)
                                                .getTextContent().trim())
                        )
                        .findFirst()
                        .ifPresent(contextParam -> cxfServletXmlLocation = contextParam
                                .getElementsByTagName(Constants.PARAM_VALUE).item(0).getTextContent().trim());


                HashMap<String, String> servletMappingParams = new HashMap<>(); // <servletName, UrlPattern>
                IntStream.range(0, servletMappingList.getLength()).mapToObj(i -> (Element) servletMappingList.item(i))
                        .forEach(servletMapping -> {
                            String urlPattern = servletMapping.getElementsByTagName(Constants.URL_PATTERN)
                                    .item(0).getTextContent();
                            if (urlPattern.endsWith("*")) {
                                urlPattern = urlPattern.substring(0, urlPattern.length() - 2);
                            } else {
                                if (urlPattern.endsWith("/")) {
                                    urlPattern = urlPattern.substring(0, urlPattern.length() - 1);
                                }
                            }
                            String servletName = servletMapping.getElementsByTagName(Constants.SERVLET_NAME).item(0).
                                    getTextContent().trim();
                            if (log.isDebugEnabled()) {
                                log.debug("adding servlet : " + servletName);
                            }
                            servletMappingParams.put(servletName, urlPattern);
                        });


                IntStream.range(0, servletList.getLength()).mapToObj(i -> (Element) servletList.item(i))
                        .filter(servlet ->
                                servletMappingParams.containsKey(servlet.getElementsByTagName(Constants.SERVLET_NAME)
                                        .item(0).getTextContent().trim()))
                        .forEach(servlet -> {
                            String servletName = servlet.getElementsByTagName(Constants.SERVLET_NAME)
                                    .item(0).getTextContent().trim();
                            if (log.isDebugEnabled()) {
                                log.debug("reading servlet: " + servletName);
                            }
                            StringBuilder urlBuilder = new StringBuilder();
                            String urlPatternString = servletMappingParams.get(servletName);
                            urlBuilder.append(urlPatternString);
                            String servletClassName = servlet.getElementsByTagName(Constants.SERVLET_CLASS)
                                    .item(0).getTextContent().trim();

                            NodeList initParams = servlet.getElementsByTagName(Constants.INIT_PARAM);
                            if (log.isDebugEnabled()) {
                                log.debug("reading servlet class name" + servletClassName);
                            }
                            switch (servletClassName) {
                                case Constants.CXF_NON_SPRING_JAXRS_SERVLET:
                                    //getting bean from init-param
                                    IntStream.range(0, initParams.getLength())
                                            .mapToObj(i -> (Element) initParams.item(i))
                                            .filter(initParam ->
                                                    Objects.equals(Constants.JAXRS_SERVICE_CLASSES,
                                                            initParam.getElementsByTagName(Constants.PARAM_NAME)
                                                                    .item(0).getTextContent().trim())
                                            )
                                            .forEach(initParam -> {
                                                String[] classNames = initParam
                                                                        .getElementsByTagName(Constants.PARAM_VALUE)
                                                                        .item(0).getTextContent().trim().split(",");
                                                if (log.isDebugEnabled()) {
                                                    log.debug("addind service class: " + classNames[0]
                                                            + " with url:" + urlBuilder);
                                                }
                                                beanParams.putAll(Arrays.stream(classNames).collect(Collectors.toMap(
                                                        className -> className,
                                                        className -> urlBuilder)));
                                            });
                                    break;
                                case Constants.CXF_SERVLET:
                                    IntStream.range(0, initParams.getLength())
                                            .mapToObj(i -> (Element) initParams.item(i))
                                            .filter(initParam ->
                                                    Objects.equals(Constants.CONFIG_LOC,
                                                            initParam.getElementsByTagName(Constants.PARAM_NAME).item(0)
                                                                        .getTextContent().trim())
                                            )
                                            .findFirst()
                                            .ifPresent(initParam ->
                                                    cxfServletXmlLocation = initParam
                                                                            .getElementsByTagName(Constants.PARAM_VALUE)
                                                                            .item(0).getTextContent().trim());
                                    try {
                                        //getting beans from servletXml file
                                        String servletXmlPath = servletContext.getRealPath("/") + cxfServletXmlLocation;
                                        if (log.isDebugEnabled()) {
                                            log.debug("reading servlet config : " + servletXmlPath);
                                        }
                                        Document servletDoc = dbFactory.newDocumentBuilder().parse(servletXmlPath);
                                        servletDoc.getDocumentElement().normalize();

                                        HashMap<String, String> serverParams = new HashMap<>(); // <beanId, address>
                                        NodeList jaxrsServerList =
                                                servletDoc.getElementsByTagName(Constants.JAXRS_SERVER);

                                        IntStream.range(0, jaxrsServerList
                                                .getLength())
                                                .mapToObj(i -> (Element) jaxrsServerList.item(i))
                                                .forEach(jaxrsServer -> {
                                                    String address = jaxrsServer.getAttribute(Constants.ADDRESS);
                                                    NodeList jaxrsServerBeans = jaxrsServer
                                                            .getElementsByTagName(Constants.JAXRS_SERVICE_BEANS);
                                                    serverParams.putAll(
                                                            IntStream.range(0, jaxrsServerBeans.getLength())
                                                            .mapToObj(i ->
                                                                    ((Element) ((Element) jaxrsServerBeans.item(i))
                                                                            .getElementsByTagName(Constants.REF)
                                                                    .item(0)).getAttribute(Constants.BEAN).trim())
                                                            .collect(Collectors.toMap(ref -> ref, ref -> address))
                                                    );
                                                });

                                        NodeList beans = servletDoc.getElementsByTagName(Constants.BEAN);
                                        beanParams.putAll(
                                                IntStream.range(0, beans.getLength())
                                                .mapToObj(i -> (Element) beans.item(i))
                                                .filter(bean -> serverParams
                                                                        .containsKey(bean.getAttribute(Constants.ID)))
                                                .collect(Collectors.toMap(
                                                        bean -> bean.getAttribute(Constants.CLASS),
                                                        bean -> urlBuilder.append(serverParams
                                                                                .get(bean.getAttribute(Constants.ID)))
                                                        )
                                                )
                                        );
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
                                    break;
                                default:
                                    if (log.isDebugEnabled()) {
                                        log.debug("servlet with non jax-rs config -- servlet class name: " +
                                                servletClassName);
                                    }
                                    //other servlet config
                                    break;
                            }

                        });
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
}
