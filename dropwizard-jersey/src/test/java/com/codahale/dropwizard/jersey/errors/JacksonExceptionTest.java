package com.codahale.dropwizard.jersey.errors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class JacksonExceptionTest {
    private final JsonProcessingException hasLocation = new JsonProcessingException("Blah blah\nwoop woop") {};
    private final JsonProcessingException noLocation = new JsonProcessingException("Oh no") {};

    @Test
    public void stripsTheLocationFromTheMessage() throws Exception {
        assertThat(new JacksonException(hasLocation).getMessage())
                .isEqualToIgnoringCase("Blah blah");
    }

    @Test
    public void doesNotBreakWhenThereIsNoLocation() throws Exception {
        assertThat(new JacksonException(noLocation).getMessage())
                .isEqualToIgnoringCase("Oh no");
    }

    @Test
    public void serializesToJson() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        assertThat(mapper.writeValueAsString(new JacksonException(hasLocation)))
                .isEqualTo("{\"message\":\"Blah blah\"}");

    }
}
