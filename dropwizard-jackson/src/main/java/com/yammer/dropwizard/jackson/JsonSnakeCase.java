package com.yammer.dropwizard.jackson;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

import java.lang.annotation.*;

/**
 * Marker annotation which indicates that the annotated case class should be serialized and
 * deserialized using {@code snake_case} JSON field names instead of {@code camelCase} field names.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonSnakeCase {

}
