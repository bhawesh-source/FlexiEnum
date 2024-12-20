package com.bhawesh_source;


import com.bhawesh_source.configs.FlexiEnum;

public class Region extends FlexiEnum {
    protected Region(String name) {
        super(name);
    }

    public static final Region all = new Region("all");

}
