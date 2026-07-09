package org.powernukkitx.recipe;

import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.descriptor.DefaultDescriptor;
import org.powernukkitx.registry.RecipeRegistry;

import java.util.List;

public class SoulCampfireRecipe extends CampfireRecipe {
    public SoulCampfireRecipe(Item result, Item ingredient) {
        this(null, result, ingredient);
    }

    public SoulCampfireRecipe(String recipeId, Item result, Item ingredient) {
        super(recipeId == null ?
                RecipeRegistry.computeRecipeId(List.of(result), List.of(new DefaultDescriptor(ingredient)), RecipeType.SOUL_CAMPFIRE) :
                recipeId, result, ingredient);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SOUL_CAMPFIRE;
    }

    @Override
    public String getRecipeIdTag() {
        return "soul_campfire";
    }
}
