package org.powernukkitx.recipe;

import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeNetId;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingContext;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingRequirement;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.ShapelessRecipePayload;
import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.registry.RecipeRegistry;

import java.util.UUID;

public abstract class SmeltingRecipe extends BaseRecipe {
    private RecipeUnlockingRequirement unlockingRequirement;

    protected SmeltingRecipe(String id) {
        super(id);
    }

    public void setInput(ItemDescriptor item) {
        this.ingredients.set(0, item);
    }

    public ItemDescriptor getInput() {
        return this.ingredients.getFirst();
    }

    public Item getResult() {
        return this.results.getFirst();
    }

    public ShapelessRecipePayload toNetwork() {
        final ShapelessRecipePayload payload = new ShapelessRecipePayload();
        payload.setRecipeId(this.getRecipeId());
        payload.getIngredients().add(this.getInput().toNetwork());
        payload.getResults().add(this.getResult().toRecipeNetwork());
        payload.setUuid( UUID.randomUUID());
        payload.setTag(this.getRecipeIdTag());
        payload.setPriority(0);
        payload.setUnlockingRequirement(this.getUnlockingRequirement());
        payload.setNetId(new RecipeNetId( RecipeRegistry.FURNACE_RECIPE_NET_ID_COUNTER++));
        return payload;
    }

    public void setUnlockingRequirement(RecipeUnlockingRequirement unlockingRequirement) {
        this.unlockingRequirement = unlockingRequirement;
    }

    public RecipeUnlockingRequirement getUnlockingRequirement() {
        if (this.unlockingRequirement != null) {
            return this.unlockingRequirement;
        }
        final RecipeUnlockingRequirement derived = new RecipeUnlockingRequirement(RecipeUnlockingContext.NONE);
        derived.getUnlockingIngredients().add(this.getInput().toNetwork());
        return derived;
    }

    public abstract String getRecipeIdTag();
}
