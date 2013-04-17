package com.yammer.dropwizard.jersey.errors;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.fest.assertions.api.Assertions.assertThat;

public class JacksonExceptionMapperTest {
    private final JacksonExceptionMapper mapper = new JacksonExceptionMapper();

    @Test
    public void returns400BadRequestOnJsonDeserProblem() throws Exception {
        final JsonMappingException exception = new JsonMappingException("Can't do that");

        final Response response = mapper.toResponse(exception);
        assertThat(response.getStatus())
                .isEqualTo(400);
        assertThat(response.getEntity())
                .isInstanceOf(JacksonException.class);

        final JacksonException jacksonException = (JacksonException) response.getEntity();

        assertThat(jacksonException.getCause())
                .isEqualTo(exception);
    }

    @Test
    public void returns500OnJsonConstructorError() throws Exception {
        final JsonMappingException exception = new JsonMappingException("No suitable constructor found");
        final Response response = mapper.toResponse(exception);
        assertThat(response.getStatus())
                .isEqualTo(500);
    }

    @Test
    public void returns500OnJsonGenerationError() throws Exception {
        final JsonGenerationException exception = new JsonGenerationException("Can't do that");
        final Response response = mapper.toResponse(exception);
        assertThat(response.getStatus())
                .isEqualTo(500);
    }
}
