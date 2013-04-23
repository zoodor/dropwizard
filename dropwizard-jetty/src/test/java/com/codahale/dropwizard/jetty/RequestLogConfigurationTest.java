package com.codahale.dropwizard.jetty;

import com.codahale.dropwizard.config.ConfigurationFactory;
import com.codahale.dropwizard.jackson.JacksonModule;
import com.codahale.dropwizard.logging.ConsoleLoggingOutput;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.inject.Guice;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import java.io.File;
import java.util.TimeZone;

import static org.fest.assertions.api.Assertions.assertThat;

public class RequestLogConfigurationTest {
    private final RequestLogConfiguration defaultConfig = new RequestLogConfiguration();
    private RequestLogConfiguration config;

    @Before
    public void setUp() throws Exception {
        final File file = new File(Resources.getResource("request-log.yml").toURI());
        final ObjectMapper objectMapper = Guice.createInjector(new JacksonModule())
                                               .getInstance(ObjectMapper.class);
        this.config = new ConfigurationFactory<RequestLogConfiguration>(Validation.buildDefaultValidatorFactory()
                                                                                  .getValidator(),
                                                                        RequestLogConfiguration.class,
                                                                        objectMapper,
                                                                        "dw-test").build(file);
    }

    @Test
    public void defaultsToUTC() throws Exception {
        assertThat(defaultConfig.getTimeZone())
                .isEqualTo(TimeZone.getTimeZone("UTC"));
    }

    @Test
    public void defaultsToLoggingToTheConsole() throws Exception {
        assertThat(defaultConfig.getOutputs())
                .hasSize(1);

        assertThat(defaultConfig.getOutputs().get(0))
                .isInstanceOf(ConsoleLoggingOutput.class);
    }

    @Test
    public void loadsTimeZoneFromConfigFile() throws Exception {
        assertThat(config.getTimeZone())
                .isEqualTo(TimeZone.getTimeZone("America/Los_Angeles"));
    }
}
