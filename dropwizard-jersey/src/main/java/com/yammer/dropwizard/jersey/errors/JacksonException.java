package com.yammer.dropwizard.jersey.errors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Splitter;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
                isGetterVisibility = JsonAutoDetect.Visibility.NONE,
                fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class JacksonException extends Exception {
    private static final Splitter LINE_SPLITTER = Splitter.on("\n").trimResults();

    public JacksonException(JsonProcessingException cause) {
        super(stripLocation(cause.getMessage()), cause);
    }

    @Override
    @JsonProperty
    public String getMessage() {
        return super.getMessage();
    }

    private static String stripLocation(String message) {
        return LINE_SPLITTER.split(message).iterator().next();
    }
}
