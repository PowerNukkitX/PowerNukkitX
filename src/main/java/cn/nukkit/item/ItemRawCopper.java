package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;

/**
 * @author joserobjr
 * @since 2021-06-13
 */
@PowerNukkitOnly
public class ItemRawCopper extends ItemRawMaterial {
    @PowerNukkitOnly
    public ItemRawCopper() {
        super(MinecraftItemID.RAW_COPPER.getNamespacedId(), "Raw Copper");
    }
}
