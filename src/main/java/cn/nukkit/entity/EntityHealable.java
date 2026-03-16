package cn.nukkit.entity;

import cn.nukkit.item.Item;

/**
 * The body can be treated by feeding it food.
 */

/**
 * @deprecated Since 2.0.0 (2026-02-19).
 * EntityHealable state handling was moved to {@link Entity}.
 * Use {@link Entity#getHealable()} and {@link Entity#isHealable()} instead.
 *
 * Planned removal: after 6 months (>= 2026-08-19).
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public interface EntityHealable {
    /**
     * Obtain the healing amount that can be used to treat food.
     */
    /*
    int getHealingAmount(Item item);
     */
}
