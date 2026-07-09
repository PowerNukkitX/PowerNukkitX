package org.powernukkitx.item.customitem.legacy.components;

public enum LegacyComponentIds {
    MAX_DAMAGE("minecraft:max_damage"),
    HAND_EQUIPPED("minecraft:hand_equipped"),
    STACKED_BY_DATA("minecraft:stacked_by_data"),
    FOIL("minecraft:foil"),
    USE_DURATION("minecraft:use_duration"),
    MAX_STACK_SIZE("minecraft:max_stack_size"),
    FOOD("minecraft:food"),
    SEED("minecraft:seed"),
    BLOCK_RENDER("minecraft:block"),
    CAMERA("minecraft:camera");

    private final String stringId;

    LegacyComponentIds(String value) {
        this.stringId = value;
    }

    public String getStringId() {
        return this.stringId;
    }

    @Override
    public String toString() {
        return this.stringId;
    }
}
