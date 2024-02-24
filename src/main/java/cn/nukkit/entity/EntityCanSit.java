package cn.nukkit.entity;

import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.data.EntityFlag;

/**
 * 可坐下实体接口<p>
 */


public interface EntityCanSit extends EntityComponent {
    default boolean isSitting() {
        return getMemoryStorage().get(CoreMemoryTypes.IS_SITTING);
    }

    default void setSitting(boolean sitting) {
        getMemoryStorage().put(CoreMemoryTypes.IS_SITTING, sitting);
        asEntity().setDataFlag(EntityFlag.SITTING, sitting);
    }
}
