package cn.nukkit.item.trim;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author glorydark
 * @date {2023/8/9} {17:26}
 */
@PowerNukkitXOnly
@Since("1.20.30-r2")
public enum ItemTrimMaterialType {

    MATERIAL_QUARTZ("quartz"),
    MATERIAL_IRON("iron"),
    MATERIAL_NETHERITE("netherite"),
    MATERIAL_REDSTONE("redstone"),
    MATERIAL_COPPER("copper"),
    MATERIAL_GOLD("gold"),
    MATERIAL_EMERALD("emerald"),
    MATERIAL_LAPIS("lapis"),
    MATERIAL_AMETHYST("amethyst");

    @Nullable
    public static ItemTrimMaterialType fromMaterialName(@NotNull String materialName) {
        for (ItemTrimMaterialType value : ItemTrimMaterialType.values()) {
            if (value.getMaterialName().equals(materialName)) {
                return value;
            }
        }
        return null;
    }

    private final String materialName;

    ItemTrimMaterialType(@NotNull String input) {
        this.materialName = input;
    }

    @NotNull
    public String getMaterialName() {
        return materialName;
    }

}
