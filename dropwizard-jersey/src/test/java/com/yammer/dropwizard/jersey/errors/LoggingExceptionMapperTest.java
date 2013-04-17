package com.yammer.dropwizard.jersey.errors;

import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoggingExceptionMapperTest {
    private final RuntimeException exception = new RuntimeException("arg");
    private final ExceptionMapper<RuntimeException> mapper = new LoggingExceptionMapper<RuntimeException>() {};

    @Test
    public void mapsExceptionsTo500Responses() throws Exception {
        final Response response = mapper.toResponse(exception);

        assertThat(response.getStatus())
                .isEqualTo(500);

        assertThat(response.getEntity())
                .isInstanceOf(LoggedException.class);

        final LoggedException e = (LoggedException) response.getEntity();

        assertThat(e.getCause())
                .isEqualTo(exception);
    }

    @Test
    public void passesThroughWebApplicationExceptions() throws Exception {
        final Response appResponse = mock(Response.class);

        final WebApplicationException appEx = mock(WebApplicationException.class);
        when(appEx.getResponse()).thenReturn(appResponse);

        final Response response = mapper.toResponse(appEx);
        assertThat(response)
                .isEqualTo(appResponse);
    }
}
