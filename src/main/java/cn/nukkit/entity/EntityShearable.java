package cn.nukkit.entity;

import cn.nukkit.entity.ai.memory.CoreMemoryTypes;

/**
 * 实体可剪切<p/>
 * 例如羊就可被剪羊毛<p/>
 * 若作用于此实体的物品的isShears()为true，则将会调用此方法
 * <br>
 * Entities that can be sheared. Stores value with {@link CoreMemoryTypes#IS_SHEARED}
 */
public interface EntityShearable extends EntityComponent {
    /**
     * @return 此次操作是否有效。若有效，将会减少物品耐久 true if shearing succeeded.
     */
    default boolean shear() {
        if (this.isSheared() || (this.asEntity() instanceof EntityAgeable age && age.isBaby())) {
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
