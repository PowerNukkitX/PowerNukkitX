package cn.nukkit.inventory;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

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

    @DeprecationDetails(since = "1.19.63-r2", reason = "Use SMITHING_TRANSFORM instead", replaceWith = "SMITHING_TRANSFORM")
    SMITHING(8),
    // For mods


    public final int networkType;

    RecipeType(int networkType) {
        this.networkType = networkType;
    }
}
