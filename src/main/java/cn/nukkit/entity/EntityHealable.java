package cn.nukkit.entity;

import cn.nukkit.item.Item;

/**
 * 实体可通过喂食食物被治疗
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
     * 获得可以治疗食物的治疗量
     */
    /*
    int getHealingAmount(Item item);
     */
}
