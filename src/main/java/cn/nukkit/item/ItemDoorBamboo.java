package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class ItemDoorBamboo extends StringItemBase {
    public ItemDoorBamboo() {
        super("minecraft:bamboo_door", "Bamboo Door");
    }
}
