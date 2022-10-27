package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class ItemIngotCopper extends Item {
    public ItemIngotCopper() {
        this(0, 1);
    }

    public ItemIngotCopper(Integer meta) {
        this(meta, 1);
    }

    public ItemIngotCopper(Integer meta, int count) {
        super(COPPER_INGOT, 0, count, "Copper Ingot");
    }
}
