package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.registry.RecipeRegistry;

import java.util.List;

public class SoulCampfireRecipe extends CampfireRecipe {
    public SoulCampfireRecipe(Item result, Item ingredient) {
        this(null, result, ingredient);
    }

    public SoulCampfireRecipe(String recipeId, Item result, Item ingredient) {
        super(recipeId == null ?
                RecipeRegistry.computeRecipeId(List.of(result), List.of(new DefaultDescriptor(ingredient)),
                        ingredient.hasMeta() ? RecipeType.SOUL_CAMPFIRE_DATA : RecipeType.SOUL_CAMPFIRE) :
                recipeId, result, ingredient);
    }

    @Override
    public RecipeType getType() {
        return this.getInput().toItem().hasMeta() ? RecipeType.SOUL_CAMPFIRE_DATA : RecipeType.SOUL_CAMPFIRE;
    }
}
