package org.powernukkitx.item.enchantment.bow;

import org.powernukkitx.entity.EntityLiving;
import org.powernukkitx.entity.projectile.EntityProjectile;
import org.powernukkitx.item.ItemBow;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.item.enchantment.EnchantmentType;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EnchantmentBow extends Enchantment {
    protected EnchantmentBow(int id, String name, Rarity rarity) {
        super(id, name, rarity, EnchantmentType.BOW);
    }

    /**
     * Called when the bow is shot
     *
     * @param user       the entity using the bow
     * @param projectile the arrow entity
     * @param bow        the bow item
     */
    public void onBowShoot(EntityLiving user, EntityProjectile projectile, ItemBow bow) {

    }
}
