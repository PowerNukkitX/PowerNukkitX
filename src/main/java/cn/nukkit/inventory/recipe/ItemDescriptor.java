package cn.nukkit.inventory.recipe;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;


public interface ItemDescriptor extends Cloneable {

    ItemDescriptorType getType();

    Item toItem();

    ItemDescriptor clone() throws CloneNotSupportedException;

    int getCount();

    default boolean match(Item item) {
        return false;
    }
}
