package cn.nukkit.entity;

import cn.nukkit.item.Item;

/**
 * 实体可通过喂食食物被治疗
 */


public interface EntityHealable {
    /**
     * 获得可以治疗食物的治疗量
     */
    int getHealingAmount(Item item);
}
