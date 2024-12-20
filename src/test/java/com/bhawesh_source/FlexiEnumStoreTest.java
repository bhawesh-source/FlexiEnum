package com.bhawesh_source;

import com.bhawesh_source.configs.FlexiEnumStore;
import com.bhawesh_source.exception.FlexiEnumException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FlexiEnumStoreTest {

    private FlexiEnumStore store;

    @BeforeEach
    void setUp() {
        store = FlexiEnumStore.getInstance();
        assertNotNull(store, "FlexiEnumStore instance should not be null.");
    }

    @Test
    void testSingletonBehavior() {
        FlexiEnumStore anotherInstance = FlexiEnumStore.getInstance();
        assertSame(store, anotherInstance, "FlexiEnumStore should follow the singleton pattern.");
    }

    @Test
    void testAddEnum() throws FlexiEnumException {
        EntityName india = store.addEnum(EntityName.class, "INDIA");
        assertNotNull(india, "Added enum should not be null.");
        assertEquals("INDIA", india.name(), "Enum name should match the provided value.");
    }

    @Test
    void testAddMultipleEnums() throws FlexiEnumException {
        store.addEnums(EntityName.class, Arrays.asList("INDIA", "EUROPE"));
        List<EntityName> values = FlexiEnumStore.values(EntityName.class);
        assertEquals(3, values.size(), "Enum count should match the default plus added enums.");
        assertTrue(values.stream().anyMatch(e -> e.name().equals("INDIA")), "Enum 'INDIA' should exist.");
        assertTrue(values.stream().anyMatch(e -> e.name().equals("GLOBAL")), "Enum 'GLOBAL' should exist.");
    }

    @Test
    void testReplaceEnums() {
        Set<String> newEnums = new HashSet<>();
        newEnums.add("INDIA");
        newEnums.add("EUROPE");
        assertDoesNotThrow(() -> store.replaceEnums(EntityName.class, newEnums));
        List<EntityName> values = FlexiEnumStore.values(EntityName.class);
        assertEquals(3, values.size(), "Enum count should match the replaced enums plus default.");
        assertTrue(values.stream().anyMatch(e -> e.name().equals("INDIA")), "Enum 'USA' should exist.");
        assertTrue(values.stream().anyMatch(e -> e.name().equals("EUROPE")), "Enum 'EUROPE' should exist.");
    }

    @Test
    void testClearEnums() throws FlexiEnumException {
        store.addEnum(EntityName.class, "INDIA");
        store.clearEnums(EntityName.class);
        List<EntityName> values = store.values(EntityName.class);
        assertEquals(1, values.size(), "Only default enums should remain after clearing.");
        assertTrue(values.contains(EntityName.GLOBAL), "Default enum 'GLOBAL' should still exist.");
    }

    @Test
    void testValueOfValidEnum() throws FlexiEnumException {
        EntityName india = store.addEnum(EntityName.class, "INDIA");
        EntityName retrieved = FlexiEnumStore.valueOf("INDIA", EntityName.class);
        assertEquals(india, retrieved, "Retrieved enum should match the added enum.");
    }

    @Test
    void testValueOfInvalidEnum() {
        FlexiEnumException exception = assertThrows(FlexiEnumException.class, () ->
                FlexiEnumStore.valueOf("INVALID", EntityName.class));
        assertEquals("Invalid value \"INVALID\" for enum com.bhawesh_source.EntityName", exception.getMessage());
    }

    @Test
    void testLoadDefaultEnums() {
        store.loadDefaultEnums(EntityName.class);
        List<EntityName> values = FlexiEnumStore.values(EntityName.class);
        assertEquals(1, values.size(), "Only default enums should be loaded initially.");
        assertTrue(values.contains(EntityName.GLOBAL), "Default enum 'GLOBAL' should exist.");
    }

    @Test
    void testNullNameInAddEnum() {
        FlexiEnumException exception = assertThrows(FlexiEnumException.class, () ->
                store.addEnum(EntityName.class, (String) null));
        assertEquals("Name cannot be null or empty", exception.getMessage());
    }

    @Test
    void testConcurrentAccess() {
        assertDoesNotThrow(() -> {
            Thread thread1 = new Thread(() -> {
                try {
                    store.addEnum(EntityName.class, "THREAD1");
                } catch (FlexiEnumException e) {
                    throw new RuntimeException(e);
                }
            });
            Thread thread2 = new Thread(() -> {
                try {
                    store.addEnum(EntityName.class, "THREAD2");
                } catch (FlexiEnumException e) {
                    throw new RuntimeException(e);
                }
            });
            thread1.start();
            thread2.start();
            thread1.join();
            thread2.join();
        });

        List<EntityName> values = FlexiEnumStore.values(EntityName.class);
        assertTrue(values.stream().anyMatch(e -> e.name().equals("THREAD1")), "Enum 'THREAD1' should exist.");
        assertTrue(values.stream().anyMatch(e -> e.name().equals("THREAD2")), "Enum 'THREAD2' should exist.");
    }

    @Test
    void testSameEnumName() throws FlexiEnumException {
        store.loadDefaultEnums(EntityName.class);
        store.loadDefaultEnums(com.bhawesh_source.differentModule.EntityName.class);
        EntityName global = FlexiEnumStore.valueOf("GLOBAL", EntityName.class);
        com.bhawesh_source.differentModule.EntityName global1 = FlexiEnumStore.valueOf("GLOBAL", com.bhawesh_source.differentModule.EntityName.class);
        assertNotEquals(global,global1);
    }
}
