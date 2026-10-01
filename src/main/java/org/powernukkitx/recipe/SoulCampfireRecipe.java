package org.powernukkitx.recipe;

import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.descriptor.DefaultDescriptor;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.registry.RecipeRegistry;

import java.util.List;
import java.util.UUID;

public class SoulCampfireRecipe extends CampfireRecipe {
    public SoulCampfireRecipe(Item result, Item ingredient) {
        this(null, result, ingredient);
    }

    public SoulCampfireRecipe(String recipeId, Item result, Item ingredient) {
        super(recipeId == null ?
                RecipeRegistry.computeRecipeId(List.of(result), List.of(new DefaultDescriptor(ingredient)), RecipeType.SOUL_CAMPFIRE) :
                recipeId, result, ingredient);
    }

    public SoulCampfireRecipe(String recipeId, UUID uuid, int netId, int priority, Item result, ItemDescriptor ingredient) {
        super(recipeId, uuid, netId, priority, result, ingredient);
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
