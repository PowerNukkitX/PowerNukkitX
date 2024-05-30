package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.registry.RecipeRegistry;

import java.util.List;


public class CampfireRecipe extends SmeltingRecipe {
    public CampfireRecipe(Item result, Item ingredient) {
        this(null, result, ingredient);
    }

    public CampfireRecipe(String recipeId, Item result, Item ingredient) {
        super(recipeId == null ?
                RecipeRegistry.computeRecipeId(List.of(result), List.of(new DefaultDescriptor(ingredient)),
                        ingredient.hasMeta() ? RecipeType.CAMPFIRE_DATA : RecipeType.CAMPFIRE) :
                recipeId);
        this.results.add(result);
        this.ingredients.add(new DefaultDescriptor(ingredient));
    }

    @Override
    public boolean match(Input input) {
        return false;
    }

    @Override
    public RecipeType getType() {
        return this.getInput().toItem().hasMeta() ? RecipeType.CAMPFIRE_DATA : RecipeType.CAMPFIRE;
    }
}
