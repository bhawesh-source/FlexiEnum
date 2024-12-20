package com.bhawesh_source;


import com.bhawesh_source.configs.FlexiEnum;

public class EntityName extends FlexiEnum {
    protected EntityName(String name) {
        super(name);
    }

    public static final EntityName GLOBAL = new EntityName("GLOBAL");
}
