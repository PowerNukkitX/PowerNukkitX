package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 代表可以攻击其他实体的实体.
 * <p>
 * Represents an entity that can attack other entities.
 */
@PowerNukkitXOnly
@Since("1.19.21-r5")
public interface EntityCanAttack {
    /**
     * 得到所有难度下不携带物品能造成的伤害.
     * <p>
     * Get the damage you can do without carrying items on all difficulties.
     *
     * @return 一个包含所有难度下伤害的数组, 0 1 2分别代表简单、普通、困难难度<br>An array containing damage on all difficulties, 0 1 2 for easy, normal and hard difficulties respectively
     */
    float[] getDiffHandDamage();

    /**
     * 得到指定难度下不携带物品能造成的伤害.
     * <p>
     * Get the damage that can be dealt without carrying the item at the specified difficulty.
     *
     * @param difficulty 难度id<br>difficulty id
     * @return 伤害<br>damage
     */
    float getDiffHandDamage(int difficulty);

    /**
     * 设置所有难度下不携带物品能造成的伤害.
     * <p>
     * Set the damage that can be dealt without carrying items on all difficulties.
     *
     * @param damages 伤害
     */
    void setDiffHandDamage(float[] damages);

    /**
     * 设置指定难度下不携带物品能造成的伤害
     * <p>
     * Set the damage that can be dealt without carrying the item at the specified difficulty.
     *
     * @param difficulty 难度
     * @param damage     伤害
     */
    void setDiffHandDamage(int difficulty, float damage);
}
