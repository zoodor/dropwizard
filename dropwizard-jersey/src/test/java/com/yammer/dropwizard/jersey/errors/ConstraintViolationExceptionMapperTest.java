package com.yammer.dropwizard.jersey.errors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.yammer.dropwizard.jackson.ValidationModule;
import com.yammer.dropwizard.jersey.JacksonMessageBodyProvider;
import com.yammer.dropwizard.jersey.errors.ConstraintViolationExceptionMapper;
import com.yammer.dropwizard.validation.ConstraintViolations;
import org.junit.Test;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static org.fest.assertions.api.Assertions.assertThat;

public class ConstraintViolationExceptionMapperTest extends JerseyTest {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory()
                                                         .getValidator();

    @Path("/test/")
    @Produces(MediaType.APPLICATION_JSON)
    public static class ExampleResource {
        @NotNull
        @SuppressWarnings("UnusedDeclaration")
        private String name;

        @GET
        public String show() {
            throw new ConstraintViolationException(
                    ConstraintViolations.typeErase(VALIDATOR.validate(this)));
        }
    }

    @Override
    protected AppDescriptor configure() {
        final ObjectMapper mapper = new ObjectMapper().registerModule(new ValidationModule());
        final ResourceConfig config = new DefaultResourceConfig();
        config.getClasses().add(ConstraintViolationExceptionMapper.class);
        config.getSingletons().add(new JacksonMessageBodyProvider(mapper, VALIDATOR));
        config.getSingletons().add(new ExampleResource());
        return new LowLevelAppDescriptor.Builder(config).build();
    }

    @Test
    public void rendersConstraintViolationExceptionsAsJSONArrays() throws Exception {
        try {
            client().resource("/test/")
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);
        } catch (UniformInterfaceException e) {
            assertThat(e.getResponse().getStatus())
                    .isEqualTo(422);

            final String entity = e.getResponse().getEntity(String.class);
            assertThat(entity)
                    .isEqualTo("[{\"property\":\"name\",\"value\":null,\"message\":\"may not be null\"}]");
        }
    }
}
