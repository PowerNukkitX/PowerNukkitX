package org.powernukkitx.recipe;

import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.descriptor.DefaultDescriptor;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.registry.RecipeRegistry;

import java.util.List;
import java.util.UUID;

public class BlastFurnaceRecipe extends SmeltingRecipe {
    public BlastFurnaceRecipe(Item result, Item ingredient) {
        this(null, result, ingredient);
    }


    public BlastFurnaceRecipe(String recipeId, Item result, Item ingredient) {
        super(recipeId == null ?
                RecipeRegistry.computeRecipeId(List.of(result), List.of(new DefaultDescriptor(ingredient)), RecipeType.BLAST_FURNACE) :
                recipeId);
        this.ingredients.add(new DefaultDescriptor(ingredient.clone()));
        this.results.add(result.clone());
    }

    public BlastFurnaceRecipe(String recipeId, UUID uuid, int netId, int priority, Item result, ItemDescriptor ingredient) {
        super(recipeId, uuid, netId, priority);
        this.ingredients.add(ingredient);
        this.results.add(result.clone());
    }

    @Override
    public boolean match(Input input) {
        return false;
    }

    @Override
    public RecipeType getType() {
        return RecipeType.BLAST_FURNACE;
    }

    @Override
    public String getRecipeIdTag() {
        return "blast_furnace";
    }
}
