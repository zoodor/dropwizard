package com.codahale.dropwizard.jersey.errors;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class JacksonExceptionMapper extends LoggingExceptionMapper<JsonProcessingException> {
    @Override
    public Response toResponse(JsonProcessingException exception) {
        /*
         * Exceptions thrown while deserializing classes which are, in fact, deserializable should
         * be considered to be malformed requests.
         */
        if (!(exception instanceof JsonGenerationException) &&
                (!exception.getMessage().startsWith("No suitable constructor found"))) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(new JacksonException(exception))
                           .build();
        }

        return super.toResponse(exception);
    }
}
