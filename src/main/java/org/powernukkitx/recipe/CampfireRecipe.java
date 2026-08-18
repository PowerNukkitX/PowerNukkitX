package org.powernukkitx.recipe;

import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.descriptor.DefaultDescriptor;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.registry.RecipeRegistry;

import java.util.List;
import java.util.UUID;


public class CampfireRecipe extends SmeltingRecipe {
    public CampfireRecipe(Item result, Item ingredient) {
        this(null, result, ingredient);
    }

    public CampfireRecipe(String recipeId, Item result, Item ingredient) {
        super(recipeId == null ?
                RecipeRegistry.computeRecipeId(List.of(result), List.of(new DefaultDescriptor(ingredient)), RecipeType.CAMPFIRE) :
                recipeId);
        this.results.add(result);
        this.ingredients.add(new DefaultDescriptor(ingredient));
    }

    public CampfireRecipe(String recipeId, UUID uuid, int netId, int priority, Item result, ItemDescriptor ingredient) {
        super(recipeId, uuid, netId, priority);
        this.results.add(result.clone());
        this.ingredients.add(ingredient);
    }

    @Override
    public boolean match(Input input) {
        return false;
    }

    @Override
    public RecipeType getType() {
        return RecipeType.CAMPFIRE;
    }

    @Override
    public String getRecipeIdTag() {
        return "campfire";
    }
}
