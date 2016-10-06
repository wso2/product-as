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


    public Path addProp(Path path, Method me) {
        Annotation[] methodAnnotations = me.getAnnotations();
        Parameter[] methodParams = me.getParameters();


        Operation operation = new Operation();
        for (Parameter param : methodParams) {
            Annotation[] paramAnnotations = param.getAnnotations();
            io.swagger.models.parameters.Parameter parameter = null;
            if (paramAnnotations.length > 0) {
                Annotation paramAnn = paramAnnotations[0];
                Class<? extends Annotation> annotationType = paramAnn.annotationType();
                if (annotationType.toString().contains("javax.ws.rs.PathParam")) {
                    parameter = new PathParameter();
                    parameter.setName(((PathParam) paramAnn).value());
                }
                if (annotationType.toString().contains("javax.ws.rs.HeaderParam")) {
                    parameter = new HeaderParameter();
                    parameter.setName(((HeaderParam) paramAnn).value());

                }
                if (annotationType.toString().contains("javax.ws.rs.QueryParam")) {
                    parameter = new QueryParameter();
                    parameter.setName(((QueryParam) paramAnn).value());
                }
                if (annotationType.toString().contains("javax.ws.rs.FormParam")) {
                    parameter = new FormParameter();
                    parameter.setName(((FormParam) paramAnn).value());
                }
            } else {
                parameter = new BodyParameter();
                parameter.setName("body param");
            }
            if (parameter != null) {
                operation.addParameter(parameter);
            }
        }

        String method = "get";
        for (Annotation ann : methodAnnotations) {
            switch (ann.annotationType().getName()) {
                case Constants.JAVAX_GET_METHOD:
                    method = "get";
                    break;
                case Constants.JAVAX_POST_METHOD :
                    method = "post";
                    break;
                case Constants.JAX_RS_PUT_METHOD :
                    method = "put";
                    break;
                case Constants.JAX_RS_DELETE_METHOD :
                    method = "delete";
                    break;
                //patch does don't support for jax-rs
                case Constants.JAX_RS_PATCH_METHOD :
                    method = "patch";
                    break;
                case Constants.JAX_RS_HEAD_METHOD :
                    method = "head";
                    break;
                case Constants.JAX_RS_OPTIONS_METHOD :
                    method = "options";
                    break;
                case Constants.JAX_RS_PATH_METHOD :
                    break;
                case Constants.JAX_RS_CONSUMES_METHOD :
                    Consumes cons = (Consumes) ann;
                    operation.addConsumes(Arrays.toString(cons.value()));
                    break;
                case Constants.JAX_RS_PRODUCES_METHOD :
                    Produces pro = (Produces) ann;
                    operation.addProduces(Arrays.toString(pro.value()));
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
