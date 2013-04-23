package com.codahale.dropwizard.validation;

import org.junit.Test;

import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;

import static org.fest.assertions.api.Assertions.assertThat;

public class MethodValidatorTest {
    public static class SubExample {
        @ValidationMethod(message = "also needs something special")
        public boolean isOK() {
            return false;
        }
    }

    public static class Example {
        @Valid
        @SuppressWarnings("UnusedDeclaration")
        private final SubExample subExample = new SubExample();

        @ValidationMethod(message = "must have a false thing")
        public boolean isNotOK() {
            return false;
        }

        @ValidationMethod(message = "must have a true thing")
        public boolean isOK() {
            return true;
        }
    }

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void complainsAboutMethodsWhichReturnFalse() throws Exception {
        assertThat(ConstraintViolations.format(ConstraintViolations.typeErase(validator.validate(new Example())))).containsOnly(
                "must have a false thing",
                "subExample also needs something special"
        );
    }
}
