package org.wso2.appserver.apieverywhere.utils;

import java.lang.reflect.Method;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

/**
 * Utility class to create an API to publish on API Publisher
 *
 * @since 6.0.0
 */
public class API {
//    private Consumes consume;
//    private Produces produce;
    private String url;
//    private Path path;
    private String type;

//    public Consumes getConsume() {
//        return consume;
//    }
//
//    public void setConsume(Consumes consume) {
//        this.consume = consume;
//    }
//
//    public Produces getProduce() {
//        return produce;
//    }
//
//    public void setProduce(Produces produce) {
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

//        consume = me.getDeclaredAnnotation(Consumes.class);
//        produce = me.getDeclaredAnnotation(Produces.class);


        Path path = me.getAnnotation(Path.class);
        this.url = baseUrl + path.value();
    }

    public API() {

    }


    public String toString() {
        return ("API:"
                + "\n url- " + url
                + "\n type- " + type
//                + "\n produces- " + produce
//                + "\n consumes- " + consume
        );
    }
}
