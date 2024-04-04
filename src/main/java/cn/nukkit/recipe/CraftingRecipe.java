package cn.nukkit.recipe;

import java.util.UUID;

/**
 * @author CreeperFace
 */
public abstract class CraftingRecipe extends BaseRecipe {
    protected UUID uuid;
    private final int priority;

    protected CraftingRecipe(String id, int priority) {
        super(id);
        this.priority = priority;
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
}
