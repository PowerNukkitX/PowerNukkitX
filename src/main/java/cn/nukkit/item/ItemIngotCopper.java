package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class ItemIngotCopper extends StringItem{
    public ItemIngotCopper() {
        super(MinecraftItemID.COPPER_INGOT.getNamespacedId(), "Copper Ingot");
    }
}
