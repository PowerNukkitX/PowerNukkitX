package org.powernukkitx.recipe;

import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeNetId;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingRequirement;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.ShapelessRecipePayload;
import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.registry.RecipeRegistry;

import java.util.UUID;

public abstract class SmeltingRecipe extends BaseRecipe {
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
        payload.getResults().add(this.getResult().toNetwork());
        payload.setUuid( UUID.randomUUID());
        payload.setTag(this.getRecipeIdTag());
        payload.setPriority(0);
        payload.setUnlockingRequirement(RecipeUnlockingRequirement.INVALID);
        payload.setNetId(new RecipeNetId( RecipeRegistry.FURNACE_RECIPE_NET_ID_COUNTER++));
        return payload;
    }

    public abstract String getRecipeIdTag();
}
