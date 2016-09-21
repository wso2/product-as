package org.wso2.appserver.configuration.context;

import java.util.Optional;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class which models a holder for context level statistics publishing configurations.
 *
 * @since 6.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class WebAppApiEverywhere {
    @XmlElement(name = "create-api")
    private Boolean createApi;

    public Boolean getCreateApi() {
        return createApi;
    }

    public void setCreateApi(Boolean createApi) {
        this.createApi = createApi;
    }

    /**
     * Merges the context level api-everywhere configuration defined globally and overridden at
     * context level (if any).
     *
     * @param configuration the local, context level group of api-everywhere configuration to be merged with
     */
    void merge(WebAppApiEverywhere configuration) {
        Optional.ofNullable(configuration)
                .ifPresent(mergeable ->
                        createApi = Optional.ofNullable(mergeable.createApi)
                                .orElse(createApi));
    }
}
