package org.wso2.appserver.apieverywhere.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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
    private Consumes consume;
    private Produces produce;
    private Annotation type;
    private String baseUrl;
    private Path path;


    public API(String baseUrl, Method me) {
        if (me.getAnnotation(GET.class) != null) {
            type = me.getAnnotation(GET.class);
        }
        if (me.getAnnotation(POST.class) != null) {
            type = me.getAnnotation(POST.class);
        }
        if (me.getAnnotation(PUT.class) != null) {
            type = me.getAnnotation(PUT.class);
        }
        if (me.getAnnotation(DELETE.class) != null) {
            type = me.getAnnotation(DELETE.class);
        }

        consume = me.getDeclaredAnnotation(Consumes.class);
        produce = me.getDeclaredAnnotation(Produces.class);


        path = me.getAnnotation(Path.class);
        this.baseUrl = baseUrl + path.value();
    }


    public String toString() {
        return ("API:"
                + "\n baseUrl- " + baseUrl
                + "\n type- " + type
                + "\n produces- " + produce
                + "\n consumes- " + consume);
    }
}
