package cn.nukkit.recipe;

public enum RecipeType {
    SHAPELESS(0),
    SHAPED(1),
    FURNACE(2),
    MULTI(4),
    USER_DATA_SHAPELESS_RECIPE(5),
    SHAPELESS_CHEMISTRY(6),
    SHAPED_CHEMISTRY(7),
    SMITHING_TRANSFORM(8),
    /**
     * @since v582
     */
    SMITHING_TRIM(9),
    BLAST_FURNACE(2),
    SMOKER(2),
    CAMPFIRE(2),
    SOUL_CAMPFIRE(2),
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
