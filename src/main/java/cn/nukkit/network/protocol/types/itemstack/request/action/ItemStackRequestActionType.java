package cn.nukkit.network.protocol.types.itemstack.request.action;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;

public enum ItemStackRequestActionType {
    TAKE(0),
    PLACE(1),
    SWAP(2),
    DROP(3),
    DESTROY(4),
    CONSUME(5),
    CREATE(6),
    PLACE_IN_ITEM_CONTAINER(7),
    TAKE_FROM_ITEM_CONTAINER(8),
    LAB_TABLE_COMBINE(9),
    BEACON_PAYMENT(10),
    MINE_BLOCK(11),
    CRAFT_RECIPE(12),
    CRAFT_RECIPE_AUTO(13),
    CRAFT_CREATIVE(14),
    CRAFT_RECIPE_OPTIONAL(15),
    CRAFT_REPAIR_AND_DISENCHANT(16),
    CRAFT_LOOM(17),
    CRAFT_NON_IMPLEMENTED_DEPRECATED(18),
    CRAFT_RESULTS_DEPRECATED(19);
    private final int id;

    private static final Int2ObjectArrayMap<ItemStackRequestActionType> VALUES = new Int2ObjectArrayMap<>();

    static {
        for (var v : values()) {
            VALUES.put(v.getId(), v);
        }
    }

    ItemStackRequestActionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ItemStackRequestActionType fromId(int id) {
        return VALUES.get(id);
    }
}
