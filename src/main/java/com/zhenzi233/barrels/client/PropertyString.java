package com.zhenzi233.barrels.client;

import net.minecraftforge.common.property.IUnlistedProperty;

public class PropertyString implements IUnlistedProperty<String> {
    private final String name;

    public PropertyString(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean isValid(String value) {
        return !value.isEmpty();
    }

    public Class<String> getType() {
        return String.class;
    }

    public String valueToString(String value) {
        return value;
    }
}
