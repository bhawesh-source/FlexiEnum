package com.bhawesh_source.configs;

import com.bhawesh_source.exception.FlexiEnumException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * A custom deserializer for {@link FlexiEnum} types to support JSON deserialization.
 * Converts JSON string representations back into FlexiEnum instances at runtime.
 *
 * @param <T> The type of the FlexiEnum being deserialized.
 */
@Slf4j
public class FlexiEnumDeserializer<T extends FlexiEnum> extends StdDeserializer<T> implements ContextualDeserializer {

    private final Class<T> clazz;

    public FlexiEnumDeserializer() {
        this(null);
    }

    public FlexiEnumDeserializer(Class<T> clazz) {
        super(clazz);
        this.clazz = clazz;
    }

    /**
     * Deserializes a JSON string into a FlexiEnum instance.
     *
     * @param p    the JSON parser.
     * @param ctxt the deserialization context.
     * @return the deserialized FlexiEnum instance.
     * @throws IOException if an error occurs during deserialization.
     */
    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            return FlexiEnumStore.valueOf(p.getText(), clazz);
        } catch (FlexiEnumException e) {
            log.error("Error Deserializing for clazz:{}", clazz);
            return null;
        }
    }

    /**
     * Creates a contextual deserializer to handle nested or specific types.
     *
     * @param ctxt     the deserialization context.
     * @param property the property associated with the deserializer.
     * @return a new deserializer instance for the specific type.
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        JavaType type;
        if (property != null) {
            type = property.getType();
        } else {
            type = ctxt.getContextualType();
        }
        if (type.getContentType() != null) {
            type = type.getContentType();
        }
        return new FlexiEnumDeserializer<>((Class<T>) type.getRawClass());
    }
}
