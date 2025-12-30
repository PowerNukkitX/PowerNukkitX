package cn.nukkit.item.customitem;

import cn.nukkit.item.Item;


/**
 * Inherit this class to implement a custom item, most of the item components will auto-compute <p>
 * at core Item class.
 * Override the methods in the {@link Item} for advanced customization.
 *
 * @author lt_name
 */
public interface CustomItem{
    /**
     * This method sets the definition of custom item
     */
    CustomItemDefinition getDefinition();
}
