package com.bhawesh_source.configs;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * A custom serializer for {@link FlexiEnum} types to support JSON serialization.
 * Converts FlexiEnum instances into their string representations (name) during serialization.
 */
public class FlexiEnumSerializer extends StdSerializer<FlexiEnum> {

    public FlexiEnumSerializer(Class t) {
        super(t);
    }

    public FlexiEnumSerializer() {
        this(null);
    }

    /**
     * Serializes a FlexiEnum instance into its string representation (name).
     *
     * @param flexiEnum     the FlexiEnum instance to serialize.
     * @param jsonGenerator the JSON generator used to write JSON content.
     * @param provider      the serializer provider.
     * @throws IOException if an error occurs during serialization.
     */
    @Override
    public void serialize(FlexiEnum flexiEnum, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeString(flexiEnum.name());
    }
}
