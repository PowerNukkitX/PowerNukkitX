package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@Since("1.19.50-r3")
@PowerNukkitXOnly
public interface RecipeInventoryHolder extends InventoryHolder {
    Inventory getIngredientView();

    Inventory getProductView();
}
