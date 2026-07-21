package org.powernukkitx.item.customitem.component;

/**
 * Enum of Bedrock item component identifiers.
 */
public enum ItemComponentIds {
    // Display
    ICON("minecraft:icon"),
    DISPLAY_NAME("minecraft:display_name"),
    ANIMATES("minecraft:animates"),

    // Stack & Use
    MAX_STACK_SIZE("minecraft:max_stack_size"),
    MAX_DAMAGE("minecraft:max_damage"),
    DURABILITY("minecraft:durability"),
    USE_ON_CLICK("minecraft:use_on_click"),
    USE_DURATION("minecraft:use_duration"),
    COOLDOWN("minecraft:cooldown"),
    FOOD("minecraft:food"),
    SEED("minecraft:seed"),

    // Equipment
    HAND_EQUIPPED("minecraft:hand_equipped"),
    EQUIPPABLE("minecraft:equippable"),
    REPAIRABLE("minecraft:repairable"),

    // Combat
    DAMAGE("minecraft:damage"),
    ENCHANTABLE("minecraft:enchantable"),
    TRIMMABLE("minecraft:trimmable"),
    TRIM_MATERIAL("minecraft:trim_material"),

    // Visual
    FOIL("minecraft:foil"),
    GLINT("minecraft:glint"),
    HIDE_DURABILITY("minecraft:hide_durability"),
    HIDE_UI("minecraft:hide_ui"),

    // Block interaction
    BLOCK("minecraft:block"),
    PLACEMENT_BLOCK("minecraft:placement_block"),
    BLOCK_PLACEMENT_FILTER("minecraft:block_placement_filter"),

    // Projectile
    PROJECTILE("minecraft:projectile"),

    // Misc
    STACKED_BY_DATA("minecraft:stacked_by_data"),
    CAMERA("minecraft:camera"),
    CRAFTING_TABLE("minecraft:crafting_table"),
    ITEM_CREATIVE_CATEGORY("minecraft:creative_category"),
    LOOT("minecraft:loot"),
    TAG("minecraft:tag");

    private final String id;

    ItemComponentIds(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getStringId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
}