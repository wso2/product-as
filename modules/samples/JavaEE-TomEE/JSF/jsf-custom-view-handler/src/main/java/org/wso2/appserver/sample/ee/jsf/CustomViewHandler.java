package org.wso2.appserver.sample.ee.jsf;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class CustomViewHandler extends ViewHandlerWrapper {
    private static final Log log = LogFactory.getLog(CustomViewHandler.class);

    private ViewHandler wrappped;

    public CustomViewHandler(ViewHandler wrappped) {
        super();
        this.wrappped = wrappped;

    }

    @Override
    public ViewHandler getWrapped() {
        return wrappped;
    }

    @Override
    public String getActionURL(FacesContext context, String viewId) {
        String url = super.getActionURL(context, viewId);
        log.debug("The getActionURL: " + url);
        return addContextPath(context, url);
    }

    @Override
    public String getRedirectURL(FacesContext context, String viewId, Map<String,
            List<String>> parameters, boolean includeViewParams) {
        String url = super.getRedirectURL(context, viewId, parameters, includeViewParams);
        log.debug("The getRedirectURL: " + url);
        return url;
    }

    @Override
    public String getResourceURL(FacesContext context, String path) {
        String url = super.getResourceURL(context, path);
        log.debug("The getResourceURL: = " + url);
        return addContextPath(context, url);
    }

    private String addContextPath(FacesContext context, String url) {
        final HttpServletRequest request = ((HttpServletRequest) context.getExternalContext().getRequest());
        String result = url;
        if (url.startsWith("/")) {
            int subpath = StringUtils.countMatches(getPath(request), "/") - 1;
            String pathPrefix = "";
            if (subpath > 0) {
                while (subpath > 0) {
                    pathPrefix += "/src/main";
                    subpath--;
                }
                pathPrefix = StringUtils.removeStart(pathPrefix, "/");
            }
            result = pathPrefix + result;
        }
        return result;
    }

    private String getPath(final HttpServletRequest request) {
        try {
            // TODO handle more than two '/'
            return StringUtils.replace(new URI(request.getRequestURI()).getPath(), "//", "/");
        } catch (final URISyntaxException e) {
            // XXX URISyntaxException ignored
            return StringUtils.EMPTY;
        }
    }
}
