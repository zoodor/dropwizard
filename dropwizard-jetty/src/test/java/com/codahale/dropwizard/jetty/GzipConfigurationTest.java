package com.codahale.dropwizard.jetty;

import com.codahale.dropwizard.util.Size;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.codahale.dropwizard.config.ConfigurationFactory;
import com.codahale.dropwizard.jackson.JacksonModule;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;

public class GzipConfigurationTest {
    private final GzipConfiguration defaultConfig = new GzipConfiguration();
    private GzipConfiguration config;

    @Before
    public void setUp() throws Exception {
        final File file = new File(Resources.getResource("gzip.yml").toURI());
        final ObjectMapper objectMapper = Guice.createInjector(new JacksonModule())
                                               .getInstance(ObjectMapper.class);
        this.config = new ConfigurationFactory<GzipConfiguration>(Validation.buildDefaultValidatorFactory()
                                                                            .getValidator(),
                                                                  GzipConfiguration.class,
                                                                  objectMapper,
                                                                  "dw-test").build(file);
    }

    @Test
    public void defaultsToEnabled() throws Exception {
        assertThat(defaultConfig.isEnabled())
                .isTrue();
    }

    @Test
    public void defaultsToExcludingEntitiesLessThan256Bytes() throws Exception {
        assertThat(defaultConfig.getMinimumEntitySize())
                .isEqualTo(Size.bytes(256));
    }

    @Test
    public void defaultsToUsingAn8KBuffer() throws Exception {
        assertThat(defaultConfig.getBufferSize())
                .isEqualTo(Size.kilobytes(8));
    }

    @Test
    public void defaultsToExcludingNoUserAgents() throws Exception {
        assertThat(defaultConfig.getExcludedUserAgents())
                .isEmpty();
    }

    @Test
    public void defaultsToNotSpecifyingCompressedMimeTypes() throws Exception {
        assertThat(defaultConfig.getCompressedMimeTypes())
                .isEmpty();
    }

    @Test
    public void loadsEnabledStateFromConfigFile() throws Exception {
        assertThat(config.isEnabled())
                .isFalse();
    }

    @Test
    public void loadsMinimumEntitySizeFromConfigFile() throws Exception {
        assertThat(config.getMinimumEntitySize())
                .isEqualTo(Size.kilobytes(1));
    }

    @Test
    public void loadsBufferSizeFromConfigFile() throws Exception {
        assertThat(config.getBufferSize())
                .isEqualTo(Size.megabytes(1));
    }

    @Test
    public void loadsExcludedUserAgentsFromConfigFile() throws Exception {
        assertThat(config.getExcludedUserAgents())
                .containsOnly("one", "two");
    }

    @Test
    public void loadsCompressedMimeTypesFromConfigFile() throws Exception {
        assertThat(config.getCompressedMimeTypes())
                .containsOnly("three", "four");
    }

    @Test
    public void hasSetters() throws Exception {
        defaultConfig.setEnabled(false);
        assertThat(defaultConfig.isEnabled())
                .isFalse();

        defaultConfig.setMinimumEntitySize(Size.bytes(1));
        assertThat(defaultConfig.getMinimumEntitySize())
                .isEqualTo(Size.bytes(1));

        defaultConfig.setBufferSize(Size.bytes(2));
        assertThat(defaultConfig.getBufferSize())
                .isEqualTo(Size.bytes(2));

        defaultConfig.setExcludedUserAgents(ImmutableSet.of("one"));
        assertThat(defaultConfig.getExcludedUserAgents())
                .containsOnly("one");

        defaultConfig.setCompressedMimeTypes(ImmutableSet.of("two"));
        assertThat(defaultConfig.getCompressedMimeTypes())
                .containsOnly("two");
    }
}
