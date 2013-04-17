package com.yammer.dropwizard.jetty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.yammer.dropwizard.logging.ConsoleLoggingOutput;
import com.yammer.dropwizard.logging.LoggingOutput;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.TimeZone;

// TODO: 4/16/13 <coda> -- write docs

public class RequestLogConfiguration {
    @NotNull
    @JsonProperty
    private TimeZone timeZone = TimeZone.getTimeZone("UTC");

    @Valid
    @NotNull
    @JsonProperty
    private ImmutableList<LoggingOutput> outputs = ImmutableList.<LoggingOutput>of(
            new ConsoleLoggingOutput()
    );

    public ImmutableList<LoggingOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(ImmutableList<LoggingOutput> outputs) {
        this.outputs = outputs;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}
