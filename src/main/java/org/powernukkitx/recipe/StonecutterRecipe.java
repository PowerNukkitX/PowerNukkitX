package org.powernukkitx.recipe;

import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeNetId;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingRequirement;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.ShapelessRecipePayload;
import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.descriptor.DefaultDescriptor;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.registry.RecipeRegistry;

import java.util.List;
import java.util.UUID;


public class StonecutterRecipe extends CraftingRecipe {
    public StonecutterRecipe(Item result, Item ingredient, int netId) {
        this(null, netId, 10, result, ingredient);
    }

    public StonecutterRecipe(String recipeId, int netId, int priority, Item result, Item ingredient) {
        this(recipeId, null, netId, priority, result, ingredient);
    }

    public StonecutterRecipe(String recipeId, UUID uuid, int netId, int priority, Item result, Item ingredient) {
        this(recipeId, uuid, netId, priority, result, ingredient, RecipeUnlockingRequirement.INVALID);
    }

    public StonecutterRecipe(String recipeId, UUID uuid, int netId, int priority, Item result, Item ingredient, RecipeUnlockingRequirement recipeUnlockingRequirement) {
        super(recipeId == null ? RecipeRegistry.computeRecipeIdWithItem(List.of(result), List.of(ingredient), RecipeType.STONECUTTER) : recipeId, netId, priority, recipeUnlockingRequirement);
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
    public boolean match(Input input) {
        return true;
    }

    @Override
    public RecipeType getType() {
        return RecipeType.STONECUTTER;
    }

    public ShapelessRecipePayload toNetwork() {
        final ShapelessRecipePayload payload = new ShapelessRecipePayload();
        payload.setRecipeId(this.getRecipeId());
        payload.getIngredients().addAll(this.getIngredients().stream().map(ItemDescriptor::toNetwork).toList());
        payload.getResults().addAll(this.getResults().stream().map(Item::toNetwork).toList());
        payload.setUuid(this.getUUID());
        payload.setTag(this.getRecipeIdTag());
        payload.setPriority(this.getPriority());
        payload.setUnlockingRequirement(this.getRequirement());
        payload.setNetId(new RecipeNetId(this.getNetId()));
        return payload;
    }

    public String getRecipeIdTag() {
        return "stonecutter";
    }
}
