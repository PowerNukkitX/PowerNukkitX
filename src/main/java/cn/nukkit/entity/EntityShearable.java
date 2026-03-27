package cn.nukkit.entity;

import cn.nukkit.entity.ai.memory.CoreMemoryTypes;

/**
 * Entities that can be sheared. Stores value with {@link CoreMemoryTypes#IS_SHEARED}
 */
public interface EntityShearable extends EntityComponent {
    /**
     * @return true if shearing succeeded.
     */
    default boolean shear() {
        if (this.isSheared() || this.asEntity().isBaby()) {
            return false;
        }
        this.setIsSheared(true);
        return true;
    }

    default boolean isSheared() {
        return getMemoryStorage().get(CoreMemoryTypes.IS_SHEARED);
    }

    default void setIsSheared(boolean isSheared) {
        getMemoryStorage().put(CoreMemoryTypes.IS_SHEARED, isSheared);
    }
}
