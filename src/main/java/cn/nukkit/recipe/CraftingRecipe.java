package cn.nukkit.recipe;

import cn.nukkit.network.protocol.types.RecipeUnlockingRequirement;

import java.util.UUID;

/**
 * @author CreeperFace
 */
public abstract class CraftingRecipe extends BaseRecipe {
    protected UUID uuid;
    private final int priority;
    protected final RecipeUnlockingRequirement recipeUnlockingRequirement;

    protected CraftingRecipe(String id, int priority, RecipeUnlockingRequirement recipeUnlockingRequirement) {
        super(id);
        this.priority = priority;
        this.recipeUnlockingRequirement = recipeUnlockingRequirement == null ? new RecipeUnlockingRequirement(RecipeUnlockingRequirement.UnlockingContext.NONE) : recipeUnlockingRequirement;
    }


    /**
     * Get the priority of this recipe,
     * the lower the value, the higher the priority.
     * and the same output recipe will be to match the higher priority
     *
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    public RecipeUnlockingRequirement getRequirement() {
        return recipeUnlockingRequirement;
    }
}
