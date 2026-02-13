package cn.nukkit.network.protocol.types.inventory;

public enum InventoryLayout {
    NONE,
    INVENTORY_ONLY,
    DEFAULT,
    RECIPE_BOOK_ONLY;

    public static final InventoryLayout[] VALUES = values();
}
