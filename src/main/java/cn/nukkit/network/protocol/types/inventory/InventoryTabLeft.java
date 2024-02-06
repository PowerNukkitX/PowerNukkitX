package cn.nukkit.network.protocol.types.inventory;

public enum InventoryTabLeft {
    NONE,
    RECIPE_CONSTRUCTION,
    RECIPE_EQUIPMENT,
    RECIPE_ITEMS,
    RECIPE_NATURE,
    RECIPE_SEARCH,
    SURVIVAL;

    public static final InventoryTabLeft[] VALUES = values();
}
