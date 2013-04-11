package com.yammer.dropwizard.validation;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.util.Set;

import static java.lang.String.format;

public class ValidationException extends Exception {
    private final ImmutableSet<ConstraintViolation<?>> violations;
    private final ImmutableList<String> errors;

    public <T> ValidationException(Set<ConstraintViolation<T>> violations) {
        this.violations = copyViolations(violations);
        this.errors = formatViolations(violations);
    }

    public ImmutableSet<ConstraintViolation<?>> getViolations() {
        return violations;
    }

    public ImmutableList<String> getErrors() {
        return errors;
    }

    private static <T> ImmutableSet<ConstraintViolation<?>> copyViolations(Set<ConstraintViolation<T>> violations) {
        final ImmutableSet.Builder<ConstraintViolation<?>> builder = ImmutableSet.builder();
        for (ConstraintViolation<T> violation : violations) {
            builder.add(violation);
        }
        return builder.build();
    }

    private static <T> ImmutableList<String> formatViolations(Set<ConstraintViolation<T>> violations) {
        final Set<String> errors = Sets.newHashSet();
        for (ConstraintViolation<T> v : violations) {
            if (v.getConstraintDescriptor().getAnnotation() instanceof ValidationMethod) {
                final ImmutableList<Path.Node> nodes = ImmutableList.copyOf(v.getPropertyPath());
                final ImmutableList<Path.Node> usefulNodes = nodes.subList(0, nodes.size() - 1);
                final String msg = v.getMessage().startsWith(".") ? "%s%s" : "%s %s";
                errors.add(format(msg, Joiner.on('.').join(usefulNodes), v.getMessage()).trim());
            } else {
                errors.add(format("%s %s (was %s)",
                                  v.getPropertyPath(),
                                  v.getMessage(),
                                  v.getInvalidValue()));
            }
        }
        return ImmutableList.copyOf(Ordering.natural().sortedCopy(errors));
    }
}
