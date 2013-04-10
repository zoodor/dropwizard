package com.yammer.dropwizard.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;

import javax.inject.Provider;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

@SuppressWarnings("FieldMayBeFinal")
public class ObjectMapperProvider implements Provider<ObjectMapper> {
    private final Injector injector;
    private final Map<MapperFeature, Boolean> mapperFeatures;
    private final Map<DeserializationFeature, Boolean> deserializationFeatures;
    private final Map<SerializationFeature, Boolean> serializationFeatures;
    private final Map<JsonGenerator.Feature, Boolean> generatorFeatures;
    private final Map<JsonParser.Feature, Boolean> parserFeatures;
    private final Map<JsonFactory.Feature, Boolean> factoryFeatures;
    private final Map<PropertyAccessor, JsonAutoDetect.Visibility> visibilityRules;

    @Inject(optional = true) private PropertyNamingStrategy propertyNamingStrategy =
            new AnnotationSensitivePropertyNamingStrategy();
    @Inject(optional = true) private AnnotationIntrospector annotationIntrospector;
    @Inject(optional = true) private DateFormat dateFormat;
    @Inject(optional = true) private TypeResolverBuilder<?> defaultTyping;
    @Inject(optional = true) private FilterProvider filters;
    @Inject(optional = true) private HandlerInstantiator handlerInstantiator;
    @Inject(optional = true) private InjectableValues injectableValues;
    @Inject(optional = true) private Locale locale;
    @Inject(optional = true) private Map<Class<?>, Class<?>> mixinAnnotations;
    @Inject(optional = true) private JsonNodeFactory nodeFactory;
    @Inject(optional = true) private JsonInclude.Include serializationInclusion;
    @Inject(optional = true) private SerializerFactory serializerFactory;
    @Inject(optional = true) private DefaultSerializerProvider serializerProvider;
    @Inject(optional = true) private SubtypeResolver subtypeResolver;
    @Inject(optional = true) private TimeZone timeZone;
    @Inject(optional = true) private TypeFactory typeFactory;
    @Inject(optional = true) private VisibilityChecker<?> visibilityChecker;

    @Inject
    public ObjectMapperProvider(Injector injector,
                                Map<MapperFeature, Boolean> mapperFeatures,
                                Map<DeserializationFeature, Boolean> deserializationFeatures,
                                Map<SerializationFeature, Boolean> serializationFeatures,
                                Map<JsonGenerator.Feature, Boolean> generatorFeatures,
                                Map<JsonParser.Feature, Boolean> parserFeatures,
                                Map<JsonFactory.Feature, Boolean> factoryFeatures,
                                Map<PropertyAccessor, JsonAutoDetect.Visibility> visibilityRules) {
        this.injector = injector;
        this.mapperFeatures = mapperFeatures;
        this.deserializationFeatures = deserializationFeatures;
        this.serializationFeatures = serializationFeatures;
        this.generatorFeatures = generatorFeatures;
        this.parserFeatures = parserFeatures;
        this.factoryFeatures = factoryFeatures;
        this.visibilityRules = visibilityRules;
    }

    @Override
    public ObjectMapper get() {
        final ObjectMapper mapper = new ObjectMapper();

        bindModules(mapper, injector);

        for (Map.Entry<MapperFeature, Boolean> entry : mapperFeatures.entrySet()) {
            mapper.configure(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<DeserializationFeature, Boolean> entry : deserializationFeatures.entrySet()) {
            mapper.configure(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<SerializationFeature, Boolean> entry : serializationFeatures.entrySet()) {
            mapper.configure(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<JsonGenerator.Feature, Boolean> entry : generatorFeatures.entrySet()) {
            mapper.getFactory().configure(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<JsonParser.Feature, Boolean> entry : parserFeatures.entrySet()) {
            mapper.getFactory().configure(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<JsonFactory.Feature, Boolean> entry : factoryFeatures.entrySet()) {
            mapper.getFactory().configure(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<PropertyAccessor, JsonAutoDetect.Visibility> rule : visibilityRules.entrySet()) {
            mapper.setVisibility(rule.getKey(), rule.getValue());
        }

        if (annotationIntrospector != null) {
            mapper.setAnnotationIntrospector(annotationIntrospector);
        }

        if (dateFormat != null) {
            mapper.setDateFormat(dateFormat);
        }

        if (defaultTyping != null) {
            mapper.setDefaultTyping(defaultTyping);
        }

        if (filters != null) {
            mapper.setFilters(filters);
        }

        if (handlerInstantiator != null) {
            mapper.setHandlerInstantiator(handlerInstantiator);
        }

        if (injectableValues != null) {
            mapper.setInjectableValues(injectableValues);
        }

        if (locale != null) {
            mapper.setLocale(locale);
        }

        if (mixinAnnotations != null) {
            mapper.setMixInAnnotations(mixinAnnotations);
        }

        if (nodeFactory != null) {
            mapper.setNodeFactory(nodeFactory);
        }

        if (propertyNamingStrategy != null) {
            mapper.setPropertyNamingStrategy(propertyNamingStrategy);
        }

        if (serializationInclusion != null) {
            mapper.setSerializationInclusion(serializationInclusion);
        }

        if (serializerFactory != null) {
            mapper.setSerializerFactory(serializerFactory);
        }

        if (serializerProvider != null) {
            mapper.setSerializerProvider(serializerProvider);
        }

        if (subtypeResolver != null) {
            mapper.setSubtypeResolver(subtypeResolver);
        }

        if (timeZone != null) {
            mapper.setTimeZone(timeZone);
        }

        if (typeFactory != null) {
            mapper.setTypeFactory(typeFactory);
        }

        if (visibilityChecker != null) {
            mapper.setVisibilityChecker(visibilityChecker);
        }

        return mapper;
    }

    private void bindModules(ObjectMapper mapper, Injector injector) {
        if (injector != null) {
            for (Key<?> key : injector.getBindings().keySet()) {
                final Type type = key.getTypeLiteral().getType();
                if (type instanceof Class) {
                    final Class<?> klass = (Class) type;
                    if (Module.class.isAssignableFrom(klass)) {
                        mapper.registerModule((Module) injector.getInstance(klass));
                    }
                }
            }

            bindModules(mapper, injector.getParent());
        }
    }
}
