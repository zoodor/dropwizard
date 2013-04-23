package com.codahale.dropwizard.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.google.common.base.Joiner;

import javax.validation.ConstraintViolation;
import java.io.IOException;

public class ValidationModule extends Module {
    private static final Joiner JOINER = Joiner.on('.');

    private static class ConstraintViolationSerializer extends JsonSerializer<ConstraintViolation<?>> {
        @Override
        public void serialize(ConstraintViolation<?> value,
                              JsonGenerator jgen,
                              SerializerProvider provider) throws IOException {
            jgen.writeStartObject();
            jgen.writeStringField("property", JOINER.join(value.getPropertyPath()));
            jgen.writeObjectField("value", value.getInvalidValue());
            jgen.writeStringField("message", value.getMessage());
            jgen.writeEndObject();
        }
    }

    private static class ValidationSerializers extends Serializers.Base {
        @Override
        public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
            if (ConstraintViolation.class.isAssignableFrom(type.getRawClass())) {
                return new ConstraintViolationSerializer();
            }

            return super.findSerializer(config, type, beanDesc);
        }
    }

    @Override
    public String getModuleName() {
        return "validation";
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new ValidationSerializers());
    }
}
