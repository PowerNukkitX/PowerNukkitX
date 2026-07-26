package org.powernukkitx.recipe;

import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.InventoryHolder;
import org.powernukkitx.inventory.InventorySlice;

/**
 * This inventory holder's inventory is used for recipe, usually in machines, such as furnaces, etc.
 */


public interface RecipeInventoryHolder extends InventoryHolder {
    /**
     * Get the ingredients view of the inventory holder's inventory, which is usually a part of the raw inventory.
     *
     * @return the ingredients view of the inventory holder's inventory
     */
    Inventory getIngredientView();

    /**
     * Get the result view of the inventory holder's inventory, which is usually a part of the raw inventory.
     *
     * @return the result view of the inventory holder's inventory
     */
    Inventory getProductView();
}
