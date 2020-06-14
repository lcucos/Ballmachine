package com.webproject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.metrics.graphite.GraphiteReporterFactory;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.Collections;
import java.util.Map;

public class WebProjectConfiguration extends Configuration {
    @NotEmpty
    private String template;
    
    
    private String uberUser = null;
    private String uberPassword = null;
    private String configFolder = null;
    
	private boolean isPiVirtual = true;

    @NotEmpty
    private String defaultName = "anonymous";

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @NotNull
    private Map<String, Map<String, String>> viewRendererConfiguration = Collections.emptyMap();

    @Valid
    private GraphiteReporterFactory graphiteReporterFactory = new GraphiteReporterFactory();


    @JsonProperty
    public boolean getPiVirtual() {
        return isPiVirtual;
    }
    @JsonProperty
    public void setPiVirtual(boolean isPiVirtual) {
        this.isPiVirtual = isPiVirtual;
    }
    
    @JsonProperty
    public String getUberUser() {
        return uberUser;
    }
    @JsonProperty
    public void setUberUser(String uberUser) {
        this.uberUser = uberUser;
    }

    @JsonProperty
    public String getUberPassword() {
        return uberPassword;
    }
    @JsonProperty
    public void setUberPassword(String uberPassword) {
        this.uberPassword = uberPassword;
    }

    @JsonProperty
    public void setConfigFolder(String config) {
        this.configFolder = config;
    }

    @JsonProperty
    public String getConfigFolder() {
        return configFolder;
    }

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }
/*
    public Template buildTemplate() {
        return new Template(template, defaultName);
    }
*/
    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory;
    }

    @JsonProperty("viewRendererConfiguration")
    public Map<String, Map<String, String>> getViewRendererConfiguration() {
        return viewRendererConfiguration;
    }

    @JsonProperty("viewRendererConfiguration")
    public void setViewRendererConfiguration(Map<String, Map<String, String>> viewRendererConfiguration) {
        ImmutableMap.Builder<String, Map<String, String>> builder = ImmutableMap.builder();
        for (Map.Entry<String, Map<String, String>> entry : viewRendererConfiguration.entrySet()) {
            builder.put(entry.getKey(), ImmutableMap.copyOf(entry.getValue()));
        }
        this.viewRendererConfiguration = builder.build();
    }

    @JsonProperty("metrics")
    public GraphiteReporterFactory getGraphiteReporterFactory() {
        return graphiteReporterFactory;
    }

    @JsonProperty("metrics")
    public void setGraphiteReporterFactory(GraphiteReporterFactory graphiteReporterFactory) {
        this.graphiteReporterFactory = graphiteReporterFactory;
    }
}
