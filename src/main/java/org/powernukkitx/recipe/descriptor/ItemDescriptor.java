package org.powernukkitx.recipe.descriptor;

import org.powernukkitx.item.Item;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.ItemDescriptorWithCount;


public interface ItemDescriptor extends Cloneable {

    ItemDescriptorType getType();

    Item toItem();

    ItemDescriptor clone() throws CloneNotSupportedException;

    int getCount();

    default boolean match(Item item) {
        return false;
    }

    ItemDescriptorWithCount toNetwork();
}
