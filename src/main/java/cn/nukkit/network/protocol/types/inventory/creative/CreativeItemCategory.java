package cn.nukkit.network.protocol.types.inventory.creative;

public enum CreativeItemCategory {
    ALL,
    CONSTRUCTION,
    NATURE,
    EQUIPMENT,
    ITEMS,
    ITEM_COMMAND_ONLY,
    UNDEFINED;

    public static final CreativeItemCategory[] VALUES = values();
}
