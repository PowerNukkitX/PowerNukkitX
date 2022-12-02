package cn.nukkit.inventory.recipe;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.50-r2")
public enum ItemDescriptorType {
    INVALID,
    DEFAULT,
    MOLANG,
    ITEM_TAG,
    DEFERRED
}
