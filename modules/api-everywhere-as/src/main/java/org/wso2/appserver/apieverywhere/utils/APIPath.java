package org.wso2.appserver.apieverywhere.utils;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Utility class to create an API to publish on API Publisher
 *
 * @since 6.0.0
 */
public class APIPath {

    private static final Log log = LogFactory.getLog(APIPath.class);

    private String url;
    private String type;
    private String[] consumes;
    private String[] produces;
    private ArrayList<Param> params = new ArrayList<>();
    private String returnType;

    public String getType() {
        if (type == null) {
            return "get";
        }
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

    public ArrayList<Param> getParams() {
        return params;
    }

    public String[] getConsumes() {
        if (consumes == null) {
            return new String[0];
        }
        String[] temp = new String[consumes.length];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = "\"" + consumes[i] + "\"";
        }
        return temp;
    }

    public String[] getProduces() {
        if (produces == null) {
            return new String[0];
        }
        String[] temp = new String[produces.length];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = "\"" + produces[i] + "\"";
        }
        return temp;
    }

    public APIPath(String baseUrl, Method me) {
        Annotation[] methodAnnotations = me.getAnnotations();
        for (Annotation ann : methodAnnotations) {
            switch (ann.annotationType().getName()) {
                case "javax.ws.rs.GET" :
                    type = "get";
                    break;
                case "javax.ws.rs.POST" :
                    type = "post";
                    break;
                case "javax.ws.rs.PUT" :
                    type = "put";
                    break;
                case "javax.ws.rs.DELETE" :
                    type = "delete";
                    break;
                //patch does dont support for jax-rs
//                case "javax.ws.rs.PATCH" :
//                    type = "patch";
//                    break;
                case "javax.ws.rs.HEAD" :
                    type = "head";
                    break;
                case "javax.ws.rs.OPTIONS" :
                    type = "options";
                    break;
                case "javax.ws.rs.Consumes" :
                    Consumes cons = (Consumes) ann;
                    consumes = cons.value();
                    break;
                case "javax.ws.rs.Produces" :
                    Produces pro = (Produces) ann;
                    produces = pro.value();
                    break;
                case "javax.ws.rs.Path" :
                    Path path = (Path) ann;
                    this.url = baseUrl + path.value();
                    break;
                default:
                    log.error("undefined annotation type " + ann);
                    break;
            }
        }

        // if the Path in class has only '/' then the url have '//'
        this.url = this.url.replace("//", "/");


        Parameter[] methodParams = me.getParameters();
        for (Parameter param : methodParams) {
            Annotation[] paramAnnotations = param.getAnnotations();
            Param paramObj = new Param();
            if (paramAnnotations.length > 0) {
                Annotation paramAnn = paramAnnotations[0];
                Class<? extends Annotation> annotationType = paramAnn.annotationType();
                if (annotationType.toString().contains("javax.ws.rs.PathParam")) {
                    paramObj.setParamType("path");
                    paramObj.setParamName(((PathParam) paramAnn).value());
                }
                if (annotationType.toString().contains("javax.ws.rs.HeaderParam")) {
                    paramObj.setParamType("header");
                    paramObj.setParamName(((HeaderParam) paramAnn).value());

                }
                if (annotationType.toString().contains("javax.ws.rs.QueryParam")) {
                    paramObj.setParamType("query");
                    paramObj.setParamName(((QueryParam) paramAnn).value());
                }
                if (annotationType.toString().contains("javax.ws.rs.FormParam")) {
                    paramObj.setParamType("formData");
                    paramObj.setParamName(((FormParam) paramAnn).value());
                }
            } else {
                paramObj.setParamType("body");
            }
            paramObj.setDataType(param.getType().getName());
            params.add(paramObj);
        }
        returnType = me.getReturnType().getName();
    }


    public String toString() {
        StringBuilder paramBuilder = new StringBuilder();
        for (Param pa : params) {
            paramBuilder.append(pa.toString());
        }
        return ("API:"
                + "\n url- " + url
                + "\n type- " + type
                + "\n produces- " + Arrays.toString(produces)
                + "\n consumes- " + Arrays.toString(consumes)
                + "\n returns- " + returnType
                + paramBuilder.toString()
        );
    }

    /**
     * Utility inner class to create an params of API
     *
     * @since 6.0.0
     */
    static class Param {
        private String paramName;
        private String paramType;
        private String dataType;

        void setParamName(String paramName) {
            this.paramName = paramName;
        }

        void setParamType(String paramType) {
            this.paramType = paramType;
        }

        void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getParamName() {
            if (paramName == null) {
                return "body arg";
            }
            return paramName;
        }

        public String getParamType() {
            return paramType;
        }

        public String getDataType() {
            return dataType;
        }

        public String toString() {
            return ("\n params:"
                    + "\n\t paramName- " + paramName
                    + "\n\t paramtype- " + paramType
                    + "\n\t datatype- " + dataType
            );
        }
    }
}
