package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 标注此物品栏拥有者的物品栏作为合成用途，常见于机器，如熔炉等。
 * <br>
 * This inventory holder's inventory is used for recipe, usually in machines, such as furnaces, etc.
 */
@Since("1.19.50-r3")
@PowerNukkitXOnly
public interface RecipeInventoryHolder extends InventoryHolder {
    /**
     * 获取该物品栏拥有者的物品栏的原料视图，这个视图通常是以{@link InventorySlice}指向的原始物品栏用于存储原料的一部分。
     * <br>
     * Get the ingredients view of the inventory holder's inventory, which is usually a part of the raw inventory.
     *
     * @return 原料视图 the ingredients view of the inventory holder's inventory
     */
    Inventory getIngredientView();

    /**
     * 获取该物品栏拥有者的物品栏的产物视图，这个视图通常是以{@link InventorySlice}指向的原始物品栏用于存储结果的一部分。
     * <br>
     * Get the result view of the inventory holder's inventory, which is usually a part of the raw inventory.
     *
     * @return 产物视图 the result view of the inventory holder's inventory
     */
    Inventory getProductView();
}
