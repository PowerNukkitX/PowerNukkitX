package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.registry.Registries;

import java.util.List;


public class CampfireRecipe extends SmeltingRecipe {
    public CampfireRecipe(Item result, Item ingredient) {
        this(null, result, ingredient);
    }

    public CampfireRecipe(String recipeId, Item result, Item ingredient) {
        super(recipeId == null ?
                Registries.RECIPE.computeRecipeId(List.of(result), List.of(new DefaultDescriptor(ingredient)),
                        ingredient.hasMeta() ? RecipeType.CAMPFIRE_DATA : RecipeType.CAMPFIRE) :
                recipeId);
        this.results.add(result);
        this.ingredients.add(new DefaultDescriptor(ingredient));
    }

    @Override
    public void setInput(ItemDescriptor item) {
        this.ingredients.set(0, item);
    }

    @Override
    public ItemDescriptor getInput() {
        return this.ingredients.get(0);
    }

    @Override
    public Item getResult() {
        return this.results.get(0);
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
