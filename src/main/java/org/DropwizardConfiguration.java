package org;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import org.hibernate.validator.constraints.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DropwizardConfiguration extends Configuration {

    @Valid
    @NotNull
    private Map<String, List<String>> stagpdfa = Collections.emptyMap();
    //private Map<String, Map<String, String>> viewRendererConfiguration = Collections.e;

    @JsonProperty
    public Map<String, List<String>> getStagpdfa() {
        return stagpdfa;
    }
    @JsonProperty
    public void setStagpdfa(Map<String, List<String>> stagpdfa) {
        this.stagpdfa = stagpdfa;
    }
}
