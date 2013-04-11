package com.yammer.dropwizard.validation;

import com.google.common.collect.ImmutableList;

import javax.validation.ConstraintViolation;
import java.util.Set;

class Util {
    static <T> ImmutableList<String> formatViolations(Set<ConstraintViolation<T>> violations) {
        return new ValidationException(violations).getErrors();
    }
}
