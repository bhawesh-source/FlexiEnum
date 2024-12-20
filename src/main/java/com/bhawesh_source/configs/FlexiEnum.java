package com.bhawesh_source.configs;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.UUID;

/**
 * Extend this class to make any class enum which support both compile time and runtime/dynamic enums.
 * Create a constructor with a single string parameter.
 * To provide a default values, create a public static variable of type subclass inside same subclass
 * Created enum values are type sensitive
 * public class Region extends FlexiEnum {
 * protected Region(String name) {
 * super(name);
 * }
 * public static Region all;
 * }
 * Use {@link FlexiEnumStore} to manage enum properties (add and get enums).
 * Note: Since this is still an enum, it is advisable to store all enums at the start of the application.
 * Default values are added FlexiEnumStore if any dynamic field is added. Or call {@link FlexiEnumStore#loadDefaultEnums(Class)} at start of application
 * It is advisable to use normal enums provided by java if you don't need flexi enums.
 */


@JsonSerialize(using = FlexiEnumSerializer.class)
@JsonDeserialize(using = FlexiEnumDeserializer.class, as = FlexiEnum.class)
public abstract class FlexiEnum {
    private final String name;

    protected FlexiEnum(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        FlexiEnum flexiEnum = (FlexiEnum) obj;
        return name.equals(flexiEnum.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
