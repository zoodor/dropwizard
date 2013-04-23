package com.codahale.dropwizard.jersey.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Random;

@Provider
public abstract class LoggingExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingExceptionMapper.class);

    // TODO: 4/17/13 <coda> -- replace with ThreadLocalRandom
    private final Random random = new Random();

    @Override
    public Response toResponse(E exception) {
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }

        final long id = randomId();
        logException(id, exception);

        return Response.serverError()
                       .entity(new LoggedException(id, exception))
                       .build();
    }

    protected void logException(long id, E exception) {
        LOGGER.error(formatLogMessage(id, exception), exception);
    }

    @SuppressWarnings("UnusedParameters")
    protected String formatLogMessage(long id, Throwable exception) {
        return String.format("Error handling a request: %016x", id);
    }

    protected long randomId() {
        return random.nextLong();
    }
}
