package org.wso2.appserver.apieverywhere;

import com.google.gson.Gson;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.FormParameter;
import io.swagger.models.parameters.HeaderParameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.wso2.appserver.apieverywhere.exceptions.APIEverywhereException;
import org.wso2.appserver.apieverywhere.utils.APICreateRequest;
import org.wso2.appserver.apieverywhere.utils.Constants;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * class to scan the service classes and build an API for web apps
 *
 * @since 6.0.0
 */
class APIBuilder {

    private static final Log log = LogFactory.getLog(APIBuilder.class);
    private Gson gson = new Gson();
    private ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    private Map<String, Path> paths = new HashMap<>();
    private List<String> consumes;
    private List<String> produces;

    /**

     * Then it will scan those classes for the annotation and get the list of the methods.
     * Then scan those methods, method params annotation and return type
     * Then initiate Build and API and return if for Creation process
     *
     * @param beanParams HashMap of <Bean class name, url pattern> of deployed web app
     * @param apiCreateRequest Object for the APIRequest to create API.
     * @return String of the APICreateRequest
     *
     */
    String build(HashMap<String, StringBuilder> beanParams, APICreateRequest apiCreateRequest) {

        beanParams.keySet().forEach(className -> {
            //append address in beans
            StringBuilder url = beanParams.get(className);

            //scanning annotations of the class
            Reflections reflections = new Reflections(className,
                    new MethodAnnotationsScanner(), new TypeAnnotationsScanner(), new SubTypesScanner());

            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(javax.ws.rs.Path.class);
            scanMethodAnnotation(url, reflections, classes);

            try {
                //interfaces of the class
                Class<?> aClass = contextClassLoader.loadClass(className);
                Class<?>[] interfaces = aClass.getInterfaces();
                for (Class in : interfaces) {
                    Reflections tempReflection = new Reflections(in.getName(),
                            new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
                    Set<Class<?>> pathInterfaces = tempReflection.getTypesAnnotatedWith(javax.ws.rs.Path.class);
                    scanMethodAnnotation(url, tempReflection, pathInterfaces);
                }
            } catch (ClassNotFoundException e) {
                log.error("The class is not found in scanning: " + e);
                throw new APIEverywhereException("The class is not found in scanning ", e);
            }
        });

        Swagger swagger = new Swagger();
        swagger.setPaths(paths);

        String swaggerString = gson.toJson(swagger);
        //remove unwanted vendor extension field in swagger.
        while (swaggerString.contains("\"vendorExtensions\":{},")) {
            swaggerString = swaggerString.replace("\"vendorExtensions\":{},", "");
        }
        while (swaggerString.contains("\"vendorExtensions\":{}")) {
            swaggerString = swaggerString.replace("\"vendorExtensions\":{}", "");
        }
        apiCreateRequest.buildAPICreateRequest(swaggerString);
        String apiCreateRequestString = gson.toJson(apiCreateRequest);
        log.info("API Builded : " + apiCreateRequestString);
            log.info("API Builded : " + apiCreateRequest.getName());

        return apiCreateRequestString;

    }

    /**
     * Scan the classes to get the annotated methods and generate APIBuilder
     * Add to the gerneratedApiPath arraylist
     *
     * @param baseUrl     the url generated from the config
     * @param reflections the Reflection object which scanned the current classes
     * @param classes     the classes for scanning
     */
    private void scanMethodAnnotation(StringBuilder baseUrl, Reflections reflections, Set<Class<?>> classes) {
        classes.forEach(cl -> {
            javax.ws.rs.Path pathAnnotation = cl.getAnnotation(javax.ws.rs.Path.class);

            if (pathAnnotation != null) {
                //append path in class annotation
                baseUrl.append(pathAnnotation.value());

                Annotation[] classAnnotations = cl.getAnnotations();
                Arrays.stream(classAnnotations).forEach(ann -> {
                            switch (ann.annotationType().getName()) {
                                case Constants.JAX_RS_CONSUMES_METHOD :
                                    Consumes cons = (Consumes) ann;
                                    consumes = Arrays.asList(cons.value());
                                    break;
                                case Constants.JAX_RS_PRODUCES_METHOD :
                                    Produces pro = (Produces) ann;
                                    produces = Arrays.asList(pro.value());
                                    break;
                                default:
                                    break;
                            }
                        }
                );
                Set<Method> methods = reflections.getMethodsAnnotatedWith(javax.ws.rs.Path.class);
                methods.forEach(me -> {
                    javax.ws.rs.Path methodPath = me.getAnnotation(javax.ws.rs.Path.class);
                    String url = baseUrl + methodPath.value();
                    // if the Path in class has only '/' then the url have '//'
                    url = url.replace("//", "/");
                    if (url.endsWith("/")) {
                        url = url.substring(0, url.lastIndexOf("/"));
                    }
                    Path path;
                    if (paths.containsKey(url)) {
                        path = paths.get(url);
                        paths.remove(url);
                    } else {
                        path = new Path();
                    }
                    path = createPath(path, me);

                    paths.put(url, path);
                });
            }
        });
    }



    private Path createPath(Path path, Method me) {
        Annotation[] methodAnnotations = me.getAnnotations();
        Parameter[] methodParams = me.getParameters();

        Operation operation = new Operation();
        operation.setConsumes(consumes);
        operation.setProduces(produces);
        Arrays.stream(methodParams).forEach(param -> {
            Annotation[] paramAnnotations = param.getAnnotations();
            if (paramAnnotations.length > 0) {
                Annotation paramAnn = paramAnnotations[0];
                Class<? extends Annotation> annotationType = paramAnn.annotationType();
                if (annotationType.toString().contains(Constants.JAX_RS_PATH_PARAM)) {
                    PathParameter pathParameter = new PathParameter();
                    pathParameter.setName(((PathParam) paramAnn).value());
                    pathParameter.setType(param.getType().getName());
                    operation.addParameter(pathParameter);
                }
                if (annotationType.toString().contains(Constants.JAX_RS_HEADER_PARAM)) {
                    HeaderParameter headerParameter = new HeaderParameter();
                    headerParameter.setName(((HeaderParam) paramAnn).value());
                    headerParameter.setType(param.getType().getName());
                    operation.addParameter(headerParameter);
                }
                if (annotationType.toString().contains(Constants.JAX_RS_QUERY_PARAM)) {
                    QueryParameter queryParameter = new QueryParameter();
                    queryParameter.setName(((QueryParam) paramAnn).value());
                    queryParameter.setType(param.getType().getName());
                    operation.addParameter(queryParameter);
                }
                if (annotationType.toString().contains(Constants.JAX_RS_FORM_PARAM)) {
                    FormParameter formParameter = new FormParameter();
                    formParameter.setName(((FormParam) paramAnn).value());
                    formParameter.setType(param.getType().getName());
                    operation.addParameter(formParameter);
                }
            } else {
                BodyParameter bodyParameter = new BodyParameter();
                bodyParameter.setName("body param");
                operation.addParameter(bodyParameter);
            }
        });

        String method = Constants.GET;
        for (Annotation ann : methodAnnotations) {
            switch (ann.annotationType().getName()) {
                case Constants.JAVAX_GET_METHOD:
                    method = Constants.GET;
                    break;
                case Constants.JAVAX_POST_METHOD :
                    method = Constants.POST;
                    break;
                case Constants.JAX_RS_PUT_METHOD :
                    method = Constants.PUT;
                    break;
                case Constants.JAX_RS_DELETE_METHOD :
                    method = Constants.DELETE;
                    break;
                //patch does don't support for jax-rs
                case Constants.JAX_RS_PATCH_METHOD :
                    method = Constants.PATCH;
                    break;
                case Constants.JAX_RS_HEAD_METHOD :
                    method = Constants.HEAD;
                    break;
                case Constants.JAX_RS_OPTIONS_METHOD :
                    method = Constants.OPTIONS;
                    break;
                case Constants.JAX_RS_PATH_METHOD :
                    break;
                case Constants.JAX_RS_CONSUMES_METHOD :
                    Consumes cons = (Consumes) ann;
                    operation.setConsumes(Arrays.asList(cons.value()));
                    break;
                case Constants.JAX_RS_PRODUCES_METHOD :
                    Produces pro = (Produces) ann;
                    operation.setProduces(Arrays.asList(pro.value()));
                    break;
                default:
                    log.info("Invalid annotation !!! " + ann.annotationType().getName());
                    break;
            }
        }
        path.set(method, operation);
        return path;
    }
}
