package com.bhawesh_source.configs;

import com.bhawesh_source.exception.FlexiEnumException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * Class used to manage {@link FlexiEnum} instances at runtime.
 * this class use "Single Access Factory" pattern to avoid initialization of enums from different places
 * All GET methods are static and can be used anywhere, Get methods don't use
 */
@Slf4j
public class FlexiEnumStore {
    private static boolean initiated = false;
    private final Map<String, Set<FlexiEnum>> possibleEnums = new ConcurrentHashMap<>();
    private final Map<String, Boolean> defaultEnumLoaded = new ConcurrentHashMap<>();
    private static FlexiEnumStore flexiEnumStore;

    /**
     * Private constructor for the FlexiEnumStore to enforce the singleton pattern.
     */
    private FlexiEnumStore() {
    }

    /**
     * Retrieves the singleton instance of the FlexiEnumStore.
     * Ensures only a single instance is created during the application's lifetime.
     *
     * @return the singleton instance of FlexiEnumStore or null if already initialized.
     */

    public static FlexiEnumStore getInstance() {
        if (!initiated) {
            initiated = true;
            flexiEnumStore = new FlexiEnumStore();
            return flexiEnumStore;
        }
        return flexiEnumStore;
    }

    /**
     * Clears all enums of the specified class type from the store, then load default enums
     *
     * @param clazz the class of the enums to clear.
     * @param <T>   the type of the FlexiEnum.
     */

    public <T extends FlexiEnum> void clearEnums(Class<T> clazz) {
        possibleEnums.remove(getEnumName(clazz));
        defaultEnumLoaded.put(getEnumName(clazz), false);
        loadDefaultEnums(clazz);
    }

    /**
     * Adds multiple enums of the specified class type to the store.
     *
     * @param clazz the class of the enums to add.
     * @param names a list of names for the enums to create and add.
     * @param <T>   the type of the FlexiEnum.
     */

    public <T extends FlexiEnum> void addEnums(Class<T> clazz, List<String> names) throws FlexiEnumException {
        for (String name : names) {
            addEnum(clazz, name);
        }
    }

    /**
     * Adds a single enum of the specified class type to the store.
     *
     * @param clazz the class of the enum to add.
     * @param names one or more parameters representing the constructor arguments of the enum.
     * @param <T>   the type of the FlexiEnum.
     * @return the created and added enum instance.
     * @throws RuntimeException if the enum creation fails.
     */

    public <T extends FlexiEnum> T addEnum(Class<T> clazz, String... names) throws FlexiEnumException {
        if (null == names || names.length == 0) {
            log.error("Name cannot be null or empty");
            throw new FlexiEnumException("Name cannot be null or empty");
        }
        for (String name : names) {
            if (null == name) {
                log.error("Name cannot be null or empty");
                throw new FlexiEnumException("Name cannot be null or empty");
            }
        }
        try {
            Class<?>[] parameterTypes = Arrays.stream(names).map(String::getClass).toArray(Class[]::new);
            if (!defaultEnumLoaded.getOrDefault(getEnumName(clazz), false)) {
                loadDefaultEnums(clazz, possibleEnums.computeIfAbsent(getEnumName(clazz), k -> new HashSet<>()));
                defaultEnumLoaded.put(getEnumName(clazz), true);
            }
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor(parameterTypes);
            declaredConstructor.setAccessible(true);
            T createdEnum = declaredConstructor.newInstance(names);
            possibleEnums.computeIfAbsent(getEnumName(clazz), k -> new HashSet<>()).add(createdEnum);
            return createdEnum;
        } catch (Exception e) {
            throw new FlexiEnumException("Failed to create instance of " + getEnumName(clazz), e);
        }
    }

    /**
     * Loads all default (predefined) enums of the specified class type into the store.
     *
     * @param clazz      the class of the enums to load.
     * @param flexiEnums a set to hold the loaded enums.
     * @param <T>        the type of the FlexiEnum.
     */

    public <T extends FlexiEnum> void loadDefaultEnums(Class<T> clazz, Set<FlexiEnum> flexiEnums) {
        Set<FlexiEnum> finalFlexiEnums = null == flexiEnums ? possibleEnums.computeIfAbsent(getEnumName(clazz), k -> new HashSet<>()) : flexiEnums;
        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
            if (field.getType().equals(clazz) && Modifier.isFinal(field.getModifiers())) {
                try {
                    T defaultEnum = (T) field.get(null);
                    finalFlexiEnums.add(defaultEnum);
                } catch (Exception e) {
                    log.error("Failed to load default enum value for " + getEnumName(clazz), e);
                }
            }
        });
    }

    /**
     * Loads all default (predefined) enums of the specified class type into the store.
     *
     * @param clazz the class of the enums to load.
     * @param <T>   the type of the FlexiEnum.
     */

    public <T extends FlexiEnum> void loadDefaultEnums(Class<T> clazz) {
        Set<FlexiEnum> finalFlexiEnums = possibleEnums.computeIfAbsent(getEnumName(clazz), k -> new HashSet<>());
        loadDefaultEnums(clazz, finalFlexiEnums);
    }

    /**
     * Replaces the enums of the specified class type in the store with a new set of names.
     * Default enums are loaded first, and then new enums are added.
     *
     * @param clazz the class of the enums to replace.
     * @param names a set of names for the new enums.
     * @param <T>   the type of the FlexiEnum.
     * @throws NoSuchMethodException     if the constructor of the enum class is not found.
     * @throws InvocationTargetException if the enum constructor throws an exception.
     * @throws InstantiationException    if the enum class cannot be instantiated.
     * @throws IllegalAccessException    if the constructor is inaccessible.
     */

    public <T extends FlexiEnum> void replaceEnums(Class<T> clazz, Set<String> names) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Set<FlexiEnum> flexiEnums = new HashSet<>();
        //Load default enums
        loadDefaultEnums(clazz, flexiEnums);
        //Add new enums
        for (String name : names) {
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
            T createdEnum = declaredConstructor.newInstance(name);
            flexiEnums.add(createdEnum);
        }
        //Replace from possible enums
        possibleEnums.put(getEnumName(clazz), flexiEnums);
    }

    /**
     * Retrieves an enum of the specified class type by its name.
     *
     * @param name  the name of the enum to retrieve.
     * @param clazz the class of the enum.
     * @param <T>   the type of the FlexiEnum.
     * @return the enum instance matching the given name.
     * @throws RuntimeException if the name is invalid or the enum is not found.
     */

    public static <T extends FlexiEnum> T valueOf(String name, Class<T> clazz) throws FlexiEnumException {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        Optional<FlexiEnum> flexiEnum = flexiEnumStore.possibleEnums.getOrDefault(getEnumName(clazz), new HashSet<>()).stream().filter(e -> e.name().equals(name)).findFirst();
        if (!flexiEnum.isPresent()) {
            throw new FlexiEnumException("Invalid value \"" + name + "\" for enum " + getEnumName(clazz));
        }
        return (T) flexiEnum.get();
    }

    /**
     * Retrieves all enums of the specified class type stored in the store.
     *
     * @param clazz the class of the enums to retrieve.
     * @param <T>   the type of the FlexiEnum.
     * @return a list of all stored enums of the specified type.
     */

    public static <T extends FlexiEnum> List<T> values(Class<T> clazz) {
        return flexiEnumStore.possibleEnums
                .getOrDefault(getEnumName(clazz), new HashSet<>())
                .stream()
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the fully qualified name of the specified enum class.
     *
     * @param clazz the class of the enum.
     * @param <T>   the type of the FlexiEnum.
     * @return the fully qualified class name as a string.
     */

    private static <T extends FlexiEnum> String getEnumName(Class<T> clazz) {
        return clazz.getName();
    }
}
