package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import lombok.ToString;

import java.util.UUID;


@ToString
public class StonecutterRecipe extends CraftingRecipe {
    public StonecutterRecipe(Item result, Item ingredient) {
        this(null, 10, result, ingredient);
    }

    public StonecutterRecipe(String recipeId, int priority, Item result, Item ingredient) {
        this(recipeId, null, priority, result, ingredient);
    }

    public StonecutterRecipe(String recipeId, UUID uuid, int priority, Item result, Item ingredient) {
        super(recipeId, priority);
        this.uuid = uuid;
        this.results.add(result.clone());
        if (ingredient.getCount() < 1) {
            throw new IllegalArgumentException("Recipe '" + recipeId + "' Ingredient amount was not 1 (value: " + ingredient.getCount() + ")");
        }
        this.ingredients.add(new DefaultDescriptor(ingredient.clone()));
    }

    public Item getResult() {
        return this.results.get(0).clone();
    }

    public Item getIngredient() {
        return ingredients.get(0).toItem().clone();
    }

    @Override
    public boolean match(Input input) {
        return true;
    }

    @Override
    public RecipeType getType() {
        return RecipeType.STONECUTTER;
    }
}
