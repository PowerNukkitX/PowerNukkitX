package cn.nukkit.recipe;

public enum RecipeType {
    SHAPELESS(0),
    SHAPED(1),
    FURNACE(2),
    FURNACE_DATA(3),
    MULTI(4),
    SHULKER_BOX(5),
    SHAPELESS_CHEMISTRY(6),
    SHAPED_CHEMISTRY(7),
    SMITHING_TRANSFORM(8),
    /**
     * @since v582
     */
    SMITHING_TRIM(9),
    BLAST_FURNACE(2),
    BLAST_FURNACE_DATA(3),
    SMOKER(2),
    SMOKER_DATA(3),
    CAMPFIRE(2),
    CAMPFIRE_DATA(3),
    STONECUTTER(0),
    CARTOGRAPHY(0),
    REPAIR(-1),
    // For mods
    MOD_PROCESS(0),//custom
    BREWING(0),//custom
    CONTAINER(0);//custom


    public final int networkType;

    RecipeType(int networkType) {
        this.networkType = networkType;
    }
}
