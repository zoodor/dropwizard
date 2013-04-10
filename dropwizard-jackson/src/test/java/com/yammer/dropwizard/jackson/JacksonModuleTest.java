package com.yammer.dropwizard.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class JacksonModuleTest {
    private final Injector injector = Guice.createInjector(
            new JacksonModule(),
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

        final ImmutableList<String> list = mapper.readValue("[]", new TypeReference<ImmutableList<String>>() {});
        assertThat(list)
                .isEmpty();
    }

    @Test
    public void includesGuavaExtrasSupport() throws Exception {
        final ObjectMapper mapper = injector.getInstance(ObjectMapper.class);

        assertThat(mapper.readValue("\"example.com:8080\"", HostAndPort.class))
                .isEqualTo(HostAndPort.fromParts("example.com", 8080));
    }

    @Test
    public void includesJodaTimeSupport() throws Exception {
        final ObjectMapper mapper = injector.getInstance(ObjectMapper.class);

        assertThat(mapper.writeValueAsString(new DateTime(-100)))
                .isEqualTo("-100");
    }
}
