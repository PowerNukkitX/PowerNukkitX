package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.registry.Registries;

import javax.annotation.Nullable;
import java.util.List;


public class SmokerRecipe extends SmeltingRecipe {
    public SmokerRecipe(Item result, Item ingredient) {
        this(null, result, ingredient);
    }

    public SmokerRecipe(@Nullable String recipeId, Item result, Item ingredient) {
        super(recipeId == null ?
                Registries.RECIPE.computeRecipeId(List.of(result), List.of(new DefaultDescriptor(ingredient)),
                        ingredient.hasMeta() ? RecipeType.SMOKER_DATA : RecipeType.SMOKER) :
                recipeId);
        this.ingredients.add(new DefaultDescriptor(ingredient.clone()));
        this.results.add(result.clone());
    }

    @Override
    public boolean match(Input input) {
        return true;
    }

    @Override
    public RecipeType getType() {
        return this.getResult().hasMeta() ? RecipeType.SMOKER_DATA : RecipeType.SMOKER;
    }
}
