package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.registry.RecipeRegistry;
import org.cloudburstmc.protocol.bedrock.data.inventory.crafting.CraftingDataEntryType;
import org.cloudburstmc.protocol.bedrock.data.inventory.crafting.RecipeUnlockingRequirement;

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
        this(recipeId, uuid, netId, priority, result, ingredient, null);
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

    public org.cloudburstmc.protocol.bedrock.data.inventory.crafting.recipe.ShapelessRecipe toNetwork() {
        return org.cloudburstmc.protocol.bedrock.data.inventory.crafting.recipe.ShapelessRecipe.of(
                CraftingDataEntryType.SHAPELESS_RECIPE,
                this.getRecipeId(),
                this.getIngredients().stream().map(ItemDescriptor::toNetwork).toList(),
                this.getResults().stream().map(Item::toNetwork).toList(),
                this.getUUID(),
                this.getRecipeIdTag(),
                this.getPriority(),
                this.getNetId(),
                this.getRequirement()
        );
    }

    public String getRecipeIdTag() {
        return "stonecutter";
    }
}
