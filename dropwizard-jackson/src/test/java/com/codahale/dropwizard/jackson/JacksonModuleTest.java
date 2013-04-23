package com.codahale.dropwizard.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.validation.ConstraintViolation;

import static org.fest.assertions.api.Assertions.assertThat;

public class JacksonModuleTest {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    public interface Thing {

    }

    @JsonTypeName("named")
    public static class NamedThing implements Thing {
        @JsonProperty
        String name;
    }

    private final Injector injector = Guice.createInjector(
            new JacksonModule() {
                @Override
                protected void configureJackson() {
                    registerSubtype(NamedThing.class);
                }
            },
            new JacksonModule("woo"),
            new JacksonModule("config")
    );

    @Test
    public void createsASingletonObjectMapper() throws Exception {
        final Key<ObjectMapper> key = Key.get(ObjectMapper.class, Names.named("woo"));
        assertThat(injector.getInstance(key))
                .isSameAs(injector.getInstance(key));
    }

    @Test
    public void usesAnnotationSensitivePropertyNamingStrategy() throws Exception {
        final ObjectMapper mapper = injector.getInstance(ObjectMapper.class);

        assertThat(mapper.getSerializationConfig().getPropertyNamingStrategy())
                .isInstanceOf(AnnotationSensitivePropertyNamingStrategy.class);
        assertThat(mapper.getDeserializationConfig().getPropertyNamingStrategy())
                .isInstanceOf(AnnotationSensitivePropertyNamingStrategy.class);
    }

    @Test
    public void includesGuavaSupport() throws Exception {
        final ObjectMapper mapper = injector.getInstance(ObjectMapper.class);
        final TypeFactory typeFactory = mapper.getTypeFactory();
        assertThat(mapper.canDeserialize(typeFactory.constructType(ImmutableList.class)))
                .isTrue();
    }

    @Test
    public void includesGuavaExtrasSupport() throws Exception {
        final ObjectMapper mapper = injector.getInstance(ObjectMapper.class);
        final TypeFactory typeFactory = mapper.getTypeFactory();
        assertThat(mapper.canDeserialize(typeFactory.constructType(HostAndPort.class)))
                .isTrue();
    }

    @Test
    public void includesJodaTimeSupport() throws Exception {
        final ObjectMapper mapper = injector.getInstance(ObjectMapper.class);
        final TypeFactory typeFactory = mapper.getTypeFactory();
        assertThat(mapper.canDeserialize(typeFactory.constructType(DateTime.class)))
                .isTrue();
    }

    @Test
    public void includesValidationSupport() throws Exception {
        final ObjectMapper mapper = injector.getInstance(ObjectMapper.class);
        assertThat(mapper.canSerialize(ConstraintViolation.class))
                .isTrue();
    }

    @Test
    public void registersSubTypes() throws Exception {
        final ObjectMapper mapper = injector.getInstance(ObjectMapper.class);

        final Thing thing = mapper.readValue("{\"type\":\"named\",\"name\":\"Happy\"}", Thing.class);
        assertThat(thing)
                .isInstanceOf(NamedThing.class);
        assertThat(((NamedThing) thing).name)
                .isEqualTo("Happy");
    }
}
