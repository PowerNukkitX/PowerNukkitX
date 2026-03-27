package cn.nukkit.entity;


/**
 * @deprecated Use {@link Entity#isAgeable()}, {@link Entity#isBaby()} and
 * {@link Entity#setBaby(boolean)} instead.
 *
 * <p>
 * This interface is kept for backward compatibility only.
 * All age-related logic has been moved into {@link Entity} to avoid
 * fragile type hierarchies and to ensure consistent behavior across
 * all entities.
 * </p>
 *
 * <p>
 * Implementations should override {@link Entity#isAgeable()} in their
 * entity class rather than implementing this interface.
 * </p>
 *
 * Planned removal: after 6 months (>= 2026-08-19).
 * @author MagicDroidX
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public interface EntityAgeable {
    /*
    default boolean isBaby() {
        return ((Entity) this).getDataFlag(EntityFlag.BABY);
    }

    default void setBaby(boolean flag) {
        var entity = (Entity) this;
        entity.setDataFlag(EntityFlag.BABY, flag);
        entity.setScale(flag ? 0.5f : 1f);
    }
     */
}
