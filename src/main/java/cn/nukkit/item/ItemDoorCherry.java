package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.0-r1")
public class ItemDoorCherry extends StringItemBase {
    public ItemDoorCherry() {
        super("minecraft:cherry_door", "Cherry Door");
    }
}