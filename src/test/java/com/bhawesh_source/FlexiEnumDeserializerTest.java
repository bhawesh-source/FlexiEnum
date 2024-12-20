package com.bhawesh_source;

import com.bhawesh_source.configs.FlexiEnumStore;
import com.bhawesh_source.exception.FlexiEnumException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlexiEnumDeserializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() throws FlexiEnumException {
        mapper = new ObjectMapper();
        FlexiEnumStore.getInstance().addEnum(EntityName.class, "LOCAL");
        FlexiEnumStore.getInstance().loadDefaultEnums(Region.class);
    }

    @Test
    void testDeserializeSingleEnum() throws JsonProcessingException {
        String json = "\"GLOBAL\"";
        EntityName entityName = mapper.readValue(json, EntityName.class);

        assertNotNull(entityName, "Deserialized enum should not be null.");
        assertEquals(EntityName.GLOBAL, entityName, "Deserialized enum should match the existing enum.");
    }

    @Test
    void testDeserializeInvalidEnum() throws JsonProcessingException {
        String json = "\"INVALID\"";
        assertNull(mapper.readValue(json, EntityName.class));
    }

    @Test
    void testDeserializeEnumList() throws JsonProcessingException {
        String json = "[\"GLOBAL\",\"LOCAL\"]";
        List<EntityName> entityNames = Arrays.asList(mapper.readValue(json, EntityName[].class));

        assertEquals(2, entityNames.size(), "Deserialized list should contain all valid enums.");
        assertTrue(entityNames.contains(EntityName.GLOBAL), "Deserialized list should contain GLOBAL enum.");
        assertTrue(entityNames.contains(new EntityName("LOCAL")), "Deserialized list should contain LOCAL enum.");
    }

    @Test
    void testDeserializeWithContextualDeserializer() throws JsonProcessingException {
        String json = "\"all\"";
        Region region = mapper.readValue(json, Region.class);

        assertNotNull(region, "Deserialized enum should not be null.");
        assertEquals(Region.all, region, "Deserialized enum should match the existing default enum.");
    }
}

