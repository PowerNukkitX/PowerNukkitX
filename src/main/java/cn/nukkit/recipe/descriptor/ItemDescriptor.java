package cn.nukkit.recipe.descriptor;

import cn.nukkit.item.Item;


public interface ItemDescriptor extends Cloneable {

    ItemDescriptorType getType();

    Item toItem();

    ItemDescriptor clone() throws CloneNotSupportedException;

    int getCount();

    default 
    /**
     * @deprecated 
     */
    boolean match(Item item) {
        return false;
    }
}
