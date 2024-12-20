package com.bhawesh_source;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlexiEnumSerializerTest {

    @Test
    void testSerializeSingleEnum() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(EntityName.GLOBAL);
        assertEquals("\"GLOBAL\"", json, "Serialization of single enum should return its name in quotes.");
    }

    @Test
    void testSerializeEnumList() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(new EntityName[]{EntityName.GLOBAL, new EntityName("LOCAL")});
        assertEquals("[\"GLOBAL\",\"LOCAL\"]", json, "Serialization of enum list should return their names in an array.");
    }
}

