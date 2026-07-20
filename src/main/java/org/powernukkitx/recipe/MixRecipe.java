package org.powernukkitx.recipe;

import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.descriptor.DefaultDescriptor;

public abstract class MixRecipe extends BaseRecipe {
    public MixRecipe(Item input, Item ingredient, Item output) {
        this(null, input, ingredient, output);
    }

    public MixRecipe(String recipeId, Item input, Item ingredient, Item output) {
        super(recipeId);
        ingredients.add(new DefaultDescriptor(input));
        ingredients.add(new DefaultDescriptor(ingredient));
        results.add(output);
    }

    public Item getIngredient() {
        return ingredients.get(1).toItem();
    }

    public Item getInput() {
        return ingredients.get(0).toItem();
    }

    public Item getResult() {
        return results.get(0);
    }

    @Override
    public boolean match(Input input) {
        return true;
    }
}
