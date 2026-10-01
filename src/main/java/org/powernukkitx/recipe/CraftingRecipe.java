package org.powernukkitx.recipe;

import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingContext;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingRequirement;

import com.google.common.base.Preconditions;

import java.util.UUID;

/**
 * @author CreeperFace
 */
public abstract class CraftingRecipe extends BaseRecipe {
    protected UUID uuid;
    private final int priority;
    private final int netId;
    protected final RecipeUnlockingRequirement recipeUnlockingRequirement;
    private String craftingTag = "crafting_table";

    protected CraftingRecipe(String id, int netId, int priority, RecipeUnlockingRequirement recipeUnlockingRequirement) {
        super(id);
        this.priority = priority;
        this.netId = netId;
        this.recipeUnlockingRequirement = recipeUnlockingRequirement == null ? new RecipeUnlockingRequirement(RecipeUnlockingContext.NONE) : recipeUnlockingRequirement;
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

    /**
     * Returns the crafting table tag used by this recipe on the network.
     *
     * @return the crafting tag
     */
    public String getCraftingTag() {
        return craftingTag;
    }

    /**
     * Sets the crafting table tag used by this recipe on the network.
     * <p>
     * The default value is {@code crafting_table}, preserving vanilla behavior.
     *
     * @param craftingTag the crafting tag
     * @return this recipe
     */
    public CraftingRecipe setCraftingTag(String craftingTag) {
        Preconditions.checkArgument(
                craftingTag != null && !craftingTag.isBlank() && craftingTag.length() <= 64,
                "Crafting tag cannot be null, blank, or exceed 64 characters"
        );

        this.craftingTag = craftingTag;
        return this;
    }

    public int getNetId() {
        return netId;
    }
}
