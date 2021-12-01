package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;

import java.util.Collection;

/**
 * @author joserobjr
 * @since 2021-09-25
 */
@PowerNukkitOnly
@Since("1.5.2.0-PN")
public class ShulkerBoxRecipe extends ShapelessRecipe {
    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public ShulkerBoxRecipe(Item result, Collection<Item> ingredients) {
        super(result, ingredients);
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public ShulkerBoxRecipe(String recipeId, int priority, Item result, Collection<Item> ingredients) {
        super(recipeId, priority, result, ingredients);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHULKER_BOX;
    }
}
