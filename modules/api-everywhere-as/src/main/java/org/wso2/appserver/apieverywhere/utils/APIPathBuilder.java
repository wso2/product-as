package org.wso2.appserver.apieverywhere.utils;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.FormParameter;
import io.swagger.models.parameters.HeaderParameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Utility class to create an API paths for every url in the web app
 *
 * @since 6.0.0
 */
public class APIPathBuilder {

    private static final Log log = LogFactory.getLog(APIPathBuilder.class);
    private List<String> consumes;
    private List<String> produces;


    public Path addProp(Path path, Method me) {
        Annotation[] methodAnnotations = me.getAnnotations();
        Parameter[] methodParams = me.getParameters();


        Operation operation = new Operation();
        operation.setConsumes(consumes);
        operation.setProduces(produces);
        Arrays.stream(methodParams).forEach(param -> {
            Annotation[] paramAnnotations = param.getAnnotations();
            io.swagger.models.parameters.Parameter parameter = null;
            if (paramAnnotations.length > 0) {
                Annotation paramAnn = paramAnnotations[0];
                Class<? extends Annotation> annotationType = paramAnn.annotationType();
                if (annotationType.toString().contains(Constants.JAX_RS_PATH_PARAM)) {
                    parameter = new PathParameter();
                    parameter.setName(((PathParam) paramAnn).value());
                }
                if (annotationType.toString().contains(Constants.JAX_RS_HEADER_PARAM)) {
                    parameter = new HeaderParameter();
                    parameter.setName(((HeaderParam) paramAnn).value());

                }
                if (annotationType.toString().contains(Constants.JAX_RS_QUERY_PARAM)) {
                    parameter = new QueryParameter();
                    parameter.setName(((QueryParam) paramAnn).value());
                }
                if (annotationType.toString().contains(Constants.JAX_RS_FORM_PARAM)) {
                    parameter = new FormParameter();
                    parameter.setName(((FormParam) paramAnn).value());
                }
            } else {
                parameter = new BodyParameter();
                parameter.setName("body param");
            }
            operation.addParameter(parameter);
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

    public void setConsumes(List<String> consumes) {
        this.consumes = consumes;
    }

    public void setProduces(List<String> produces) {
        this.produces = produces;
    }
}
