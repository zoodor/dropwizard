package com.codahale.dropwizard.jersey.errors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
                isGetterVisibility = JsonAutoDetect.Visibility.NONE,
                fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class LoggedException extends Exception {
    private final long id;

    public LoggedException(long id, Throwable e) {
        super("There was an error processing your request. It has been logged.", e);
        this.id = id;
    }

    @JsonProperty
    public String getId() {
        return String.format("%016x", id);
    }

    @Override
    @JsonProperty
    public String getMessage() {
        return super.getMessage();
    }
}
