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
    private final UUID uuid;
    private final int netId;
    private final int priority;
    private RecipeUnlockingRequirement unlockingRequirement;

    protected SmeltingRecipe(String id) {
        this(id, UUID.randomUUID(), RecipeRegistry.FURNACE_RECIPE_NET_ID_COUNTER++, 0);
    }

    protected SmeltingRecipe(String id, UUID uuid, int netId, int priority) {
        super(id);
        this.uuid = uuid;
        this.netId = netId;
        this.priority = priority;
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

    public int getNetId() {
        return this.netId;
    }

    public ShapelessRecipePayload toNetwork() {
        final ShapelessRecipePayload payload = new ShapelessRecipePayload();
        payload.setRecipeId(this.getRecipeId());
        payload.getIngredients().add(this.getInput().toNetwork());
        payload.getResults().add(this.getResult().toRecipeNetwork());
        payload.setUuid(this.uuid);
        payload.setTag(this.getRecipeIdTag());
        payload.setPriority(this.priority);
        payload.setUnlockingRequirement(this.getUnlockingRequirement());
        payload.setNetId(new RecipeNetId(this.netId));
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
