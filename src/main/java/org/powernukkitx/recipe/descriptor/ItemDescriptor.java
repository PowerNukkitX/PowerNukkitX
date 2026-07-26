package org.powernukkitx.recipe.descriptor;

import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.RecipeIngredient;
import org.powernukkitx.item.Item;


public interface ItemDescriptor extends Cloneable {

    ItemDescriptorType getType();

    Item toItem();

    ItemDescriptor clone() throws CloneNotSupportedException;

    int getCount();

    default boolean match(Item item) {
        return false;
    }

    RecipeIngredient toNetwork();
}
