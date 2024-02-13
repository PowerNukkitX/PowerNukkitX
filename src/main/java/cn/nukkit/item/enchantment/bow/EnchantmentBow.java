package cn.nukkit.item.enchantment.bow;

import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.item.ItemBow;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EnchantmentBow extends Enchantment {
    protected EnchantmentBow(int id, String name, Rarity rarity) {
        super(id, name, rarity, EnchantmentType.BOW);
    }

    /**
     * 当弓箭射击时被调用
     *
     * @param user       使用弓的实体
     * @param projectile 箭实体
     * @param bow        弓物品
     */
    public void onBowShoot(EntityLiving user, EntityProjectile projectile, ItemBow bow) {

    }
}
