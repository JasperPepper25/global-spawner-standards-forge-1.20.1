package com.jasperpepper.globalspawnerstandards.config;

import java.util.Locale;

public enum StructureFilterMode {
    BLACKLIST,
    WHITELIST;

    public static StructureFilterMode fromConfig(String value) {
        if (value == null) return BLACKLIST;
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return BLACKLIST;
        }
    }
}
