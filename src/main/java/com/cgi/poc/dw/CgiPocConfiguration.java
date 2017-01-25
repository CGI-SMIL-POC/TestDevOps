package com.cgi.poc.dw;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Dropwizard Configuration class.
 *
 */
public class CgiPocConfiguration extends Configuration {

    /**
     * A factory to read database configuration from the configuration file.
     */
    @Valid
    @NotNull
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    /**
     * Obtain database connection parameters from the configuration file.
     *
     * @return Data source factory.
     */
    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

}
