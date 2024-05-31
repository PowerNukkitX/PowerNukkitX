package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.registry.RecipeRegistry;

import java.util.List;
import java.util.UUID;


public class StonecutterRecipe extends CraftingRecipe {
    /**
     * @deprecated 
     */
    
    public StonecutterRecipe(Item result, Item ingredient) {
        this(null, 10, result, ingredient);
    }
    /**
     * @deprecated 
     */
    

    public StonecutterRecipe(String recipeId, int priority, Item result, Item ingredient) {
        this(recipeId, null, priority, result, ingredient);
    }
    /**
     * @deprecated 
     */
    

    public StonecutterRecipe(String recipeId, UUID uuid, int priority, Item result, Item ingredient) {
        super(recipeId == null ? RecipeRegistry.computeRecipeIdWithItem(List.of(result), List.of(ingredient), RecipeType.STONECUTTER) : recipeId, priority);
        this.uuid = uuid;
        this.results.add(result.clone());
        if (ingredient.getCount() < 1) {
            throw new IllegalArgumentException("Recipe '" + recipeId + "' Ingredient amount was not 1 (value: " + ingredient.getCount() + ")");
        }
        this.ingredients.add(new DefaultDescriptor(ingredient.clone()));
    }

    public Item getResult() {
        return this.results.getFirst().clone();
    }

    public Item getIngredient() {
        return ingredients.getFirst().toItem().clone();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean match(Input input) {
        return true;
    }

    @Override
    public RecipeType getType() {
        return RecipeType.STONECUTTER;
    }
}
