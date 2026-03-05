package cn.nukkit.entity;

import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.data.EntityFlag;



/**
 * @deprecated Since 2.0.0 (2026-02-19).
 * Anger state handling was moved to {@link Entity}.
 * Use {@link Entity#isAngry()} and {@link Entity#setAngry(boolean)} instead.
 *
 * Planned removal: after 6 months (>= 2026-08-26).
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public interface EntityAngryable extends EntityComponent {
    /*
    default boolean isAngry() {
        return getMemoryStorage().get(CoreMemoryTypes.IS_ANGRY);
    }

    default void setAngry(boolean angry) {
        getMemoryStorage().put(CoreMemoryTypes.IS_ANGRY, angry);
        asEntity().setDataFlag(EntityFlag.ANGRY, angry);
    }
     */
}
