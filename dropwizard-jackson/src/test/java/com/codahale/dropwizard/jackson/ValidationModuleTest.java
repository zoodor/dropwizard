package com.codahale.dropwizard.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

public class ValidationModuleTest {
    private static class Example {
        @NotNull
        @SuppressWarnings("UnusedDeclaration")
        String wooHoo;
    }

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new ValidationModule());

    @Test
    public void serializesConstraintViolationsAsJsonObjects() throws Exception {
        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        final Set<ConstraintViolation<Example>> violations = validator.validate(new Example());

        assertThat(mapper.writeValueAsString(violations.iterator().next()))
                .isEqualTo("{\"property\":\"wooHoo\",\"value\":null,\"message\":\"may not be null\"}");
    }
}
