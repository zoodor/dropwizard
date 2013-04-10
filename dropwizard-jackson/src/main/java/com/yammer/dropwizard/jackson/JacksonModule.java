package com.yammer.dropwizard.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.Maps;
import com.google.inject.PrivateModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedElementBuilder;
import com.google.inject.name.Names;

import java.lang.annotation.Annotation;
import java.util.Map;

public class JacksonModule extends PrivateModule {
    private final Annotation annotation;
    private final Class<? extends Annotation> annotationType;
    private final Map<MapperFeature, Boolean> mapperFeatures;
    private final Map<DeserializationFeature, Boolean> deserializationFeatures;
    private final Map<SerializationFeature, Boolean> serializationFeatures;
    private final Map<JsonGenerator.Feature, Boolean> generatorFeatures;
    private final Map<JsonParser.Feature, Boolean> parserFeatures;
    private final Map<JsonFactory.Feature, Boolean> factoryFeatures;
    private final Map<PropertyAccessor, JsonAutoDetect.Visibility> visibilityRules;

    public JacksonModule() {
        this(null, null);
    }

    public JacksonModule(String name) {
        this(Names.named(name), null);
    }

    public JacksonModule(Annotation annotation) {
        this(annotation, null);
    }

    public JacksonModule(Class<? extends Annotation> annotationType) {
        this(null, annotationType);
    }

    private JacksonModule(Annotation annotation, Class<? extends Annotation> annotationType) {
        this.annotation = annotation;
        this.annotationType = annotationType;
        this.mapperFeatures = Maps.newLinkedHashMap();
        this.deserializationFeatures = Maps.newLinkedHashMap();
        this.serializationFeatures = Maps.newLinkedHashMap();
        this.generatorFeatures = Maps.newLinkedHashMap();
        this.parserFeatures = Maps.newLinkedHashMap();
        this.factoryFeatures = Maps.newLinkedHashMap();
        this.visibilityRules = Maps.newLinkedHashMap();
    }

    @Override
    protected final void configure() {
        bind(GuavaModule.class);
        bind(GuavaExtrasModule.class);
        bind(JodaModule.class);

        enable(JsonParser.Feature.ALLOW_COMMENTS);
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        configureJackson();

        bind(new TypeLiteral<Map<MapperFeature, Boolean>>(){}).toInstance(mapperFeatures);
        bind(new TypeLiteral<Map<DeserializationFeature, Boolean>>() {}).toInstance(deserializationFeatures);
        bind(new TypeLiteral<Map<SerializationFeature, Boolean>>(){}).toInstance(serializationFeatures);
        bind(new TypeLiteral<Map<JsonGenerator.Feature, Boolean>>(){}).toInstance(generatorFeatures);
        bind(new TypeLiteral<Map<JsonParser.Feature, Boolean>>(){}).toInstance(parserFeatures);
        bind(new TypeLiteral<Map<JsonFactory.Feature, Boolean>>(){}).toInstance(factoryFeatures);
        bind(new TypeLiteral<Map<PropertyAccessor, JsonAutoDetect.Visibility>>(){}).toInstance(visibilityRules);

        bindObjectMapperProvider();
    }

    private void bindObjectMapperProvider() {
        final AnnotatedBindingBuilder<ObjectMapper> binder = bind(ObjectMapper.class);
        final AnnotatedElementBuilder expose = expose(ObjectMapper.class);
        if (annotation == null && annotationType == null) {
            binder.toProvider(ObjectMapperProvider.class)
                  .in(Scopes.SINGLETON);
        } else if (annotation != null) {
            binder.annotatedWith(annotation)
                  .toProvider(ObjectMapperProvider.class)
                  .in(Scopes.SINGLETON);
            expose.annotatedWith(annotation);
        } else {
            binder.annotatedWith(annotationType)
                  .toProvider(ObjectMapperProvider.class)
                  .in(Scopes.SINGLETON);
            expose.annotatedWith(annotationType);
        }
    }

    protected void configureJackson() {

    }

    protected final void enable(JsonParser.Feature... features) {
        for (JsonParser.Feature feature : features) {
            parserFeatures.put(feature, Boolean.TRUE);
        }
    }

    protected final void disable(JsonParser.Feature... features) {
        for (JsonParser.Feature feature : features) {
            parserFeatures.put(feature, Boolean.FALSE);
        }
    }

    protected final void enable(JsonGenerator.Feature... features) {
        for (JsonGenerator.Feature feature : features) {
            generatorFeatures.put(feature, Boolean.TRUE);
        }
    }

    protected final void disable(JsonGenerator.Feature... features) {
        for (JsonGenerator.Feature feature : features) {
            generatorFeatures.put(feature, Boolean.FALSE);
        }
    }

    protected final void enable(MapperFeature... features) {
        for (MapperFeature feature : features) {
            mapperFeatures.put(feature, Boolean.TRUE);
        }
    }

    protected final void disable(MapperFeature... features) {
        for (MapperFeature feature : features) {
            mapperFeatures.put(feature, Boolean.FALSE);
        }
    }

    protected final void enable(SerializationFeature... features) {
        for (SerializationFeature feature : features) {
            serializationFeatures.put(feature, Boolean.TRUE);
        }
    }

    protected final void disable(SerializationFeature... features) {
        for (SerializationFeature feature : features) {
            serializationFeatures.put(feature, Boolean.FALSE);
        }
    }

    protected final void enable(DeserializationFeature... features) {
        for (DeserializationFeature feature : features) {
            deserializationFeatures.put(feature, Boolean.TRUE);
        }
    }

    protected final void disable(DeserializationFeature... features) {
        for (DeserializationFeature feature : features) {
            deserializationFeatures.put(feature, Boolean.FALSE);
        }
    }

    protected final void enable(JsonFactory.Feature... features) {
        for (JsonFactory.Feature feature : features) {
            factoryFeatures.put(feature, Boolean.TRUE);
        }
    }

    protected final void disable(JsonFactory.Feature... features) {
        for (JsonFactory.Feature feature : features) {
            factoryFeatures.put(feature, Boolean.FALSE);
        }
    }

    protected final void setVisibilityRule(PropertyAccessor accessor,
                                           JsonAutoDetect.Visibility visibility) {
        visibilityRules.put(accessor, visibility);
    }
}
