package org.wso2.appserver.apieverywhere.utils;

/**
 * The constants used in api everywhere module.
 *
 * @since 6.0.0
 */
public class Constants {

    /**
     * The version of rest api of API Publisher
     */
    public static final String API_PUBLISHER_API_VERSION = "v0.10";

    /**
     * The http POST method
     */
    public static final String HTTP_POST_METHOD = "POST";

    /**
     * Jax-rs method types.
     */
    public static final String JAVAX_GET_METHOD = "javax.ws.rs.GET";

    public static final String JAVAX_POST_METHOD = "javax.ws.rs.POST";

    public static final String JAX_RS_DELETE_METHOD = "javax.ws.rs.DELETE";

    public static final String JAX_RS_PUT_METHOD = "javax.ws.rs.PUT";

    public static final String JAX_RS_PATCH_METHOD = "javax.ws.rs.PATCH";

    public static final String JAX_RS_HEAD_METHOD = "javax.ws.rs.HEAD";

    public static final String JAX_RS_OPTIONS_METHOD = "javax.ws.rs.OPTIONS";

    public static final String JAX_RS_CONSUMES_METHOD = "javax.ws.rs.Consumes";

    public static final String JAX_RS_PRODUCES_METHOD = "javax.ws.rs.Produces";

    public static final String JAX_RS_PATH_METHOD = "javax.ws.rs.Path";

    public static final String JAX_RS_PATH_PARAM = "javax.ws.rs.PathParam";

    public static final String JAX_RS_HEADER_PARAM = "javax.ws.rs.HeaderParam";

    public static final String JAX_RS_QUERY_PARAM = "javax.ws.rs.QueryParam";

    public static final String JAX_RS_FORM_PARAM = "javax.ws.rs.FormParam";


    public static final String GET = "get";
    public static final String POST = "post";
    public static final String PUT = "put";
    public static final String DELETE = "delete";
    public static final String PATCH = "patch";
    public static final String HEAD = "head";
    public static final String OPTIONS = "options";

    /**
     * Success codes for http requests
     */
    public static final int CREATE_API_SUCCESS_CODE = 201;


    public static final int REQUEST_ACCESS_TOKEN_SUCCESS_CODE = 200;

    /**
     * Constants for jax-rs config scanning
     **/
    public static final String WEB_XML_LOCATION = "/WEB-INF/web.xml";

    public static final String SERVLET = "servlet";

    public static final String SERVLET_MAPPING = "servlet-mapping";

    public static final String CONTEXT_PARAM = "context-param";

    public static final String PARAM_NAME = "param-name";

    public static final String PARAM_VALUE = "param-value";

    public static final String CONTEXT_CONFIG_LOC = "contextConfigLocation";

    public static final String CONFIG_LOC = "config-location";

    public static final String URL_PATTERN = "url-pattern";

    public static final String SERVLET_NAME = "servlet-name";

    public static final String SERVLET_CLASS = "servlet-class";

    public static final String INIT_PARAM = "init-param";

    public static final String CXF_NON_SPRING_JAXRS_SERVLET = "org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet";

    public static final String CXF_SERVLET = "org.apache.cxf.transport.servlet.CXFServlet";

    public static final String JAXRS_SERVER = "jaxrs:server";

    public static final String JAXRS_SERVICE_BEANS = "jaxrs:serviceBeans";

    public static final String JAXRS_SERVICE_CLASSES = "jaxrs.serviceClasses";

    public static final String ADDRESS = "address";

    public static final String REF = "ref";

    public static final String BEAN = "bean";

    public static final String ID = "id";

    public static final String CLASS = "class";
}
