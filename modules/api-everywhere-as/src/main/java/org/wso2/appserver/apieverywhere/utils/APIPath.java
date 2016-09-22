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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Utility class to create an API paths for every url in the web app
 *
 * @since 6.0.0
 */
public class APIPath {

    private static final Log log = LogFactory.getLog(APIPath.class);

    private String url;
    private ArrayList<APIProp> apiProps = new ArrayList<>();

    public String getUrl() {
        return url;
    }

    public APIPath(String baseUrl) {
       this.url = baseUrl;
    }

    ArrayList<APIProp> getApiProps() {
        return apiProps;
    }

    public void addProp(Method me) {
        APIProp apiProp = new APIProp();
        Annotation[] methodAnnotations = me.getAnnotations();
        for (Annotation ann : methodAnnotations) {
            switch (ann.annotationType().getName()) {
                case "javax.ws.rs.GET" :
                    apiProp.setType(APIType.get);
                    break;
                case "javax.ws.rs.POST" :
                    apiProp.setType(APIType.post);
                    break;
                case "javax.ws.rs.PUT" :
                    apiProp.setType(APIType.put);
                    break;
                case "javax.ws.rs.DELETE" :
                    apiProp.setType(APIType.delete);
                    break;
                //patch does don't support for jax-rs
                case "javax.ws.rs.PATCH" :
                    apiProp.setType(APIType.patch);
                    break;
                case "javax.ws.rs.HEAD" :
                    apiProp.setType(APIType.head);
                    break;
                case "javax.ws.rs.OPTIONS" :
                    apiProp.setType(APIType.options);
                    break;
                case "javax.ws.rs.Consumes" :
                    Consumes cons = (Consumes) ann;
                    apiProp.setConsumes(cons.value());
                    break;
                case "javax.ws.rs.Produces" :
                    Produces pro = (Produces) ann;
                    apiProp.setProduces(pro.value());
                    break;
                case "javax.ws.rs.Path" :
                    break;
                default:
                    log.error("undefined annotation type " + ann);
                    break;
            }
        }

        Parameter[] methodParams = me.getParameters();
        for (Parameter param : methodParams) {
            Annotation[] paramAnnotations = param.getAnnotations();
            Param paramObj = new Param();
            if (paramAnnotations.length > 0) {
                Annotation paramAnn = paramAnnotations[0];
                Class<? extends Annotation> annotationType = paramAnn.annotationType();
                if (annotationType.toString().contains("javax.ws.rs.PathParam")) {
                    paramObj.setParamType(ParamType.path);
                    paramObj.setParamName(((PathParam) paramAnn).value());
                }
                if (annotationType.toString().contains("javax.ws.rs.HeaderParam")) {
                    paramObj.setParamType(ParamType.header);
                    paramObj.setParamName(((HeaderParam) paramAnn).value());

                }
                if (annotationType.toString().contains("javax.ws.rs.QueryParam")) {
                    paramObj.setParamType(ParamType.query);
                    paramObj.setParamName(((QueryParam) paramAnn).value());
                }
                if (annotationType.toString().contains("javax.ws.rs.FormParam")) {
                    paramObj.setParamType(ParamType.formData);
                    paramObj.setParamName(((FormParam) paramAnn).value());
                }
            } else {
                paramObj.setParamType(ParamType.body);
            }
            paramObj.setDataType(param.getType().getName());
            apiProp.addParam(paramObj);
        }
        apiProp.setReturnType(me.getReturnType().getName());
        apiProps.add(apiProp);
    }

    public String toString() {
        StringBuilder probBuilder = new StringBuilder();
        for (APIProp pr : apiProps) {
            probBuilder.append(pr.toString());
        }
        return ("API:"
                + "\n url- " + url
                + probBuilder.toString()
        );
    }

    /**
     * Utility inner class to add properties of API definition
     *
     * @since 6.0.0
     */
    static class APIProp {
        private APIType type;
        private String[] consumes;
        private String[] produces;
        private ArrayList<Param> params = new ArrayList<>();
        private String returnType;

        public void setType(APIType type) {
            this.type = type;
        }

        void setConsumes(String[] consumes) {
            this.consumes = consumes;
        }

        void setProduces(String[] produces) {
            this.produces = produces;
        }

        void addParam(Param param) {
            params.add(param);
        }

        void setReturnType(String returnType) {
            this.returnType = returnType;
        }

        String getType() {
            if (type == null) {
                return APIType.get.toString();
            }
            return type.toString();
        }

        ArrayList<Param> getParams() {
            return params;
        }

        String[] getConsumes() {
            if (consumes == null) {
                return new String[0];
            }
            return this.consumes;
        }

        String[] getProduces() {
            if (produces == null) {
                return new String[0];
            }
            return this.produces.clone();
        }

        public String toString() {
            StringBuilder paramBuilder = new StringBuilder();
            for (Param pa : params) {
                paramBuilder.append(pa.toString());
            }
            return ("\n props"
                    + "\n\t type- " + type
                    + "\n\t produces- " + Arrays.toString(produces)
                    + "\n\t consumes- " + Arrays.toString(consumes)
                    + "\n\t returns- " + returnType
                    + paramBuilder.toString()
            );
        }
    }

    /**
     * Utility inner class to create an params of API defniation
     *
     * @since 6.0.0
     */
    static class Param {
        private String paramName;
        private ParamType paramType;
        private String dataType;

        void setParamName(String paramName) {
            this.paramName = paramName;
        }

        void setParamType(ParamType paramType) {
            this.paramType = paramType;
        }

        void setDataType(String dataType) {
            this.dataType = dataType;
        }

        String getParamName() {
            if (paramName == null) {
                return "body arg";
            }
            return paramName;
        }

        String getParamType() {
            if (paramType == null) {
                return ParamType.body.toString();
            }
            return paramType.toString();
        }

        String getDataType() {
            return dataType;
        }

        public String toString() {
            return ("\n\t params:"
                    + "\n\t\t paramName- " + paramName
                    + "\n\t\t paramtype- " + paramType
                    + "\n\t\t datatype- " + dataType
            );
        }
    }


    /**
     * http types of the api path
     */
    private enum APIType {
        get,
        post,
        put,
        delete,
        head,
        options,
        patch
    }

    /**
     * types of params in API Path
     */
    private enum ParamType {
        body,
        path,
        header,
        query,
        formData
    }
}
