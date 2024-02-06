package cn.nukkit.network.protocol.types.inventory;

public enum InventoryLayout {
    NONE,
    SURVIVAL,
    RECIPE_BOOK,
    CREATIVE;

    public static final InventoryLayout[] VALUES = values();
}
