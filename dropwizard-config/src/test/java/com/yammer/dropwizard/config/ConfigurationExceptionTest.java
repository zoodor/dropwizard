package com.yammer.dropwizard.config;

import com.yammer.dropwizard.validation.ConstraintViolations;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

public class ConfigurationExceptionTest {
    private static class Example {
        @NotNull
        String name;
    }

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final Set<ConstraintViolation<Example>> violations = validator.validate(new Example());
    private final ConfigurationException e = new ConfigurationException("config.yml",
                                                                        ConstraintViolations.typeErase(
                                                                                violations));

    @Test
    public void formatsTheViolationsIntoAHumanReadableMessage() throws Exception {
        assertThat(e.getMessage())
                .isEqualTo("config.yml has the following errors:\n" +
                                   "  * name may not be null (was null)\n");
    }

    @Test
    public void hasASetOfConstraintViolations() throws Exception {
        assertThat((Object) e.getConstraintViolations())
                .isEqualTo(violations);
    }
}
