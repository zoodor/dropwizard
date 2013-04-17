package com.yammer.dropwizard.jersey.errors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

public class LoggedExceptionTest {
    private final IOException cause = new IOException("Things went poorly");
    private final LoggedException exception = new LoggedException(10019, cause);

    @Test
    public void hasAnId() throws Exception {
        assertThat(exception.getId())
                .isEqualTo("0000000000002723");
    }

    @Test
    public void serializesToJson() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();

        assertThat(mapper.writeValueAsString(exception))
                .isEqualTo("{\"id\":\"0000000000002723\",\"message\":\"There was an error processing your request. It has been logged.\"}");
    }
}
