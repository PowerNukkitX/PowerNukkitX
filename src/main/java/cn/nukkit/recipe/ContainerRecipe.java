package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.registry.RecipeRegistry;

import java.util.List;

public class ContainerRecipe extends MixRecipe {
    public ContainerRecipe(Item input, Item ingredient, Item output) {
        this(null, input, ingredient, output);
    }

    public ContainerRecipe(String recipeId, Item input, Item ingredient, Item output) {
        super(recipeId == null ? RecipeRegistry.computeRecipeIdWithItem(List.of(output), List.of(input, ingredient), RecipeType.CONTAINER) : recipeId,
                input, ingredient, output);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.CONTAINER;
    }
}
