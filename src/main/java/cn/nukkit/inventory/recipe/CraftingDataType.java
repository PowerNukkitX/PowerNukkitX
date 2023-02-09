package cn.nukkit.inventory.recipe;

public enum CraftingDataType {
    SHAPELESS,
    SHAPED,
    FURNACE,
    FURNACE_DATA,
    MULTI,
    SHULKER_BOX,
    SHAPELESS_CHEMISTRY,
    SHAPED_CHEMISTRY,
    /**
     * @since v567
     */
    SMITHING_TRANSFORM;

    private static final CraftingDataType[] VALUES = values();

    public static CraftingDataType byId(int id) {
        if (id >= 0 && id < VALUES.length) {
            return VALUES[id];
        }
        throw new UnsupportedOperationException("Unknown CraftingDataType ID: " + id);
    }
}
