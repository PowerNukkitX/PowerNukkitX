package cn.nukkit.recipe;

import cn.nukkit.item.Item;

import java.util.Collection;
import java.util.List;

/**
 * @author joserobjr
 * @since 2021-09-25
 */


public class ShulkerBoxRecipe extends ShapelessRecipe {

    public ShulkerBoxRecipe(Item result, Collection<Item> ingredients) {
        super(result, ingredients);
    }

    public ShulkerBoxRecipe(String recipeId, int priority, Item result, Collection<Item> ingredients) {
        super(recipeId, priority, result, ingredients);
    }

    public ShulkerBoxRecipe(String recipeId, int priority, Item result, List<ItemDescriptor> ingredients) {
        super(recipeId, priority, result, ingredients);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHULKER_BOX;
    }
}
