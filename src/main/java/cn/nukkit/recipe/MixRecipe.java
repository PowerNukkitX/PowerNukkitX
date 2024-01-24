package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.Recipe;
import lombok.ToString;

@ToString
public abstract class MixRecipe implements Recipe {

    private final Item input;
    private final Item ingredient;
    private final Item output;
    private final String recipeId;

    public MixRecipe(Item input, Item ingredient, Item output) {
        this(null, input, ingredient, output);
    }

    public MixRecipe(String recipeId, Item input, Item ingredient, Item output) {
        this.recipeId = recipeId;
        this.input = input.clone();
        this.ingredient = ingredient.clone();
        this.output = output.clone();
    }

    @Override
    public String getRecipeId() {
        return recipeId;
    }

    public Item getIngredient() {
        return ingredient.clone();
    }

    public Item getInput() {
        return input.clone();
    }

    @Override
    public Item getResult() {
        return output.clone();
    }
}
