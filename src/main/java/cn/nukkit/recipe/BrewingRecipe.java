package cn.nukkit.recipe;


import cn.nukkit.item.Item;
import cn.nukkit.registry.Registries;

import java.util.List;


public class BrewingRecipe extends MixRecipe {

    public BrewingRecipe(Item input, Item ingredient, Item output) {
        super(input, ingredient, output);
    }

    public BrewingRecipe(String recipeId, Item input, Item ingredient, Item output) {
        super(recipeId == null ? Registries.RECIPE.computeRecipeIdWithItem(List.of(output), List.of(input, ingredient), RecipeType.BREWING) : recipeId, input, ingredient, output);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.BREWING;
    }
}