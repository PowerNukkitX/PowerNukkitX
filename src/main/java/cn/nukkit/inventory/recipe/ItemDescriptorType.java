package cn.nukkit.inventory.recipe;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public enum ItemDescriptorType {
    INVALID,
    DEFAULT,
    MOLANG,
    ITEM_TAG,
    DEFERRED,
    /**
     * @since v575
     */
    COMPLEX_ALIAS
}
