package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;

/**
 * @author joserobjr
 * @since 2021-06-13
 */
@PowerNukkitOnly
public class ItemRawGold extends ItemRawMaterial {
    @PowerNukkitOnly
    public ItemRawGold() {
        super(MinecraftItemID.RAW_GOLD.getNamespacedId(), "Raw Gold");
    }
}
