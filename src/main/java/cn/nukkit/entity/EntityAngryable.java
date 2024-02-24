package cn.nukkit.entity;

import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.data.EntityFlag;

/**
 * 可生气实体<p>
 */


public interface EntityAngryable extends EntityComponent {
    default boolean isAngry() {
        return getMemoryStorage().get(CoreMemoryTypes.IS_ANGRY);
    }

    default void setAngry(boolean angry) {
        getMemoryStorage().put(CoreMemoryTypes.IS_ANGRY, angry);
        asEntity().setDataFlag(EntityFlag.ANGRY, angry);
    }
}
