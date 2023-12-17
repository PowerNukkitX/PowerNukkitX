package cn.nukkit.entity;

import cn.nukkit.entity.ai.memory.CoreMemoryTypes;

/**
 * 可坐下实体接口<p>
 */


public interface EntityCanSit extends EntityComponent {
    default boolean isSitting() {
        return getMemoryStorage().get(CoreMemoryTypes.IS_SITTING);
    }

    default void setSitting(boolean sitting) {
        getMemoryStorage().put(CoreMemoryTypes.IS_SITTING, sitting);
        asEntity().setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_SITTING, sitting);
    }
}
