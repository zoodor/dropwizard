package com.codahale.dropwizard.config;

import com.codahale.dropwizard.validation.ConstraintViolations;
import com.google.common.collect.ImmutableList;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * An exception thrown where there is an error parsing a configuration object.
 */
public class ConfigurationException extends ConstraintViolationException {
    private static final long serialVersionUID = 5325162099634227047L;

    /**
     * Creates a new ConfigurationException for the given file with the given errors.
     *
     * @param file       the bad configuration file
     * @param violations the constraint violations
     */
    public ConfigurationException(String file, Set<ConstraintViolation<?>> violations) {
        super(formatMessage(file, violations), violations);
    }

    private static String formatMessage(String file, Set<ConstraintViolation<?>> violations) {
        final ImmutableList<String> errors = ConstraintViolations.format(violations);
        final StringBuilder msg = new StringBuilder(file)
                .append(" has the following errors:\n");
        for (String error : errors) {
            msg.append("  * ").append(error).append('\n');
        }
        return msg.toString();
    }
}
