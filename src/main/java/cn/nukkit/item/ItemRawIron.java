package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;

/**
 * @author joserobjr
 * @since 2021-06-12
 */
@PowerNukkitOnly
public class ItemRawIron extends ItemRawMaterial {
    @PowerNukkitOnly
    public ItemRawIron() {
        super(MinecraftItemID.RAW_IRON.getNamespacedId(), "Raw Iron");
    }
}
