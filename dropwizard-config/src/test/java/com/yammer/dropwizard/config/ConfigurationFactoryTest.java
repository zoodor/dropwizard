package com.yammer.dropwizard.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.File;
import java.util.Iterator;
import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;

public class ConfigurationFactoryTest {
    @SuppressWarnings("UnusedDeclaration")
    public static class Example {
        @NotNull
        @Pattern(regexp = "[\\w]+[\\s]+[\\w]+")
        private String name;

        public String getName() {
            return name;
        }
    }

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ConfigurationFactory<Example> factory =
            new ConfigurationFactory<Example>(validator,
                                              Example.class,
                                              new ObjectMapper(),
                                              "dw-config-test");

    private File malformedFile;
    private File invalidFile;
    private File validFile;

    @Before
    public void setUp() throws Exception {
        tearDown();
        this.malformedFile = new File(Resources.getResource("factory-test-malformed.yml").toURI());
        this.invalidFile = new File(Resources.getResource("factory-test-invalid.yml").toURI());
        this.validFile = new File(Resources.getResource("factory-test-valid.yml").toURI());
    }

    @After
    public void tearDown() throws Exception {
        final Iterator<Object> iterator = System.getProperties().keySet().iterator();
        while (iterator.hasNext()) {
            final String key = (String) iterator.next();
            if (key.startsWith("dw-config-test")) {
                iterator.remove();
            }
        }
    }

    @Test
    public void loadsValidConfigFiles() throws Exception {
        final Example example = factory.build(validFile);
        assertThat(example.getName())
                .isEqualTo("Coda Hale");
    }

    @Test
    public void throwsAnExceptionOnMalformedFiles() throws Exception {
        try {
            factory.build(malformedFile);
            failBecauseExceptionWasNotThrown(JsonMappingException.class);
        } catch (JsonMappingException e) {
            assertThat(e.getMessage())
                    .startsWith("Can not instantiate");
        }
    }

    @Test
    public void throwsAnExceptionOnInvalidFiles() throws Exception {
        try {
            factory.build(invalidFile);
        } catch (ConfigurationException e) {
            if ("en".equals(Locale.getDefault().getLanguage())) {
                assertThat(e.getMessage())
                        .endsWith("factory-test-invalid.yml has the following errors:\n" +
                                          "  * name must match \"[\\w]+[\\s]+[\\w]+\" (was Boop)\n");
            }
        }
    }

    @Test
    public void overridesConfigValuesWithSystemProperties() throws Exception {
        System.setProperty("dw-config-test.name", "Whee Yay");

        final Example example = factory.build(validFile);
        assertThat(example.getName())
                .isEqualTo("Whee Yay");
    }
}
