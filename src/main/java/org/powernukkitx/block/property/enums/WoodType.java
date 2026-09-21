package org.powernukkitx.block.property.enums;

import lombok.Getter;


public enum WoodType {

    OAK("Oak"),

    SPRUCE("Spruce"),

    BIRCH("Birch"),

    JUNGLE("Jungle"),

    ACACIA("Acacia"),

    DARK_OAK("Dark Oak"),

    CHERRY("Cherry"),

    MANGROVE("Mangrove"),

    PALE_OAK("Pale Oak"),

    POPLAR("Poplar");

    @Getter
    private final String name;

    WoodType(String name) {
        this.name = name;
    }
}
