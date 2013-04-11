package com.yammer.dropwizard.validation;

import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

public class ValidationExceptionTest {
    @SuppressWarnings("UnusedDeclaration")
    public static class Example {
        @NotNull
        private String name;
    }

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final Set<ConstraintViolation<Example>> violations = validator.validate(new Example());
    private final ValidationException exception = new ValidationException(violations);

    @Test
    public void hasASetOfConstraintViolations() throws Exception {
        assertThat(exception.getViolations())
                .containsAll(violations);
    }

    @Test
    public void hasASetOfErrorMessages() throws Exception {
        assertThat(exception.getErrors())
                .containsOnly("name may not be null (was null)");
    }
}
