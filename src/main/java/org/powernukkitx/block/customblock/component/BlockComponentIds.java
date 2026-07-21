package org.powernukkitx.block.customblock.component;

/**
 * Enum of Bedrock block component identifiers.
 * Only contains actual component IDs used in block definitions.
 */
public enum BlockComponentIds {
    // Physics & collision
    BREATHABILITY("minecraft:breathability"),
    COLLISION_BOX("minecraft:collision_box"),
    SELECTION_BOX("minecraft:selection_box"),
    FRICTION("minecraft:friction"),
    REDSTONE_CONDUCTOR("minecraft:redstone_conductor"),

    // Visuals
    GEOMETRY("minecraft:geometry"),
    MATERIAL_INSTANCES("minecraft:material_instances"),
    TRANSFORMATION("minecraft:transformation"),
    DISPLAY_NAME("minecraft:display_name"),
    MAP_COLOR("minecraft:map_color"),

    // Light
    LIGHT_EMISSION("minecraft:light_emission"),
    LIGHT_DAMPENING("minecraft:light_dampening"),

    // Destruction
    DESTRUCTIBLE_BY_EXPLOSION("minecraft:destructible_by_explosion"),
    DESTRUCTIBLE_BY_MINING("minecraft:destructible_by_mining"),

    // Interaction
    CUSTOM_COMPONENTS("minecraft:custom_components"),
    CRAFTING_TABLE("minecraft:crafting_table"),
    WATERLOGGED("minecraft:waterlogged"),

    // Categories
    CREATIVE_CATEGORY("minecraft:creative_category"),

    // Events
    ON_PLAYER_PLACING("minecraft:on_player_placing"),
    ON_PLAYER_DESTROYING("minecraft:on_player_destroying"),

    // Misc
    BLOCK_SOUND("minecraft:block_sound"),
    ENTITY_COLLISION("minecraft:entity_collision"),
    PLAYER_COLLISION("minecraft:player_collision"),
    SHEARABLE("minecraft:shearable"),
    SILK_TOUCH("minecraft:silk_touch"),
    TICKING("minecraft:ticking"),
    UNSUPPORTED("minecraft:unsupported"),
    PLACEMENT_FILTER("minecraft:placement_filter"),
    ITEM_DROP("minecraft:item_drop"),
    LOOT("minecraft:loot");

    private final String id;

    BlockComponentIds(String id) {
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