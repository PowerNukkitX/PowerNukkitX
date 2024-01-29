package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.registry.Registries;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static cn.nukkit.recipe.RecipeType.SHULKER_BOX;

/**
 * @author joserobjr
 * @since 2021-09-25
 */


public class ShulkerBoxRecipe extends ShapelessRecipe {
    public ShulkerBoxRecipe(Item result, Collection<Item> ingredients) {
        super(result, ingredients);
    }

    public ShulkerBoxRecipe(String recipeId, int priority, Item result, Collection<Item> ingredients) {
        this(recipeId, priority, result, ingredients.stream().map(item -> (ItemDescriptor) new DefaultDescriptor(item)).toList());
    }

    public ShulkerBoxRecipe(String recipeId, int priority, Item result, List<ItemDescriptor> ingredients) {
        this(recipeId, null, priority, result, ingredients);
    }

    public ShulkerBoxRecipe(String recipeId, UUID uuid, int priority, Item result, List<ItemDescriptor> ingredients) {
        super(recipeId == null ? Registries.RECIPE.computeRecipeId(List.of(result), ingredients, SHULKER_BOX) : recipeId, uuid, priority, result, ingredients);
    }

    @Override
    public RecipeType getType() {
        return SHULKER_BOX;
    }
}
