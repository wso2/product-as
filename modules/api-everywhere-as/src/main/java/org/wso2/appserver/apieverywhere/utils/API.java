package org.wso2.appserver.apieverywhere.utils;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Utility class to create an API to publish on API Publisher
 *
 * @since 6.0.0
 */
public class API {

    private static final Log log = LogFactory.getLog(API.class);

    private String url;
    private String type;
    private String[] consume;
    private String[] produce;
    private Map<String, String> paramsMap = new HashMap<>();
    private Class<?> returnType;

//    public String[] getConsume() {
//        return consume;
//    }
//
//    public void setConsume(String[] consume) {
//        this.consume = consume;
//    }
//
//    public String[] getProduce() {
//        return produce;
//    }
//
//    public void setProduce(String[] produce) {
//        this.produce = produce;
//    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public API(String baseUrl, Method me) {
        if (me.getAnnotation(GET.class) != null) {
            type = "get";
        }
        if (me.getAnnotation(POST.class) != null) {
            type = "post";
        }
        if (me.getAnnotation(PUT.class) != null) {
            type = "put";
        }
        if (me.getAnnotation(DELETE.class) != null) {
            type = "delete";
        }

        Consumes cons = me.getAnnotation(Consumes.class);
        if (cons != null) {
            consume = cons.value();
        }
        Produces prod = me.getAnnotation(Produces.class);
        if (prod != null) {
            produce = prod.value();
        }


        Path path = me.getAnnotation(Path.class);
        this.url = baseUrl + path.value();

        // if the Path in class has only '/' then the url have '//'
        this.url = this.url.replace("//", "/");


        Parameter[] params = me.getParameters();
        for (Parameter param : params) {
            Annotation[] paramAnnotations = param.getAnnotations();
            String paramAnnotation = null;
            if (paramAnnotations.length > 0) {
                Annotation paramAnn = paramAnnotations[0];
                Class<? extends Annotation> annotationType = paramAnn.annotationType();
                log.info("annotation type :" + annotationType);
                if (annotationType.toString().contains("javax.ws.rs.PathParam")) {
                    paramAnnotation = "path";
                }
                if (annotationType.toString().contains("javax.ws.rs.HeaderParam")) {
                    paramAnnotation = "header";
                }
                if (annotationType.toString().contains("javax.ws.rs.QueryParam")) {
                    paramAnnotation = "query";
                }
            } else {
                paramAnnotation = "body";
            }
            paramsMap.put(param.getType().getName(), paramAnnotation);
        }
        returnType = me.getReturnType();
    }


    public String toString() {
        return ("API:"
                + "\n url- " + url
                + "\n type- " + type
                + "\n produces- " + Arrays.toString(produce)
                + "\n consumes- " + Arrays.toString(consume)
                + "\n params- " + paramsMap.keySet() + " : " + paramsMap.values()
                + "\n returns- " + returnType
        );
    }
}
